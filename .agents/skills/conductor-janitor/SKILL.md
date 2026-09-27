---
name: conductor-janitor
description: Automates Conductor track cleanup, including archiving, registry cleanup, and project documentation synchronization. Use this when a track is marked as completed in plan.md.
---

# Conductor Janitor

This skill automates the administrative cleanup of completed Conductor tracks.

## Cleanup Workflow

When this skill is active, execute these steps in order:

### 1. Verification
Ensure the track is ready for cleanup:
- Verify all tasks in `plan.md` are marked as `[x]`.
- Confirm `git status` is clean (or changes are already committed).

### 2. Archiving (PowerShell 5.1 Safe)
Move the track folder to the archive without using `&&` or forbidden redirection.
- **Commands**:
  1. `New-Item -ItemType Directory -Force -Path conductor/archive/<track_id>`
  2. `Move-Item conductor/tracks/<track_id>/* conductor/archive/<track_id>/`
  3. `Remove-Item -Recurse conductor/tracks/<track_id>`

### 3. Registry Cleanup
Remove the track entry from `conductor/tracks.md`.
- **Action**: Use `replace` to remove the markdown block starting with `- [ ]` or `- [x]` matching the track description.

### 4. Documentation Synchronization
Update project-level documents based on the track's results.
- **Action**: Read `evidence_report.md` and propose a `replace` call for `conductor/product.md` (Current Status section) and `conductor/tech-stack.md` if necessary.

### 5. Final Commit (AGENTS.md Compliant)
Stage and commit the cleanup.
- **Message Format**: `chore(conductor): Track '<track_id>' をアーカイブしドキュメントを同期`

## Critical Constraints
- **PowerShell Syntax**: NEVER use `&&` or `;` to connect commands. Use sequential tool calls.
- **Individual Staging**: Stage files individually as per `AGENTS.md`.
- **Validation**: Verify each file move and replacement before proceeding to the next step.
