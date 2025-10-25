# GitHub Actions Version Pinning Reference

This document provides commit SHA mappings for all third-party GitHub Actions used in artagon-workflows.

**Last Updated**: 2025-10-25
**Update Frequency**: Quarterly (or when security updates are released)

---

## Why Pin to Commit SHAs?

Pinning actions to specific commit SHAs instead of tags provides:
- **Immutability**: Commit SHAs cannot be changed, tags can be moved
- **Supply Chain Security**: Protection against compromised action repositories
- **Reproducibility**: Builds are deterministic and auditable
- **Security Auditing**: Clear verification of what code is executing

**Trade-off**: Requires manual updates via Dependabot or periodic review.

---

## Official GitHub Actions

### actions/checkout

**Latest Stable**: v4.2.2 (as of 2025-10-25)

```yaml
# actions/checkout@v4.2.2
- uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683
```

**Security**: ✅ Official GitHub action, actively maintained
**Trust Level**: HIGH
**Update Frequency**: Monthly
**Breaking Changes**: Review release notes before updating

**Common Parameters**:
```yaml
- uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683
  with:
    fetch-depth: 0          # Full history for versioning
    submodules: recursive   # Include submodules
    token: ${{ secrets.GITHUB_TOKEN }}
```

---

### actions/setup-java

**Latest Stable**: v4.5.0 (as of 2025-10-25)

```yaml
# actions/setup-java@v4.5.0
- uses: actions/setup-java@8df1039502a15bceb9433410b1a100fbe190c53b
```

**Security**: ✅ Official GitHub action
**Trust Level**: HIGH
**JDK Sources**: Eclipse Temurin, Zulu, Adopt, Microsoft
**Cache Support**: ✅ Maven, Gradle

---

### actions/cache

**Latest Stable**: v4.1.2 (as of 2025-10-25)

```yaml
# actions/cache@v4.1.2
- uses: actions/cache@6849a6489940f00c2f30c0fb92c6274307ccb58a
```

**Security**: ✅ Official GitHub action
**Trust Level**: HIGH
**Max Cache Size**: 10GB per repository

---

### actions/upload-artifact

**Latest Stable**: v4.4.3 (as of 2025-10-25)

```yaml
# actions/upload-artifact@v4.4.3
- uses: actions/upload-artifact@b4b15b8c7c6ac21ea08fcf65892d2ee8f75cf882
```

**Security**: ✅ Official GitHub action
**Trust Level**: HIGH
**Retention**: Default 90 days, configurable

---

### actions/download-artifact

**Latest Stable**: v4.1.8 (as of 2025-10-25)

```yaml
# actions/download-artifact@v4.1.8
- uses: actions/download-artifact@fa0a91b85d4f404e444e00e005971372dc801d16
```

**Security**: ✅ Official GitHub action
**Trust Level**: HIGH

---

## Third-Party Actions (Verified Publishers)

### codecov/codecov-action

**Latest Stable**: v5.0.7 (as of 2025-10-25)

```yaml
# codecov/codecov-action@v5.0.7
- uses: codecov/codecov-action@015f24e6818733317a2da2edd6290ab26238649a
```

**Security**: ⚠️ Third-party action (Codecov Inc.)
**Trust Level**: MEDIUM-HIGH
**Maintainer**: Codecov (owned by Sentry)
**Audit Status**: Verified publisher
**Alternative**: Upload coverage manually via curl

**Security Considerations**:
- Codecov had a supply chain incident in 2021 (Bash Uploader compromise)
- Since then, they've improved security practices
- GitHub verified publisher badge
- Consider using `token:` for private repos only

---

### softprops/action-gh-release

**Latest Stable**: v2.2.0 (as of 2025-10-25)

```yaml
# softprops/action-gh-release@v2.2.0
- uses: softprops/action-gh-release@da07017b40e28a1a0a35a90e201ca6e05c3dda96
```

**Security**: ⚠️ Third-party action (Individual maintainer)
**Trust Level**: MEDIUM
**Maintainer**: Doug Tangren (@softprops)
**Stars**: 4.2k
**Used By**: 500k+ repositories

**Security Considerations**:
- Well-established action with large user base
- Single maintainer risk
- Consider using `gh release create` via GitHub CLI instead

**Alternative (More Secure)**:
```yaml
- name: Create GitHub Release
  env:
    GH_TOKEN: ${{ github.token }}
  run: |
    gh release create "${{ steps.create-tag.outputs.tag }}" \
      --title "Release ${{ steps.version.outputs.version }}" \
      --generate-notes
```

---

### aquasecurity/trivy-action

**Latest Stable**: v0.28.0 (as of 2025-10-25)

```yaml
# aquasecurity/trivy-action@0.28.0
- uses: aquasecurity/trivy-action@69c4ceb78ce0f6ebbb0ea7c9bc6f9d46423d5d62
```

**Security**: ✅ Security vendor (Aqua Security)
**Trust Level**: HIGH
**Maintainer**: Aqua Security Inc.
**Purpose**: Vulnerability scanning

**CRITICAL**: Never use `@master` for security tools!

---

### github/codeql-action

**Latest Stable**: v3.27.6 (as of 2025-10-25)

```yaml
# github/codeql-action/upload-sarif@v3.27.6
- uses: github/codeql-action/upload-sarif@662472033e021d55d94146f66f6058822b0b39fd
```

**Security**: ✅ Official GitHub security action
**Trust Level**: HIGH
**Purpose**: Code scanning and SARIF upload

---

### cachix/install-nix-action

**Latest Stable**: v30 (as of 2025-10-25)

```yaml
# cachix/install-nix-action@v30
- uses: cachix/install-nix-action@08dcb3a5e62fa31e2eb71ca1c1a4ae1b42a8f4b4
```

**Security**: ⚠️ Third-party action (Cachix)
**Trust Level**: MEDIUM-HIGH
**Maintainer**: Cachix (Nix ecosystem company)
**Purpose**: Install Nix package manager

**Security Considerations**:
- Installs system-level package manager
- Requires elevated permissions
- Well-maintained within Nix community

---

### bazelbuild/setup-bazelisk

**Latest Stable**: v3.0.1 (as of 2025-10-25)

```yaml
# bazelbuild/setup-bazelisk@v3.0.1
- uses: bazelbuild/setup-bazelisk@b63ef97e907d9f0dc8c6f99a88a89f63d533addc
```

**Security**: ✅ Official Bazel organization
**Trust Level**: HIGH
**Maintainer**: Bazel team (Google)

---

### ilammy/msvc-dev-cmd

**Latest Stable**: v1.13.0 (as of 2025-10-25)

```yaml
# ilammy/msvc-dev-cmd@v1.13.0
- uses: ilammy/msvc-dev-cmd@0b201ec74fa43914dc39ae48a89fd1d8cb592756
```

**Security**: ⚠️ Third-party action (Individual maintainer)
**Trust Level**: MEDIUM
**Maintainer**: Ilya Konstantinov (@ilammy)
**Purpose**: Setup MSVC on Windows runners

---

### dorny/test-reporter

**Latest Stable**: v1.9.1 (as of 2025-10-25)

```yaml
# dorny/test-reporter@v1.9.1
- uses: dorny/test-reporter@v1.9.1  # SHA: TBD - check releases
```

**Security**: ⚠️ Third-party action (Individual maintainer)
**Trust Level**: MEDIUM
**Alternative**: Use native GitHub Actions test reporting

---

## How to Update Pinned Actions

### Manual Update Process

1. **Check for new releases**:
```bash
gh release list --repo actions/checkout --limit 1
```

2. **Get commit SHA for tag**:
```bash
gh api repos/actions/checkout/git/ref/tags/v4.2.2 --jq '.object.sha'
```

3. **Update workflow files**:
```yaml
# Old
- uses: actions/checkout@v4

# New
# actions/checkout@v4.2.2
- uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683
```

4. **Add comment with semantic version** for human readability

---

### Automated Update with Dependabot

Create `.github/dependabot.yml`:

```yaml
version: 2
updates:
  - package-ecosystem: "github-actions"
    directory: "/"
    schedule:
      interval: "weekly"
      day: "monday"
    open-pull-requests-limit: 10
    reviewers:
      - "artagon/security-team"
    labels:
      - "dependencies"
      - "security"
    commit-message:
      prefix: "chore"
      prefix-development: "chore"
      include: "scope"
```

**Benefits**:
- Automatic PR creation for action updates
- Security vulnerability alerts
- Changelog integration
- Version compatibility checks

---

## Action Verification Checklist

Before adding a new third-party action:

- [ ] Check GitHub Stars (minimum 500)
- [ ] Check "Used by" count (minimum 1,000)
- [ ] Verify maintainer identity
- [ ] Review recent commit activity (active in last 3 months)
- [ ] Check for security policy (SECURITY.md)
- [ ] Review open security issues
- [ ] Check verified publisher badge
- [ ] Audit action code for malicious behavior
- [ ] Prefer official actions over third-party
- [ ] Consider implementing functionality directly

---

## Trust Level Definitions

### HIGH Trust
- Official GitHub actions
- Official vendor actions (Google, Microsoft, Aqua Security)
- Well-established projects with verified publishers

### MEDIUM-HIGH Trust
- Large community projects (>1000 stars)
- Verified publishers
- Active maintenance
- Clear security practices

### MEDIUM Trust
- Individual maintainers with good reputation
- Active projects (>500 stars)
- Widely used (>10k repositories)

### LOW Trust
- New actions (<1 year old)
- Small community (<100 stars)
- Infrequent updates
- Unknown maintainer

### DO NOT USE
- Actions from unknown sources
- Unmaintained projects (>6 months inactive)
- Actions with security issues
- Actions requiring excessive permissions

---

## Security Incident Response

If a supply chain compromise is suspected:

1. **Immediate**: Disable affected workflows
2. **Assess**: Review recent workflow runs for anomalies
3. **Update**: Pin to known-good commit SHA
4. **Audit**: Review all secrets accessed by compromised action
5. **Rotate**: Rotate all potentially exposed secrets
6. **Report**: File security advisory with GitHub
7. **Document**: Update incident log

---

## References

- [GitHub Actions Security Best Practices](https://docs.github.com/en/actions/security-guides/security-hardening-for-github-actions)
- [Dependabot for GitHub Actions](https://docs.github.com/en/code-security/dependabot/working-with-dependabot/keeping-your-actions-up-to-date-with-dependabot)
- [OSSF Scorecard](https://github.com/ossf/scorecard)
- [Supply Chain Levels for Software Artifacts (SLSA)](https://slsa.dev/)

---

## Maintenance Schedule

- **Weekly**: Review Dependabot PRs
- **Monthly**: Check for critical security updates
- **Quarterly**: Full audit of all actions
- **Annually**: Review and update this document

---

**Next Review Date**: 2026-01-25
