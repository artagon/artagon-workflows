# Proposal: Fix Shellcheck Warnings (CI Blocker)

## Why

The CI pipeline is failing due to shellcheck warnings detected by actionlint:

- **SC2086**: Double quote to prevent globbing and word splitting
- **SC2046**: Quote this to prevent word splitting
- **SC2035**: Use ./*glob* so names with dashes won't become options

These warnings affect multiple workflows and block all PRs from merging.

## What Changes

Fix shellcheck warnings in affected workflows by:
1. Quoting variable expansions
2. Quoting command substitutions
3. Using proper glob patterns

## Impact

- Affected workflows: ~10 workflows with shell scripts
- Breaking changes: None
- CI impact: Unblocks all PRs

## Scope

### In Scope

- Fix SC2086 (double quote variables)
- Fix SC2046 (quote command substitutions)
- Fix SC2035 (glob patterns)
- Verify actionlint passes

### Out of Scope

- Functional changes to workflows
- Other security fixes (separate changes)
