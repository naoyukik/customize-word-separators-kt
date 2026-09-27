// .opencode/plugins/v2/MemPalaceAutosaveHook.ts
//
// OpenCode2 (V2 Plugin API) 用の MemPalace 自動保存フック。
// N 回の応答ステップ (session.step.ended) ごとに、AI へ MemPalace への保存を促す
// プロンプトを注入する。V1 の session.idle は V2 beta のイベント語彙に存在しない
// ため、実在する session.step.ended で代替する。
// V1 の .opencode/plugins/MemPalaceAutosaveHook.ts を V2 へ移植したもの。

import { Plugin } from "@opencode-ai/plugin"
import * as fs from "fs"
import * as path from "path"
import * as os from "os"

// === 設定 ===
const SAVE_INTERVAL = 15
const STATE_DIR = path.join(os.homedir(), ".mempalace", "hook_state_opencode")

function log(msg: string) {
  const line = `[${new Date().toISOString()}] ${msg}\n`
  fs.appendFileSync(path.join(STATE_DIR, "mempalace-debug.log"), line)
}

function ensureStateDir() {
  fs.mkdirSync(STATE_DIR, { recursive: true })
}

function loadCount(sessionID: string): number {
  const file = path.join(STATE_DIR, `${sessionID}.count`)
  try {
    return parseInt(fs.readFileSync(file, "utf-8").trim(), 10) || 0
  } catch {
    return 0
  }
}

function saveCount(sessionID: string, count: number) {
  ensureStateDir()
  fs.writeFileSync(path.join(STATE_DIR, `${sessionID}.count`), String(count))
}

export default Plugin.define({
  id: "ryntra.mempalace-autosave",
  async setup(ctx) {
    const controller = new AbortController()

    void (async () => {
      for await (const event of ctx.event.subscribe({ signal: controller.signal })) {
        if (event.type !== "session.step.ended") continue

        const sessionID = event.data.sessionID ?? "default"
        const count = loadCount(sessionID) + 1

        if (count < SAVE_INTERVAL) {
          saveCount(sessionID, count)
          continue
        }

        saveCount(sessionID, 0)

        try {
          await ctx.session.prompt({
            sessionID,
            text:
              "MemPalace save checkpoint. Write a brief session diary entry covering key topics, decisions, and code changes since the last save. Use verbatim quotes where possible. Continue after saving.",
          })
        } catch (err) {
          log(`FAILED to inject save prompt: ${err}`)
        }
      }
    })()

    return () => controller.abort()
  },
})
