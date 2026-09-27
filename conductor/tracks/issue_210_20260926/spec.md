# Track Issue #210: IntelliJ 2026.3 への対応

## Background

- 現状 `pluginUntilBuild=262.*` までしか宣言していないため、2026.3 系ではプラグインがインストールできない。
- Issue #210 は「IntelliJ 2026.3 がリリースされた」前提で書かれているが、2026-09-26 時点で 2026.3 の安定版は存在しない。
- 一次情報（`data.services.jetbrains.com`）で確認した事実関係は次の通り。

| 項目 | 値 | 確認元 |
| --- | --- | --- |
| 最新安定版 | 2026.2.3（ビルド 262.6653.22、2026-09-16 公開） | `data.services.jetbrains.com/products/releases?code=IU&type=release` |
| 最新 2026.3 EAP | 263.5701.42（2026-09-25 公開） | `data.services.jetbrains.com/products/releases?code=IU&type=eap` |
| ブランチ一覧 | 2026.2 が最終行。2026.3 の行は未掲載 | `plugins.jetbrains.com/docs/intellij/build-number-ranges.html` |

- IntelliJ Platform Gradle Plugin 2.x の 既定は installer 解決（`useInstaller = true`）で、installer は公開リリースのみを配信する。
- 一方で EAP チャンネルの installer は `https://download.jetbrains.com/idea/idea-263.5701.42.exe` に存在する（HTTP 200 を確認済み）。
- Maven リポジトリ側では bare な `263.5701.42` は存在せず、`263.5701.42-EAP-SNAPSHOT` などのサフィックス付きだけが `intellij-repository/snapshots` に存在する。
- 参考として、現在の `262.6653.22` も `intellij-repository/releases` には存在せず（404）、installer 経由でのみ取得可能である。
- 以上から `platformVersion=263.5701.42` の installer 解決は成立すると考えられるが、この推測には実地検証を要する。Phase 0 で解決可否を先に確認する。

## Goals

- 2026.3 ブランチでプラグインが動作し、JetBrains Marketplace でインストール可能になる。
- 0.6.11 としてリリース可能な状態まで到達する。

## Decisions

対話フェーズ（4問）で確定した方針を以下に記録する。

| # | 論点 | 決定 |
| --- | --- | --- |
| 1 | 2026.3 のビルド指定 | EAP 263.5701.42 を採用する |
| 2 | pluginVersion | 0.6.11（過去 4 回と同じパッチリリース方針） |
| 3 | 検証で問題が出た場合 | 本トラック内で修正まで完了させる |
| 4 | 手動検証の方法 | `runIde` で起動した 2026.3 IDE で検証する |

## Requirements

- `gradle.properties` の4値、`pluginUntilBuild`・`verifierVersionUntil`・`platformVersion`・`pluginVersion` を目標値へ更新する。
- `CHANGELOG.md` の `## [Unreleased]` 配下に 2026.3 対応のエントリを追加する。
- `./gradlew buildPlugin` と `./gradlew verifyPlugin` が成功する。
- Plugin Verifier が報告する `compatibility problem` は本トラック内で解消する。
- `runIde` で起動した 2026.3 IDE における 4 アクション（Next / Prev / Next with Selection / Prev with Selection）の挙動をユーザーが確認する。
- `conductor/tech-stack.md` のサポートバージョン表記を現行の互換範囲に合わせる。

## Out of Scope

- 2026.3 安定版公開後のビルド番号の丸め。EAP ビルド番号のまま公開されるため、必要なら follow-up Issue を起票する。
- 単語区切り判定ロジックやカーソル移動挙動の機能変更。
- 依存ライブラリの更新。Renovate の管轄外とする。

## Impact Analysis

変更対象ファイルは以下のみ。機能コード（`src/`）は原則変更しないが、263 でコンパイルエラーが出た場合は修正が発生する。

| ファイル | 変更内容 |
| --- | --- |
| `gradle.properties` | 4 値のバージョン bump |
| `CHANGELOG.md` | `## [Unreleased]` にエントリ追加 |
| `conductor/tech-stack.md` | サポートバージョン表記の更新 |
| `src/**` | 263 で API 破壊があった場合のみ修正 |

具体的な値の変更内容は次の通り。

| プロパティ | 変更前 | 変更後 |
| --- | --- | --- |
| `pluginVersion` | `0.6.10` | `0.6.11` |
| `pluginSinceBuild` | `223` | 据え置き |
| `pluginUntilBuild` | `262.*` | `263.*` |
| `verifierVersionSince` | `2022.3.3` | 据え置き |
| `verifierVersionUntil` | `262.6653.22` | `263.5701.42` |
| `platformType` | `IU` | 据え置き |
| `platformVersion` | `262.6653.22` | `263.5701.42` |

`pluginSinceBuild=223` を据え置く根拠は、issue #148「fix: Expand supported IntelliJ versions」が未解決のままだからである。ビルド番号レンジの下限を上げれば、旧バージョン利用者がプラグインを更新できなくなる。本 Issue のスコープには含めない。

## Risks

| リスク | 影響 | 対策 |
| --- | --- | --- |
| EAP installer が Gradle から解決できない | Phase 0 で判明 | `useInstaller = false` による multi-OS archive 解決へのフォールバックを Phase 0 で試行する |
| 263 で API が破壊されている | コンパイルエラー | 本トラック内で修正する（決定 3） |
| EAP はビルド間で API が動く | 公開後に動作しなくなる | 安定版公開時の再検証を follow-up Issue 化する |
| `untilBuild=263.*` で 2022.3 側が壊れる | 回帰 | `verifierVersionSince=2022.3.3` による Plugin Verifier の下限検証を維持する |
