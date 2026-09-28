# Track Issue #210: IntelliJ 2026.3 への対応

## Background

- 現状 `pluginUntilBuild=262.*` までしか宣言していないため、2026.3 系ではプラグインがインストールできない。
- Issue #210 は「IntelliJ 2026.3 がリリースされた」前提で書かれているが、2026-09-26 時点で 2026.3 の安定版は存在しない。
- 一次情報（`data.services.jetbrains.com`）で確認した事実関係は次の通り。

| 項目 | 値 | 確認元 |
| --- | --- | --- |
| 最新安定版 | 2026.2.3（ビルド 262.10968.63、2026-09-16 公開） | `data.services.jetbrains.com/products/releases?code=IU&type=release` |
| 最新 2026.3 EAP | 263.5701.42（2026-09-25 公開） | `data.services.jetbrains.com/products/releases?code=IU&type=eap` |
| ブランチ一覧 | 2026.2 が最終行。2026.3 の行は未掲載 | `plugins.jetbrains.com/docs/intellij/build-number-ranges.html` |

- IntelliJ Platform Gradle Plugin 2.x の 既定は installer 解決（`useInstaller = true`）で、installer は公開リリースのみを配信する。
- 一方で EAP チャンネルの installer は `https://download.jetbrains.com/idea/idea-263.5701.42.exe` に存在する（HTTP 200 を確認済み）。
- Maven リポジトリ側では bare な `263.5701.42` は存在せず、`263.5701.42-EAP-SNAPSHOT` のようなサフィックス付きの座標も `intellij-repository/snapshots` に存在しない（HTTP 404 を確認済み）。
- 参考として、現在の `262.6653.22` も `intellij-repository/releases` には存在せず（404）、installer 経由でのみ取得可能である。
- 以上から `platformVersion=263.5701.42` の installer 解決は成立すると考えられる。Phase 0 の実地検証（後述「Phase 0 検証結果」）で成立を確定した。

## Phase 0 検証結果

2026-09-27 〜 2026-09-28 に実地検証を行った。詳細な証拠は [assets/evidence_report.md](./assets/evidence_report.md) を参照。

### 事実誤認の訂正

本 spec の初期記述には次の誤認があった。いずれも一次情報で訂正済み。

- 「最新安定版 2026.2.3（ビルド 262.6653.22）」は誤り。`262.6653.22` は **2026-05-28 付の EAP** であり、安定版ではない。安定版は `262.10968.63`。
- 「Maven `intellij-repository/snapshots` に `263.5701.42-EAP-SNAPSHOT` 等が存在する」は誤り。2026.3 については 404 で存在しない。

### 解決可否の実地検証

| 検証 | 結果 |
| --- | --- |
| `platformVersion=263.5701.42` への切替後 `./gradlew buildPlugin` | 成功。解決座標は `idea:idea:263.5701.42` |
| `compileClasspath` の実体 | 全 JAR が `idea-263.5701.42-win/lib/` 配下 |
| 2026.3 でのコンパイル | エラーなし |
| 2026.3 でのプラグインロード | `buildSearchableOptions` が 2026.3 IDE を起動して 369 configurables を列挙 |
| `useInstaller = false` / `jetbrainsRuntime()` の追加 | **不要** と確定 |

### Java 25 要件の実測

公式アナウンスは「2026.2 以降は source/target 25 が必要」と述べているが、実測では成立しなかった。

- 2026.3 の JBR は `25.0.4.1`、プラットフォームのクラスは major 69（Java 25）。
- `jvmToolchain(21)` のまま `compileKotlin` は成功し、生成物は major 65（Java 21）のまま。Kotlin コンパイラは JDK の `ClassReader` ではなく独自のクラスファイル reader を使うため、class version 69 の入力を拒否しない。

このため `jvmToolchain` を 25 へ上げる必要はなく、Java 21 バイトコードを維持できる。

### バイトコード・レベルと `pluginSinceBuild` の不整合（既存欠陥）

実測により、**0.6.8 / 0.6.9 / 0.6.10 は `since-build="223"` を宣言しながら 2022.3〜2024.1 では動作しない**ことが判明した。

| 事実 | 確認方法 |
| --- | --- |
| 公開済み 0.6.10 の全 24 クラスが major 65（Java 21） | Marketplace から取得した ZIP を展開して class file version を実測 |
| `since-build="223"` を宣言 | 同 ZIP 内の `META-INF/plugin.xml` を実測 |
| 2022.3〜2024.1 は Java 17 ランタイム | build-number-ranges.html の Platform Versions 表（`2022.3 / 223 / 17`、`2024.1 / 241 / 17`） |
| 旧 JVM は major 65 をロードできない | JDK 8 で `Class.forName` を実行し `UnsupportedClassVersionError: class file version 65.0, ... only recognizes class file versions up to 52.0` を実証 |
| Plugin Verifier は検証を通す | Verifier は API 解決可否のみを検査し class file version は検査しない |
| Marketplace の「Compatible with 2022.3 — 2026.2.3」は宣言値 | `api/plugins/13613/updates` の `compatibleVersions` は `since`/`until` から算出される |

原因履歴は次の通り。

- `jvmToolchain(21)` は 2025-01-18（`b13e0e6`、IPGP v2 化）で導入。当時の `pluginSinceBuild` は 243。
- `pluginSinceBuild` を 223 へ引き下げたのは 2025-10-09（`4e4c884`、0.6.8）。この時点で Java 21 バイトコードとの不整合が発生した。
- 0.6.5〜0.6.7 は `pluginSinceBuild=243/244` で整合していた。

`jvmToolchain` を 17 へ下げる方法は、263 のクラス（major 69）を Java 17 でコンパイルできないため採用できない。したがって `pluginSinceBuild` を 242（2024.2 = Java 21 ランタイム）で揃えることを決定した。

## Goals

- 2026.3 ブランチでプラグインが動作し、JetBrains Marketplace でインストール可能になる。
- 0.6.11 としてリリース可能な状態まで到達する。

## Decisions

対話フェーズ（4問）で確定した方針を以下に記録する。Phase 0 の実地検証で追加された決定を下表に追加した。

| # | 論点 | 決定 |
| --- | --- | --- |
| 1 | 2026.3 のビルド指定 | EAP 263.5701.42 を採用する |
| 2 | pluginVersion | 0.6.11（過去 4 回と同じパッチリリース方針） |
| 3 | 検証で問題が出た場合 | 本トラック内で修正まで完了させる |
| 4 | 手動検証の方法 | `runIde` で起動した 2026.3 IDE で検証する |
| 5 | バイトコード・レベルと `pluginSinceBuild` の不整合 | `pluginSinceBuild` を 242 へ上げて宣言と実態を揃える（2026-09-28 にユーザーが決定） |

決定 5 の根拠:

- 0.6.8 以降、`since-build="223"` を宣言しながら Java 21 バイトコードを出力しており、2022.3〜2024.1（Java 17 ランタイム）ではロードできない。
- `jvmToolchain` を 17 へ下げる方法は 263（class file version 69）を Java 17 でコンパイルできないため採用できない。
- 宣言と実態を揃えることで、2022.3〜2024.1 のユーザーが更新後にプラグインを起動できなくなる事故を防ぐ。
- Issue #148「fix: Expand supported IntelliJ versions」との方向は矛盾しない。同 Issue の目的は対応下限の拡張であり、動作しない 223 を宣言し続けることはその目的に反する。

## Requirements

- `gradle.properties` の6値、`pluginVersion`・`pluginSinceBuild`・`pluginUntilBuild`・`verifierVersionSince`・`verifierVersionUntil`・`platformVersion` を目標値へ更新する。`platformType` は据え置きとする。
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
| `gradle.properties` | 6 値のバージョン bump（`platformType` は据え置き） |
| `CHANGELOG.md` | `## [Unreleased]` にエントリ追加 |
| `conductor/tech-stack.md` | サポートバージョン表記の更新 |
| `conductor/tracks/issue_210_20260926/assets/evidence_report.md` | Phase 0 の調査報告（新規追加） |
| `src/**` | 変更なし。Phase 0 の実地検証で 263 に API 破壊が無いことを確認済み |

具体的な値の変更内容は次の通り。

| プロパティ | 変更前 | 変更後 |
| --- | --- | --- |
| `pluginVersion` | `0.6.10` | `0.6.11` |
| `pluginSinceBuild` | `223` | `242` |
| `pluginUntilBuild` | `262.*` | `263.*` |
| `verifierVersionSince` | `2022.3.3` | `2024.2.6` |
| `verifierVersionUntil` | `262.6653.22` | `263.5701.42` |
| `platformType` | `IU` | 据え置き |
| `platformVersion` | `262.6653.22` | `263.5701.42` |

`verifierVersionSince` の変更理由:

- 本プロジェクトの規約は「宣言下限の最新パッチを検証する」ことである。現状の `2022.3.3` は 223 系の最新リリース（`223.8836.41` / 2023-03-08）である。
- `pluginSinceBuild` を 242 へ上げたため、対応する 242 系の最新リリースである `2024.2.6`（`242.26775.15` / 2025-04-23）へ変更する。
- 下限の検証対象を 2022.3.3 のまま残すと、宣言範囲外の IDE を検証することになり意味がない。

`pluginSinceBuild=223` を据え置かない根拠（決定 5）:

- 0.6.8 以降、223 を宣言しながら Java 21 バイトコードを出力しており、2022.3〜2024.1（Java 17 ランタイム）では `UnsupportedClassVersionError` でロードできない。宣言と実態が不一致の状態である。
- 224〜241 の世代（2023.1〜2024.1）も Java 17 ランタイムである。242（2024.2）が Java 21 ランタイムの最初のプラットフォームとなる。
- 対応下限の拡張は Issue #148 の管轄であり、本 Issue では実行可能な最小の下限 242 を採用する。

## Risks

| リスク | 影響 | 対策 |
| --- | --- | --- |
| EAP installer が Gradle から解決できない | ビルド不能 | **解消済み**。Phase 0 で installer 経由の解決が成立することを実地検証した。追加の Gradle 設定変更は不要 |
| 263 で API が破壊されている | コンパイルエラー | **解消済み**。Phase 0 で 263 に対するコンパイルとプラグインロードが成功することを確認した。使用 API に削除対象は含まれない |
| `untilBuild=263.*` で下限側が壊れる | 回帰 | `verifierVersionSince=2024.2.6` による Plugin Verifier の下限検証を維持する。242（Java 21）〜263（Java 25）の両方で Java 21 バイトコードがロード可能であることを確認済み |
| EAP はビルド間で API が動く | 公開後に動作しなくなる | 安定版公開時の再検証は次回リリース時に実施する。2026-09-28 のユーザー判断により Issue 化は保留し、必要になった時点で起票する |
