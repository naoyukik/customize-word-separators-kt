---
name: autonomous-researcher
description: Enforce autonomous technical research and evidence-based implementation. Search for and obtain the latest documentation for IntelliJ Platform SDK, Kotlin, Gradle, and related technologies. Verify consistency with project conventions and use it when proposing implementations.
---

# Autonomous Researcher

## 概要

このスキルは、エージェントが「知識の欠如」や「古い情報」に基づいた誤った実装を行うのを防ぎ、常に最新の公式ドキュメント（1次ソース）に立脚した、根拠ある提案を行うためのワークフローを定義する。

## 強制ワークフロー (Evidence-Based Workflow)

いかなる実装や修正を提案する前にも、以下のステップを完了させなければならない。

### Step 1: 外部調査 (Search & Fetch)

- `google_web_search` を使用し、IntelliJ Platform Plugin SDKサイトで対象となるAPI、ライブラリ、またはプロトコルの最新仕様を調査する。
- 関連するURLを特定したら、`web_fetch` で内容を詳細に解析する。
- 詳細は `references/research_guidelines.md` を参照。

### Step 2: 内部規約との整合性検証 (Local Alignment**)**

- 取得した外部知識が、プロジェクト固有の規約（`conductor/code_styleguides/`）と矛盾しないか検証する。
- 例：IDE 固有の API 呼び出しは `Presentation` や `Settings` レイヤーに閉じ込めているか、コアロジックが Pure Kotlin
  に保たれているか。

### Step 3: 証拠レポートの作成 (Evidence Reporting)

- 実装提案を行う際、必ず冒頭に `Evidence Report` を記述する。
- レポートには、参照したURL、調査で見つかった重要な事実（制約や落とし穴）、およびプロジェクト規約との整合性を明記する。
- テンプレートとして `assets/evidence_report_template.md` を使用すること。

## ガイドライン

- **"I don't know" を恐れない**: 調査で確証が得られない場合、推測でコードを書くのではなく、不足している情報をユーザーに報告し、さらなる調査方針を相談する。
- **1次ソースを優先する**: ブログ記事や Stack Overflow よりも、公式ドキュメント（IntelliJ Platform Plugin SDK,
  RFC）を優先的に参照する。
- **Regressions への警戒**: ライブラリのバージョンアップによる破壊的変更や、OSの挙動変更がないか、GitHub Issue
  やリリースノートを検索する。

## リソース

- **`references/research_guidelines.md`**: 効果的な検索クエリとチェックリスト。
- **`assets/evidence_report_template.md`**: 実装提案に添付するレポートのテンプレート。
