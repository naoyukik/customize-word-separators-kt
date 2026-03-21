---
name: kotlin-custom-word-separators
description: Assists in Kotlin development for the "Customize Word Separators" IntelliJ plugin. Provides advanced expertise in layered architecture responsibility management, WordParser regular expressions, IDE action implementations, and strict naming conventions to ensure the quality of refactoring and feature extensions.
---

# Skill: Kotlin Custom Word Separators

このスキルは、IntelliJ プラットフォーム向け「Customize Word Separators」プラグインの保守・拡張を専門とする。
Gemini CLI は、プロジェクトのアーキテクチャや単語判定ロジック、および Kotlin 命名規約を理解し、高品質なコードを提供する。

## Quick Navigation
詳細な実装ガイドや仕様については、以下の参照資料を読み込むこと。

- **Architecture Details**: [references/architecture.md](./references/architecture.md) を読み込み、各層の責務を確認する。
- **Word Parser Specifications**: [references/word-parser-patterns.md](./references/word-parser-patterns.md)
  を読み込み、単語判定に使用される正規表現と改善方針を確認する。
- **Naming Conventions**: [references/naming-conventions.md](./references/naming-conventions.md)
  を読み込み、本プロジェクトにおける命名標準を確認する。

## Implementation Workflows

### 1. リファクタリング

コアロジック（`domain`, `application`）のリファクタリングを行う際は、以下のステップを踏むこと。

1. `references/architecture.md` を読み込み、修正箇所の所属層を確認。
2. 他の層との境界を明確にし、Pure Kotlin であるべきロジックを特定。
3. リファクタリング前後で、`TextOperations` などのユーティリティを介した挙動の整合性を確保。

### 2. 新しい文字種の追加

新しい単語区切りパターンを追加する際は、以下のステップを踏むこと。

1. `references/word-parser-patterns.md` に定義されている正規表現の範囲と重複がないか確認。
2. `WordParser.kt` に新しい定数として正規表現を追加し、`wordParse` メソッドに統合。

## Coding Conventions

- **Layer Integrity**: `application` 層から `presentation` 層への逆方向の依存や、`domain` 層での IDE 固有クラス（`Caret`,
  `Editor`等）の直接使用は避ける。
- **Concise & Pure**: ロジックは簡潔に保ち、不必要な副作用を排除する。
