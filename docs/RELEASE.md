# Release Process

This document describes the Maven-specific release process for Artagon projects. For other languages, see the language-specific guides below.

## Language-Specific Release Guides

Choose the appropriate guide for your project:

### Java / Maven Projects
**[Java Release Strategy](RELEASE_JAVA.md)**

Comprehensive guide for Java projects covering:
- Maven Central publishing with GPG signing
- SNAPSHOT version management
- Release branch strategy
- OSSRH deployment and Nexus staging
- Multi-module projects
- Gradle integration

### C Projects
**[C Release Strategy](RELEASE_C.md)**

Complete guide for C projects covering:
- CMake and Autotools builds
- Tag-based releases
- ABI stability guidelines
- Source tarball creation
- **Linux package distribution** (DEB, RPM, Alpine APK, Arch PKGBUILD)
- Homebrew formulas for macOS
- Long-term maintenance branches

### C++ Projects
**[C++ Release Strategy](RELEASE_CPP.md)**

Comprehensive guide for C++ projects covering:
- CMake and Bazel builds
- ABI/API compatibility management
- PIMPL pattern for stability
- LTS (Long-Term Support) strategy
- **Package distribution** (Debian, Fedora, Arch, Alpine, Conan)
- vcpkg integration
- Multi-platform releases

### Rust Projects
**[Rust Release Strategy](RELEASE_RUST.md)**

Complete guide for Rust projects covering:
- Cargo workspace management
- crates.io publishing
- MSRV (Minimum Supported Rust Version) policy
- Semantic versioning for Rust
- Binary distribution
- Cross-compilation

---

## Quick Reference

| Language | Build System | Registry | Key Workflows |
|----------|-------------|----------|---------------|
| **Java** | Maven, Gradle | Maven Central | `maven_ci.yml`, `maven-central-release.yml` |
| **C** | CMake, Autotools | Source tarballs, system packages | `cmake_c_ci.yml`, `cmake_c_release.yml` |
| **C++** | CMake, Bazel | Source tarballs, system packages, Conan | `cmake_cpp_ci.yml`, `cmake_cpp_release.yml`, `bazel_multi_release.yml` |
| **Rust** | Cargo | crates.io | Custom workflows (TBD) |

---

# Maven Release Process

This section describes the Maven-specific release process. For comprehensive Java release strategy, see **[RELEASE_JAVA.md](docs/RELEASE_JAVA.md)**.

## Release Strategy

We use a **release branch strategy** where:

- ✅ **`main` branch**: Always has SNAPSHOT versions (e.g., `1.0.9-SNAPSHOT`)
- ✅ **`release-X.Y.Z` branches**: Have release versions without SNAPSHOT (e.g., `1.0.8`)
- ✅ **Tags**: Created on release branches (e.g., `v1.0.8`)
- ✅ **Hotfixes**: Applied to release branches and cherry-picked to main if needed

## Workflow Overview

```
main (1.0.9-SNAPSHOT)
  ↓
  Create release-1.0.8 branch from commit at 1.0.8-SNAPSHOT
  ↓
release-1.0.8 (1.0.8-SNAPSHOT → remove SNAPSHOT → 1.0.8)
  ↓
  Tag v1.0.8, deploy to Maven Central
  ↓
release-1.0.8 stays at 1.0.8 (for hotfixes)
main stays at 1.0.9-SNAPSHOT
```

## Prerequisites

Before releasing, ensure you have:

1. **Credentials configured**:
   - OSSRH username and password
   - GPG key for signing artifacts
   - GitHub personal access token (for automated releases)

2. **GitHub Secrets** (for workflow automation):
   ```
   OSSRH_USERNAME
   OSSRH_PASSWORD
   GPG_PRIVATE_KEY
   GPG_PASSPHRASE
   ```

3. **Clean repository**:
   ```bash
   git status  # Should show no uncommitted changes
   ```

## Release Process

### Option 1: Using GitHub Actions Workflow (Recommended)

#### Step 1: Prepare Main Branch

Ensure `main` is at the next SNAPSHOT version:

```bash
# On main branch
mvn help:evaluate -Dexpression=project.version -q -DforceStdout
# Should show: 1.0.9-SNAPSHOT (or whatever the next version should be)
```

If not, use the version bump workflow:

```yaml
# Trigger via GitHub UI: Actions → Bump Maven Version
# Inputs:
#   new-version: 1.0.9-SNAPSHOT
#   branch: main
```

#### Step 2: Create Release Branch

From the commit where the version is at the SNAPSHOT you want to release:

```bash
# Example: Releasing 1.0.8 from a commit at 1.0.8-SNAPSHOT
git checkout -b release-1.0.8 <commit-sha>
git push origin release-1.0.8
```

**Important**: The release branch must have a SNAPSHOT version when created.

#### Step 3: Trigger Release Workflow

Navigate to GitHub Actions and trigger the **Maven Release** workflow:

```
Actions → Maven Release (Reusable) → Run workflow
Branch: release-1.0.8
```

The workflow will:
1. ✅ Validate you're on a `release-*` branch
2. ✅ Validate version is SNAPSHOT
3. ✅ Remove `-SNAPSHOT` suffix to create release version
4. ✅ Build and deploy to OSSRH
5. ✅ Create git tag (`v1.0.8`)
6. ✅ Push release branch and tag
7. ✅ Automatically release from Nexus staging (if enabled)
8. ✅ Create GitHub release

#### Step 4: Verify Release

1. **Check GitHub**: Release should appear at `https://github.com/<org>/<repo>/releases`
2. **Check OSSRH**: https://s01.oss.sonatype.org/
3. **Wait for Maven Central sync**: 2-4 hours
4. **Verify on Maven Central**: https://search.maven.org/search?q=g:org.artagon

---

### Option 2: Using Local Script

#### Step 1: Prepare Main Branch

```bash
# Ensure main is at next SNAPSHOT
git checkout main
mvn versions:set -DnewVersion=1.0.9-SNAPSHOT -DgenerateBackupPoms=false
git add .
git commit -m "chore: bump main to 1.0.9-SNAPSHOT"
git push origin main
```

#### Step 2: Create Release Branch

```bash
# From commit at 1.0.8-SNAPSHOT
git checkout -b release-1.0.8 <commit-at-1.0.8-SNAPSHOT>
```

#### Step 3: Run Release Script

```bash
./artagon-common/scripts/deploy/mvn_release.sh
```

The script will:
1. ✅ Validate you're on a `release-*` branch
2. ✅ Validate version is SNAPSHOT
3. ✅ Remove `-SNAPSHOT` to create release version
4. ✅ Update checksums
5. ✅ Create commit and tag
6. ✅ Deploy to OSSRH

#### Step 4: Push Changes

```bash
git push origin release-1.0.8 --tags
```

#### Step 5: Release from Nexus

```bash
# Option A: Using script
./artagon-common/scripts/deploy/mvn_release_nexus.sh

# Option B: Manual
# Visit https://s01.oss.sonatype.org/
# Login → Staging Repositories → Close → Release
```

#### Step 6: Create GitHub Release

Navigate to: `https://github.com/<org>/<repo>/releases/new?tag=v1.0.8`

---

## Hotfix Process

### Apply Hotfix to Release Branch

```bash
# Checkout the release branch
git checkout release-1.0.8

# Make your fixes
git commit -m "fix: critical bug in feature X"

# Create hotfix tag (increment patch version or add suffix)
git tag -a v1.0.8.1 -m "Hotfix 1.0.8.1"

# Deploy
mvn clean deploy -Possrh-deploy,artagon-oss-release

# Push
git push origin release-1.0.8 --tags
```

### Cherry-pick to Main (if needed)

```bash
git checkout main
git cherry-pick <hotfix-commit-sha>
git push origin main
```

---

## Version Bump Process

### Bumping Main to Next SNAPSHOT

#### Using GitHub Actions:

```
Actions → Bump Maven Version
Inputs:
  new-version: 1.1.0-SNAPSHOT
  branch: main
```

#### Using Maven:

```bash
git checkout main
mvn versions:set -DnewVersion=1.1.0-SNAPSHOT -DgenerateBackupPoms=false
git add .
git commit -m "chore: bump main to 1.1.0-SNAPSHOT"
git push origin main
```

---

## Branch Management

### Release Branch Lifecycle

```
release-1.0.8: Created → Released → Kept for hotfixes
release-1.1.0: Created → Released → Kept for hotfixes
main: Continuous development with SNAPSHOT versions
```

**DO NOT DELETE** release branches - they're needed for hotfixes.

### When to Create Release Branches

Create release branches when:
- Feature development for next version is complete
- Ready to stabilize for release
- Need to isolate release work from ongoing development

**Timing Example**:
```
Week 1-4: Develop features on main (1.1.0-SNAPSHOT)
Week 5: Create release-1.1.0, main bumps to 1.2.0-SNAPSHOT
Week 5-6: Stabilize release-1.1.0, continue development on main
Week 6: Release 1.1.0 from release-1.1.0 branch
```

---

## Common Scenarios

### Scenario 1: Standard Release

```bash
# 1. Ensure main is at next SNAPSHOT
main: 1.0.9-SNAPSHOT

# 2. Create release branch from commit at 1.0.8-SNAPSHOT
git checkout -b release-1.0.8 <commit>
git push origin release-1.0.8

# 3. Run workflow or script (removes SNAPSHOT)
release-1.0.8: 1.0.8-SNAPSHOT → 1.0.8

# 4. Result
main: 1.0.9-SNAPSHOT (unchanged)
release-1.0.8: 1.0.8 (stays here)
tag v1.0.8: created
```

### Scenario 2: Major Version Release

```bash
# 1. Bump main to next major SNAPSHOT
git checkout main
mvn versions:set -DnewVersion=2.0.0-SNAPSHOT -DgenerateBackupPoms=false
git commit -am "chore: bump main to 2.0.0-SNAPSHOT"
git push origin main

# 2. Create release branch from commit at 1.5.0-SNAPSHOT
git checkout -b release-1.5.0 <commit-at-1.5.0-SNAPSHOT>
git push origin release-1.5.0

# 3. Release 1.5.0 from release branch
# Run workflow or script

# 4. Result
main: 2.0.0-SNAPSHOT (new major version)
release-1.5.0: 1.5.0 (final 1.x release)
```

### Scenario 3: Emergency Hotfix

```bash
# 1. Find the release branch
git checkout release-1.0.8

# 2. Fix the bug
git commit -m "fix: critical security issue"

# 3. Create hotfix tag
git tag -a v1.0.8.1 -m "Security hotfix"

# 4. Deploy
mvn clean deploy -Possrh-deploy,artagon-oss-release

# 5. Push
git push origin release-1.0.8 --tags

# 6. Cherry-pick to main if needed
git checkout main
git cherry-pick <fix-commit-sha>
git push origin main
```

---

## Rollback Procedures

### Before Pushing

If release script fails or you need to cancel:

```bash
# Undo last commit and remove tag
git reset --hard HEAD~1
git tag -d v1.0.8
```

### After Pushing (Not Recommended)

If you must rollback after pushing:

```bash
# Delete remote tag
git push origin :refs/tags/v1.0.8

# Force push branch to previous state
git reset --hard HEAD~1
git push origin release-1.0.8 --force

# Contact Sonatype to drop staging repository
```

**Warning**: Only rollback if artifacts haven't been released to Maven Central yet.

---

## Validation Checklist

Before releasing, verify:

- [ ] Main branch is at next SNAPSHOT version
- [ ] Release branch has SNAPSHOT version
- [ ] All tests pass: `mvn clean verify`
- [ ] No SNAPSHOT dependencies: `mvn dependency:tree | grep SNAPSHOT`
- [ ] Security checksums updated
- [ ] License files present
- [ ] POM metadata complete (name, description, licenses, developers, scm)
- [ ] GPG key available for signing
- [ ] OSSRH credentials configured

---

## Troubleshooting

### Error: "Must run from release-* branch"

**Cause**: Trying to release from `main` or other branch

**Solution**: Create release branch first:
```bash
git checkout -b release-X.Y.Z main
git push origin release-X.Y.Z
```

### Error: "Version must be SNAPSHOT"

**Cause**: Release branch has non-SNAPSHOT version

**Solution**: Release branch should start with SNAPSHOT version from main

### Error: "GPG signing failed"

**Cause**: GPG key not configured or passphrase incorrect

**Solution**:
```bash
# List keys
gpg --list-secret-keys

# Import key
gpg --import private-key.asc

# Test signing
echo "test" | gpg --clearsign
```

### Error: "OSSRH authentication failed"

**Cause**: Invalid credentials

**Solution**:
```bash
# Check ~/.m2/settings.xml
cat ~/.m2/settings.xml | grep ossrh

# Or use environment variables
export OSSRH_USERNAME="your-username"
export OSSRH_PASSWORD="your-password"
```

---

## FAQ

**Q: Why don't we use maven-release-plugin?**

A: We use a custom process because:
- Better suited for CI/CD workflows
- More control over multi-project releases
- Simpler integration with GitHub Actions
- Clearer git history

**Q: Can I release from main?**

A: No. Main must always have SNAPSHOT versions. Releases are only created from `release-*` branches.

**Q: What if I need to release an old version?**

A: Check out the release branch for that version and apply changes there. The branch still exists for this purpose.

**Q: Do release branches get deleted?**

A: No. Keep release branches for hotfixes.

**Q: How do I know what the next version should be?**

A: Follow semantic versioning:
- **Patch** (1.0.X): Bug fixes, security patches
- **Minor** (1.X.0): New features, backward-compatible
- **Major** (X.0.0): Breaking changes

**Q: Can I have multiple release branches?**

A: Yes! You can maintain multiple release lines simultaneously (e.g., `release-1.0.8`, `release-1.1.0`, `release-2.0.0`).

---

## References

- [Semantic Versioning](https://semver.org/)
- [Maven Central Publishing](https://central.sonatype.org/publish/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Artagon Workflows Repository](https://github.com/artagon/artagon-workflows)
