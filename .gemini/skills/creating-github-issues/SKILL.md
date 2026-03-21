---
name: creating-github-issues
description: Standardizing the creation and management of GitHub Issues. This skill MANDATES the use of a specific title format (type: Japanese Description) and a body template including Background/Purpose, Tasks, and Goal sections. It ensures consistency in tracking work items and facilitates clear communication of intent and success criteria. Use whenever creating or updating Issues to maintain project transparency.
---

# GitHub Issue作成ガイドライン

## タイトル形式

```
type: 日本語での簡潔な説明
```

接頭辞（type）は半角小文字とする。

## Type一覧

| Type       | 用途                           |
|------------|------------------------------|
| `feat`     | 新機能の追加                       |
| `fix`      | バグ修正                         |
| `refactor` | リファクタリング（機能変更を伴わないコードの整理・改善） |
| `style`    | コードの意味に影響しない修正（空白、フォーマット等）   |
| `docs`     | ドキュメントの更新                    |
| `test`     | テストの追加・修正                    |
| `chore`    | ビルドプロセスやツール、依存ライブラリの更新       |

## 内容テンプレート

```markdown
## 背景・目的 (Background / Purpose)

なぜこの作業が必要なのか、現状の課題は何かを記述する。
関連する設計指針やガイドラインがある場合は明記する。

## タスク (Tasks)

- [ ] 具体的な作業1
- [ ] 具体的な作業2
- [ ] 具体的な作業3

## ゴール (Goal)

どのような状態になれば、この Issue を「完了（Closed）」とみなすことができるかを定義する。
```

## 運用ルール

- **適切な粒度**: 1つのIssueで扱う範囲を大きくしすぎない。多岐にわたる場合は分割を検討
- **リファクタリングの根拠**: `refactor` Issueでは、どのコード規約やアーキテクチャ方針に基づいた変更なのかを明示
- **透明性**: 思考過程や途中の気付きはIssueのコメント欄に随時記録

## GitHub CLI (gh) による Issue 作成時の注意点

PowerShell 環境から `gh issue create` や `gh issue edit` を使用して Issue の本文を作成・更新する場合、ヒアドキュメントを利用することが推奨される。
その際、Markdown内のインラインコード（バッククォート `` ` ``）がPowerShellのエスケープ文字として解釈され消失してしまうのを防ぐため、
**必ずシングルクォートのヒアドキュメント（`@' ... '@`）を使用すること。**

**正しい例:**

```powershell
$body = @'
## ゴール (Goal)
- `WordParser` の `wordParse` メソッドで絵文字の境界判定を実装すること
'@
$body | Out-File -Encoding UTF8 temporary.local\issue_body.md
gh issue create --title "feat: xxx" --body-file temporary.local\issue_body.md
```
