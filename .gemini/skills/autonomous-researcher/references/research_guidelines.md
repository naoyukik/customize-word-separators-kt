# リサーチクエリのガイドライン (IntelliJ Platform SDK 版)

効果的な調査を行うための検索クエリとアプローチの指針。

## 1. IntelliJ Platform SDK 調査

- **Query**: `site:plugins.jetbrains.com/docs/intellij [機能名/クラス名]`
- **Check**:
    - 対応する IDE バージョン (since-build / until-build)
    - 使用すべきインターフェース (Service, Extension Point, Action)
    - 推奨されるスレッドモデル (Read/Write Action, EDT)

## 2. Kotlin / Coroutines 調査

- **Query**: `kotlin lang documentation [キーワード]` または `kotlinx coroutines [メソッド名]`
- **Check**:
    - `kotlinlang.org` の最新リファレンス
    - 言語バージョンごとの構文（Context Receivers 等）
    - パフォーマンス上の制約（インライン関数、ボクシング）

## 3. Gradle / ビルドツール調査

- **Query**: `gradle intellij platform plugin [タスク名/設定]`
- **Check**:
    - GitHub (`JetBrains/intellij-platform-gradle-plugin`) の README / Wiki
    - バージョンカタログ (`libs.versions.toml`) の書き方
    - 依存関係の競合解消方法

## 4. TUI / ターミナル調査 (必要に応じて)

- **Query**: `ansi escape sequence [コード] behavior`
- **Check**:
    - 各プラットフォーム（Windows, macOS, Linux）のエディタ上での描画差異
