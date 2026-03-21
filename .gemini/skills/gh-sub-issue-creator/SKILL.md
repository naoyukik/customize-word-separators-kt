---
name: gh-sub-issue-creator
description: Handles the creation of GitHub Sub-issues. Use this skill when you need to add existing issues as sub-issues to a parent Epic/Issue. It provides the correct usage of the `sub_issue_write` tool and fallback procedures using GitHub CLI (`gh api`) if the tool fails.
---

# GitHub Sub-issue Creator

This skill provides the exact protocol for adding sub-issues to a parent issue in a GitHub repository.

## The Core Concept

When using the `sub_issue_write` tool, the `sub_issue_id` parameter **MUST NOT** be the human-readable issue number (
e.g., `74`). Instead, it requires the **internal Node ID or Global Issue ID** (a large numeric identifier like
`3971828927`) returned by GitHub's REST or GraphQL API.

## Step 1: Obtain the Internal Numeric `id` of the Target Sub-issue

Before calling `sub_issue_write`, you must retrieve the internal numeric `id` (NOT the issue number) of the issue you
want to attach.

### Method A: Use the `issue_read` Tool (Recommended)

Call `issue_read` with the `method: "get"` and the human-readable `issue_number` of the **child** issue.
The output will contain an `"id"` field with a large numeric value.

```json5
// Example: Get info for issue #74
{
  "owner": "naoyukik",
  "repo": "customize-word-separators-kt",
  "issue_number": 74,
  "method": "get"
}
// Output snippet: {"id": 3971828927, "number": 74, ...}
```

Use the value `3971828927`.

### Method B: Using GitHub GraphQL API (GitHub-Efficiency)

情報の正確な取得とトークン効率を最大化するため、以下の手順で GraphQL を使用して ID を取得する。

1. **コマンド実行**: `run_shell_command` を使用し、直接 `gh api graphql` を実行して ID を取得する。

    ```powershell
    gh api graphql -f query='
      query($owner: String!, $repo: String!, $number: Int!) {
        repository(owner: $owner, name: $repo) {
          issue(number: $number) { id }
        }
      }' -f owner='naoyukik' -f repo='customize-word-separators-kt' -F number=ISSUE_NUMBER
    ```

2. **内容取得**: `run_shell_command` の実行結果（例: `{"data":{"repository":{"issue":{"id":"I_..."}}}}`）から ID を取得する。

### Method C: When Creating a New Issue

If you just created the issue using `issue_write`, the output already contains the `id`.

```json
{"id":"3971828927","url":"..."}
```

---

## Step 2: Use the `sub_issue_write` Tool

Once you have the internal `id`, call the `sub_issue_write` tool.

```json5
// Correct Example
{
  "owner": "naoyukik",
  "repo": "customize-word-separators-kt",
  "issue_number": 73,        // The human-readable number of the PARENT issue
  "sub_issue_id": 3971828927,// The internal ID of the CHILD issue
  "method": "add"
}
```

```json5
// INCORRECT Example (Will result in 404 error)
{
  "owner": "naoyukik",
  "repo": "customize-word-separators-kt",
  "issue_number": 73,
  "sub_issue_id": 74,        // ERROR: Do not use the issue number here!
  "method": "add"
}
```

## Step 3: Fallback Procedure (If `sub_issue_write` fails)

もし `sub_issue_write` ツールが 404 や 422 エラーで失敗した場合、以下の **GitHub-Efficiency** プロトコルに従い、GitHub
GraphQL API を直接使用して確認またはリンクを試みる。

1. **既にリンクされているか確認する**:
   422 エラーは「既にサブイシューである」ことを示す場合がある。以下のコマンドで確認せよ。
   ```powershell
   gh api graphql -f query='
     query($owner: String!, $repo: String!, $number: Int!) {
       repository(owner: $owner, name: $repo) {
         issue(number: $number) {
           subIssues(first: 10) { nodes { number } }
         }
       }
     }' -f owner='naoyukik' -f repo='customize-word-separators-kt' -F number=PARENT_NUMBER
   ```

2. **GitHub GraphQL API でサブイシューを追加する**:
   ツールが正常に動作しない場合は、`mutation (addSubIssue)` を直接実行してリンクする。
   ```powershell
   gh api graphql -f query='
     mutation($issueId: ID!, $subIssueId: ID!) {
       addSubIssue(input: { issueId: $issueId, subIssueId: $subIssueId }) {
         subIssue { id }
       }
     }' -f issueId='PARENT_NODE_ID' -f subIssueId='CHILD_NODE_ID'
   ```
   実行結果の JSON を確認し、エラーがなければ成功である。
