import sys
import json

def main():
    try:
        # デバッグ出力を完全に排除
        raw_input = sys.stdin.read()
        if not raw_input:
            # 入力がない場合は安全のため allow を返す（あるいは無視）
            print(json.dumps({"decision": "allow"}))
            return

        input_data = json.loads(raw_input)
        command = input_data.get("tool_input", {}).get("command", "")

        if "&&" in command:
            print(json.dumps({
                "decision": "deny",
                "reason": "&& is not allowed in PowerShell5.1.",
                "systemMessage": "⚠️ Restricted: &&"
            }))
        else:
            print(json.dumps({"decision": "allow"}))

    except Exception:
        # エラー時は何もしないか、デフォルトの allow を返す
        print(json.dumps({"decision": "allow"}))

if __name__ == "__main__":
    main()
