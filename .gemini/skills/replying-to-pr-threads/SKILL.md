---
name: replying-to-pr-threads
description: Replies to specific GitHub Pull Request review threads using GraphQL API. Use when addressing individual review comments.
---

# GitHub PRレビューコメントへの返信方法

特定のレビューコメントスレッドに対して返信を行う場合、以下の手順を実行する。

## 1. スレッドIDの取得

GitHub MCP Serverの `pull_request_read` ツールを使用し、`method: "get_review_comments"` を指定してレビューコメント一覧を取得する。
レスポンス内の `reviewThreads` 配下にある `ID` (例: `PRRT_...`) がスレッドIDである。

## 2. 返信コマンドの実行 (GitHub-Efficiency)

取得したスレッドID (`<THREAD_ID>`) と返信内容 (`<BODY>`) を用いて、以下の **GitHub-Efficiency** プロトコルで返信を実行する。

1. **コマンド実行**: `run_shell_command` を使用し、直接 `gh api graphql` を実行して返信を投稿する。

    ```powershell
    gh api graphql -f query='
      mutation($threadId: ID!, $body: String!) {
        addPullRequestReviewThreadReply(input: {pullRequestReviewThreadId: $threadId, body: $body}) {
          comment { url }
        }
      }' -f threadId='<THREAD_ID>' -f body='<BODY>\n\nCommented by Gemini CLI'
    ```

2. **結果確認**: `run_shell_command` の実行結果（例:
   `{"data":{"addPullRequestReviewThreadReply":{"comment":{"url":"..."}}}}`）から、投稿が成功したかを確認する。
