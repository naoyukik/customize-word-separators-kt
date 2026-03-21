# Track Issue #187: Gemini CLI Skill/Command Enhancement

## Background
- 現状、プロジェクトには Gemini CLI 用のスキルやコマンドが配置されているが、一部のフック設定に不備（タイポやファイルの欠落）があり、エラーが発生している。
- また、このプロジェクト固有のワークフローを強化するため、Gemini CLI のスキルやコマンドを整理・追加する必要がある。

## Goals
- **Fix Existing Hooks:** `.gemini/settings.json` と実際のファイル名の不整合（ハイフン vs アンダースコア）を修正する。
- **Add Project-Specific Skills:** プロジェクトのアーキテクチャ（レイヤードアーキテクチャ）や Kotlin コーディング規約を遵守させるためのスキルを追加。
- **Enhance Commands:** 開発効率を向上させるための Gemini CLI カスタムコマンドを追加。

## Requirements
- すべてのフックがエラーなく動作すること。
- スキルが正しくロードされ、開発中に適切なアドバイスが得られること。
- 追加されたコマンドが意図通りに動作すること。
