# Copilot Instructions (OpenSpec Workflow)

This repository uses OpenSpec. Follow the authoritative workflow in `openspec/AGENTS.md`.

Core rules:
- Specs live in GitHub Issues labeled `spec` and as deltas under `openspec/changes/`.
- Built behavior lives in `openspec/specs/`.
- Proposals and tasks live in `openspec/changes/<change-id>/`.
- Link every implementation PR to its parent spec issue.

Before you code or review:
1. Run `openspec list` to identify the active change.
2. Read `openspec/changes/<change-id>/proposal.md` and `openspec/changes/<change-id>/tasks.md`.
3. Read the spec deltas in `openspec/changes/<change-id>/specs/` and the base spec in `openspec/specs/`.
4. Follow the conventions in `openspec/project.md` and `openspec/contributing.md`.

Key directories:
- `.github/workflows/`: Reusable GitHub Actions workflow files.
- `openspec/`: OpenSpec workflow sources (specs, changes, guidance).
- `openspec/specs/`: Capability specifications (workflow-security, maven-workflows, cmake-workflows, bazel-workflows).
- `.agents/`: AI agent context, policies, and workflow guides.
- `docs/`: Build and release documentation.
- `examples/`: Usage examples for each build system.
- `test/`: Test fixtures for workflow validation.

## Security Requirements (CRITICAL)

Every workflow change MUST comply with these requirements:

1. **Pin ALL actions to commit SHAs**
   ```yaml
   # actions/checkout@v4.2.2
   uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683
   ```

2. **Add explicit permissions to ALL jobs**
   ```yaml
   jobs:
     build:
       permissions:
         contents: read
   ```

3. **Validate ALL user inputs before shell execution**
   ```yaml
   - name: Validate inputs
     run: |
       INPUT="${{ inputs.value }}"
       if ! echo "$INPUT" | grep -qE '^[SAFE]*$'; then
         exit 1
       fi
   ```

4. **NEVER put secrets in command-line arguments**

5. **ALWAYS verify binary download checksums**

Implementation expectations:
- Keep changes scoped to the spec and proposal. Flag anything out of scope.
- Update `openspec/changes/<change-id>/tasks.md` as items are completed.
- Follow workflow naming convention: `<buildsystem>_[lang]_<category>.yml`
- Add or update examples to cover new workflows.

Good vs bad patterns:
- Good: "PR references Spec #22, updates tasks.md, and implements every acceptance criteria item."
- Good: "All actions pinned to SHAs with version comments."
- Good: "Permissions block with least-privilege access."
- Bad: "Adds new features not mentioned in the spec or proposal."
- Bad: "Uses mutable action tags (@v4, @main, @master)."
- Bad: "Missing permissions block or input validation."

For review guidance, follow `.github/copilot-review-instructions.md`.
