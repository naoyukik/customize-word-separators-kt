---
name: referencing-commit-convention
description: コミットメッセージの型 (type) 定義と具体例。基本フォーマットは AGENTS.md を参照せよ。
---

# Commit Convention - Type Definitions

本スキルは、コミットメッセージにおける `type` の適切な選択を支援する。

## 1. Type の定義 (Taxonomy)

| Type       | 適用場面                                |
|:-----------|:------------------------------------|
| `feat`     | 新機能の追加、または仕様変更を伴う機能拡張。              |
| `fix`      | バグ修正、または予期せぬ動作の修正。                  |
| `refactor` | 機能を変えずにコードの内部構造を改善する変更。             |
| `style`    | 動作に影響しない、フォーマットやタイポの修正（detekt 指摘等）。 |
| `docs`     | ドキュメント（.md, .txt 等）のみの変更。           |
| `test`     | テストコードの追加、または既存テストの修正。              |
| `chore`    | ビルド設定、依存関係の更新、またはツールの設定変更。          |

## 2. 具体例 (Examples)

### 機能追加とIssue連携

```text
feat: 文字種判定に絵文字のサポートを追加

- ユーザーが絵文字の境界でカーソルを止められるように実装

ref: 110

Co-Authored-By: gemini-cli <218195315+gemini-cli@users.noreply.github.com>
```

### アーキテクチャ改善

```text
refactor: MoveCaretWordService から WordParser への直接依存を排除

ref: 117

Co-Authored-By: gemini-cli <218195315+gemini-cli@users.noreply.github.com>
```
