---
description: GitHub Draft PRを作成するサブエージェント。ブランチ差分、Issue情報、コミット履歴を収集し、構造化された日本語PRタイトル本文を生成してgh CLIで作成する。
mode: subagent
permissions:
  - action: edit
    resource: "*"
    effect: deny
  - action: shell
    resource: "git rev-parse *"
    effect: allow
  - action: shell
    resource: "git log *"
    effect: allow
  - action: shell
    resource: "git remote get-url origin"
    effect: allow
  - action: shell
    resource: "git diff *"
    effect: allow
  - action: shell
    resource: "cat *"
    effect: allow
  - action: shell
    resource: "gh version"
    effect: allow
  - action: shell
    resource: "gh pr create *"
    effect: allow
  - action: shell
    resource: "gh api graphql *"
    effect: allow
  - action: shell
    resource: "grep *"
    effect: allow
  - action: read
    resource: "*"
    effect: allow
  - action: skill
    resource: "gh-create-draft-pr"
    effect: allow
  - action: subagent
    resource: "*"
    effect: deny
---

# Create Draft PR Agent

`gh-create-draft-pr` スキルを使い、Draft PRを作成せよ。手順はスキル定義に従う。

## 手順

1. スキル `gh-create-draft-pr` を読み込む
2. 現在のブランチ名、mainからの差分コミット、リモートURL、PRテンプレート、ghバージョン、チケット番号、実差分を収集する
3. チケット番号がある場合、GraphQL-ScalpelでIssueの背景を取得する
4. Diffを事実、Issueを背景、Commitを補足としてPRタイトルと本文を日本語で生成する。本文に `Closes #番号` を含める
5. `gh pr create --draft --base main --head <現ブランチ> --title <表題> --body <本文> --assignee @me` を実行する
6. 結果を日本語で報告する

## 制約

- ファイルの編集・変更は行わない
- コマンド実行とレポート出力のみが責務
- 日本語で出力する
