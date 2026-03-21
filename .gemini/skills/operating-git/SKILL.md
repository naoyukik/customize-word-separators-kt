---
name: operating-git
description: Gitワークフローの詳細なコマンド実行とトラブルシューティング。ステージングの原則は AGENTS.md を参照せよ。
---

# Operating Git - Procedure & Examples

本スキルは、Git 操作の具体的な手順と、ミスを防ぐための確認フローを提供する。基本的な行動原則（git add . の禁止等）については
`AGENTS.md` を最優先で遵守せよ。

## 1. 推奨されるステージング手順

意図しないファイルの混入を防ぐため、以下の手順を習慣化すること。

```bash
# 1. 変更内容の確認
git status
git diff

# 2. ファイルを個別にステージング (AGENTS.md 遵守)
git add path/to/file1 path/to/file2

# 3. ステージングされた内容の最終監査
git diff --staged
```

## 2. コミットの作成

コミットメッセージの作成については `referencing-commit-convention` スキル、および `AGENTS.md` の規約に従うこと。

## 3. トラブルシューティング

### 誤って `git add .` してしまった場合

直ちに以下のコマンドでステージングを解除せよ。

```bash
git reset
```

### コミット後にミスに気づいた場合

プッシュ前であれば、修正後に `amend` を検討せよ。

```bash
git add <forgotten_file>
git commit --amend --no-edit
```
