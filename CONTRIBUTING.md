# Contributing to Artagon Workflows

Thank you for your interest in contributing to Artagon Workflows! This document provides guidelines for contributing workflow improvements, bug fixes, and new workflows.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Testing Workflows](#testing-workflows)
- [Documentation](#documentation)
- [Versioning and Releases](#versioning-and-releases)
- [Workflow Best Practices](#workflow-best-practices)

## Code of Conduct

This project follows professional open-source development practices. Be respectful, constructive, and collaborative.

## Getting Started

### Prerequisites

- GitHub account with access to Artagon organization
- Understanding of GitHub Actions and workflow syntax
- Familiarity with the build system you're working with (Maven, CMake, Bazel)
- Test repository for workflow testing

### Repository Structure

```
artagon-workflows/
├── .github/workflows/    # Reusable workflow files
├── docs/                 # Workflow documentation
├── examples/             # Usage examples
├── .agents/              # Agent preferences
└── LICENSE               # License information
```

## Development Workflow

**ALL development must follow the issue-driven workflow.**

### 1. Create Issue

Every change starts with a GitHub issue:

```bash
gh issue create \
  --title "Add support for Java 26 to Maven workflows" \
  --label "enhancement" \
  --body "Description of the change..."
```

### 2. Create Semantic Branch

Create a branch following the semantic format:

```bash
# Format: <type>/<issue>-<description>
# Examples:
git checkout -b feat/42-add-java-26-support
git checkout -b fix/38-maven-cache-issue
git checkout -b docs/45-update-bazel-guide
```

**Branch Types:**
- `feat` - New workflow or feature
- `fix` - Bug fix
- `docs` - Documentation only
- `refactor` - Workflow refactoring
- `perf` - Performance improvement
- `test` - Testing improvements
- `chore` - Maintenance tasks

### 3. Make Changes

Edit workflow files, documentation, and examples as needed.

**Important:** Never test workflows directly in artagon-workflows. Use a separate test repository.

### 4. Test Thoroughly

See [Testing Workflows](#testing-workflows) section below.

### 5. Commit with Semantic Format

```bash
git commit -m "feat(maven): add Java 26 support

Add Java 26 to supported versions in Maven CI workflow.
Update documentation and examples.

Closes #42"
```

**Commit Message Format:**
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:** feat, fix, docs, style, refactor, perf, test, build, ci, chore

**Scopes:** maven, cpp, c, bazel, workflows, ci, docs, examples

### 6. Push and Create PR

```bash
git push -u origin feat/42-add-java-26-support

# Create PR
gh pr create \
  --title "feat(maven): add Java 26 support" \
  --body "Closes #42"
```

### 7. Code Review

- Address reviewer feedback
- Update documentation if requested
- Ensure all CI checks pass
- Get approval from maintainers

### 8. Merge

After approval, maintainers will merge the PR and create a release tag if appropriate.

## Testing Workflows

**CRITICAL:** Never test workflows directly in artagon-workflows repository.

### Testing Process

1. **Create Test Repository**
   ```bash
   # Create a test repo with the project type you're testing
   gh repo create my-org/test-maven-workflow --private
   ```

2. **Copy Workflow to Test Repo**
   ```yaml
   # .github/workflows/test-ci.yml in test repository
   name: Test CI

   on:
     push:
       branches: [main]
     workflow_dispatch:

   jobs:
     ci:
       uses: your-username/artagon-workflows/.github/workflows/maven-ci.yml@your-branch
       secrets: inherit
   ```

3. **Test All Input Combinations**
   - Test with different input parameters
   - Test with different tool versions
   - Test error conditions
   - Verify caching works correctly

4. **Test with Real Projects**
   - Test with at least 2-3 real Artagon projects
   - Verify existing projects aren't broken by changes
   - Check backward compatibility

### Test Checklist

- [ ] Workflow completes successfully
- [ ] All input parameters work as documented
- [ ] Default values are sensible
- [ ] Error messages are clear and helpful
- [ ] Caching improves performance
- [ ] Secrets are handled securely
- [ ] Documentation matches actual behavior
- [ ] Examples work without modification
- [ ] Backward compatibility maintained (within major version)

## Documentation

### Required Documentation Updates

When adding or modifying workflows, update:

1. **Workflow File Comments**
   - Document purpose at top of file
   - Explain complex logic
   - Document all inputs and outputs

2. **README.md**
   - Add new workflows to the list
   - Update quick start if needed
   - Update feature list if applicable

3. **Workflow-Specific Documentation**
   - `docs/MAVEN.md` for Maven workflows
   - `docs/CPP.md` for C/C++ workflows
   - `docs/BAZEL.md` for Bazel workflows

4. **Examples**
   - Add or update examples in `examples/` directory
   - Ensure examples are tested and working
   - Include comments explaining usage

### Documentation Format

**Workflow Documentation Template:**

```markdown
## workflow-name.yml

**Purpose:** Brief description

**Usage:**
\`\`\`yaml
jobs:
  job-name:
    uses: artagon/artagon-workflows/.github/workflows/workflow-name.yml@v1
    with:
      param: value
    secrets: inherit
\`\`\`

**Inputs:**
- `param-name` (type, required/optional, default) - Description

**Outputs:**
- `output-name` - Description

**Secrets:**
- `SECRET_NAME` - Description

**Example:** Link to examples directory
```

## Versioning and Releases

### Semantic Versioning

Workflows follow semantic versioning:
- **Major (v1 → v2):** Breaking changes
- **Minor (v1.0 → v1.1):** New features, backward compatible
- **Patch (v1.0.0 → v1.0.1):** Bug fixes

### Breaking Changes

A change is breaking if it:
- Removes or renames required inputs
- Changes default behavior in incompatible ways
- Removes support for previously supported versions
- Changes output format breaking downstream consumers

**For Breaking Changes:**
1. Increment major version
2. Document in CHANGELOG.md
3. Provide migration guide
4. Update all examples
5. Announce to users

### Release Branch Strategy

**IMPORTANT**: This repository uses a release branch strategy:

- **`main` branch**: Development branch, always has SNAPSHOT versions
- **`release-*` branches**: Stable release branches with release versions (no SNAPSHOT)
- **Tags**: Created on release branches

**Branch Versions:**
```
main: Always SNAPSHOT (e.g., 1.0.9-SNAPSHOT, 2.0.0-SNAPSHOT)
release-1.0.8: 1.0.8 (no SNAPSHOT)
release-2.0.0: 2.0.0 (no SNAPSHOT)
```

### Release Process for Maintainers

When ready to create a new release:

#### 1. Ensure Main is at Next SNAPSHOT

```bash
# Check current version on main
git checkout main
# Should show next SNAPSHOT version (e.g., 1.0.9-SNAPSHOT)
```

If not, bump the version:
```bash
# Bump to next SNAPSHOT version
git checkout main
# Update version in relevant files
git commit -m "chore: bump main to 1.0.9-SNAPSHOT"
git push origin main
```

#### 2. Create Release Branch

```bash
# Create release branch from commit at desired SNAPSHOT version
# Example: releasing 1.0.8 from a commit where version is 1.0.8-SNAPSHOT
git checkout -b release-1.0.8 <commit-at-1.0.8-SNAPSHOT>
git push origin release-1.0.8
```

#### 3. Trigger Release Workflow

Navigate to GitHub Actions and run the release workflow from the release branch. The workflow will:
- Remove `-SNAPSHOT` from version (1.0.8-SNAPSHOT → 1.0.8)
- Commit the release version
- Create tag `v1.0.8`
- Push release branch and tag
- Create GitHub release

#### 4. Release Branch Lifecycle

**Keep release branches** - they're used for hotfixes:
```
release-1.0.8: 1.0.8 (kept for 1.0.x hotfixes)
release-1.1.0: 1.1.0 (kept for 1.1.x hotfixes)
release-2.0.0: 2.0.0 (kept for 2.0.x hotfixes)
```

### Hotfix Process

To release a hotfix on an existing release:

```bash
# 1. Checkout release branch
git checkout release-1.0.8

# 2. Make fixes
git commit -m "fix: critical security issue"

# 3. Create hotfix tag
git tag -a v1.0.8.1 -m "Security hotfix 1.0.8.1"

# 4. Push
git push origin release-1.0.8 --tags

# 5. Cherry-pick to main if needed
git checkout main
git cherry-pick <fix-commit-sha>
git push origin main
```

### Version Management

**For Maven Projects:**
- Main branch POM versions must end with `-SNAPSHOT`
- Release branches remove the `-SNAPSHOT` suffix

**For Workflows Repository:**
- Main branch is always development
- Releases use git tags (v1.2.0, v2.0.0, etc.)
- Major version tags (v1, v2) point to latest in that series

See **[RELEASE.md](RELEASE.md)** for complete release documentation.

## Workflow Best Practices

### Design Principles

1. **Reusability**
   - Use `workflow_call` trigger
   - Make workflows configurable via inputs
   - Provide sensible defaults

2. **Clarity**
   - Use descriptive names for jobs and steps
   - Add comments for complex logic
   - Provide clear error messages

3. **Performance**
   - Use caching for dependencies
   - Enable parallel execution where possible
   - Minimize checkout operations

4. **Security**
   - Never hardcode credentials
   - Use secrets for sensitive data
   - Validate inputs
   - Use specific action versions (not @latest)

5. **Reliability**
   - Handle errors gracefully
   - Provide fallback strategies
   - Test edge cases
   - Document limitations

### Common Patterns

**Caching Dependencies:**
```yaml
- name: Cache Maven dependencies
  uses: actions/cache@v4
  with:
    path: ~/.m2/repository
    key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
    restore-keys: |
      ${{ runner.os }}-maven-
```

**Multi-Version Support:**
```yaml
inputs:
  java-version:
    description: 'Java version to use'
    required: false
    type: string
    default: '25'
```

**Error Handling:**
```yaml
- name: Build project
  run: |
    if ! mvn clean install; then
      echo "Build failed. Check logs above for details."
      exit 1
    fi
```

### Code Style

- Use 2-space indentation in YAML
- Keep lines under 120 characters when possible
- Use descriptive names for inputs and outputs
- Group related steps
- Add comments for non-obvious logic

## Questions or Problems?

- **Issues:** https://github.com/artagon/artagon-workflows/issues
- **Discussions:** https://github.com/artagon/artagon-workflows/discussions
- **Documentation:** https://github.com/artagon/artagon-workflows/tree/main/docs

## Related Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Reusing Workflows](https://docs.github.com/en/actions/using-workflows/reusing-workflows)
- [artagon-common Repository](https://github.com/artagon/artagon-common)

Thank you for contributing to Artagon Workflows!
