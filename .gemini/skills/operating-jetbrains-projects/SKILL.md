---
name: operating-jetbrains-projects
description: Operates JetBrains IDEs (IntelliJ, etc.) via MCP. Supports project exploration, code reading, structural refactoring, and execution/verification using full IDE capabilities for efficient development.
---

# 鉄則（Absolute Principles）

1. **プロジェクトパスの明示**: すべての呼び出しで `projectPath` をフルパスで指定せよ。
2. **推測より確認**: ファイル構造やシンボル情報は必ず `list_directory_tree` や `get_symbol_info` で裏付けを取れ。
3. **精密な外科手術**: 編集は `replace_text_in_file` で最小限の差分のみを適用せよ。
4. **構造的整合性**: 名前変更には必ず `rename_refactoring` を用い、テキスト置換を避けよ。
5. **execute_terminal_command**: 基本的に使用しない。`run_sell_command` でどうしても実行できない場合のみ、ユーザーに確認を取ってから使用すること。

# ツール・カタログ（Toolbox Reference）

## 1. 探索とナビゲーション（Exploration & Navigation）

大規模プロジェクトの全容を迅速に把握するための手段です。

| ツール名                         | 用途                     | 備考                 |
|:-----------------------------|:-----------------------|:-------------------|
| `list_directory_tree`        | プロジェクト構造の俯瞰            | `maxDepth` を指定して使用 |
| `find_files_by_name_keyword` | ファイル名による特定             | インデックスによる高速検索      |
| `find_files_by_glob`         | パターン（例: `**/*.kt`）での検索 | 特定の拡張子やディレクトリの絞り込み |
| `search_in_files_by_text`    | 文字列による一括検索             | 定義や定数の使用箇所の特定      |
| `search_in_files_by_regex`   | 正規表現によるパターン抽出          | 複雑なコードパターンの特定      |

## 2. 読解と分析（Reading & Understanding）

コードの意味と依存関係を正確に理解するための手段です。

| ツール名                       | 用途              | 備考                 |
|:---------------------------|:----------------|:-------------------|
| `get_file_text_by_path`    | ファイル内容の読み込み     | 編集前の現状把握に必須        |
| `get_symbol_info`          | 型情報・定義・参照の取得    | IDEの静的解析を利用した正確な情報 |
| `get_project_dependencies` | 外部ライブラリ等の依存関係確認 | プロジェクト構成の理解        |
| `get_project_modules`      | モジュール構成の把握      | 多階層プロジェクトの分析       |

## 3. 編集とリファクタリング（Editing & Refactoring）

安全性と整合性を保ちながらコードを修正するための手段です。

| ツール名                   | 用途        | 備考                           |
|:-----------------------|:----------|:-----------------------------|
| `replace_text_in_file` | テキストの部分置換 | **最優先ツール**。一意な `oldText` を指定 |
| `create_new_file`      | 新規ファイルの作成 | `overwrite` フラグに注意           |
| `rename_refactoring`   | シンボルの名前変更 | 変数・クラス・関数の改名に必須              |

## 4. 検証とデバッグ（Verification & Debugging）

修正の正しさを証明し、品質を担保するための手段です。

| ツール名                        | 用途          | 備考                        |
|:----------------------------|:------------|:--------------------------|
| `get_file_problems`         | 構文エラー・警告の取得 | 編集後の**必須ステップ**            |
| `get_run_configurations`    | 実行構成のリスト取得  | テストやアプリの起動設定を確認           |
| `execute_run_configuration` | 構成の実行       | 修正後の動作確認に使用               |
| `execute_terminal_command`  | 任意コマンドの実行   | `rm -rf` 等の破壊的コマンドには細心の注意 |

- **execute_terminal_command**については基本的に使用しない。run_sell_commandでどうしても実行できない場合、ユーザーに確認を取ってから使用すること。

# ワークフロー例

### ステップ：精密なコード修正

1. `search_in_files_by_text` で修正対象を検索。
2. `get_file_text_by_path` で文脈を確認。
3. `replace_text_in_file` で修正を適用。
4. `get_file_problems` で構文エラーがないことを確認。
5. （必要に応じて）`execute_run_configuration` でテストを実行。

# エラー処理

- `file not found` の場合、`list_directory_tree` でパスの存在を再確認せよ。
- `text not found` の場合、`get_file_text_by_path` を再実行し、正確な `oldText` を取得し直せ。
