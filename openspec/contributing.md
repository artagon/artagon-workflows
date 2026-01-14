# Contributing to Artagon Workflows

Thanks for helping improve the Artagon reusable workflows collection. This guide covers conventions, security requirements, and the OpenSpec workflow.

## Prerequisites

- Git
- GitHub CLI (`gh`)
- yamllint (optional, for local validation)
- actionlint (optional, for local validation)

## Quick Start

```bash
# Clone repository
git clone https://github.com/artagon/artagon-workflows.git
cd artagon-workflows

# Run local validation (if actionlint available)
actionlint .github/workflows/*.yml
yamllint .github/workflows/*.yml
```

## Security Requirements (MANDATORY)

Every workflow change MUST comply with these requirements:

### 1. Action Pinning

```yaml
# CORRECT - Pin to commit SHA with version comment
# actions/checkout@v4.2.2
uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683

# WRONG - Never use mutable tags
uses: actions/checkout@v4
uses: aquasecurity/trivy-action@master
```

### 2. Permissions

```yaml
# CORRECT - Explicit permissions
jobs:
  build:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: read
    steps: [...]

# WRONG - Missing permissions block
jobs:
  build:
    runs-on: ubuntu-latest
    steps: [...]
```

### 3. Input Validation

```yaml
# CORRECT - Validate before use
- name: Validate inputs
  run: |
    INPUT="${{ inputs.user-input }}"
    if ! echo "$INPUT" | grep -qE '^[SAFE_PATTERN]*$'; then
      echo "Invalid input"
      exit 1
    fi

# WRONG - Direct use without validation
run: mvn clean install ${{ inputs.maven-args }}
```

### 4. Secret Handling

```yaml
# CORRECT - Secret in config file
- env:
    GPG_PASSPHRASE: ${{ secrets.GPG_PASSPHRASE }}
  run: |
    cat > ~/.m2/settings.xml <<EOF
    <settings>
      <profiles><profile>
        <properties>
          <gpg.passphrase>${GPG_PASSPHRASE}</gpg.passphrase>
        </properties>
      </profile></profiles>
    </settings>
    EOF

# WRONG - Secret in command line
run: mvn deploy -Dgpg.passphrase="${{ secrets.GPG_PASSPHRASE }}"
```

### 5. Binary Downloads

```yaml
# CORRECT - Checksum verification
- run: |
    wget -O /tmp/tool https://example.com/tool
    echo "EXPECTED_SHA256  /tmp/tool" | sha256sum --check

# WRONG - No verification
- run: wget https://example.com/tool && chmod +x tool
```

## Branch Naming Strategy

### For OpenSpec Changes

```bash
# Required format
feature/workflows(<issue#>)-<short-name>

# Examples
feature/workflows(42)-add-gradle-workflows
feature/workflows(55)-update-action-versions
```

### For Other Changes

```bash
# Format
<type>/<scope>-<description>

# Types
feat/     - New feature or capability
fix/      - Bug fix
docs/     - Documentation only
security/ - Security improvements
refactor/ - Code restructuring
test/     - Adding or updating tests
chore/    - Build process, tooling

# Examples
feat/maven-add-java26-support
fix/cmake-permissions-block
security/pin-trivy-action
docs/update-release-guide
```

## Commit Message Format

```bash
# Security changes
security: pin all GitHub Actions to commit SHAs

- Pin actions/checkout, setup-java, cache, upload-artifact
- Fix Trivy @master vulnerability
- Prevents supply chain attacks

Ref: SECURITY_AUDIT.md Issue #1 (CRITICAL)

# Feature additions
feat(maven): add Java 26 support

- Update maven_ci.yml with Java 26 option
- Test with temurin-26
- Update documentation

Closes #123

# Documentation
docs: update security guidelines

- Add input validation examples
- Document action pinning policy
```

## OpenSpec Workflow

### Creating a Change Proposal

1. **Check existing work**
   ```bash
   openspec list           # Active changes
   openspec list --specs   # Existing capabilities
   ```

2. **Create change directory**
   ```bash
   CHANGE=add-gradle-workflows
   mkdir -p openspec/changes/$CHANGE/specs/gradle-workflows
   ```

3. **Write proposal.md**
   ```markdown
   ## Why

   [1-2 sentences on problem/opportunity]

   ## What Changes

   - [Bullet list of changes]
   - [Mark breaking changes with **BREAKING**]

   ## Impact

   - Affected specs: [list capabilities]
   - Affected code: [key files/systems]
   ```

4. **Write tasks.md**
   ```markdown
   ## 1. Implementation

   - [ ] 1.1 Create workflow file
   - [ ] 1.2 Add permissions block
   - [ ] 1.3 Pin all actions
   - [ ] 1.4 Add input validation
   - [ ] 1.5 Update documentation
   - [ ] 1.6 Create example usage
   ```

5. **Write spec deltas**
   ```markdown
   ## ADDED Requirements

   ### Requirement: Gradle CI Workflow

   The workflow SHALL build and test Gradle projects.

   #### Scenario: Successful build

   - **WHEN** valid Gradle project detected
   - **THEN** build completes successfully
   ```

6. **Validate**
   ```bash
   openspec validate $CHANGE --strict
   ```

### Implementing Changes

1. Read proposal.md and tasks.md
2. Implement tasks sequentially
3. Mark tasks complete as you go: `- [x]`
4. Run validation before PR

### Archiving Changes

After merge and deployment:

```bash
git checkout main && git pull
git checkout -b archive/add-gradle-workflows

openspec archive add-gradle-workflows --yes
openspec validate --strict

git add openspec/
git commit -m "Archive add-gradle-workflows after deployment"
git push origin archive/add-gradle-workflows
```

## Pre-Commit Checklist

Before committing workflow changes:

- [ ] All actions pinned to commit SHAs
- [ ] Comment with semantic version above SHA
- [ ] Permissions block present on all jobs
- [ ] Input validation added for user inputs
- [ ] GPG secrets handled via config files
- [ ] Binary downloads have checksum verification
- [ ] Workflow tested (at minimum: linting)
- [ ] Documentation updated
- [ ] Semantic commit message

## Adding New Workflows

When adding a new workflow:

1. **Follow naming convention**: `<buildsystem>_[lang]_<category>.yml`
2. **Pin all actions** to commit SHAs
3. **Add explicit permissions** block
4. **Validate all user inputs**
5. **Add inline documentation**
6. **Create example** in `examples/`
7. **Document in README.md**
8. **Add to test suite**

## Testing

### Local Validation

```bash
# Run actionlint
actionlint .github/workflows/*.yml

# Run yamllint
yamllint .github/workflows/*.yml

# Check for unpinned actions
grep -rn "uses:.*@v[0-9]$" .github/workflows/

# Check for missing permissions
for f in .github/workflows/*.yml; do
  grep -q "permissions:" "$f" || echo "Missing: $f"
done
```

### CI Validation

Push changes triggers:
- `.github/workflows/test_lint.yml` - actionlint, yamllint, security validation

## Pull Requests

- Keep PRs focused (single capability or fix)
- Include screenshots for UI changes
- Reference related issues
- Follow commit message conventions
- Ensure CI passes before requesting review

## Common Tasks

### Pin a New Action

```bash
# Find latest release
gh release list --repo actions/checkout --limit 1

# Get commit SHA for tag
gh api repos/actions/checkout/git/ref/tags/v4.2.2 --jq '.object.sha'

# Update workflow
# actions/checkout@v4.2.2
uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683
```

### Add Permissions to Workflow

```yaml
# CI (read-only)
permissions:
  contents: read
  packages: read

# Release (write access)
permissions:
  contents: write
  packages: write

# Security scanning
permissions:
  contents: read
  security-events: write
```

### Add Input Validation

```yaml
- name: Validate inputs
  run: |
    INPUT="${{ inputs.user-input }}"
    # Use allowlist pattern
    if ! echo "$INPUT" | grep -qE '^[-A-Za-z0-9=.,_:/ ]*$'; then
      echo "Invalid input: contains disallowed characters"
      exit 1
    fi
    echo "Input validation passed"
```

## Resources

- [GitHub Actions Security](https://docs.github.com/en/actions/security-guides/security-hardening-for-github-actions)
- [OSSF Scorecard](https://github.com/ossf/scorecard)
- [SLSA Framework](https://slsa.dev/)
- [actionlint](https://github.com/rhysd/actionlint)
- [yamllint](https://yamllint.readthedocs.io/)
