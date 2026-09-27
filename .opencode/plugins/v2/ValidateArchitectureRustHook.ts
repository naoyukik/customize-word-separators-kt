// .opencode/plugins/v2/ValidateArchitectureRustHook.ts
//
// `.codex/hooks/validate_architecture.py` の opencode2 (V2 Plugin API) 移植。
// Rust レイヤードアーキテクチャの命名・配置・依存・隔離ルールを検証する。
// 元フックと同様に警告のみ (allow相当) とし、ツール実行をブロックしない。
//
// 検証対象: tool 入力中のファイルパス + 内容、および command 文字列中の `src/...rs`。
// 違反検出時: `tool.execute.after` の result へ警告文を追記し、モデルに修正を促す。

import { Plugin } from "@opencode-ai/plugin"

// === アーキテクチャ定義 (Suffix vs Directory) ===
const ARCH_RULES: Record<string, string> = {
  "_resolver.rs": "gui/resolver",
  "_gui_driver.rs": "gui/driver",
  "_request.rs": "gui",
  "_response.rs": "gui",
  "_workflow.rs": "application",
  "_input.rs": "application",
  "_result.rs": "application",
  "_entity.rs": "domain/model",
  "_value.rs": "domain/model",
  "_domain_service.rs": "domain/service",
  "_protocol_handler.rs": "domain/service",
  "_repository.rs": "domain/repository",
  "_repository_impl.rs": "infra/repository",
  "_io_driver.rs": "infra/driver",
}

// === 依存許可ルール (From Layer -> Allowed Target Paths) ===
const DEPENDENCY_RULES: Record<string, string[]> = {
  "gui/resolver": ["application", "domain", "gui/driver"],
  "gui/driver": ["domain"],
  "application": ["domain"],
  "domain/model": ["domain/model"],
  "domain/repository": ["domain/model"],
  "domain/service": ["domain/model", "domain/repository"],
  "domain": [],
  "infra/repository": ["domain", "infra/driver"],
  "infra/driver": ["domain"],
}

const WHITELIST_FILES = new Set(["mod.rs", "lib.rs", "main.rs", "build.rs", "resource.rs"])
const WHITELIST_COMMANDS = new Set(["rm", "del", "mv", "move", "git"])

type Target = { path: string; text?: string }

function basename(p: string): string {
  return p.split(/[\\/]/).pop() ?? p
}

function dirname(p: string): string {
  const idx = Math.max(p.lastIndexOf("/"), p.lastIndexOf("\\"))
  return idx < 0 ? "" : p.slice(0, idx)
}

function checkNamingAndLocation(filePath: string): string | null {
  const filename = basename(filePath)
  const pathDir = dirname(filePath).replace(/\\/g, "/").toLowerCase()

  if (WHITELIST_FILES.has(filename)) return null

  let requiredDir: string | null = null
  for (const [suffix, layerDir] of Object.entries(ARCH_RULES)) {
    if (filename.endsWith(suffix)) {
      requiredDir = layerDir
      break
    }
  }

  if (!requiredDir) {
    return `命名規則違反: '${filename}' には有効な接尾辞 (Suffix Rule) が必要。architecture_rules.md を確認せよ。`
  }

  if (!pathDir.includes(requiredDir)) {
    return `配置違反: '${filename}' は '${requiredDir}' 配下に配置すること。`
  }

  return null
}

function checkWindowsApiIsolation(filePath: string, content?: string): string | null {
  if (!content) return null
  const pathDir = dirname(filePath).replace(/\\/g, "/").toLowerCase()
  if (pathDir.includes("domain") || pathDir.includes("application")) {
    if (/\buse\s+windows\b/.test(content) || /\bwindows::\b/.test(content)) {
      return "隔離命令違反: Domain層およびApplication層で 'windows' クレートの直接使用は禁止。Pure Rust定義を使用せよ。"
    }
  }
  return null
}

function validateDependence(filePath: string, content?: string): string | null {
  if (!content) return null
  const pathDir = dirname(filePath).replace(/\\/g, "/").toLowerCase()

  let currentLayer: string | null = null
  for (const layerDir of Object.values(ARCH_RULES)) {
    if (pathDir.includes(layerDir)) {
      currentLayer = layerDir
      break
    }
  }
  if (!currentLayer) return null

  const allowedTargets = DEPENDENCY_RULES[currentLayer] ?? []
  const refs = content.match(/\bcrate::([^\s;:(]+)/g)?.map((m) => m.replace(/^crate::/, "")) ?? []

  for (const ref of refs) {
    if (ref.includes("infra") && ref.includes("impl")) {
      return `DIの掟違反: 具象実装 '${ref}' の直接参照は禁止。Repository Trait を使用せよ。`
    }

    let isAllowed = false
    for (const allowed of allowedTargets) {
      if (ref.startsWith(allowed.replace(/\//g, "::"))) {
        isAllowed = true
        break
      }
    }

    if (currentLayer.startsWith("gui/") && ref.startsWith("gui")) {
      isAllowed = true
    }

    if (
      ref.startsWith(currentLayer.replace(/\//g, "::")) ||
      ref.startsWith("common") ||
      ref.startsWith("get_instance_handle")
    ) {
      isAllowed = true
    }

    if (!isAllowed) {
      return `依存の掟違反: レイヤー '${currentLayer}' から '${ref}' への依存は許可されていない。`
    }
  }

  return null
}

function asRecord(input: unknown): Record<string, unknown> {
  if (typeof input === "object" && input !== null) return input as Record<string, unknown>
  return {}
}

function asString(v: unknown): string | undefined {
  return typeof v === "string" && v.length > 0 ? v : undefined
}

// tool input から検証対象を抽出する。元Pythonの tool_input 抽出と同等。
function extractTargets(input: unknown): Target[] {
  const args = asRecord(input)
  const targets: Target[] = []

  const filePath =
    asString(args["file_path"]) ??
    asString(args["pathInProject"]) ??
    asString(args["filePath"]) ??
    asString(args["path"]) ??
    asString(args["file"])
  const content =
    asString(args["text"]) ??
    asString(args["content"]) ??
    asString(args["code"]) ??
    asString(args["newText"]) ??
    asString(args["new_text"])

  if (filePath) targets.push({ path: filePath, text: content })

  const command = asString(args["command"]) ?? asString(args["cmd"])
  if (command) {
    const subCommands = command.split(/[;&|]/)
    let allSafe = true
    let hasTokens = false
    for (const sub of subCommands) {
      const tokens = sub.trim().split(/\s+/).filter(Boolean)
      if (tokens.length === 0) continue
      hasTokens = true
      const exe = basename(tokens[0]).toLowerCase()
      if (!WHITELIST_COMMANDS.has(exe)) {
        allSafe = false
        break
      }
    }
    if (!(hasTokens && allSafe)) {
      const matches = command.match(/src\/[^\s"'=,]+\.rs/g) ?? []
      for (const m of matches) targets.push({ path: m })
    }
  }

  return targets
}

function validateTargets(targets: Target[]): string[] {
  const errors: string[] = []
  for (const { path, text } of targets) {
    if (!path.endsWith(".rs")) continue
    if (WHITELIST_FILES.has(basename(path))) continue

    const errNaming = checkNamingAndLocation(path)
    if (errNaming) errors.push(errNaming)

    if (text) {
      const errWin = checkWindowsApiIsolation(path, text)
      if (errWin) errors.push(errWin)
      const errDep = validateDependence(path, text)
      if (errDep) errors.push(errDep)
    }
  }
  return errors
}

function appendWarning(result: unknown, warning: string): unknown {
  if (typeof result === "string") {
    return `${result}\n\n${warning}`
  }
  if (typeof result === "object" && result !== null) {
    const r = result as Record<string, unknown>
    if (typeof r["output"] === "string") {
      return { ...r, output: `${r["output"]}\n\n${warning}` }
    }
    return { ...r, output: warning }
  }
  return warning
}

export default Plugin.define({
  id: "ryntra.validate-architecture",
  async setup(ctx) {
    await ctx.tool.hook("execute.after", (event) => {
      if (event.status !== "completed") return
      const targets = extractTargets(event.input)
      if (targets.length === 0) return
      const errors = validateTargets(targets)
      if (errors.length === 0) return

      const combined = errors.join("\n")
      const warning =
        `アーキテクチャ警告: 可能な限り直ちに修正すること。\n${combined}`
      console.error(`[ryntra.validate-architecture] ${combined}`)
      event.result = appendWarning(event.result, warning) as typeof event.result
    })

    // shell 直叩き (command 文字列中の src/*.rs) の命名・配置のみを事前検査する。
    // 内容を伴わないため依存・隔離検査は行わない。警告のみでブロックしない。
    await ctx.shell.hook("create.before", (event) => {
      const matches = event.command.match(/src\/[^\s"'=,]+\.rs/g) ?? []
      if (matches.length === 0) return
      const errors = validateTargets(matches.map((path) => ({ path })))
      if (errors.length > 0) {
        console.error(`[ryntra.validate-architecture] shell: ${errors.join(" / ")}`)
      }
    })
  },
})
