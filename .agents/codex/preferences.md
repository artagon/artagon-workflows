# Codex Preferences for Artagon Workflows

## Attribution

- Do NOT include Codex or other AI attribution in commits or PRs.
- All commits must list the human author only (no "Co-Authored-By" AI metadata).

## Issue-Driven Development Workflow

**MANDATORY:** ALL development follows the issue-driven workflow with semantic commits.

### Workflow Overview

1. **Issue First**: Every change requires a GitHub issue
2. **Semantic Branch**: Create branch with format `<type>/<issue>-<description>`
3. **Semantic Commits**: Format `<type>(<scope>): <subject>` with issue reference
4. **Pull Request**: Create PR with proper template
5. **Review & Merge**: All changes via PR, never direct to main

### Semantic Commit Format

```
<type>(<scope>): <subject>

<body>

Closes #<issue>
```

**Types:** feat, fix, docs, style, refactor, perf, test, build, ci, chore

**Scopes:** maven, cpp, c, bazel, workflows, ci, docs, examples

**Branch Format:** `<type>/<issue>-<description>`

### Workflow Development Guidelines

- Test workflows in separate repositories before merging
- Maintain backward compatibility within major versions
- Document all input parameters and secrets
- Provide migration guides for breaking changes
- Use caching to improve workflow performance

## Change Hygiene

- Update documentation when workflow behavior changes
- Keep examples current with workflow changes
- Update version tags after releases
- Maintain CHANGELOG.md for significant changes
- Test workflows with real projects before releasing

## Coding Style

- Follow GitHub Actions best practices
- Use clear, descriptive parameter names
- Provide sensible defaults for optional inputs
- Include error handling in workflows
- Use caching for dependencies

## Tooling Expectations

- Test workflow changes in test repositories
- Validate YAML syntax before committing
- Update documentation alongside workflow changes
- Create examples for new workflows or significant changes

## Context Memory

- Record workflow changes and patterns in project-context.md
- Note breaking changes and migration requirements
- Document new workflow features and capabilities

## Quality Standards

- **Workflows**: Must work across multiple projects, handle errors gracefully
- **Documentation**: Include examples, parameter descriptions, usage instructions
- **Security**: Proper secret handling, no hardcoded credentials
- **Performance**: Use caching, parallel jobs where appropriate

## Guiding Principle

Treat artagon-workflows as a professional workflow library used by multiple projects. Changes must be well-tested, documented, and maintain backward compatibility.

**All development must follow the issue-driven workflow.** This ensures traceability, proper review process, and maintains library quality standards.
