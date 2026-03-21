# **New Track Initialization Protocol**

新しい「トラック」（機能開発やバグ修正）を開始し、仕様と計画を策定するための厳格なシーケンスを規定する。

## **1. コンテキストのロードと確認**

1. **コア・コンテキストの検証**: `Universal File Resolution Protocol` を使用して、以下のファイルが存在することを確認せよ。
    - Product Definition
    - Tech Stack
    - Workflow
2. **失敗時の対応**: いずれかが欠けている場合、直ちに処理を中断し、「Conductor is not set up」と報告せよ。

## **2. トラック情報の取得**

1. **説明の取得**: GitHub Issue 等から情報を得るか、ユーザーに簡潔な説明を求めよ。
2. **タイプ推論**: 説明に基づき、タイプ（Feature, Bug, Chore, Refactor等）を自律的に判断せよ。ユーザーに分類させてはならない。

## **3. 対話的な仕様策定 (`spec.md`)**

1. **開始宣言**: 質問攻めにすることをユーザーに伝えよ。
2. **質問フェーズ**:
    - **逐次質問**: 質問は必ず一つずつ行い、ユーザーの回答を待て。
    - **オプション提示**: 可能な限り 2-3 個の選択肢 (A, B, C) を提示せよ。
    - **自由入力**: すべての質問の最後の選択肢は必ず「Type your own answer」とせよ。
    - **質問タイプ**:
        - **Additive**: 範囲や目標を広げる質問。「(Select all that apply)」を付与せよ。
        - **Exclusive Choice**: 根幹的な決定。単一回答を求めよ。
3. **ドラフト作成**: 十分な情報が集まったら `spec.md` を書き、承認を得よ。

## **4. 計画策定 (`plan.md`)**

1. **プラン生成**: `spec.md` と `workflow.md` の手法（例：TDD）に基づき、フェーズ・タスク・サブタスクの階層リストを作成せよ。
2. **Phase0として調査タスクを追加**: autonomous-researcherスキルに基づく調査タスクを追加せよ。
3. **完了メタタスクの注入**: 各フェーズの最後に必ず以下のタスクを挿入せよ。
    - [ ] Task: Conductor - Static Analysis (Detekt) & Format Check。&&は使えないので個別に実行すること。
    - [ ] Task: Conductor - `gradle check` を実行して品質を検証
    - [ ] Task: Conductor - User Manual Verification '<Phase Name>' (Protocol in workflow.md)`
    - [ ] Task: Conductor - '<Phase Name>' の成果をコミット`
4. **承認**: ユーザーの承認を得よ。

## **5. アーティファクトの生成とレジストリ更新**

1. **重複チェック**: 既存のトラック名との重複を確認せよ。
2. **ディレクトリ作成**: `conductor/tracks/<track_id>/` を作成せよ。
3. **ファイル書き出し**: `metadata.json`, `spec.md`, `plan.md`, `index.md` を書き出せ。
4. **レジストリ更新**: `conductor/tracks.md` の末尾に新しいトラックを追加せよ。
5. **コミット**: `chore(conductor): Add new track '<description>'` というメッセージでレジストリ変更をコミットせよ。
