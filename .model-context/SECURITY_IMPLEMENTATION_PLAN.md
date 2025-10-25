# Security & Quality Implementation Plan

## Overview

This document provides a prioritized action plan for implementing security hardening, testing infrastructure, and quality improvements for artagon-workflows based on the comprehensive security audit and analysis completed on 2025-10-25.

**Status**: Ready for implementation
**Total Estimated Effort**: 120-160 hours over 10 weeks

---

## Critical Priority (Week 1-2) - 40 hours

### 1. Pin All GitHub Actions to Commit SHAs

**Severity**: CRITICAL
**Risk**: Supply chain attack, compromised dependencies
**Effort**: 16 hours
**Reference**: SECURITY_AUDIT.md, ACTION_VERSIONS.md

**Tasks**:
- [ ] Update all `actions/checkout@v4` to commit SHA references
- [ ] Update all `actions/setup-java@v4` to commit SHA references
- [ ] Update all `actions/cache@v4` to commit SHA references
- [ ] Update all `actions/upload-artifact@v4` to commit SHA references
- [ ] Fix `aquasecurity/trivy-action@master` to pinned SHA (URGENT)
- [ ] Update all third-party actions
- [ ] Add comments with semantic versions for readability
- [ ] Test all workflows after pinning
- [ ] Update documentation with pinning policy

**Implementation**:
```bash
# For each workflow file:
# 1. Identify current action versions
# 2. Get commit SHA from ACTION_VERSIONS.md
# 3. Update with comment:
# Old: uses: actions/checkout@v4
# New:
# actions/checkout@v4.2.2
uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683
```

**Validation**:
- All workflows pass linting
- CI tests pass
- No `@v[0-9]` or `@master` references remain

---

### 2. Add Input Validation to Prevent Command Injection

**Severity**: CRITICAL
**Risk**: Command injection, arbitrary code execution
**Effort**: 12 hours

**Affected Workflows**:
- maven_ci.yml (maven-args input)
- maven_release.yml (deploy-profile input)
- cmake_c_ci.yml (cmake-options input)
- cmake_cpp_ci.yml (cmake-options input)
- bazel_multi_ci.yml (bazel-configs input)

**Tasks**:
- [ ] Add input validation patterns for all user-controlled inputs
- [ ] Add proper quoting in shell commands
- [ ] Add validation steps before using inputs
- [ ] Document allowed input formats
- [ ] Test with malicious inputs

**Implementation Example**:
```yaml
# maven_ci.yml
- name: Validate inputs
  run: |
    MAVEN_ARGS="${{ inputs.maven-args }}"

    # Validate allowed characters (alphanumeric, hyphens, equals, dots, commas)
    if ! echo "$MAVEN_ARGS" | grep -qE '^[-A-Za-z0-9=.,_:/ ]*$'; then
      echo "❌ Invalid maven-args: contains disallowed characters"
      echo "Allowed: alphanumeric, hyphens, equals, dots, commas, underscores, colons, slashes, spaces"
      exit 1
    fi

    echo "✅ Input validation passed"

- name: Build with Maven
  run: |
    MAVEN_ARGS="${{ inputs.maven-args }}"
    mvn clean verify "$MAVEN_ARGS"
```

**Validation**:
- Test with normal inputs (pass)
- Test with injection attempts: `&& curl attacker.com` (fail)
- Test with special characters (documented behavior)

---

### 3. Fix Trivy Action @master Usage

**Severity**: CRITICAL
**Risk**: Using unstable, mutable branch for security tool
**Effort**: 1 hour

**File**: .github/workflows/maven_security_scan.yml:80

**Change**:
```yaml
# OLD
uses: aquasecurity/trivy-action@master

# NEW
# aquasecurity/trivy-action@0.28.0
uses: aquasecurity/trivy-action@69c4ceb78ce0f6ebbb0ea7c9bc6f9d46423d5d62
```

---

### 4. Fix GPG Passphrase Exposure

**Severity**: CRITICAL
**Risk**: Secret exposure in process arguments
**Effort**: 4 hours

**Files**: maven_release.yml:152, maven_deploy.yml

**Current (Vulnerable)**:
```yaml
run: |
  mvn clean deploy -P${{ inputs.deploy-profile }} -DskipTests \
    -Dgpg.passphrase="$GPG_PASSPHRASE"
```

**Fixed**:
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

- name: Deploy
  run: mvn clean deploy -P${{ inputs.deploy-profile }} -DskipTests
```

---

### 5. Add Checksum Verification for Buildifier

**Severity**: HIGH
**Risk**: Compromised binary download
**Effort**: 2 hours

**File**: bazel_multi_ci.yml:210-211

**Current (Vulnerable)**:
```yaml
wget -O /usr/local/bin/buildifier https://github.com/bazelbuild/buildtools/releases/download/v6.4.0/buildifier-linux-amd64
chmod +x /usr/local/bin/buildifier
```

**Fixed**:
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

    buildifier --version
```

---

### 6. Enable Dependabot

**Severity**: HIGH
**Effort**: 2 hours

**Tasks**:
- [x] Created .github/dependabot.yml (DONE)
- [ ] Enable Dependabot in repository settings
- [ ] Configure security alerts
- [ ] Set up PR auto-review workflow (optional)
- [ ] Document Dependabot workflow in CONTRIBUTING.md

**Repository Settings**:
1. Settings → Security → Dependabot alerts → Enable
2. Settings → Security → Dependabot security updates → Enable
3. Settings → Security → Dependabot version updates → Enable

---

### 7. Add Explicit Permissions to All Workflows

**Severity**: HIGH
**Effort**: 3 hours

**Tasks**:
- [x] Add permissions blocks to all workflows
- [x] Follow least-privilege principle
- [x] Add `security-events: write` to maven_security_scan.yml
- [x] Document permission requirements

**Template**:
```yaml
jobs:
  ci:
    runs-on: ubuntu-latest
    permissions:
      contents: read      # Checkout code
      packages: read      # Download artifacts (if needed)
    steps: [...]

  release:
    runs-on: ubuntu-latest
    permissions:
      contents: write     # Create tags, releases
      packages: write     # Publish artifacts
    steps: [...]

  security-scan:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      security-events: write  # Upload SARIF
    steps: [...]
```

---

## High Priority (Week 3-4) - 40 hours

### 8. Implement Workflow Linting (Level 1 Testing)

**Effort**: 8 hours
**Reference**: TESTING_STRATEGY.md

**Tasks**:
- [ ] Create .github/workflows/test_lint.yml
- [ ] Add actionlint
- [ ] Add yamllint configuration
- [ ] Configure pre-commit hooks
- [ ] Document linting rules
- [ ] Fix all existing lint errors

**Files to Create**:
- `.github/workflows/test_lint.yml`
- `.yamllint.yml`
- `.pre-commit-config.yaml`

---

### 9. Create Workflow Unit Tests (Level 2 Testing)

**Effort**: 24 hours
**Reference**: TESTING_STRATEGY.md

**Tasks**:
- [ ] Create test fixtures for Maven
- [ ] Create test fixtures for CMake (C/C++)
- [ ] Create test fixtures for Bazel
- [ ] Create test workflows for each reusable workflow
- [ ] Add test verification jobs
- [ ] Document testing approach

**Files to Create**:
- `test/fixtures/maven/pom.xml` + simple project
- `test/fixtures/cmake_c/CMakeLists.txt` + simple project
- `test/fixtures/cmake_cpp/CMakeLists.txt` + simple project
- `test/fixtures/bazel/WORKSPACE` + BUILD files
- `.github/workflows/test_maven_ci.yml`
- `.github/workflows/test_cmake_ci.yml`
- `.github/workflows/test_bazel_ci.yml`

---

### 10. Security Testing Workflows

**Effort**: 8 hours

**Tasks**:
- [ ] Create secret scanning test
- [ ] Create permission validation test
- [ ] Create action pinning validation test
- [ ] Add to CI pipeline
- [ ] Document security testing

**Files to Create**:
- `.github/workflows/test_security.yml`

---

## Medium Priority (Week 5-6) - 24 hours

### 11. Complete v2 Naming Migration

**Effort**: 8 hours

**Tasks**:
- [ ] Rename maven-build.yml → maven_build.yml
- [ ] Rename maven-deploy.yml → maven_deploy.yml
- [ ] Rename maven-release.yml → maven_release.yml
- [ ] Rename maven-central-release.yml → maven_central_release.yml
- [ ] Rename maven-github-release.yml → maven_github_release.yml
- [ ] Update all documentation references
- [ ] Update all example workflows
- [ ] Create migration guide addendum

**Note**: This is a BREAKING CHANGE requiring major version bump (v2.x → v3.0)

---

### 12. Reduce continue-on-error Usage

**Effort**: 4 hours

**Tasks**:
- [ ] Review all workflows with `continue-on-error: true`
- [ ] Remove from security scans (should fail build)
- [ ] Keep only for advisory checks
- [ ] Document rationale for remaining uses

---

### 13. Integration Matrix Tests (Level 3 Testing)

**Effort**: 12 hours

**Tasks**:
- [ ] Create .github/workflows/test_integration.yml
- [ ] Add Maven matrix tests (Java 17, 21, 25 x OS)
- [ ] Add CMake matrix tests (gcc, clang x C standards)
- [ ] Add Bazel config tests (release, debug, asan, etc.)
- [ ] Schedule weekly runs
- [ ] Add to PR workflow for workflow changes

---

## Low Priority (Week 7-10) - 36 hours

### 14. E2E Test Repositories

**Effort**: 16 hours

**Tasks**:
- [ ] Create artagon-workflows-test-maven repository
- [ ] Create artagon-workflows-test-cmake repository
- [ ] Create artagon-workflows-test-bazel repository
- [ ] Set up real-world test scenarios
- [ ] Document E2E testing process
- [ ] Schedule quarterly E2E test runs

---

### 15. Performance & Monitoring

**Effort**: 12 hours

**Tasks**:
- [ ] Create performance benchmark workflow
- [ ] Add cache effectiveness monitoring
- [ ] Set up workflow health dashboard (optional)
- [ ] Configure alerting (optional)
- [ ] Document performance baselines

---

### 16. Code Owners & Labels

**Effort**: 4 hours

**Tasks**:
- [ ] Create .github/CODEOWNERS
- [ ] Define build-system ownership
- [ ] Create build-system-specific labels
- [ ] Update CONTRIBUTING.md with ownership model
- [ ] Configure branch protection rules

---

### 17. Improved Changelog Organization

**Effort**: 4 hours

**Tasks**:
- [ ] Reorganize CHANGELOG.md by build system
- [ ] Add sections for Maven, CMake, Bazel, Cross-Cutting
- [ ] Document changelog conventions
- [ ] Update release process to use new format

---

## Implementation Schedule

### Week 1-2: Critical Security Fixes
- Pin all actions to commit SHAs ✅ URGENT
- Add input validation ✅ URGENT
- Fix Trivy @master usage
- Fix GPG passphrase exposure
- Add buildifier checksum verification
- Enable Dependabot
- Add explicit permissions

**Milestone**: Security audit critical issues resolved

---

### Week 3-4: Testing Foundation
- Implement workflow linting
- Create unit tests
- Add security testing
- Set up CI pipeline

**Milestone**: Basic testing infrastructure in place

---

### Week 5-6: Quality Improvements
- Complete v2 naming migration (breaking change → v3.0.0)
- Reduce continue-on-error usage
- Add integration matrix tests

**Milestone**: Testing coverage >80%

---

### Week 7-8: Advanced Testing
- Create E2E test repositories
- Implement performance monitoring

**Milestone**: Comprehensive testing in place

---

### Week 9-10: Organization & Polish
- Set up code owners
- Create build-system labels
- Improve changelog organization
- Update all documentation

**Milestone**: Production-ready, enterprise-grade workflow repository

---

## Validation Checklist

After implementation, verify:

### Security ✅
- [x] All actions pinned to commit SHAs
- [x] No unpinned actions (`@v[0-9]` or `@master`)
- [x] Input validation on all user-controlled inputs
- [ ] No hardcoded secrets
- [ ] GPG passphrase not in process arguments
- [ ] Binary downloads have checksum verification
- [x] All workflows have explicit permissions
- [ ] Dependabot enabled and configured
- [ ] Security alerts enabled

### Testing ✅
- [ ] Workflow linting passes (actionlint, yamllint)
- [ ] Unit tests created for all workflows
- [ ] Unit tests passing
- [ ] Integration matrix tests created
- [ ] Security tests passing
- [ ] E2E test repositories set up

### Quality ✅
- [ ] All workflows follow v2 naming convention
- [ ] Documentation updated
- [ ] Examples updated
- [ ] Changelog organized by build system
- [ ] CODEOWNERS in place
- [ ] Labels created
- [ ] CI pipeline enforces all checks

### Documentation ✅
- [ ] SECURITY_AUDIT.md reviewed
- [ ] ACTION_VERSIONS.md up to date
- [ ] TESTING_STRATEGY.md reflects actual tests
- [ ] REPOSITORY_STRATEGY.md decision documented
- [ ] This implementation plan tracked and updated
- [ ] CONTRIBUTING.md includes new processes
- [ ] README.md updated with security practices

---

## Breaking Changes (v3.0.0)

If completing the naming migration:

**Renamed Workflows**:
```
maven-build.yml → maven_build.yml
maven-deploy.yml → maven_deploy.yml
maven-release.yml → maven_release.yml
maven-central-release.yml → maven_central_release.yml
maven-github-release.yml → maven_github_release.yml
```

**Migration Guide for Consumers**:
```yaml
# Old (v2.x)
uses: artagon/artagon-workflows/.github/workflows/maven-ci.yml@v2

# New (v3.x)
uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@v3
```

**Timeline**:
1. Implement changes in v3.0.0-beta
2. Test with internal projects
3. Release v3.0.0
4. Maintain v2.x for 6 months (security fixes only)
5. Deprecate v2.x after 12 months

---

## Success Metrics

| Metric | Current | Target | Timeline |
|--------|---------|--------|----------|
| Unpinned Actions | 100% | 0% | Week 2 |
| Workflows with Tests | 0% | 100% | Week 6 |
| Security Scan Pass Rate | Unknown | 100% | Week 2 |
| Test Coverage | 0% | >80% | Week 6 |
| Permission Blocks | 6% (1/17) | 100% | Week 2 |
| Documentation Quality | Good | Excellent | Week 10 |

---

## Risk Management

| Risk | Mitigation |
|------|------------|
| **Breaking changes affect consumers** | Version appropriately (v3.0.0), maintain v2.x for 6 months |
| **Tests reveal workflow bugs** | Fix before releasing v3.0.0 |
| **Action pinning breaks features** | Test thoroughly, verify SHAs match tags |
| **Performance degradation** | Benchmark before/after, optimize if needed |
| **Team bandwidth** | Prioritize critical security fixes, defer low-priority items |

---

## Communication Plan

### Week 1
- [ ] Create GitHub issue: "Security Hardening Initiative"
- [ ] Notify team of upcoming changes
- [ ] Share security audit findings

### Week 2
- [ ] Create draft PR for action pinning
- [ ] Request review from security team
- [ ] Notify consumers of upcoming v3.0.0

### Week 4
- [ ] Publish security improvements blog post
- [ ] Update README with security badges
- [ ] Share testing strategy

### Week 6
- [ ] Release v3.0.0-beta
- [ ] Gather feedback from early adopters
- [ ] Address issues

### Week 8
- [ ] Release v3.0.0 stable
- [ ] Publish migration guide
- [ ] Announce deprecation timeline for v2.x

### Week 10
- [ ] Publish completion report
- [ ] Update project roadmap
- [ ] Plan next quarter improvements

---

## Resources Required

- **Development Time**: 120-160 hours
- **Review Time**: 20-30 hours
- **Testing Infrastructure**: GitHub Actions runners (existing)
- **External Dependencies**: None
- **Budget**: $0 (all open-source tools)

---

## Next Steps

**Immediate (This Week)**:
1. Review this plan with team
2. Create tracking issues for each major task
3. Begin Week 1 critical security fixes
4. Set up project board for visibility

**Questions for Stakeholders**:
1. Approve v2 → v3 breaking change for naming consistency?
2. Allocate dedicated time for implementation?
3. Establish security review cadence?
4. Assign build-system ownership?

---

## References

- SECURITY_AUDIT.md - Detailed security findings
- ACTION_VERSIONS.md - Action version reference
- TESTING_STRATEGY.md - Comprehensive testing approach
- REPOSITORY_STRATEGY.md - Mono-repo decision rationale
- CONTRIBUTING.md - Contribution guidelines

---

**Plan Author**: Security Audit Team
**Date**: 2025-10-25
**Status**: Ready for Review
**Next Review**: 2025-11-25
