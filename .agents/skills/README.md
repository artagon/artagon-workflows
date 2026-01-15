# AI Agent Skills

This directory contains skill definitions for AI agents used in the reusable GitHub Actions workflows.

## Available Skills

| Skill | File | Description |
|-------|------|-------------|
| Code Reviewer | `code-reviewer.md` | PR code review with quality scoring |
| Security Analyst | `security-analyst.md` | Security vulnerability detection |
| Issue Triager | `issue-triager.md` | Issue categorization and prioritization |
| Code Fixer | `code-fixer.md` | Automated code repair |
| OpenSpec Reviewer | `openspec-reviewer.md` | Design document validation |
| Doc Generator | `doc-generator.md` | Documentation generation |

## Skill Structure

Each skill definition includes:

- **Role**: What the agent does
- **Capabilities**: What the agent can do
- **Instructions**: How the agent should behave
- **Output Format**: Expected response structure

## Using Skills in Workflows

Skills are referenced by workflows that load the skill definition and include it in the AI prompt. The skill content is prepended to the specific task instructions.

Example workflow usage:

```yaml
- name: Load skill
  id: skill
  run: |
    if [ -f ".agents/skills/code-reviewer.md" ]; then
      cat .agents/skills/code-reviewer.md > skill_prompt.txt
    fi

- name: Run AI Review
  uses: anthropics/claude-code-action@v1
  with:
    prompt: |
      $(cat skill_prompt.txt)

      Now review this specific PR...
```

## OpenSpec Integration

All skills are designed to work with OpenSpec design documents:

1. Skills reference `openspec/` directory for design context
2. Skills validate changes against documented architecture
3. Skills suggest updates to specs when needed

## Adding New Skills

1. Create a new `.md` file in this directory
2. Follow the standard skill structure
3. Update this README with the new skill
4. Reference the skill in relevant workflows
