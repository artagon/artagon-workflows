# AI Agent Instructions

This repository uses **OpenSpec** for spec-driven development.

## Getting Started

For comprehensive instructions on working with this repository, read:

- **[openspec/AGENTS.md](openspec/AGENTS.md)** - Complete workflow and security patterns
- **[openspec/project.md](openspec/project.md)** - Project context and conventions
- **[openspec/contributing.md](openspec/contributing.md)** - Contribution guidelines

## Quick Reference

### Security Requirements (Non-Negotiable)

1. **Action Pinning**: ALL GitHub Actions pinned to commit SHAs
2. **Permissions**: ALL jobs have explicit `permissions:` blocks
3. **Input Validation**: ALL user inputs validated before shell execution
4. **Secret Handling**: NEVER put secrets in command-line arguments
5. **Binary Verification**: ALWAYS verify checksums for downloads

### OpenSpec Commands

```bash
openspec list              # Active changes
openspec list --specs      # Capabilities
openspec show [item]       # View details
openspec validate --strict # Validate
openspec archive <id> --yes # Archive
```

### Directory Structure

```
openspec/
├── AGENTS.md           # Detailed AI instructions
├── project.md          # Project context
├── contributing.md     # Contribution guidelines
├── specs/              # Capability specifications
└── changes/            # Change proposals

.agents/
├── context/            # Runtime context
├── policies/           # Security policies
└── workflows/          # Agent workflow guides
```

## Before Any Task

1. Read `openspec/AGENTS.md` for workflow instructions
2. Check `openspec/project.md` for conventions
3. Review `.agents/policies/guardrails.md` for security constraints
4. Run `openspec list` to see active changes

## Resources

- [openspec/specs/workflow-security/spec.md](openspec/specs/workflow-security/spec.md) - Security requirements
- [.agents/policies/guardrails.md](.agents/policies/guardrails.md) - Security guardrails
- [.agents/context/glossary.md](.agents/context/glossary.md) - Terminology reference
