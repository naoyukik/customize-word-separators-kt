// .opencode/plugins/v2/ValidateArchitectureTsHook.ts
//
// `conductor/code_styleguides/architecture_rules.md` (TypeScript) の検証フック。
// Rule 1 (責務サフィックス・配置)、Rule 2 (依存方向)、Rule 3 (隔離) を検査する。
// 警告のみ (allow相当) とし、ツール実行をブロックしない。
//
// 検証対象: tool 入力中のファイルパス + 内容、および command 文字列中の `src/...ts(x)`。
// 違反検出時: `tool.execute.after` の result へ警告文を追記し、モデルに修正を促す。

import { Plugin } from "@opencode-ai/plugin"

// === Rule 1: サフィックス規約 (サフィックス -> 配置層) ===
type SuffixRule = { suffix: string; layer: "application" | "infrastructure" | "presentation"; portsOnly?: boolean }

const SUFFIX_RULES: SuffixRule[] = [
  { suffix: "UseCase.ts", layer: "application" },
  { suffix: "Command.ts", layer: "application" },
  { suffix: "Repository.ts", layer: "application", portsOnly: true },
  { suffix: "Exporter.ts", layer: "application", portsOnly: true },
  { suffix: "Importer.ts", layer: "application", portsOnly: true },
  { suffix: "View.tsx", layer: "presentation" },
  { suffix: "Adapter.ts", layer: "presentation" },
  { suffix: "Store.ts", layer: "presentation" },
]

// === Rule 2: 依存許可ルール (From Layer -> Allowed Target Layers) ===
const DEPENDENCY_RULES: Record<string, string[]> = {
  presentation: ["presentation", "application", "domain", "shared"],
  application: ["application", "domain", "shared"],
  domain: ["domain", "shared"],
  infrastructure: ["infrastructure", "application", "domain", "shared"],
}

// === Rule 3: 隔離対象 (domain / application で禁止) ===
const ISOLATION_PATTERNS: { pattern: RegExp; label: string }[] = [
  { pattern: /\bfrom\s+['"]react[^'"]*['"]/, label: "React" },
  { pattern: /\brequire\(\s*['"]react[^'"]*['"]\s*\)/, label: "React" },
  { pattern: /['"]@xyflow\/react['"]/, label: "React Flow (@xyflow/react)" },
  { pattern: /\bfrom\s+['"]zustand[^'"]*['"]/, label: "Zustand" },
  { pattern: /\brequire\(\s*['"]zustand[^'"]*['"]\s*\)/, label: "Zustand" },
  { pattern: /['"]@tauri-apps\/[^'"]+['"]/, label: "Tauri" },
  { pattern: /\blocalStorage\b/, label: "localStorage" },
]

const WHITELIST_FILES = new Set(["main.tsx", "vite-env.d.ts"])
const WHITELIST_COMMANDS = new Set(["rm", "del", "mv", "move", "git"])

type Target = { path: string; text?: string }

function basename(p: string): string {
  return p.split(/[\\/]/).pop() ?? p
}

function dirname(p: string): string {
  const idx = Math.max(p.lastIndexOf("/"), p.lastIndexOf("\\"))
  return idx < 0 ? "" : p.slice(0, idx)
}

function normalize(p: string): string {
  return p.replace(/\\/g, "/")
}

// `apps/web/src/<layer>/...` から層名を抽出する。該当なしは null。
function detectLayer(filePath: string): string | null {
  const m = normalize(filePath).match(/\/src\/(presentation|application|domain|infrastructure|shared)\//)
  return m ? m[1] : null
}

function isTestFile(filename: string): boolean {
  return /\.test\.[cm]?[tj]sx?$/.test(filename) || /\.spec\.[cm]?[tj]sx?$/.test(filename)
}

function checkNamingAndLocation(filePath: string): string | null {
  if (detectLayer(filePath) === null) return null
  const filename = basename(filePath)
  if (WHITELIST_FILES.has(filename)) return null
  if (filename.endsWith(".d.ts") || isTestFile(filename)) return null

  const normDir = normalize(filePath).toLowerCase()

  for (const rule of SUFFIX_RULES) {
    if (!filename.endsWith(rule.suffix)) continue

    // ポート抽象と具象実装の振り分け (Repository.ts / Exporter.ts / Importer.ts)
    if (rule.portsOnly) {
      const stem = filename.slice(0, -rule.suffix.length)
      if (normDir.includes("/application/")) {
        return null
      }
      if (normDir.includes("/infrastructure/")) {
        if (stem.length === 0) {
          return `命名規則違反(Rule 1): infrastructure配下の '${filename}' には具象名の接頭辞が必要 (例 FileDocumentRepository.ts)。`
        }
        return null
      }
      return `配置違反(Rule 1): '${filename}' は application/ports (抽象) または infrastructure (具象実装) 配下に配置すること。`
    }

    if (!normDir.includes(`/${rule.layer}/`)) {
      return `配置違反(Rule 1): '${filename}' は '${rule.layer}' 配下に配置すること。`
    }
    return null
  }

  // 既知サフィックスに該当しないファイル (Entity等) は検査しない
  return null
}

function checkIsolation(filePath: string, content?: string): string | null {
  if (!content) return null
  const layer = detectLayer(filePath)
  if (layer !== "domain" && layer !== "application") return null

  for (const { pattern, label } of ISOLATION_PATTERNS) {
    if (pattern.test(content)) {
      return `隔離違反(Rule 3): ${layer}層で '${label}' への依存は禁止。`
    }
  }
  return null
}

// 相対import先を字句的に解決し、層名を抽出する。解決不可は null。
function resolveImportLayer(fromDir: string, spec: string): string | null {
  if (!spec.startsWith(".")) return null
  const parts = (fromDir + "/" + spec).split("/")
  const stack: string[] = []
  for (const p of parts) {
    if (p === "" || p === ".") continue
    if (p === "..") stack.pop()
    else stack.push(p)
  }
  const resolved = stack.join("/")
  const m = resolved.match(/\/src\/(presentation|application|domain|infrastructure|shared)\//)
  if (m) return m[1]
  if (/(^|\/)src\/(presentation|application|domain|infrastructure|shared)\//.test(resolved)) {
    return resolved.match(/src\/(presentation|application|domain|infrastructure|shared)\//)![1]
  }
  return null
}

function validateDependence(filePath: string, content?: string): string | null {
  if (!content) return null
  const layer = detectLayer(filePath)
  if (layer === null) return null
  const allowed = DEPENDENCY_RULES[layer]
  if (!allowed) return null

  const fromDir = normalize(dirname(filePath))
  const specs = new Set<string>()
  const importRe = /(?:import|export)[^'"]*?from\s+['"]([^'"]+)['"]/g
  const dynamicRe = /import\(\s*['"]([^'"]+)['"]\s*\)/g
  let m: RegExpExecArray | null
  while ((m = importRe.exec(content)) !== null) specs.add(m[1])
  while ((m = dynamicRe.exec(content)) !== null) specs.add(m[1])

  for (const spec of specs) {
    const target = resolveImportLayer(fromDir, spec)
    if (target === null) continue
    if (!allowed.includes(target)) {
      return `依存違反(Rule 2): '${layer}' から '${target}' への依存は許可されていない ('${spec}')。`
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

// tool input から検証対象を抽出する。
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
      const matches = command.match(/src\/[^\s"'=,]+\.[cm]?[tj]sx?/g) ?? []
      for (const m of matches) targets.push({ path: m })
    }
  }

  return targets
}

function validateTargets(targets: Target[]): string[] {
  const errors: string[] = []
  for (const { path, text } of targets) {
    if (!/\.[cm]?[tj]sx?$/.test(path)) continue
    if (WHITELIST_FILES.has(basename(path))) continue

    const errNaming = checkNamingAndLocation(path)
    if (errNaming) errors.push(`${path}: ${errNaming}`)

    if (text) {
      const errIso = checkIsolation(path, text)
      if (errIso) errors.push(`${path}: ${errIso}`)

      const errDep = validateDependence(path, text)
      if (errDep) errors.push(`${path}: ${errDep}`)
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
  id: "ryntra.validate-architecture-ts",
  async setup(ctx) {
    await ctx.tool.hook("execute.after", (event) => {
      if (event.status !== "completed") return
      const targets = extractTargets(event.input)
      if (targets.length === 0) return
      const errors = validateTargets(targets)
      if (errors.length === 0) return

      const combined = errors.join("\n")
      const warning = `アーキテクチャ警告(Rule 1-3): 可能な限り直ちに修正すること。\n${combined}`
      console.error(`[ryntra.validate-architecture-ts] ${combined}`)
      event.result = appendWarning(event.result, warning) as typeof event.result
    })

    // shell 直叩き (command 文字列中の src/*.ts(x)) の命名・配置のみを事前検査する。
    // 内容を伴わないため依存・隔離検査は行わない。警告のみでブロックしない。
    await ctx.shell.hook("create.before", (event) => {
      const matches = event.command.match(/src\/[^\s"'=,]+\.[cm]?[tj]sx?/g) ?? []
      if (matches.length === 0) return
      const errors = validateTargets(matches.map((path) => ({ path })))
      if (errors.length > 0) {
        console.error(`[ryntra.validate-architecture-ts] shell: ${errors.join(" / ")}`)
      }
    })
  },
})
