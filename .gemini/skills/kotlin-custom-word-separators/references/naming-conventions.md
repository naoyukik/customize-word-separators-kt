# Kotlin Naming Conventions

本プロジェクトでは Kotlin の標準的な命名規約を遵守し、コードの可読性と保守性を最大化する。

## 1. 基本ルール

| 対象                            | 形式                         | 例                                       |
|:------------------------------|:---------------------------|:----------------------------------------|
| クラス、インターフェース、オブジェクト           | `PascalCase`               | `WordParser`, `MoveCaretWordService`    |
| 関数、変数、プロパティ                   | `camelCase`                | `wordParse()`, `currentOffset`          |
| 定数（`const val`, `val` トップレベル） | `SCREAMING_SNAKE_CASE`     | `MAX_LINE_LENGTH`, `DEFAULT_PATTERN`    |
| パッケージ                         | `lower.case`               | `net.dstribe.customize_word_separators` |
| bool型変数・関数                    | 述語形式 (`is`, `has`, `can`等) | `isNext`, `hasSelection`, `canMove()`   |

## 2. IntelliJ Platform 固有の命名

- **Action**: `<ActionName>Action` (例: `NextWordAction`)
- **Service**: `<ServiceName>Service` (例: `MoveCaretWordService`)
- **Configurable**: `<SettingsName>Configurable` (例: `AppSettingsConfigurable`)

## 3. レイヤードアーキテクチャに基づく命名

- **Command (Domain)**: `<Action>Command` (例: `MoveCaretCommand`)
- **State (Settings)**: `<Feature>State` (例: `AppSettingsState`)

## 4. 鉄則

- 意味のある名前を付け、`tmp`, `data` などの曖昧な名前を避ける。
- 名前からその役割（計算、状態保持、副作用の実行等）が推測できるようにする。
- 略語は避け、単語を完全に綴る（`cnt` -> `count`, `idx` -> `index`）。
