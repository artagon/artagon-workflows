# AI Agent Skills - GitHub Actions Workflows

This directory contains skill definitions for AI agents that review, fix, and document GitHub Actions workflows.

## Available Skills

| Skill | File | Purpose |
|-------|------|---------|
| Code Reviewer | `code-reviewer.md` | Review workflow PRs for security and best practices |
| Security Analyst | `security-analyst.md` | Detect vulnerabilities in workflows |
| Issue Triager | `issue-triager.md` | Triage workflow-related issues |
| Code Fixer | `code-fixer.md` | Fix security and reliability issues |
| OpenSpec Reviewer | `openspec-reviewer.md` | Validate workflow specifications |
| Doc Generator | `doc-generator.md` | Generate workflow documentation |

## Workflow-Specific Focus

All skills are tailored for GitHub Actions workflows and include:

- **Action Pinning**: SHA vs tag validation
- **Injection Prevention**: Untrusted input handling
- **Permission Scoping**: Least privilege verification
- **Secret Handling**: Secure secret usage patterns
- **Supply Chain Security**: Action source validation

## OpenSpec Integration

All skills use the official `openspec` CLI:

```bash
# List changes and specs
openspec list --changes
openspec list --specs

# View specific items
openspec show <name>
openspec show <name> --json

# Validate
openspec validate
openspec validate --strict
```

## Using Skills in Workflows

Skills are loaded by AI workflows and included in prompts:

```yaml
- name: Load agent skill
  id: skill
  run: |
    SKILL_PATH=".agents/skills/code-reviewer.md"
    if [ -f "$SKILL_PATH" ]; then
      echo "has_skill=true" >> $GITHUB_OUTPUT
    fi

- name: Run AI Review
  uses: anthropics/claude-code-action@v1
  with:
    prompt: |
      ${{ steps.skill.outputs.has_skill == 'true' &&
          'Read .agents/skills/code-reviewer.md for instructions.' ||
          'You are a code reviewer.' }}
```

## Security Patterns

### Injection Vulnerabilities

```yaml
# VULNERABLE
run: echo "${{ github.event.issue.title }}"

# SAFE
env:
  TITLE: ${{ github.event.issue.title }}
run: echo "$TITLE"
```

### Action Pinning

```yaml
# INSECURE
uses: actions/checkout@v4

# SECURE
uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683
```

### Permission Minimization

```yaml
# OVERLY PERMISSIVE
permissions: write-all

# LEAST PRIVILEGE
permissions:
  contents: read
  pull-requests: write
```

## Adding New Skills

1. Create `skill-name.md` following the template structure
2. Focus on GitHub Actions workflow specifics
3. Include `openspec` CLI integration
4. Update this README
5. Reference in relevant workflow prompts
