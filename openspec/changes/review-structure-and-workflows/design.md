# Design: Review Structure and Workflows

## Audit Findings (2026-01-14)

### P0 - Critical Issues

| Workflow | Issue | Status |
|----------|-------|--------|
| `maven_sbom_generate.yml:199` | `sigstore/cosign-installer@v4.0.0` unpinned | ✅ FIXED |

### P1 - Action Version Updates Recommended

| Action | Current | Latest Stable | Status |
|--------|---------|---------------|--------|
| actions/checkout | v4.2.2 | v4.2.2 (v6 pre-release) | ✅ Current |
| actions/setup-java | v4.x | v5.1.0 | ⚠️ Update available |
| actions/upload-artifact | v4.x | v6.0.0 | ⚠️ Update available |
| actions/cache | v4.x | v4.2.0 | ✅ Current |
| softprops/action-gh-release | v2.5.0 | v2.5.0 | ✅ UPDATED |
| sigstore/cosign-installer | v4.0.0 | v4.0.0 | ✅ STANDARDIZED |

### P2 - Consistency Issues

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
| softprops/action-gh-release | 24 | da07017b40e28a1a0a35a90e201ca6e05c3dda96 |
| actions/upload-artifact | 21 | b4b15b8c7c6ac21ea08fcf65892d2ee8f75cf882 |
| actions/setup-java | 13 | 8df1039502a15bceb9433410b1a100fbe190c53b |
| cachix/install-nix-action | 8 | 08dcb3a5e62fa31e2eb71ca1c1a4ae1b42a8f4b4 |
| bazelbuild/setup-bazelisk | 8 | b63ef97e907d9f0dc8c6f99a88a89f63d533addc |
| actions/cache | 7 | 1bd1e32a3bdc45362d1e726936510720a7c30a57 |

### Actions with Version Inconsistencies

**softprops/action-gh-release** (3 different SHAs):
- `da07017b40e28a1a0a35a90e201ca6e05c3dda96` (24 uses)
- `c062e08bd532815e2082a85e87e3ef29c3e6d191` (1 use)
- `6da8fa9354ddfdc4aeace5fc48d7f679b5214090` (1 use)

**sigstore/cosign-installer** (3 different versions):
- `v4.0.0` (unpinned - CRITICAL)
- `f713795cb21599bc4e5c4b58cbad1da852d7eeb9`
- `dc72c7d5c4d10cd6bcb8cf6e3fd625a9e5e537da`

---

## Security Compliance

### Permissions Check

```bash
# Workflows missing permissions block
for f in .github/workflows/*.yml; do
  grep -q "permissions:" "$f" || echo "Missing: $f"
done
```

### Unpinned Actions

```bash
# Found 1 unpinned action
maven_sbom_generate.yml:199: sigstore/cosign-installer@v4.0.0
```

---

## Recommended Actions

### Immediate (P0)

1. **Fix unpinned cosign-installer**
   ```yaml
   # Before
   uses: sigstore/cosign-installer@v4.0.0

   # After (pin to latest v3.x SHA)
   # sigstore/cosign-installer@v3.10.1
   uses: sigstore/cosign-installer@<SHA>
   ```

### Short-term (P1)

1. Update actions/setup-java to v5.1.0
2. Update actions/upload-artifact to v6.0.0
3. Standardize softprops/action-gh-release version
4. Standardize sigstore/cosign-installer version

### Medium-term (P2)

1. Review all action versions quarterly
2. Set up Dependabot for action updates
3. Create action version documentation

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
