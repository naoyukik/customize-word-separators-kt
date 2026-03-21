import sys
import json
import re

def main():
    try:
        raw_input = sys.stdin.read()
        if not raw_input:
            print(json.dumps({"decision": "allow"}))
            return

        input_data = json.loads(raw_input)
        command = input_data.get("tool_input", {}).get("command", "")

        # git add . / git add -A / git add --all を検出
        # 空白の数やクォートの有無に対応
        if re.search(r'git\s+add\s+(\.|-A|--all)(\s+|$)', command):
            print(json.dumps({
                "decision": "deny",
                "reason": "git add . or -A is prohibited. Please specify files individually, or use 'git add conductor/' for documentation.",
                "systemMessage": "⚠️ Prohibited: git add all"
            }))
        else:
            print(json.dumps({"decision": "allow"}))

    except Exception:
        print(json.dumps({"decision": "allow"}))

if __name__ == "__main__":
    main()
