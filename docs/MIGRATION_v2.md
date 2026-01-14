# Migration Guide: v1 to v2

**Breaking Changes:** Workflow file naming convention updated

This guide helps you migrate from v1 to v2 of artagon-workflows, which introduces a consistent build-system-first naming convention.

---

## Overview

### What Changed

We've standardized all workflow naming to follow a consistent pattern:
- **Format:** `<buildsystem>_[lang]_<category>.yml`
- **Delimiter:** Underscores (`_`) for all semantic separation
- **Build System First:** Language included when build system is multi-language

### Why This Change

1. **Consistency:** All workflows now follow the same naming pattern
2. **Clarity:** Build system context always clear (CMake, Bazel, Maven)
3. **Predictability:** Easy to guess file names
4. **Scalability:** Simple to add new languages and build systems

---

## Workflow Renames

### Complete Mapping

| Old Name (v1) | New Name (v2) | Type |
|---------------|---------------|------|
| `bazel-ci.yml` | `bazel_multi_ci.yml` | Multi-language |
| `bazel-release.yml` | `bazel_multi_release.yml` | Multi-language |
| `c-ci.yml` | `cmake_c_ci.yml` | Build system added |
| `c-release.yml` | `cmake_c_release.yml` | Build system added |
| `cpp-ci.yml` | `cmake_cpp_ci.yml` | Build system added |
| `cpp-release.yml` | `cmake_cpp_release.yml` | Build system added |
| `maven-ci.yml` | `maven_ci.yml` | Delimiter only |
| `maven-central-release.yml` | `maven_central_release.yml` | Delimiter only |
| `maven-github-release.yml` | `maven_github_release.yml` | Delimiter only |
| `update-submodule.yml` | `update_submodule.yml` | Delimiter only |

### Unchanged Workflows

These workflows already followed the new convention:
- `maven_build.yml`
- `maven_bump_version.yml`
- `maven_deploy.yml`
- `maven_release.yml`
- `maven_release_branch.yml`
- `maven_release_tag.yml`
- `maven_security_scan.yml`

---

## Example Directory Renames

| Old Path (v1) | New Path (v2) |
|---------------|---------------|
| `examples/c/` | `examples/cmake_c/` |
| `examples/cpp/` | `examples/cmake_cpp/` |
| `examples/bazel/` | `examples/bazel_multi/` |

**Unchanged:**
- `examples/maven/` (already correct)
- `examples/rust/` (no change)

---

## Migration Instructions

### For Repository Owners

#### Step 1: Update Workflow References

Find and replace in your `.github/workflows/` directory:

```bash
# C projects (CMake)
sed -i 's|c-ci.yml|cmake_c_ci.yml|g' .github/workflows/*.yml
sed -i 's|c-release.yml|cmake_c_release.yml|g' .github/workflows/*.yml

# C++ projects (CMake)
sed -i 's|cpp-ci.yml|cmake_cpp_ci.yml|g' .github/workflows/*.yml
sed -i 's|cpp-release.yml|cmake_cpp_release.yml|g' .github/workflows/*.yml

# Bazel projects
sed -i 's|bazel-ci.yml|bazel_multi_ci.yml|g' .github/workflows/*.yml
sed -i 's|bazel-release.yml|bazel_multi_release.yml|g' .github/workflows/*.yml

# Maven projects
sed -i 's|maven-ci.yml|maven_ci.yml|g' .github/workflows/*.yml
```

#### Step 2: Update Version Pin

Change your workflow version from `@v1` to `@v2`:

```yaml
# Before (v1)
uses: artagon/artagon-workflows/.github/workflows/c-ci.yml@v1

# After (v2)
uses: artagon/artagon-workflows/.github/workflows/cmake_c_ci.yml@v2
```

#### Step 3: Test Your Workflows

1. Create a pull request with the changes
2. Verify all workflows run successfully
3. Check for any broken references

### Quick Migration Examples

#### Maven Project

**Before:**
```yaml
# .github/workflows/ci.yml
jobs:
  ci:
    uses: artagon/artagon-workflows/.github/workflows/maven-ci.yml@v1
```

**After:**
```yaml
# .github/workflows/ci.yml
jobs:
  ci:
    uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@v2
```

#### C Project

**Before:**
```yaml
# .github/workflows/ci.yml
jobs:
  ci:
    uses: artagon/artagon-workflows/.github/workflows/c-ci.yml@v1
```

**After:**
```yaml
# .github/workflows/ci.yml
jobs:
  ci:
    uses: artagon/artagon-workflows/.github/workflows/cmake_c_ci.yml@v2
```

#### C++ Project

**Before:**
```yaml
# .github/workflows/ci.yml
jobs:
  ci:
    uses: artagon/artagon-workflows/.github/workflows/cpp-ci.yml@v1
```

**After:**
```yaml
# .github/workflows/ci.yml
jobs:
  ci:
    uses: artagon/artagon-workflows/.github/workflows/cmake_cpp_ci.yml@v2
```

#### Bazel Project

**Before:**
```yaml
# .github/workflows/ci.yml
jobs:
  ci:
    uses: artagon/artagon-workflows/.github/workflows/bazel-ci.yml@v1
```

**After:**
```yaml
# .github/workflows/ci.yml
jobs:
  ci:
    uses: artagon/artagon-workflows/.github/workflows/bazel_multi_ci.yml@v2
```

---

## Compatibility

### v1 Support Timeline

- **v1 workflows:** ❌ Removed in v2.0.0
- **Migration period:** N/A (breaking change)
- **Recommendation:** Migrate immediately to v2

### Staying on v1

If you cannot migrate immediately, pin to v1:

```yaml
# Pin to v1 (old names)
uses: artagon/artagon-workflows/.github/workflows/c-ci.yml@v1

# This will continue to work but won't receive updates
```

**Warning:** v1 will not receive bug fixes or new features.

---

## Testing Your Migration

### Automated Testing

Create a test PR to verify changes:

```bash
# Create test branch
git checkout -b test/migrate-to-v2

# Update workflow references
sed -i 's/@v1/@v2/g' .github/workflows/*.yml
sed -i 's|c-ci.yml|cmake_c_ci.yml|g' .github/workflows/*.yml
sed -i 's|cpp-ci.yml|cmake_cpp_ci.yml|g' .github/workflows/*.yml
# ... (other replacements)

# Commit and push
git add .github/workflows/
git commit -m "test: migrate to artagon-workflows v2"
git push -u origin test/migrate-to-v2

# Create PR and verify all checks pass
gh pr create --title "Migrate to artagon-workflows v2" --body "Testing v2 migration"
```

### Manual Verification

1. **Check workflow runs:** All workflows should complete successfully
2. **Verify artifacts:** Builds produce expected outputs
3. **Test releases:** Create a test release if applicable
4. **Review logs:** No errors about missing workflows

---

## Common Issues

### Issue: "Workflow not found"

**Error:**
```
Unable to resolve action `artagon/artagon-workflows/.github/workflows/c-ci.yml@v2`
```

**Solution:**
You're using the old workflow name. Update to new name:
```yaml
# Wrong
uses: artagon/artagon-workflows/.github/workflows/c-ci.yml@v2

# Correct
uses: artagon/artagon-workflows/.github/workflows/cmake_c_ci.yml@v2
```

### Issue: "Invalid workflow reference"

**Error:**
```
The workflow 'artagon/artagon-workflows/.github/workflows/maven-ci.yml@v2' is not valid
```

**Solution:**
Use underscore instead of hyphen:
```yaml
# Wrong
uses: artagon/artagon-workflows/.github/workflows/maven-ci.yml@v2

# Correct
uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@v2
```

### Issue: Workflow works locally but fails in CI

**Cause:** Local files cached with old names

**Solution:**
```bash
# Clear GitHub Actions cache
gh cache delete --all

# Re-run workflow
gh run rerun <run-id>
```

---

## Migration Checklist

Use this checklist to track your migration:

### Pre-Migration

- [ ] Review this migration guide
- [ ] Identify all workflows using artagon-workflows
- [ ] Create backup branch
- [ ] Plan testing approach

### Migration

- [ ] Update C workflow references (`c-ci.yml` → `cmake_c_ci.yml`)
- [ ] Update C++ workflow references (`cpp-ci.yml` → `cmake_cpp_ci.yml`)
- [ ] Update Bazel workflow references (`bazel-ci.yml` → `bazel_multi_ci.yml`)
- [ ] Update Maven workflow references (`maven-ci.yml` → `maven_ci.yml`)
- [ ] Change version pins from `@v1` to `@v2`
- [ ] Update any local copies of example workflows

### Testing

- [ ] Create test PR
- [ ] Verify CI workflows run successfully
- [ ] Test build outputs
- [ ] Test release process (if applicable)
- [ ] Review workflow logs for errors

### Post-Migration

- [ ] Merge migration PR
- [ ] Monitor production workflows
- [ ] Update team documentation
- [ ] Archive v1 branch (if applicable)

---

## Getting Help

### Resources

- **Naming Conventions:** [docs/NAMING_CONVENTIONS.md](docs/NAMING_CONVENTIONS.md)
- **Examples:** [examples/](examples/)
- **Release Guides:** [docs/RELEASE_*.md](docs/)

### Support

- **Issues:** https://github.com/artagon/artagon-workflows/issues
- **Discussions:** https://github.com/artagon/artagon-workflows/discussions

### Reporting Migration Problems

If you encounter issues:

1. Check this guide first
2. Review [NAMING_CONVENTIONS.md](docs/NAMING_CONVENTIONS.md)
3. Search existing issues
4. Create new issue with:
   - Old workflow configuration
   - New configuration attempt
   - Error messages
   - Workflow run logs

---

## Summary

### Key Changes

✅ **Renamed Workflows:** Consistent `buildsystem_lang_category.yml` pattern
✅ **Renamed Examples:** Match workflow naming convention
✅ **Improved Clarity:** Build system always explicit for multi-language tools
✅ **Better Scalability:** Easy to add new languages and build systems

### Migration Steps

1. Find and replace old workflow names
2. Update version pins from `@v1` to `@v2`
3. Test in PR before merging
4. Monitor after deployment

### Timeline

- **v2.0.0 Released:** Breaking changes introduced
- **v1 Support:** Discontinued
- **Migration Required:** Immediately for continued support

---

## Changelog: v1 → v2

### Breaking Changes

- 🔴 Renamed `bazel-ci.yml` → `bazel_multi_ci.yml`
- 🔴 Renamed `bazel-release.yml` → `bazel_multi_release.yml`
- 🔴 Renamed `c-ci.yml` → `cmake_c_ci.yml`
- 🔴 Renamed `c-release.yml` → `cmake_c_release.yml`
- 🔴 Renamed `cpp-ci.yml` → `cmake_cpp_ci.yml`
- 🔴 Renamed `cpp-release.yml` → `cmake_cpp_release.yml`
- 🔴 Renamed `maven-ci.yml` → `maven_ci.yml`
- 🔴 Renamed `maven-central-release.yml` → `maven_central_release.yml`
- 🔴 Renamed `maven-github-release.yml` → `maven_github_release.yml`
- 🔴 Renamed `update-submodule.yml` → `update_submodule.yml`
- 🔴 Renamed example directories to match workflow naming

### New Features

- ✨ Consistent naming convention across all workflows
- ✨ Build system context always clear
- ✨ Improved documentation structure
- ✨ Language-specific release strategy guides

### Documentation

- 📝 Added [NAMING_CONVENTIONS.md](docs/NAMING_CONVENTIONS.md)
- 📝 Added [RELEASE_JAVA.md](docs/RELEASE_JAVA.md)
- 📝 Added [RELEASE_C.md](docs/RELEASE_C.md)
- 📝 Added [RELEASE_CPP.md](docs/RELEASE_CPP.md)
- 📝 Added [RELEASE_RUST.md](docs/RELEASE_RUST.md)
- 📝 Updated all examples with new naming
- 📝 Created this migration guide

---

**Questions?** See [docs/NAMING_CONVENTIONS.md](docs/NAMING_CONVENTIONS.md) or open an issue.
