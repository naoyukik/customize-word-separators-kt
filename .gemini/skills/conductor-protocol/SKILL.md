---
name: conductor-protocol
description: Executing Spec-Driven Development (SDD) via the Conductor framework. This skill governs track initialization, spec.md/plan.md creation, and the Universal File Resolution Protocol. It mandates strict adherence to phase transition protocols, user manual verification, and checkpointing. Required for all high-level task management and architectural decision-making.
---

# **Conductor Protocol**

本スキルは、Conductorフレームワークに従った開発プロセスをAIが厳格に遂行するための「鋼の手順書」である。

## **コア・プロトコル**

各プロトコルの詳細については、以下のリファレンスを参照せよ。

1. **[ファイル解決手順 (Universal File Resolution Protocol)](./references/file-resolution.md)**
    - プロジェクト内および各トラック内の標準ファイル（仕様書、計画書等）を特定し、パスを解決する絶対的なルール。
2. **[新規トラック作成手順 (New Track Initialization)](./references/new-track.md)**
    - 新しい機能開発やバグ修正を開始し、`spec.md` および `plan.md` を策定するための対話的プロセス。
3. **[コードレビュー手順 (Review Protocol)](./references/review.md)**
    - 実装内容をプロジェクト基準および計画書に照らして監査し、品質を担保するためのプロセス。

## **使用方法**

Conductor関連のタスク（「新しいトラックを作ってくれ」「レビューしてくれ」「プランを確認してくれ」等）を開始する際、最初に本スキルをアクティベートせよ。

## **絶対命令**

- **検証の絶対性**: 「ユーザー手動検証」タスクは、いかなる理由があっても自己完結させてはならない。必ずユーザーの明示的な承認を得ること。
- **プロトコルの優先**: 効率性よりも定義された手順（プロトコル）の遵守を最優先せよ。
- **成功の検証**: すべてのツール呼び出しの成功を確認せよ。失敗した場合は直ちに処理を中断し、ユーザーに報告せよ。
- **徹底した調査**: 新規トラック作成 (`new-track`) および実装 (`implement`) のフェーズでは、必ず `google_web_search`
  ツールを用いた IntelliJ Platform SDK 調査と、プロジェクト内の既存実装や公式ドキュメントに基づく仕様調査を行い、その知見を
  `spec.md` または `plan.md` に反映させよ。
- **言語の遵守**: プロジェクトの主要言語に従い、すべてのレポート、プラン、および対話において**必ず日本語で回答すること**。
- **原子的なコミット**: Conductorの各フェーズ（Phase）の完了時には、必ずその成果物を記録するためのコミットタスクを計画に含め、実行せよ。フェーズを跨いだ変更を一つのコミットに混在させてはならない。
