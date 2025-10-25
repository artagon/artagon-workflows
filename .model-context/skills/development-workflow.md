# Development Workflow for Artagon Workflows

**Purpose**: Standard development workflow for contributing to artagon-workflows
**Audience**: Developers, AI models, contributors
**Last Updated**: 2025-10-25

---

## 🎯 Overview

This project follows an **issue-driven development workflow** with semantic versioning and strict quality gates. All changes must go through pull requests with automated validation.

**Core Principles**:
1. **Issue First**: Every change starts with a GitHub issue
2. **Semantic Branches**: Branch names follow a convention
3. **Semantic Commits**: Commits trigger version bumps
4. **Automated Testing**: CI must pass before merge
5. **No Direct Commits**: Main branch is protected

**Reference**: `.agents/claude/preferences.md`

---

## 📋 Development Workflow Steps

### Step 1: Create GitHub Issue

**Before writing any code**, create an issue describing:
- What problem you're solving
- Why it needs to be solved
- Proposed solution (if known)

**Issue Types**:
```
feat: New workflow or feature (MINOR version bump)
fix: Bug fix (PATCH version bump)
docs: Documentation only
refactor: Code refactoring
perf: Performance improvement
test: Adding/updating tests
chore: Maintenance tasks
```

**Example Issue**:
```
Title: Add Java 26 support to Maven CI workflow
Labels: enhancement, maven

Description:
## Problem
Java 26 is now available but our maven_ci.yml doesn't support it.

## Solution
- Update maven_ci.yml to include Java 26 in version options
- Test with temurin-26 distribution
- Update documentation

## Acceptance Criteria
- [ ] maven_ci.yml supports java-version: '26'
- [ ] Tests pass with Java 26
- [ ] docs/MAVEN.md updated
```

---

### Step 2: Create Semantic Branch

**Branch Naming Convention**:
```
<type>/<issue-number>-<short-description>

Examples:
feat/123-add-java-26-support
fix/45-trivy-action-pinning
docs/67-update-security-guide
refactor/89-simplify-maven-ci
```

**Create Branch**:
```bash
# Get latest main
git checkout main
git pull origin main

# Create semantic branch
git checkout -b feat/123-add-java-26-support

# Verify branch name
git branch --show-current
```

**Branch Types**:
| Type | Description | Version Impact | Example |
|------|-------------|----------------|---------|
| `feat/` | New feature | MINOR (0.X.0) | feat/123-rust-workflow |
| `fix/` | Bug fix | PATCH (0.0.X) | fix/45-gpg-passphrase |
| `docs/` | Documentation | None | docs/67-security-guide |
| `refactor/` | Code refactor | PATCH | refactor/89-cleanup |
| `perf/` | Performance | PATCH | perf/12-cache-improvement |
| `test/` | Tests only | None | test/34-add-fixtures |
| `chore/` | Maintenance | None | chore/56-update-deps |

---

### Step 3: Make Changes

**Follow Security Guidelines**:
```yaml
# ✅ ALWAYS: Pin actions to commit SHAs
# actions/checkout@v4.2.2
- uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683

# ✅ ALWAYS: Add explicit permissions
jobs:
  build:
    permissions:
      contents: read
      packages: read

# ✅ ALWAYS: Validate user inputs
- name: Validate inputs
  run: |
    if ! echo "${{ inputs.value }}" | grep -qE '^[SAFE]*$'; then
      exit 1
    fi
```

**Code Quality Checklist**:
- [ ] Follow existing code style
- [ ] Use consistent naming (`<buildsystem>_[lang]_<category>.yml`)
- [ ] Add inline documentation
- [ ] Update relevant docs in `docs/`
- [ ] Add example in `examples/` if new workflow
- [ ] No hardcoded secrets
- [ ] No Claude attribution in commits

---

### Step 4: Write Semantic Commits

**Commit Message Format**:
```
<type>(<scope>): <subject>

[optional body]

[optional footer]
```

**Type** (triggers version bump):
```
feat:     New feature (MINOR: 0.1.0 → 0.2.0)
fix:      Bug fix (PATCH: 0.1.0 → 0.1.1)
docs:     Documentation only (no version bump)
refactor: Code refactoring (PATCH)
perf:     Performance improvement (PATCH)
test:     Adding tests (no version bump)
chore:    Maintenance (no version bump)
```

**Scope** (optional, helps organization):
```
(maven)   Maven-related changes
(cmake)   CMake-related changes
(bazel)   Bazel-related changes
(rust)    Rust-related changes
(security) Security improvements
(ci)      CI/CD improvements
(docs)    Documentation
```

**Subject** (required):
- Use imperative mood: "add" not "added"
- No capitalization of first letter
- No period at the end
- Max 50 characters

**Body** (optional):
- Explain what and why, not how
- Wrap at 72 characters
- Separated from subject by blank line

**Footer** (optional):
```
Closes #123
Fixes #456
Ref: SECURITY_AUDIT.md Issue #1
Breaking Change: <description>
```

**Example Commits**:

```bash
# Good: Feature with body
git commit -m "feat(maven): add Java 26 support

- Add java-version: '26' option to maven_ci.yml
- Test with temurin-26 distribution
- Update documentation in docs/MAVEN.md

Closes #123"

# Good: Bug fix
git commit -m "fix(security): pin trivy action to commit SHA

Trivy was using @master which is a mutable reference.
Pinned to v0.28.0 SHA for supply chain security.

Fixes #45
Ref: SECURITY_AUDIT.md Issue #1 (CRITICAL)"

# Good: Documentation
git commit -m "docs: update security best practices

- Add action pinning examples
- Document GPG passphrase handling
- Add input validation patterns"

# Bad: Too vague
git commit -m "update stuff"

# Bad: Wrong format
git commit -m "Added Java 26."

# Bad: Has Claude attribution (forbidden)
git commit -m "feat: add feature

🤖 Generated with Claude Code"
```

**Commit Frequently**:
```bash
# Make small, logical commits
git add maven_ci.yml
git commit -m "feat(maven): add Java 26 to version options"

git add docs/MAVEN.md
git commit -m "docs(maven): document Java 26 support"

git add examples/maven/ci.yml
git commit -m "docs(maven): update example with Java 26"
```

---

### Step 5: Push and Create Pull Request

**Push Branch**:
```bash
# Push to origin
git push origin feat/123-add-java-26-support

# Or set upstream and push
git push -u origin feat/123-add-java-26-support
```

**Create Pull Request**:

Use GitHub CLI or web interface:

```bash
# Using gh CLI (recommended)
gh pr create \
  --title "feat(maven): add Java 26 support" \
  --body "$(cat <<'EOF'
## Summary
- Add Java 26 support to maven_ci.yml
- Update documentation and examples
- Test with temurin-26 distribution

## Changes
- Modified: `.github/workflows/maven_ci.yml`
- Modified: `docs/MAVEN.md`
- Modified: `examples/maven/ci.yml`

## Testing
- [x] Local actionlint passes
- [x] Manual workflow trigger with Java 26
- [x] All existing tests still pass

## Checklist
- [x] Actions pinned to commit SHAs
- [x] Permissions blocks present
- [x] Documentation updated
- [x] Examples updated
- [x] Semantic commit messages
- [x] No Claude attribution

Closes #123
EOF
)" \
  --base main \
  --head feat/123-add-java-26-support
```

**PR Title Format**:
```
<type>(<scope>): <description>

Examples:
feat(maven): add Java 26 support
fix(security): pin all actions to commit SHAs
docs: update security guidelines
```

**PR Description Template**:
```markdown
## Summary
Brief description of what this PR does and why.

## Changes
- List of modified files
- Key changes made

## Testing
- [ ] actionlint passes
- [ ] yamllint passes
- [ ] Manual testing performed
- [ ] All existing workflows still work

## Checklist
- [ ] Actions pinned to commit SHAs (if applicable)
- [ ] Permissions blocks added (if new workflow)
- [ ] Input validation added (if user inputs)
- [ ] Documentation updated
- [ ] Examples updated (if applicable)
- [ ] Semantic commit messages
- [ ] No Claude attribution

Closes #<issue-number>
```

---

### Step 6: Automated CI Checks

**Pull requests trigger automatic validation**:

```yaml
# .github/workflows/test_lint.yml runs:
✓ actionlint - Workflow syntax validation
✓ yamllint - YAML formatting validation
✓ Security validation:
  - Check for unpinned actions
  - Check for hardcoded secrets
  - Verify permissions blocks exist
  - No @master/@main action references
```

**If CI Fails**:
```bash
# 1. Review the error in GitHub Actions tab
# 2. Fix the issue locally
# 3. Commit the fix
git add .
git commit -m "fix: address CI feedback - pin missing action"

# 4. Push the fix
git push origin feat/123-add-java-26-support

# CI will automatically re-run
```

**Common CI Failures**:

| Error | Fix |
|-------|-----|
| "Unpinned action found" | Pin action to commit SHA |
| "Missing permissions block" | Add explicit permissions |
| "YAML syntax error" | Run `yamllint .github/workflows/` locally |
| "Action not found" | Verify action exists and SHA is correct |

---

### Step 7: Code Review

**Review Process**:
1. Maintainer reviews code
2. Automated checks must pass
3. At least one approval required
4. No unresolved conversations

**Addressing Review Feedback**:
```bash
# Make requested changes
vim .github/workflows/maven_ci.yml

# Commit with semantic message
git commit -am "refactor(maven): simplify version detection logic

Apply reviewer feedback to use simpler regex pattern."

# Push update
git push origin feat/123-add-java-26-support
```

**Review Checklist for Reviewers**:
- [ ] Semantic commit messages
- [ ] Actions pinned to SHAs
- [ ] Permissions appropriate
- [ ] Input validation present (if applicable)
- [ ] No hardcoded secrets
- [ ] Documentation updated
- [ ] Examples provided
- [ ] Tests pass
- [ ] No Claude attribution
- [ ] Follows existing patterns

---

### Step 8: Merge

**Merge Strategy**: Squash and Merge (default)

**Why Squash?**:
- Clean main branch history
- Single commit per feature
- Easier to revert if needed
- Commit message becomes release note

**Merge Commit Format**:
```
<type>(<scope>): <description> (#PR-number)

Body from PR description

Closes #<issue-number>
```

**After Merge**:
```bash
# Update local main
git checkout main
git pull origin main

# Delete feature branch (local)
git branch -d feat/123-add-java-26-support

# Delete feature branch (remote)
git push origin --delete feat/123-add-java-26-support
# Or use: gh pr close 123 --delete-branch
```

---

## 🔄 Special Workflows

### Hotfix Workflow (Urgent Fixes)

For critical security issues or production bugs:

```bash
# 1. Create hotfix branch from main
git checkout main
git pull origin main
git checkout -b fix/critical-security-issue

# 2. Make minimal fix
vim .github/workflows/maven_security_scan.yml

# 3. Commit with clear explanation
git commit -am "fix(security): pin trivy action to prevent supply chain attack

CRITICAL: Trivy was using @master which can be moved by attackers.
Pinned to v0.28.0 SHA immediately.

This is a critical security fix and should be merged ASAP.

Ref: SECURITY_AUDIT.md Issue #1"

# 4. Push and create PR with [URGENT] tag
git push -u origin fix/critical-security-issue
gh pr create --title "[URGENT] fix(security): pin trivy action" --body "..."

# 5. Request immediate review
# 6. Merge as soon as CI passes
```

**Hotfix Criteria**:
- Security vulnerability (CRITICAL or HIGH)
- Production outage
- Data loss risk
- Widespread user impact

---

### Breaking Change Workflow

For changes that break existing workflows (major version bump):

```bash
# 1. Branch with BREAKING prefix
git checkout -b feat/BREAKING-rename-workflows-v3

# 2. Make breaking changes
mv maven-ci.yml maven_ci.yml
mv maven-deploy.yml maven_deploy.yml

# 3. Update all references
find examples/ -name "*.yml" -exec sed -i 's/maven-ci/maven_ci/g' {} \;
find docs/ -name "*.md" -exec sed -i 's/maven-ci/maven_ci/g' {} \;

# 4. Create migration guide
cat > MIGRATION_v3.md <<EOF
# Migration Guide: v2 → v3

## Breaking Changes
- All workflow files renamed from hyphens to underscores
- Example: maven-ci.yml → maven_ci.yml

## Migration Steps
\`\`\`yaml
# Old (v2)
uses: artagon/artagon-workflows/.github/workflows/maven-ci.yml@v2

# New (v3)
uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@v3
\`\`\`
EOF

# 5. Commit with BREAKING CHANGE footer
git commit -am "feat!: rename workflows to use underscores

BREAKING CHANGE: All workflow files now use underscores instead of hyphens
for consistency with v2.0.0 naming convention.

- maven-ci.yml → maven_ci.yml
- maven-deploy.yml → maven_deploy.yml
- ...

See MIGRATION_v3.md for full migration guide."

# 6. Create PR with clear warning
gh pr create --title "feat!: v3.0.0 breaking changes - workflow renaming" \
  --body "## ⚠️ BREAKING CHANGES

  This PR introduces breaking changes for v3.0.0.

  See MIGRATION_v3.md for details."
```

**Breaking Change Indicators**:
```
# In commit message:
feat!: description         # ! indicates breaking change

# Or in footer:
BREAKING CHANGE: description of what breaks
```

---

## 🧪 Testing Workflows

### Local Testing

**1. Lint Workflows Locally**:
```bash
# Install actionlint
bash <(curl https://raw.githubusercontent.com/rhysd/actionlint/main/scripts/download-actionlint.bash)

# Run actionlint
./actionlint -color -verbose

# Install yamllint
pip install yamllint

# Run yamllint
yamllint .github/workflows/
```

**2. Validate Action SHAs**:
```bash
# Check for unpinned actions
grep -rn "uses:.*@v[0-9]$" .github/workflows/

# Should return nothing if all pinned correctly
```

**3. Check Permissions**:
```bash
# List workflows without permissions
for f in .github/workflows/*.yml; do
  grep -q "permissions:" "$f" || echo "Missing: $f"
done
```

**4. Test with act (optional)**:
```bash
# Install act
brew install act  # macOS
# or download from: https://github.com/nektos/act

# List workflows
act -l

# Dry run a workflow
act -n

# Run a specific workflow (limited functionality)
act push
```

### Manual Workflow Testing

**Trigger workflow manually**:
```bash
# Via GitHub CLI
gh workflow run maven_ci.yml

# Via web interface
# Go to Actions → Select workflow → Run workflow
```

**Test with different inputs**:
```yaml
# Add workflow_dispatch for testing
on:
  workflow_call:
    # ... existing inputs
  workflow_dispatch:  # Add this for manual testing
    inputs:
      # Mirror workflow_call inputs
      java-version:
        type: choice
        options: ['17', '21', '25', '26']
        default: '21'
```

---

## 📊 Version Management

### Semantic Versioning

**Format**: `MAJOR.MINOR.PATCH`

**Version Bumps**:
```
feat:      MINOR bump (0.1.0 → 0.2.0)
fix:       PATCH bump (0.1.0 → 0.1.1)
feat!:     MAJOR bump (0.1.0 → 1.0.0)
BREAKING CHANGE: MAJOR bump
```

**Current Version**: Check latest tag:
```bash
git describe --tags --abbrev=0
# Example: v2.0.0
```

**Version History**:
```bash
# View all tags
git tag -l

# View tag with message
git show v2.0.0
```

### Release Process

**When to Release**:
- After merging significant features
- After critical security fixes
- On a regular schedule (monthly)
- When requested by maintainers

**Release Workflow** (maintainers only):
```bash
# 1. Ensure main is ready
git checkout main
git pull origin main

# 2. Run full test suite
# (CI should have already passed)

# 3. Create release tag
# Version determined by commit messages since last release
git tag -a v2.1.0 -m "Release v2.1.0

## Features
- Add Java 26 support to Maven workflows
- Improve security scanning performance

## Fixes
- Pin all actions to commit SHAs
- Fix GPG passphrase exposure

## Documentation
- Update security guidelines
- Add migration guide for v3.0.0"

# 4. Push tag
git push origin v2.1.0

# 5. Create GitHub Release
gh release create v2.1.0 \
  --title "Release v2.1.0" \
  --generate-notes

# 6. Update documentation
# version numbers in README, examples, etc.
```

---

## 🔍 Troubleshooting

### Common Issues

#### Issue: "Branch name doesn't follow convention"
```
Error: Branch must match: <type>/<issue>-<description>

Fix:
git branch -m feat/123-add-java-26-support
```

#### Issue: "Commit message doesn't follow semantic format"
```
Error: Commit must match: <type>(<scope>): <subject>

Fix:
git commit --amend -m "feat(maven): add Java 26 support"
```

#### Issue: "Action not pinned to SHA"
```
Error: Found: uses: actions/checkout@v4

Fix:
# actions/checkout@v4.2.2
uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683
```

#### Issue: "Missing permissions block"
```
Error: Workflow missing explicit permissions

Fix:
jobs:
  build:
    permissions:
      contents: read
      packages: read
```

#### Issue: "CI failing on actionlint"
```
Error: actionlint found syntax errors

Fix:
# Run locally
./actionlint -color -verbose

# Fix errors
# Commit and push
```

---

## 📋 Checklists

### Pre-Commit Checklist
- [ ] Changes follow project conventions
- [ ] Actions pinned to commit SHAs with comments
- [ ] Permissions blocks present
- [ ] Input validation added (if applicable)
- [ ] No hardcoded secrets
- [ ] Semantic commit message
- [ ] No Claude attribution
- [ ] Documentation updated
- [ ] Local tests pass (actionlint, yamllint)

### Pre-PR Checklist
- [ ] All commits are semantic
- [ ] Branch name follows convention
- [ ] Issue exists and is referenced
- [ ] Description explains changes
- [ ] Testing section completed
- [ ] Checklist items verified
- [ ] Ready for review

### Pre-Merge Checklist (Reviewers)
- [ ] CI checks passing
- [ ] Code quality acceptable
- [ ] Security requirements met
- [ ] Documentation adequate
- [ ] Tests sufficient
- [ ] Breaking changes documented
- [ ] No unresolved conversations

---

## 🎓 Examples

### Complete Feature Development

**Scenario**: Add Rust/Cargo workflow support

```bash
# 1. Create issue #234

# 2. Create branch
git checkout -b feat/234-add-rust-cargo-workflow

# 3. Create workflow file
cat > .github/workflows/cargo_ci.yml <<EOF
name: Rust CI (Reusable)

on:
  workflow_call:
    inputs:
      rust-version:
        type: string
        default: 'stable'

jobs:
  build:
    runs-on: ubuntu-latest
    permissions:
      contents: read

    steps:
      # actions/checkout@v4.2.2
      - uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683

      # actions/cache@v4.1.2
      - uses: actions/cache@6849a6489940f00c2f30c0fb92c6274307ccb58a
        with:
          path: |
            ~/.cargo
            target/
          key: \${{ runner.os }}-cargo-\${{ hashFiles('**/Cargo.lock') }}

      - name: Install Rust
        run: |
          rustup toolchain install \${{ inputs.rust-version }}
          rustup default \${{ inputs.rust-version }}

      - name: Build
        run: cargo build --release

      - name: Test
        run: cargo test
EOF

# 4. Commit
git add .github/workflows/cargo_ci.yml
git commit -m "feat(rust): add Cargo CI workflow

- Add cargo_ci.yml with Rust support
- Include caching for cargo and target directories
- Support custom Rust version input

Ref #234"

# 5. Add documentation
cat > docs/RUST.md <<EOF
# Rust/Cargo Workflow Documentation
...
EOF

git add docs/RUST.md
git commit -m "docs(rust): add Cargo workflow documentation"

# 6. Add example
mkdir -p examples/rust
cat > examples/rust/ci.yml <<EOF
name: CI
on: [push, pull_request]
jobs:
  ci:
    uses: artagon/artagon-workflows/.github/workflows/cargo_ci.yml@v2
    with:
      rust-version: 'stable'
EOF

git add examples/rust/
git commit -m "docs(rust): add usage example"

# 7. Push and create PR
git push -u origin feat/234-add-rust-cargo-workflow

gh pr create \
  --title "feat(rust): add Cargo CI workflow" \
  --body "## Summary
Add comprehensive Rust/Cargo CI workflow support.

## Changes
- New workflow: .github/workflows/cargo_ci.yml
- Documentation: docs/RUST.md
- Example: examples/rust/ci.yml

## Testing
- [x] actionlint passes
- [x] yamllint passes
- [x] Manual test with sample Rust project

## Checklist
- [x] Actions pinned to SHAs
- [x] Permissions blocks present
- [x] Documentation complete
- [x] Example provided

Closes #234"

# 8. Address review feedback, merge when approved
```

---

## 📞 Getting Help

### Documentation
- **This file**: Development workflow
- **context.md**: Project overview and guidelines
- **github-workflows.md**: Security best practices
- **instructions.md**: AI model instructions

### Support Channels
- GitHub Issues for bugs and features
- GitHub Discussions for questions
- Pull Request comments for code review

### Maintainers
- Check CODEOWNERS for specific areas
- Tag appropriate maintainers in PRs
- Allow 2-3 business days for review

---

**Last Updated**: 2025-10-25
**Version**: 1.0
**Status**: Active

---

**Remember**: Quality over speed. Take time to do it right. Follow the checklist. Write good commit messages. Your contributions make this project better for everyone! 🚀
