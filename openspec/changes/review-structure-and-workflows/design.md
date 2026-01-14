# Design: Review Structure and Workflows

## Audit Findings (2026-01-14)

### P0 - Critical Issues

| Workflow | Issue | Status |
|----------|-------|--------|
| `maven_github_release.yml:121-126` | Secret passed on CLI (`-Dtoken=${{ secrets.GITHUB_TOKEN }}`) | ❌ OPEN |
| `cmake_cpack_release.yml:120-126` | Secret passed on CLI (`echo "${{ secrets.gpg-private-key }}"`) | ❌ OPEN |
| `maven_sbom_generate.yml:199` | `sigstore/cosign-installer@v4.0.0` unpinned | ✅ FIXED |

### P1 - High Issues (Security)

| Area | Issue | Status |
|------|-------|--------|
| Permissions | Jobs missing explicit `permissions:` in `bazel_multi_release.yml` (`create-release`, `build-linux`, `build-macos`, `build-windows`, `build-source`) | ❌ OPEN |
| Input validation | User inputs used in shell without validation (`update_submodule.yml`, `bazel_multi_release.yml`, `maven_deploy.yml`, `maven_github_release.yml`, `maven_sbom_generate.yml`, `cmake_c_release.yml`, `cmake_cpp_release.yml`, `cmake_cpack_release.yml`) | ❌ OPEN |
| Binary verification | Downloads without checksums (`maven_sbom_generate.yml` CycloneDX CLI, `test_lint.yml` actionlint) | ❌ OPEN |

### P2 - Medium Issues

| Issue | Files Affected | Status |
|-------|----------------|--------|
| linuxdeploy checksum sourced from `continuous` (not release-pinned) | `cmake_c_release.yml`, `cmake_cpp_release.yml` | ❌ OPEN |
| Security validation does not enforce per-job permissions or input validation | `test_security.yml` | ❌ OPEN |

### Action Version Updates Recommended

| Action | Current | Latest Stable | Status |
|--------|---------|---------------|--------|
| actions/checkout | v4.2.2 | v4.2.2 (v6 pre-release) | ✅ Current |
| actions/setup-java | v4.x | v5.1.0 | ⚠️ Update available |
| actions/upload-artifact | v4.x | v6.0.0 | ⚠️ Update available |
| actions/cache | v4.x | v4.2.0 | ✅ Current |
| softprops/action-gh-release | v2.5.0 | v2.5.0 | ✅ UPDATED |
| sigstore/cosign-installer | v4.0.0 | v4.0.0 | ✅ STANDARDIZED |

### Consistency Issues (Non-blocking)

| Issue | Files Affected | Status |
|-------|----------------|--------|
| Multiple versions of softprops/action-gh-release | 9 workflows | ✅ FIXED - All now use v2.5.0 |
| Multiple versions of sigstore/cosign-installer | 3 workflows | ✅ FIXED - All now use v4.0.0 |

---

## Fixes Applied (2026-01-14)

### sigstore/cosign-installer standardized to v4.0.0
- **SHA**: `faadad0cce49287aee09b3a48701e75088a2c6ad`
- **Files updated**:
  - `maven_sbom_generate.yml` (was unpinned v4.0.0)
  - `gradle_release.yml` (was `dc72c7d5c4d10cd6bcb8cf6e3fd625a9e5e537da`)
  - `release.yml` (was v3 `f713795cb21599bc4e5c4b58cbad1da852d7eeb9`)

### softprops/action-gh-release standardized to v2.5.0
- **SHA**: `a06a81a03ee405af7f2048a818ed3f03bbf83c7b`
- **Files updated**:
  - `bazel_multi_release.yml` (6 occurrences)
  - `maven_release.yml`
  - `cmake_cpp_release.yml` (7 occurrences)
  - `maven_central_release.yml`
  - `cmake_c_release.yml` (7 occurrences)
  - `maven_github_release.yml`
  - `cmake_cpack_release.yml` (was v2.0.8)
  - `release.yml` (was v2)
  - `gradle_release.yml`

---

## Action Inventory

### Most Used Actions (by count)

| Action | Count | SHA |
|--------|-------|-----|
| actions/checkout | 65 | 11bd71901bbe5b1630ceea73d27597364c9af683 |
| softprops/action-gh-release | 26 | a06a81a03ee405af7f2048a818ed3f03bbf83c7b |
| actions/upload-artifact | 21 | b4b15b8c7c6ac21ea08fcf65892d2ee8f75cf882 |
| actions/setup-java | 13 | 8df1039502a15bceb9433410b1a100fbe190c53b |
| cachix/install-nix-action | 8 | 08dcb3a5e62fa31e2eb71ca1c1a4ae1b42a8f4b4 |
| bazelbuild/setup-bazelisk | 8 | b63ef97e907d9f0dc8c6f99a88a89f63d533addc |
| actions/cache | 7 | 1bd1e32a3bdc45362d1e726936510720a7c30a57 |

### Actions with Version Inconsistencies

No multi-SHA usage detected for softprops/action-gh-release or sigstore/cosign-installer in the current scan.

---

## Security Compliance Summary

- **Permissions**: `bazel_multi_release.yml` missing explicit job permissions (see P1).
- **Input validation**: Multiple workflows interpolate `inputs.*` directly into shell commands without allowlist checks (see P1).
- **Secret handling**: Secrets appear in command-line arguments in two workflows (see P0).
- **Binary verification**: At least two downloads lack checksum verification (see P1).
- **Action pinning**: No unpinned `uses:` entries found in current scan.

---

## Recommended Actions

### Immediate (P0)

1. Remove secrets from CLI arguments in `maven_github_release.yml` and `cmake_cpack_release.yml` (use env + config files).
2. Add explicit `permissions:` blocks for all jobs in `bazel_multi_release.yml`.

### Short-term (P1)

1. Add input validation steps before shell usage in workflows listed in P1.
2. Add checksum verification for CycloneDX CLI (`maven_sbom_generate.yml`) and actionlint (`test_lint.yml`).
3. Pin linuxdeploy checksum to the same release as the binary download.
4. Update actions/setup-java to v5.1.0 and actions/upload-artifact to v6.0.0.

### Medium-term (P2)

1. Strengthen `test_security.yml` to enforce per-job permissions and input validation checks.
2. Introduce reusable composite actions: `validate-inputs`, `download-verified`, `maven-settings`, `release-metadata`.
3. Schedule quarterly action version reviews and document action versions.

---

## Methodology

### Action Version Audit Process

1. Extract all `uses:` statements from workflows
2. Parse action name and current SHA/version
3. Query GitHub API for latest release
4. Compare and flag outdated actions

### Commands Used

```bash
# Find all action uses
grep -rh "uses:" .github/workflows/*.yml | sort | uniq -c

# Find unpinned actions
grep -rn "uses:.*@v[0-9]" .github/workflows/*.yml | grep -v "# v"

# Get latest release
gh release list --repo <owner>/<repo> --limit 1

# Get SHA for tag
gh api repos/<owner>/<repo>/git/ref/tags/<tag> --jq '.object.sha'
```

---

## Workflow Structure Review (Task 3)

### Workflow Inventory

**Total workflows:** 31
**Reusable workflows (workflow_call):** 27
**Internal/utility workflows:** 4 (`copilot-setup-steps.yml`, `test_lint.yml`, `test_security.yml`, `release.yml`)

### Naming Convention Analysis

| Convention | Count | Status |
|------------|-------|--------|
| `maven_*.yml` | 10 | ✅ Consistent |
| `cmake_*.yml` | 5 | ✅ Consistent |
| `bazel_*.yml` | 2 | ✅ Consistent |
| `gradle_*.yml` | 2 | ✅ Consistent |
| Other snake_case | 11 | ✅ Consistent |
| **Kebab-case** | 1 | ⚠️ `copilot-setup-steps.yml` |

**Finding:** `copilot-setup-steps.yml` uses kebab-case while all other workflows use snake_case.

### Documentation Completeness

**Reusable workflows with usage docs:** 17/27 (63%)

**Missing usage documentation:**
- `gradle_build.yml`
- `gradle_release.yml`
- `maven_build.yml`
- `maven_bump_version.yml`
- `maven_deploy.yml`
- `maven_release_branch.yml`
- `maven_release_tag.yml`
- `maven_release.yml`
- `maven_sbom_generate.yml`
- `maven_security_scan.yml`

### Deprecated Patterns Check

- ✅ No deprecated `set-output` or `save-state` commands
- ✅ No old artifact action versions (v1-v3)
- ✅ All actions properly pinned to SHAs

---

## OpenSpec Accuracy Review (Task 4)

### workflow-security spec vs implementation

| Requirement | Spec | Implementation | Gap |
|-------------|------|----------------|-----|
| Action Pinning | All SHAs + comments | ✅ Compliant | None |
| Explicit Permissions | All jobs | ❌ `bazel_multi_release.yml` missing | P1 |
| Input Validation | Allowlist patterns | ❌ 8 workflows missing | P1 |
| Secret Handling | No CLI args | ❌ 2 workflows violate | P0 |
| Binary Verification | SHA256 checksums | ❌ 2 downloads missing | P1 |
| Naming Convention | `{system}_{category}.yml` | ⚠️ 1 exception | Minor |
| Dependabot | Weekly updates | ✅ Configured | None |
| Security Validation | actionlint + checks | ⚠️ Partial | P2 |

### maven-workflows spec vs implementation

| Workflow | Spec | Implementation | Gap |
|----------|------|----------------|-----|
| maven_ci.yml | Java 21 default | ✅ Java 25 default | Updated |
| maven_deploy.yml | Env var credentials | ✅ Compliant | None |
| maven_release.yml | GPG via settings.xml | ✅ Compliant | None |
| maven_central_release.yml | SBOM + attestation | ✅ Compliant | None |
| maven_security_scan.yml | SARIF upload | ✅ Compliant | None |
| maven_sbom_generate.yml | CycloneDX generation | ✅ Compliant | None |

### cmake-workflows spec vs implementation

| Workflow | Spec | Implementation | Gap |
|----------|------|----------------|-----|
| cmake_c_ci.yml | Multi-platform | ✅ Linux/macOS/Windows | None |
| cmake_cpp_ci.yml | C++ standards | ✅ 11-23 supported | None |
| cmake_c_release.yml | Tarballs + zips | ✅ Compliant | None |
| cmake_cpp_release.yml | Multi-platform | ✅ Compliant | None |
| cmake_cpack_release.yml | DEB/RPM/TGZ | ✅ Compliant | None |

### bazel-workflows spec vs implementation

| Workflow | Spec | Implementation | Gap |
|----------|------|----------------|-----|
| bazel_multi_ci.yml | Build + test | ✅ Compliant | None |
| bazel_multi_release.yml | Multi-target | ✅ Compliant | None |
| Buildifier integration | SHA verification | ⚠️ Not verified | Minor |

---

## Pattern Consistency Review (Task 5)

### Caching Strategies

**Workflows using `actions/cache`:** 6/31

| Workflow | Cache Target |
|----------|--------------|
| `maven_ci.yml` | Maven ~/.m2 |
| `maven_build.yml` | Maven ~/.m2 |
| `maven_central_release.yml` | Maven ~/.m2 |
| `maven_github_release.yml` | Maven ~/.m2 |
| `gradle_build.yml` | Gradle ~/.gradle |
| `bazel_multi_ci.yml` | Bazel cache |

**Missing caching opportunities:**
- CMake builds (could cache build directories)
- Nix builds (could cache /nix/store)

### Error Handling Patterns

| Pattern | Usage | Recommendation |
|---------|-------|----------------|
| `set -euo pipefail` | Inconsistent | Standardize |
| Exit code checking | Some workflows | Standardize |
| Error messages | Inconsistent | Use consistent format |

### Input/Output Patterns

| Pattern | Count | Notes |
|---------|-------|-------|
| `workflow_call` inputs | 27 workflows | Well structured |
| `workflow_call` outputs | ~15 workflows | Could add more |
| `GITHUB_OUTPUT` usage | Consistent | ✅ Modern syntax |

---

## Documentation Review (Task 6)

### docs/ Folder Inventory

| File | Purpose | Status |
|------|---------|--------|
| `MAVEN.md` | Maven workflow usage | ✅ Current |
| `CPP.md` | C++ workflow usage | ✅ Current |
| `BAZEL.md` | Bazel workflow usage | ✅ Current |
| `RELEASE.md` | Release process | ✅ Current |
| `RELEASE_JAVA.md` | Java release guide | ✅ Current |
| `RELEASE_C.md` | C release guide | ✅ Current |
| `RELEASE_CPP.md` | C++ release guide | ✅ Current |
| `RELEASE_RUST.md` | Rust release guide | ⚠️ No Rust workflows |
| `CPACK_PACKAGING.md` | CPack usage | ✅ Current |
| `NAMING_CONVENTIONS.md` | File naming | ✅ Current |
| `OSS_RELEASE_STRATEGIES.md` | OSS patterns | ✅ Current |
| `WORKFLOWS_USAGE.md` | General usage | ✅ Current |
| `MIGRATION_v2.md` | v2 migration | ✅ Current |
| `TESTING.md` | Testing guide | ✅ Current |

### README.md Review

- ✅ Badges current
- ✅ Quick start examples
- ✅ Project structure documented
- ✅ Build system matrix
- ⚠️ References `@v1` tag (should verify exists)

### Missing Documentation

- Gradle workflows (no `GRADLE.md`)
- Security best practices guide
- Troubleshooting guide

---

## Final Recommendations Summary

### Create Issues For:

1. **P0-secrets-cli** - Remove secrets from CLI in 2 workflows
2. **P1-permissions** - Add permissions to bazel_multi_release.yml
3. **P1-input-validation** - Add input validation to 8 workflows
4. **P1-checksums** - Add checksums to binary downloads
5. **P2-docs-missing** - Add usage docs to 10 workflows
6. **P2-gradle-docs** - Create GRADLE.md documentation
7. **P2-naming** - Rename copilot-setup-steps.yml to snake_case
