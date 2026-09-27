# Implementation Plan: IntelliJ 2026.3 対応

**Track**: issue_210_20260926
**Type**: feature
**Spec**: [spec.md](./spec.md)

## 前提条件

- 作業ブランチは `main` から切り出す。命名規則は `<issue-number>-<slug>` とし、210 の場合は `210-support-intellij-20263` を使う。
- フェーズを跨いだ変更を一つのコミットに混在させない。
- 各フェーズは完了時にユーザー承認を得てから次へ進む。

## テスト方針

本 Issue はビルド設定の bump が中心であり、新規のロジックは追加しない。

`src/test` には現在テストが存在せず、Unit Test の追加は未解決の Issue #188 の管轄である。既存テストの green 維持のみを本トラックの検証条件とする。ただし Phase 2 で 263 起因のソース修正が必要になった場合は、その修正に対してのみ TDD（red → green → refactor）を適用する。

## Phase 0: 調査と解決可否の検証

- [ ] Task: 2026.3 のビルド番号と配布状況を一次情報で調査する
  - Subtask: `autonomous-researcher` スキルに従い、`data.services.jetbrains.com` と `plugins.jetbrains.com/docs/intellij/build-number-ranges.html` を再確認する。
  - Subtask: 2026.3 の安定版が EAP 段階にあることを前提としたまま、263 ブランチの最新ビルド番号を特定する。
  - Subtask: 調査結果を `assets/evidence_report_template.md` の形式で Evidence Report として本 Phase の記録に残す。
- [ ] Task: `platformVersion=263.5701.42` の解決可否を実地検証する
  - Subtask: `gradle.properties` の一時的な書き換えにより Gradle が 263 を解決できるかを `./gradlew dependencies --configuration intellijPlatformDependency` 等の解決のみを行うタスクで確認する。
  - Subtask: 解決できない場合、`useInstaller = false` による multi-OS archive 解決と `jetbrainsRuntime()` の明示を追加する fallback を試す。
  - Subtask: snapshots リポジトリに存在する `263.5701.42-EAP-SNAPSHOT` / `263.5701.42-EAP` をそのまま `platformVersion` に書く案も比較検討する。
  - Subtask: 検証結果に合わせて `build.gradle.kts` の修正要否を確定する。
- [ ] Task: 2026.3 固有の API 破壊の有無を調査する
  - Subtask: IntelliJ Platform SDK の Incompatible API Changes（2026.3 節）を調査する。
  - Subtask: 本プラグインが使用する `AnAction`、`AnActionEvent`、`Editor`、`TextField`、`TextArea`、`projectConfigurable` に破壊がないか照合する。
- [ ] Task: 2026.3 安定版公開時の follow-up 方針を確定する
  - Subtask: 公開後の再検証として必要なタスクを整理し、Phase 3 で起票する Issue の内容を作成する。
- [ ] Task: Conductor - Static Analysis (Detekt) & Format Check。&&は使えないので個別に実行すること。
- [ ] Task: Conductor - `gradle check` を実行して品質を検証
- [ ] Task: Conductor - User Manual Verification 'Phase 0: 調査と解決可否の検証' (Protocol in workflow.md)
- [ ] Task: Conductor - 'Phase 0: 調査と解決可否の検証' の成果をコミット

## Phase 1: ビルド設定とドキュメントの bump

- [ ] Task: `gradle.properties` のバージョンを更新する
  - Subtask: `pluginVersion` を `0.6.11` にする。
  - Subtask: `pluginUntilBuild` を `263.*` にする。
  - Subtask: `verifierVersionUntil` を Phase 0 で確定した 2026.3 のビルド番号にする。
  - Subtask: `platformVersion` を Phase 0 で確定した 2026.3 のビルド番号にする。
  - Subtask: `pluginSinceBuild`、`verifierVersionSince`、`platformType` は変更しないことを `git diff` で確認する。
- [ ] Task: `CHANGELOG.md` にエントリを追加する
  - Subtask: `## [Unreleased]` 配下に `### Changed` セクションを追加する。
  - Subtask: 内容は「Support for IntelliJ versions 2026.3」となり、過去 4 回のエントリ表現に揃える。
  - Subtask: ファイル末尾の比較リンク定義に `[Unreleased]` への参照が既存のまま有効であることを確認する。
- [ ] Task: `conductor/tech-stack.md` のサポート表記を更新する
  - Subtask: `IntelliJ Platform SDK` 行の「IntelliJ IDEA 2022.3 - 2025.2 Support」を「2022.3 - 2026.3 Support」に修正する。
- [ ] Task: Conductor - Static Analysis (Detekt) & Format Check。&&は使えないので個別に実行すること。
- [ ] Task: Conductor - `gradle check` を実行して品質を検証
- [ ] Task: Conductor - User Manual Verification 'Phase 1: ビルド設定とドキュメントの bump' (Protocol in workflow.md)
- [ ] Task: Conductor - 'Phase 1: ビルド設定とドキュメントの bump' の成果をコミット

## Phase 2: ビルドと Plugin Verifier 検証

- [ ] Task: プラグインのビルドが成功することを確認する
  - Subtask: `./gradlew buildPlugin` を実行する。
  - Subtask: 263 でコンパイルエラーが出た場合、TDD サイクル（red → green → refactor）を適用して修正する。
  - Subtask: 生成された ZIP の `META-INF/plugin.xml` に `until-build="263.*"` が反映されていることを確認する。
- [ ] Task: Plugin Verifier の結果を検証する
  - Subtask: `./gradlew verifyPlugin` を実行する。
  - Subtask: 検証対象が `2022.3.3` と `263.5701.42` の両建てになっていることを確認する。
  - Subtask: 報告された `compatibility problem` を全て解消する（決定 3）。
  - Subtask: 解消不能な報告が出た場合は、影響範囲と回避策を本 Phase の記録に明記してユーザーに相談する。
- [ ] Task: 全体品質を検証する
  - Subtask: `./gradlew check` を実行する。
  - Subtask: Kover のカバレッジレポートが生成されることを確認する。しきい値は設定されていないため 0% でも失敗しない。
- [ ] Task: Conductor - Static Analysis (Detekt) & Format Check。&&は使えないので個別に実行すること。
- [ ] Task: Conductor - `gradle check` を実行して品質を検証
- [ ] Task: Conductor - User Manual Verification 'Phase 2: ビルドと Plugin Verifier 検証' (Protocol in workflow.md)
- [ ] Task: Conductor - 'Phase 2: ビルドと Plugin Verifier 検証' の成果をコミット

## Phase 3: 実機での手動検証とリリース準備

- [ ] Task: `runIde` で 2026.3 IDE を起動して動作を確認する
  - Subtask: `./gradlew runIde` を実行する（決定 4）。
  - Subtask: 起動した IDE のバージョンが 2026.3 であることを確認する。
  - Subtask: 4 アクションそれぞれについて、漢字・ひらがな・カタカナ・英数字の区切りでカーソルが移動することを IDE 上で確認する。
  - Subtask: 選択付きアクション（Next with Selection / Prev with Selection）の選択範囲が正しいことを確認する。
  - Subtask: TextField と TextArea 上でも移動が機能することを確認する。
  - Subtask: 設定画面（Preferences | Settings | Customize Word Separators）が正常に開くことを確認する。
- [ ] Task: follow-up Issue を起票する
  - Subtask: 2026.3 安定版公開後に EAP ビルド番号を正式版へ差し替える Issue を作成する。
  - Subtask: Issue には現在の EAP ビルド番号と、使用した公開手順（marketplace / ./gradlew publishPlugin）を記載する。
- [ ] Task: 最終差分の監査を実施する
  - Subtask: `git diff main...HEAD` で意図しない変更が含まれていないことを確認する。
  - Subtask: spec.md の Requirements 項目が全て充足されているか 1 項目ずつ照合する。
- [ ] Task: Conductor - Static Analysis (Detekt) & Format Check。&&は使えないので個別に実行すること。
- [ ] Task: Conductor - `gradle check` を実行して品質を検証
- [ ] Task: Conductor - User Manual Verification 'Phase 3: 実機での手動検証とリリース準備' (Protocol in workflow.md)
- [ ] Task: Conductor - 'Phase 3: 実機での手動検証とリリース準備' の成果をコミット

## 完了条件

- `gradle.properties` の 4 値が目標値に到達している。
- `CHANGELOG.md` に 2026.3 対応のエントリがある。
- `./gradlew buildPlugin` と `./gradlew verifyPlugin` が成功している。
- 2026.3 IDE での手動検証にユーザーが承認している。
- follow-up Issue が起票されている。
