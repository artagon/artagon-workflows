# Agent Handbook

Runtime context and policies for AI agents working on Artagon Workflows.

## Directory Structure

```
.agents/
├── README.md                 # This file
├── context/
│   └── glossary.md           # Terminology and branch context
├── policies/
│   ├── guardrails.md         # Security constraints
│   └── release-checklist.md  # Pre-release verification
└── workflows/
    ├── add-workflow.md       # Adding new workflows
    ├── update-actions.md     # Updating action versions
    └── security-audit.md     # Security audit procedures
```

## Quick Reference

### Before Starting Any Task

1. Read `openspec/AGENTS.md` for spec-driven workflow
2. Check `openspec/project.md` for project conventions
3. Review `.agents/policies/guardrails.md` for security constraints
4. Run `openspec list` to see active changes

### Security Requirements (Non-Negotiable)

- ALL actions pinned to commit SHAs
- ALL jobs have explicit permissions
- ALL user inputs validated
- NO secrets in command-line arguments
- ALL binary downloads verified with checksums

### Key Documents

| Document | Purpose |
|----------|---------|
| `openspec/AGENTS.md` | Spec-driven development instructions |
| `openspec/project.md` | Project context and conventions |
| `openspec/contributing.md` | Contribution guidelines |
| `.agents/policies/guardrails.md` | Security constraints |
| `.agents/context/glossary.md` | Terminology reference |
