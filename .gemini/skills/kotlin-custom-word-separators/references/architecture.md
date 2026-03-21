# IntelliJ Plugin Architecture: Customize Word Separators

本プロジェクトは、IntelliJ Platform SDK をベースとしたレイヤードアーキテクチャを採用している。
各層の責務を厳守し、層を跨ぐ直接的な依存を避けること。

## 1. Domain Layer (`net.dstribe.customize_word_separators.domain`)

- **責務**: 単語区切りの論理計算、正規表現のパース、およびプラットフォーム非依存のコマンド。
- **主要クラス**:
    - `WordParser`: 文字種（CJK, ひらがな, カタカナ等）の判定と単語境界のパース。
    - `BuiltinWordActionCommand`: 標準アクションの代替ロジック。
    - `MoveCaretCommand`: カーソル移動の物理的な指示。
- **制約**: IntelliJ API への直接依存は最小限（`TextRange` 等の基本的なユーティリティに留める）とし、ビジネスロジックを
  Pure Kotlin で保つ。

## 2. Application Layer (`net.dstribe.customize_word_separators.application`)

- **責務**: エディタの状態（Caret, Selection, Folding）を把握し、Domain 層のロジックを呼び出すオーケストレーション。
- **主要クラス**:
    - `MoveCaretWordService`: カーソル移動の全体制御。
- **制約**: エディタの `FoldRegion` 処理などの IDE 固有の状態管理はこの層で行う。

## 3. Presentation Layer (`net.dstribe.customize_word_separators.presentation`)

- **責務**: IDE アクションのハンドリングと、ユーザー入力（ショートカット等）の受信。
- **主要クラス**:
    - `NextPrevWordEditorActionHandler`: 標準アクションのフック。
    - `NextWordAction` 等: プラグインのアクション定義。

## 4. Settings Layer (`net.dstribe.customize_word_separators.settings`)

- **責務**: ユーザー設定の永続化と、設定画面（UI）の提供。
- **主要クラス**:
    - `AppSettingsState`: `PersistentStateComponent` を実装した永続化設定。
    - `AppSettingsConfigurable`: `Configurable` を実装した UI 定義。
