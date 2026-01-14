# Claude Instructions

This repository uses **OpenSpec** for spec-driven development.

## Getting Started

For comprehensive instructions, read:

- **[openspec/AGENTS.md](openspec/AGENTS.md)** - Complete workflow and security patterns
- **[openspec/project.md](openspec/project.md)** - Project context and conventions

## Critical Security Requirements

ALL workflows in this repository MUST follow these requirements:

1. **Pin ALL actions to commit SHAs** (40 characters)
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
   ```yaml
   # WRONG: mvn -Dpassword="${{ secrets.PASS }}"
   # CORRECT: Use env block and config files
   ```

5. **ALWAYS verify binary download checksums**

## Quick Commands

```bash
# OpenSpec workflow
openspec list              # Active changes
openspec list --specs      # Capabilities
openspec validate --strict # Validate

# Security checks
actionlint .github/workflows/*.yml
grep -rn "uses:.*@v[0-9]$" .github/workflows/  # Find unpinned
```

## Key Documents

| Document | Purpose |
|----------|---------|
| `openspec/AGENTS.md` | Detailed AI instructions |
| `openspec/project.md` | Project conventions |
| `openspec/specs/workflow-security/spec.md` | Security requirements |
| `.agents/policies/guardrails.md` | Security constraints |

## Commit Messages

Do NOT include Claude attribution in commits for this repository.

```bash
# CORRECT format
feat(maven): add new workflow feature

- Implementation details
- References

Closes #123
```
