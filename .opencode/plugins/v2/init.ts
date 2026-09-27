// .opencode/plugins/v2/init.ts
//
// OpenCode2 (V2 Plugin API) 用の初期化フック。
// セッション開始時（最初の応答ステップ開始）に、開発で必要な参照スキルを読み込む
// プロンプトを注入する。V1 の session.created は V2 beta のイベント語彙に存在しない
// ため、実在する session.step.started で代替する（セッションIDごとに初回のみ）。

import { Plugin } from "@opencode-ai/plugin"

export default Plugin.define({
  id: "ryntra.init",
  async setup(ctx) {
    const controller = new AbortController()
    const injected = new Set<string>()

    void (async () => {
      for await (const event of ctx.event.subscribe({ signal: controller.signal })) {
        if (event.type !== "session.step.started") continue

        const sessionID = event.data.sessionID
        if (!sessionID) continue
        if (injected.has(sessionID)) continue

        injected.add(sessionID)

        try {
          await ctx.session.prompt({
            sessionID,
            text: "スキル referencing-commit-convention, operating-jetbrains-projects, ide-index-mcp, operating-git を読み込め。",
          })
        } catch (err) {
          console.error(`[ryntra.init] session.prompt 失敗: ${err}`)
        }
      }
    })()

    return () => controller.abort()
  },
})
