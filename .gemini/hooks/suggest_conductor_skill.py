import sys
import json

def main():
    try:
        raw_input = sys.stdin.read()
        if not raw_input:
            print(json.dumps({"decision": "allow"}))
            return

        input_data = json.loads(raw_input)

        # BeforeAgentイベントの場合、promptフィールドにユーザーの入力が含まれる
        # BeforeToolイベント(run_shell_command)の場合、tool_input.commandに含まれる
        prompt = input_data.get("prompt", "")
        tool_input = input_data.get("tool_input", {})
        command = tool_input.get("command", "") if isinstance(tool_input, dict) else ""

        # /conductor で始まる、あるいは含むメッセージを検出
        if "/conductor" in prompt or "/conductor" in command:
            print(json.dumps({
                "decision": "allow",
                "systemMessage": "💡 Conductor関連の操作が検出されました。必ず 'conductor-protocol' スキルをアクティベートし、その手順（spec.md/plan.mdの作成、ユーザー検証等）を厳格に遵守してください。"
            }))
        else:
            print(json.dumps({"decision": "allow"}))

    except Exception:
        print(json.dumps({"decision": "allow"}))

if __name__ == "__main__":
    main()
