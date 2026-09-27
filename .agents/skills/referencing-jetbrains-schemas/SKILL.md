---
name: referencing-jetbrains-schemas
description: Provides strict JSON schemas for JetBrains MCP Server tools. Use when resolving validation errors due to missing parameters or verifying unknown argument structures.
---

# 運用原則
1. **正確性の追求**: ツールの呼び出しに失敗した場合、本スキルのスキーマと現在の引数を照合し、型や必須項目の不一致を特定せよ。
2. **プロジェクトパスの優先**: `projectPath` が判明している場合は、曖昧さを回避するため常に含めること。
3. **自律的解決**: スキーマに記載がないツールについては、過去の実行ログからパターンを分析し、確証が得られない場合のみクライアントへ仕様を問いかけよ。

# ツール・スキーマ・カタログ

## get_file_text_by_path
ファイル内容を読み取るためのツール。巨大なファイルは `truncateMode` で制御する。

* **詳細スキーマ**:
```json
{
    "type": "object",
    "properties": {
      "pathInProject": {
        "type": "string",
        "description": "Path relative to the project root"
      },
      "truncateMode": {
        "enum": [
          "START",
          "MIDDLE",
          "END",
          "NONE"
        ],
        "description": "How to truncate the text: from the start, in the middle, at the end, or don't truncate at all"
      },
      "maxLinesCount": {
        "type": "integer",
        "description": "Max number of lines to return. Truncation will be performed depending on truncateMode."
      },
      "projectPath": {
        "type": "string",
        "description": " The project path. Pass this value ALWAYS if you are aware of it. It reduces numbers of ambiguous calls. \n In the case you know only the current working directory you can use it as the project path.\n If you're not aware about the project path you can ask user about it."
      }
    },
    "required": [
      "pathInProject"
    ]
}
```

## replace_text_in_file
ファイル内のテキストを精密に置換する。

* **詳細スキーマ**:
```json
{
    "type": "object",
    "properties": {
      "pathInProject": {
        "type": "string",
        "description": "Path to target file relative to project root"
      },
      "oldText": {
        "type": "string",
        "description": "Text to be replaced"
      },
      "newText": {
        "type": "string",
        "description": "Replacement text"
      },
      "replaceAll": {
        "type": "boolean",
        "description": "Replace all occurrences"
      },
      "caseSensitive": {
        "type": "boolean",
        "description": "Case-sensitive search"
      },
      "projectPath": {
        "type": "string",
        "description": " The project path. Pass this value ALWAYS if you are aware of it. It reduces numbers of ambiguous calls. \n In the case you know only the current working directory you can use it as the project path.\n If you're not aware about the project path you can ask user about it."
      }
    },
    "required": [
      "pathInProject",
      "oldText",
      "newText"
    ]
}
```