# Evidence Report: Phase 0 調査と解決可否の検証

- **Track**: issue_210_20260926
- **調査日**: 2026-09-27 〜 2026-09-28
- **対象 Issue**: [#210 feat: Add support for IntelliJ 2026.3](https://github.com/naoyukik/customize-word-separators-kt/issues/210)

## 1. Discovery Summary (Step 1)

- **[Problem Statement]**: `pluginUntilBuild=262.*` のため IntelliJ 2026.3 でプラグインがインストールできない。2026.3 announced に対してビルド設定を bump し、0.6.11 としてリリース可能な状態へ到達する。
- **[Scope]**: `gradle.properties` のバージョン 4 値の更新、`CHANGELOG.md` へのエントリ追加、`conductor/tech-stack.md` のサポート表記更新、Plugin Verifier による互換性確認、`runIde` での手動検証。
- **[Non-Goals]**: 単語区切り判定ロジックの変更、依存ライブラリの更新、2026.3 安定版公開後のビルド番号丸め、`pluginSinceBuild` の見直し（Issue #148 の管轄）。
- **[Constraints]**:
  - `pluginSinceBuild=223`（2022.3 = Java 17 ランタイム）を据え置く（spec.md Decisions / リスク表）。
  - 機能コード（`src/`）の無変更を優先する。
- **[Success Criteria]**: `./gradlew buildPlugin` と `./gradlew verifyPlugin` が成功し、2026.3 IDE での手動検証にユーザーが承認する。

## 2. Codebase Findings (Step 2)

- **[Similar Implementations]**:
  - `build.gradle.kts:40` — `create(platformType, platformVersion)`。`useInstaller` を明示しないため IPGP 2.x の既定（installer 解決）が適用される。
  - `build.gradle.kts:20-22` — `kotlin { jvmToolchain(21) }`。
  - `build.gradle.kts:110-122` — `pluginVerification.ides` に `verifierVersionSince` と `verifierVersionUntil` の 2 IDE を登録。
  - コミット `b8eac78`（2026-05-30, 0.6.10）— 2026.2 対応の先行例。`262.6653.22`（EAP ビルド）を `platformVersion` に設定している。
- **[Architecture and Dependency Notes]**:
  - `src/` は `domain`（Pure Kotlin）/ `application` / `presentation` / `settings` の 4 層。IDE 固有 API は `presentation` と `settings` のみに閉じており、`domain` は IntelliJ 依存を持たない。今回のバージョン bump で層構造に影響しない。
  - アドオン依存は `<depends>com.intellij.modules.platform</depends>` のみ（`src/main/resources/META-INF/plugin.xml:7`）。`platformBundledPlugins` / `platformPlugins` は空。
- **[Reusable Components]**: なし（設定値の変更のみ）。
- **[Estimated Impact Area]**:
  - 2026.3 で使用中の API すべてを列挙した結果、削除リストに該当する API は **0 件**。

  | 使用 API | 参照箇所 |
  | --- | --- |
  | `com.intellij.openapi.actionSystem.AnAction` / `AnActionEvent` / `DataContext` | `presentation/*.kt`, `NextPrevWordEditorActionHandler.kt:3-4` |
  | `com.intellij.openapi.editor.Caret` / `Editor` | `NextPrevWordEditorActionHandler.kt:5-6` |
  | `com.intellij.openapi.editor.actionSystem.EditorActionHandler` | `NextPrevWordEditorActionHandler.kt:7` |
  | `com.intellij.openapi.options.Configurable` | `settings/AppSettingsConfigurable.kt:3` |
  | `com.intellij.openapi.ui.DialogPanel` | `settings/AppSettingsConfigurable.kt:5` |
  | `com.intellij.ui.dsl.builder.*`（**Kotlin UI DSL 2**） | `settings/AppSettingsConfigurable.kt:6-9` |
  | `com.intellij.openapi.projectRoots.Sdk` | 未使用 |

## 3. Clarifying Questions (Step 3)

- **[Open Questions]**:
  1. `platformVersion=263.5701.42` は Gradle で解決できるか（installer 経路）。
  2. 2026.3 でコンパイルエラーが発生するか。
  3. 2026.3 の Java 25 要件が `jvmToolchain(21)` と `pluginSinceBuild=223` を破綻させないか。
- **[User Answers / Delegations]**:
  1. spec.md の Decisions で 4 問すべて解消済み。
  2. 2026-09-28: バイトコード・レベルの不整合の扱いについてユーザーに確認した結果、**`pluginSinceBuild` を 242 へ上げて宣言と実態を揃える**方針を採用（spec.md Decisions 決定 5）。
- **[Unresolved Items]**:
  1. なし。`verifyPlugin` の実測は Phase 2 で実施する。

## 4. 将来の修正で期待される挙動 (Expected Behavior)

- 2026.3（263.5701.42）を SDK として、指定の `jvmToolchain` でコンパイルできる。
- 生成物の `META-INF/plugin.xml` に `since-build="242" until-build="263.*"` が反映される。
- 生成物のバイトコード（major 65 / Java 21）は `pluginSinceBuild=242` の対象環境（2024.2 以降）でロード可能である。
- 4 アクション（Next / Prev / Next with Selection / Prev with Selection）が 2026.3 IDE 上で文字種ごとにカーソルを移動する。設定画面が正常に開く。

## 5. Architecture Options (Step 4)

### Option A: Minimal Changes（spec.md の当初方針）
- **[Change Targets]**: `gradle.properties` の 4 値のみ。`build.gradle.kts` と `src/` は無変更。
- **[Pros]**: 差分最小。過去 4 回（0.6.6〜0.6.10）と同じ進め方。前リリース 0.6.10 も EAP ビルドを `platformVersion` にしていた前例あり。
- **[Cons/Risks]**: 「2026.2 以降は source/target 25 が必要」という公式アナウンスがあり、`jvmToolchain(21)` のまま通ると予想した（→ 実測で否定済み）。
- **[Validation Plan]**: `buildPlugin` / `verifyPlugin` / `runIde`。

### Option B: Clean Architecture
- **[Change Targets]**: `pluginSinceBuild` を 242（2024.2 = Java 21）へ引き上げ、`jvmToolchain` を明示的に 21 で固定、`platformVersion` はサポート下限（2022.3.3）に固定。
- **[Pros]**: 公式推奨（最も古い SDK でビルド）に合致。バイトコード・レベルと `sinceBuild` の整合が恒久的に取れ、保守コストが最小。
- **[Cons/Risks]**: Issue #148「fix: Expand supported IntelliJ versions」の意図（223 への対応を拡張する）に反する。2022.3〜2024.1 ユーザーがプラグインを更新できなくなる。**Issue #148 の管轄を超える変更となる。**
- **[Validation Plan]**: `verifyPlugin` による下限・上限の両建て検証。

### Option C: Pragmatic Balance
- **[Change Targets]**: `platformVersion` は据え置き（Java 21 でビルド可能な版）、`pluginUntilBuild` のみ `263.*` へ。
- **[Pros]**: ビルド環境の変更が不要。
- **[Cons/Risks]**: 263 の API 破壊をコンパイル時に検出できない。`buildSearchableOptions` や `runIde` によるフォールバック検証のみ。万一 263 で API が消えていた場合にリリース波及する。
- **[Validation Plan]**: `verifyPlugin`（263 に対する検証のみ実効）。

- **[Recommended Option]**: **Option A**
- **[Reason]**: 実測により 263.5701.42 の解決・コンパイル・IDE 上でのプラグインロードがすべて成立し、生成物のバイトコードは Java 21（major 65）のまま維持される。`pluginSinceBuild=223` を据え置けるため Option B の副作用（223〜241 のサポート断）が回避できる。Option C は 263 で発生しうる API 破壊をコンパイル時に検証できないため、万一のリリース波及リスクが残る。

## 6. 推奨される実装方針 (Implementation Strategy)

- **[Architecture Alignment]**: `src/` の層構造（domain / application / presentation / settings）に変更なし。設定ファイルのみの変更で完結する。
- **[Logic Changes]**: なし。`gradle.properties` の 4 値、`CHANGELOG.md`、`conductor/tech-stack.md` のみ。
- **[Validation Plan]**: `./gradlew buildPlugin` → ZIP 内の `plugin.xml` 確認 → `./gradlew verifyPlugin`（2022.3.3 と 263.5701.42）→ `./gradlew check` → `./gradlew runIde` での手動検証。

## 7. Evidence and Alignment

- **[Source URLs]**:
  - `https://data.services.jetbrains.com/products/releases?code=IU&type=eap`
  - `https://data.services.jetbrains.com/products/releases?code=IU&type=release`
  - `https://plugins.jetbrains.com/docs/intellij/build-number-ranges.html`
  - `https://plugins.jetbrains.com/docs/intellij/api-changes-list-2026.html`
  - `https://download.jetbrains.com/idea/idea-263.5701.42.exe`（HEAD → 200）
  - `https://www.jetbrains.com/intellij-repository/releases/com/jetbrains/intellij/idea/ideaIU/263.5701.42/ideaIU-263.5701.42.pom`（HEAD → 404）
  - `https://www.jetbrains.com/intellij-repository/snapshots/com/jetbrains/intellij/idea/ideaIU/263.5701.42-EAP-SNAPSHOT/maven-metadata.xml`（HEAD → 404）
- **[Research Date]**: 2026-09-27 / 2026-09-28
- **[Key Findings]**:

  **発見 1: 2026.3 は EAP 段階。最新 EAP は 263.5701.42（2026-09-25）、安定版は存在しない。**
  最新安定版は 2026.2.3 / `262.10968.63`（2026-09-16）。`plugins.jetbrains.com` のブランチ表も 2026.2 が最終行で 263 の記載はない。

  **発見 2: spec.md に事実誤認が 2 点ある。**
  - spec.md は「最新安定版 2026.2.3（ビルド `262.6653.22`）」と記載しているが、`262.6653.22` は **2026-05-28 付の EAP** であり安定版ではない。安定版は `262.10968.63`。
  - spec.md は「`intellij-repository/snapshots` に `263.5701.42-EAP-SNAPSHOT` 等が存在する」と記載しているが、**2026.3 については 404 で存在しない**。Maven リポジトリ経由の 263 解決は成立しない。

  **発見 3: installer 経由の 263 解決は成立する（実地検証済み）。**
  - `HEAD idea-263.5701.42.exe` → **200**。`.tar.gz` / `-aarch64.tar.gz` も 200。
  - `gradle.properties` を `platformVersion=263.5701.42` に書き換えた状態で `./gradlew buildPlugin` が **BUILD SUCCESSFUL（3m38s）**。
  - 解決結果の座標は `idea:idea:263.5701.42`。`compileClasspath` の全 JAR が `idea-263.5701.42-win/lib/` 配下であることを確認した。
  - `./gradlew dependencies --configuration intellijPlatformDependency` も `idea:idea:263.5701.42` を返す。
  - 追加の Gradle 設定変更（`useInstaller = false`、`jetbrainsRuntime()` の明示）は **不要** と確定。

  **発見 4: Java 25 要件は実測で否定された。ただし別の潜在リスクが見つかった。**
  - 2026.3 の JBR は `JAVA_VERSION="25.0.4.1"` / `JBR-25.0.4.1+1-610.67-nomod`。
  - プラットフォームのクラスファイルは **major 69（Java 25）**（`intellij.platform.ide.impl.jar` の 1832 クラスが該当）。
  - にもかかわらず `jvmToolchain(21)` のまま `compileKotlin` は成功し、生成物のバイトコードは **major 65（Java 21）**。Kotlin コンパイラは JDK の `ClassReader` ではなく独自のクラスファイル reader を使うため、class version 69 の入力が拒否されなかった。
  - `buildSearchableOptions` が 2026.3 IDE を実際に起動して 369 configurables を列挙した = プラグインが 2026.3 上でロードされる実証。
  - **ただし**: major 65（Java 21）のバイトコードは **Java 17 ランタイム（2022.3〜2024.1）では `UnsupportedClassVersionError` でロードできない**。履歴を調べた結果、`jvmToolchain(21)` は 2025-01-18（`b13e0e6`、IPGP v2 化、当時は `pluginSinceBuild=243`）で導入され、`pluginSinceBuild=223` への引き下げは 2025-10-09（`4e4c884`、0.6.8）で起きている。**0.6.8 / 0.6.9 / 0.6.10 はこの不整合を既に含む**。本 Issue が原因ではない既存欠陥であり、`verifyPlugin` の `verifierVersionSince=2022.3.3` で顕在化する可能性がある。

  **発見 5: 2026.3 の破壊的変更は本プラグインに影響しない。**
  `api-changes-list-2026.html` の 2026.3 節に記載された 4 項目のうち、本プラグインとの関連は以下。
  - モジュール分割（`intellij.platform.debugger` / `externalSystem` / `remoteServers`）: 本プラグインは未使用。
  - `Sdk` が `UserDataHolderEx` を継承: 本プラグインは `Sdk` を実装しない。
  - OkHttp の unbundling: 本プラグインは okhttp3 を使用しない。
  - **Kotlin UI DSL 1.0 完全削除**（`com.intellij.ui.layout.*` の 11 クラス）: 本プラグインは `settings/AppSettingsConfigurable.kt:6-9` で **Kotlin UI DSL 2**（`com.intellij.ui.dsl.builder.*`）を使用しており、削除対象のパッケージに触れていない。
- **[Local Constraint Alignment]**:
  - `conductor/code_styleguides/general.md`: 「Surgical Changes」に従い、`src/` には手を触れない。
  - `conductor/product-guidelines.md`「Modular & Maintainable」: 「将来の IDE バージョンアップに伴うメンテナンスコストを最小限に抑える」に従い、本 Issue では `useInstaller` 等のビルド設定の追加変更を行わず、JetBrains の既定の解決経路を維持する。
- **[Potential Regressions]**:
  - 発見 4 のバイトコード・レベル不整合は `pluginSinceBuild=242` への変更で解消する。ただし 2022.3〜2024.1 のユーザーには 0.6.11 が配信されなくなる。この範囲は 0.6.8〜0.6.10 でも起動できなかったため、実質的な機能損失はない。
  - EAP はビルド間で API が動く。2026.3 安定版公開時に再検証が必要。
- **[Residual Risks / Unknowns]**:
  - `./gradlew verifyPlugin` を未実行。2024.2.6 と 263.5701.42 に対する検証結果は Phase 2 で確定する。
  - `runIde` による 4 アクションの手動検証は Phase 3 の user's manual verification 待ち。
  - 一時ファイル（`temporary.local/`）は `.gitignore` 除外対象。削除してよい。
