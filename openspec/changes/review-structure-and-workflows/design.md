# Design: Review Structure and Workflows

## Audit Methodology

### Action Version Audit

**Process:**
1. Extract all `uses:` statements from workflows
2. Parse action name and current SHA/version
3. Query GitHub API for latest release
4. Compare and flag outdated actions

**Tools:**
```bash
# Find all action uses
grep -rn "uses:" .github/workflows/ | grep -v "^#"

# Get latest release for an action
gh release list --repo actions/checkout --limit 1

# Get SHA for a tag
gh api repos/actions/checkout/git/ref/tags/v4.2.2 --jq '.object.sha'
```

### Security Compliance Checklist

| Check | Command | Expected |
|-------|---------|----------|
| Actions pinned | `grep -rn "uses:.*@[a-f0-9]\{40\}"` | All actions |
| No mutable tags | `grep -rn "uses:.*@v[0-9]$"` | No matches |
| No @main/@master | `grep -rn "uses:.*@\(main\|master\)$"` | No matches |
| Permissions present | `grep -l "permissions:" workflows/*.yml` | All workflows |

### Workflow Inventory

**Expected Workflows by Category:**

| Category | Count | Naming Pattern |
|----------|-------|----------------|
| Maven CI/Release | 9+ | `maven_*.yml` |
| CMake C | 2+ | `cmake_c_*.yml` |
| CMake C++ | 2+ | `cmake_cpp_*.yml` |
| Bazel | 2+ | `bazel_*.yml` |
| Gradle | 2+ | `gradle_*.yml` |
| Security | 3+ | `*security*.yml`, `codeql.yml` |
| Utility | 3+ | Various |

## Findings Template

### Action Version Report

| Action | Current Version | Current SHA | Latest Version | Latest SHA | Status |
|--------|-----------------|-------------|----------------|------------|--------|
| actions/checkout | v4.2.2 | abc123... | vX.Y.Z | def456... | ✅/⚠️ |

### Security Findings

| Workflow | Issue | Severity | Recommendation |
|----------|-------|----------|----------------|
| example.yml | Missing permissions | High | Add permissions block |

### Spec Accuracy

| Spec | Accurate | Gaps | Recommended Updates |
|------|----------|------|---------------------|
| workflow-security | Yes/No | List | Description |

## Recommendations Priority

- **P0 (Critical)**: Security vulnerabilities, unpinned actions
- **P1 (High)**: Outdated actions with security fixes
- **P2 (Medium)**: Outdated actions, missing docs
- **P3 (Low)**: Style inconsistencies, minor improvements

## Follow-up Actions

After audit completion:
1. Create issues for P0/P1 findings
2. Create PR for action version updates
3. Update specs with accurate information
4. Update documentation as needed
