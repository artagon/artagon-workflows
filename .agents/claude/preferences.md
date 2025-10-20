# Claude Code Preferences for Artagon Workflows

## Git Commit Attribution

**DO NOT** include Claude attribution in git commits.

Specifically:
- Do NOT include Claude or other AI attribution in commits or PRs
- Do NOT add "🤖 Generated with [Claude Code](https://claude.com/claude-code)"
- Do NOT add "Co-Authored-By: Claude <noreply@anthropic.com>"

All commits should show only the human author.

## Issue-Driven Development Workflow

**MANDATORY:** ALL development must follow the issue-driven workflow.

### Workflow Steps

1. **Create or Find Issue**
   - Every change MUST start with a GitHub issue
   - Use `gh issue create` with appropriate labels
   - Label types map to commit types:
     - `enhancement` → feat
     - `bug` → fix
     - `documentation` → docs
     - `chore` → chore

2. **Create Semantic Branch**
   - Format: `<type>/<issue>-<short-description>`
   - Types: feat, fix, docs, style, refactor, perf, test, build, ci, chore

3. **Make Changes with Semantic Commits**
   - Format: `<type>(<scope>): <subject>`
   - Always include issue reference: `Closes #42` or `Fixes #38`
   - Use imperative mood: "add" not "added"
   - Start subject with lowercase
   - No period at end
   - Subject <= 50 chars (72 max)

4. **Push and Create PR**
   - Push: `git push -u origin <branch-name>`
   - PR title must follow semantic format
   - Links to issue automatically

5. **Never Push to Main Directly**
   - All changes via feature branches
   - All changes via pull requests
   - All PRs require review

### Semantic Commit Format

**Types:**
- `feat` - New workflow or feature (MINOR semver)
- `fix` - Bug fix in workflow (PATCH semver)
- `docs` - Documentation only
- `style` - Formatting (no logic change)
- `refactor` - Workflow refactoring
- `perf` - Performance improvement
- `test` - Tests for workflows
- `build` - Build system changes
- `ci` - CI configuration changes
- `chore` - Maintenance

**Scopes:**
- `maven`, `cpp`, `c`, `bazel`, `workflows`, `ci`, `docs`, `examples`

**Breaking Changes:**
- Add `!` after type/scope: `feat(maven)!:`
- Include `BREAKING CHANGE:` in footer with migration guide

## Workflow Development Standards

### Testing Workflows

- Test workflow changes in a separate test repository before merging
- Never test directly in artagon-workflows main branch
- Verify workflows work with real projects before releasing

### Documentation Requirements

- Update workflow documentation when changing parameters
- Update examples when changing workflow behavior
- Keep README.md current with available workflows
- Document all inputs, outputs, and secrets

### Backward Compatibility

- Maintain backward compatibility within major versions
- Use semver for breaking changes
- Provide migration guides for breaking changes
- Deprecate old parameters before removing (1 version minimum)

### Workflow Best Practices

- Use `workflow_call` trigger for reusable workflows
- Provide sensible defaults for all optional inputs
- Document required secrets clearly
- Use caching to improve performance
- Support multiple versions of tools (Java, CMake, etc.)

## Quality Standards

- **Workflows**: Must work across multiple projects, include error handling
- **Documentation**: Must include examples, parameter descriptions, usage instructions
- **Security**: No secrets in workflow files, proper secret handling
- **Performance**: Use caching, parallel jobs where possible

## Change Hygiene

- Update documentation when workflow behavior changes
- Keep examples current with workflow changes
- Update version tags after releases
- Maintain CHANGELOG.md for all changes

## References

- **Contributing**: [CONTRIBUTING.md](../../CONTRIBUTING.md)
- **Maven Docs**: [docs/MAVEN.md](../../docs/MAVEN.md)
- **C/C++ Docs**: [docs/CPP.md](../../docs/CPP.md)
- **Bazel Docs**: [docs/BAZEL.md](../../docs/BAZEL.md)

## General Principle

This is a professional workflow library used by multiple projects. Changes must be well-tested, documented, and maintain backward compatibility within major versions.

All development must follow the issue-driven workflow with semantic commits. This is not optional.
