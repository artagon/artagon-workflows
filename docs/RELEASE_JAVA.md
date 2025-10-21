# Java Release Strategy

**Language:** Java
**Build Systems:** Maven, Gradle
**Package Registries:** Maven Central, GitHub Packages
**Based on:** Spring Framework, Apache Maven, Apache Kafka patterns

---

## Table of Contents

- [Release Strategy Overview](#release-strategy-overview)
- [Branching Model](#branching-model)
- [Version Management](#version-management)
- [Release Process](#release-process)
- [Hotfix Process](#hotfix-process)
- [Best Practices](#best-practices)
- [Automation](#automation)
- [Troubleshooting](#troubleshooting)

---

## Release Strategy Overview

Artagon Java projects use a **release branch strategy** with SNAPSHOT versioning, aligning with industry standards from Apache Maven, Spring Framework, and the broader Java ecosystem.

### Core Principles

- ✅ **`main` branch**: Always has SNAPSHOT versions (e.g., `1.0.9-SNAPSHOT`)
- ✅ **`release-X.Y.Z` branches**: Have release versions without SNAPSHOT (e.g., `1.0.8`)
- ✅ **Tags**: Created on release branches (e.g., `v1.0.8`)
- ✅ **Hotfixes**: Applied to release branches, cherry-picked to main if needed
- ✅ **Semantic Versioning**: MAJOR.MINOR.PATCH

### Why This Strategy?

**Java Ecosystem Standards:**
- SNAPSHOT suffix is Maven/Gradle convention for development versions
- Maven Central requires non-SNAPSHOT versions for releases
- Allows parallel development and release stabilization

**Industry Alignment:**
- **Spring Framework**: Fix-and-forward-merge with maintenance branches
- **Apache Maven**: Weekly releases from master (trunk-based)
- **Apache Kafka**: Time-based releases with feature freeze

---

## Branching Model

### Branch Types

#### Main Branch
```
main: 1.0.9-SNAPSHOT
  ↓
  Always SNAPSHOT
  Continuous development
  Never released directly
```

**Purpose:** Active development, next version preparation

**Rules:**
- Version MUST end with `-SNAPSHOT`
- All tests must pass before merge
- No direct commits (use PRs)
- Feature flags for incomplete work

#### Release Branches
```
release-1.0.8: 1.0.8-SNAPSHOT → 1.0.8
  ↓
  Stabilization only
  Tagged for release
  Kept for hotfixes
```

**Purpose:** Release stabilization and long-term maintenance

**Rules:**
- Created from main at SNAPSHOT version
- SNAPSHOT removed during release process
- Stays at release version permanently
- Never deleted (needed for hotfixes)

**Naming:** `release-MAJOR.MINOR.PATCH`

#### Feature Branches (Optional)
```
feat/123-add-authentication
  ↓
  Merged to main via PR
```

**Purpose:** Isolated feature development

---

## Version Management

### Semantic Versioning

Java projects MUST follow [Semantic Versioning 2.0.0](https://semver.org/):

```
MAJOR.MINOR.PATCH-SNAPSHOT

Examples:
- 1.0.0-SNAPSHOT  (development)
- 1.0.0           (release)
- 1.0.1-SNAPSHOT  (next patch development)
- 1.1.0-SNAPSHOT  (next minor development)
- 2.0.0-SNAPSHOT  (next major development)
```

### Version Bumping Rules

**Patch (1.0.X):**
- Bug fixes
- Security patches
- Documentation updates
- No API changes

**Minor (1.X.0):**
- New features (backward-compatible)
- New public API methods
- Deprecations (with migration path)
- Performance improvements

**Major (X.0.0):**
- Breaking API changes
- Removed deprecated features
- Architectural changes
- Requires user migration

### SNAPSHOT Convention

**Development versions:**
```xml
<version>1.0.9-SNAPSHOT</version>
```

**What SNAPSHOT means:**
- Not production-ready
- Can change at any time
- Published to snapshot repository
- Never promoted to Maven Central

**Release versions:**
```xml
<version>1.0.8</version>
```

**What release means:**
- Production-ready
- Immutable (never changes)
- Published to Maven Central
- Permanent reference

---

## Release Process

### Prerequisites

#### 1. Environment Setup

**Maven Settings** (`~/.m2/settings.xml`):
```xml
<settings>
  <servers>
    <server>
      <id>ossrh</id>
      <username>${env.OSSRH_USERNAME}</username>
      <password>${env.OSSRH_PASSWORD}</password>
    </server>
  </servers>

  <profiles>
    <profile>
      <id>ossrh</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <gpg.executable>gpg</gpg.executable>
        <gpg.passphrase>${env.GPG_PASSPHRASE}</gpg.passphrase>
      </properties>
    </profile>
  </profiles>
</settings>
```

**Required Secrets:**
- `OSSRH_USERNAME` - Sonatype OSSRH username
- `OSSRH_PASSWORD` - Sonatype OSSRH password
- `GPG_PRIVATE_KEY` - GPG private key for artifact signing
- `GPG_PASSPHRASE` - GPG key passphrase

#### 2. Pre-Release Checklist

- [ ] All tests pass: `mvn clean verify`
- [ ] No SNAPSHOT dependencies: `mvn dependency:tree | grep SNAPSHOT`
- [ ] POM metadata complete (name, description, licenses, developers, scm)
- [ ] License files present
- [ ] Security checksums updated
- [ ] Documentation up to date
- [ ] CHANGELOG.md updated
- [ ] Main branch is at next SNAPSHOT version

### Option 1: GitHub Actions Workflow (Recommended)

#### Step 1: Prepare Main Branch

Ensure main is at the **next** SNAPSHOT version:

```bash
git checkout main
mvn help:evaluate -Dexpression=project.version -q -DforceStdout
# Should show: 1.0.9-SNAPSHOT (next version after release)
```

If not at next SNAPSHOT:
```bash
mvn versions:set -DnewVersion=1.0.9-SNAPSHOT -DgenerateBackupPoms=false
git add .
git commit -m "chore: bump main to 1.0.9-SNAPSHOT"
git push origin main
```

#### Step 2: Create Release Branch

From a commit where version is at the SNAPSHOT you want to release:

```bash
# Example: Releasing 1.0.8 from commit at 1.0.8-SNAPSHOT
git checkout -b release-1.0.8 <commit-sha-at-1.0.8-SNAPSHOT>
git push origin release-1.0.8
```

**Critical:** The release branch MUST have a SNAPSHOT version when created.

#### Step 3: Trigger Release Workflow

```
GitHub → Actions → Maven Release → Run workflow
  Branch: release-1.0.8
```

The workflow automatically:
1. ✅ Validates release branch name
2. ✅ Validates SNAPSHOT version
3. ✅ Removes `-SNAPSHOT` suffix
4. ✅ Builds and tests
5. ✅ Signs with GPG
6. ✅ Deploys to OSSRH staging
7. ✅ Creates tag `v1.0.8`
8. ✅ Pushes release branch and tag
9. ✅ Releases from Nexus staging
10. ✅ Creates GitHub release

#### Step 4: Verify Release

**Check GitHub:**
```
https://github.com/<org>/<repo>/releases/tag/v1.0.8
```

**Check Maven Central** (2-4 hours after release):
```
https://search.maven.org/artifact/org.artagon/<artifact-id>/1.0.8/jar
```

**Verify in your project:**
```xml
<dependency>
  <groupId>org.artagon</groupId>
  <artifactId>your-artifact</artifactId>
  <version>1.0.8</version>
</dependency>
```

### Option 2: Local Script

#### Step 1: Create Release Branch

```bash
git checkout -b release-1.0.8 <commit-at-1.0.8-SNAPSHOT>
```

#### Step 2: Run Release Script

```bash
./artagon-common/scripts/deploy/mvn_release.sh
```

The script performs the same validation and release steps as the workflow.

#### Step 3: Push Changes

```bash
git push origin release-1.0.8 --tags
```

#### Step 4: Release from Nexus

**Option A - Using script:**
```bash
./artagon-common/scripts/deploy/mvn_release_nexus.sh
```

**Option B - Manual:**
1. Visit https://s01.oss.sonatype.org/
2. Login with OSSRH credentials
3. Navigate to Staging Repositories
4. Find your staging repository
5. Click "Close" (validates artifacts)
6. Click "Release" (publishes to Maven Central)

#### Step 5: Create GitHub Release

```bash
gh release create v1.0.8 --generate-notes
```

---

## Hotfix Process

### When to Use Hotfixes

- Critical security vulnerabilities
- Production-breaking bugs
- Data corruption issues
- Severe performance regressions

### Hotfix Workflow

#### Step 1: Checkout Release Branch

```bash
git checkout release-1.0.8
git pull origin release-1.0.8
```

#### Step 2: Apply Fix

```bash
# Make your changes
vim src/main/java/...

# Commit
git add .
git commit -m "fix: resolve critical security vulnerability in AuthManager

CVE-2024-XXXXX: SQL injection in user authentication

Closes #456"
```

#### Step 3: Create Hotfix Tag

**For patch increment:**
```bash
# Update version 1.0.8 → 1.0.9
mvn versions:set -DnewVersion=1.0.9 -DgenerateBackupPoms=false
git add .
git commit -m "chore: bump to 1.0.9 for hotfix"
git tag -a v1.0.9 -m "Hotfix release 1.0.9"
```

**For patch suffix (alternative):**
```bash
# Keep 1.0.8, add suffix
git tag -a v1.0.8.1 -m "Security hotfix 1.0.8.1"
```

#### Step 4: Deploy Hotfix

```bash
mvn clean deploy -Possrh-deploy,artagon-oss-release
```

#### Step 5: Push Changes

```bash
git push origin release-1.0.8 --tags
```

#### Step 6: Cherry-pick to Main

```bash
git checkout main
git cherry-pick <hotfix-commit-sha>

# Resolve conflicts if any
git add .
git cherry-pick --continue

git push origin main
```

#### Step 7: Create GitHub Release

```bash
gh release create v1.0.9 \
  --title "Security Hotfix 1.0.9" \
  --notes "Critical security fix for CVE-2024-XXXXX

See commit <sha> for details."
```

---

## Best Practices

### 1. Development Workflow

**Feature Development:**
```bash
# Always develop on main or feature branches
git checkout main
git checkout -b feat/123-add-caching

# Develop with SNAPSHOT version
mvn clean install
# Version: 1.1.0-SNAPSHOT

# Merge to main when ready
```

**SNAPSHOT Publishing:**
```bash
# Publish to GitHub Packages for testing
mvn clean deploy -Possrh-deploy

# Available at:
# https://oss.sonatype.org/content/repositories/snapshots/
```

### 2. Release Timing

**Time-Based Releases (Recommended):**
- Weekly: Small libraries, fast iteration (like Apache Maven)
- Monthly: Medium projects, regular features (like Tokio)
- Quarterly: Large projects, stable releases (like Apache Kafka)

**Feature-Based Releases:**
- When major feature is complete
- Before breaking API changes
- For security updates

### 3. Version Support Policy

**Recommended Support Windows:**

**Option A - Rolling Support (Small Projects):**
- Latest version only
- Security fixes for previous version (90 days)

**Option B - Multiple Version Support (Medium Projects):**
- Latest minor of current major
- Latest minor of previous major
- Example: Support 1.9.x and 2.3.x

**Option C - LTS Model (Large Projects):**
- Standard releases: 1 year support
- LTS releases: 3-5 year support
- Example: 1.8 LTS (5 years), 1.9, 1.10, 2.0 LTS (5 years)

### 4. Dependency Management

**Avoid SNAPSHOT Dependencies in Releases:**
```bash
# Before release, check:
mvn dependency:tree | grep SNAPSHOT

# If found, either:
# 1. Wait for dependency to release
# 2. Vendor the dependency
# 3. Use a released version
```

**Bill of Materials (BOM):**
```xml
<!-- Artagon BOM for dependency management -->
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.artagon</groupId>
      <artifactId>artagon-bom</artifactId>
      <version>1.0.8</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

### 5. Testing Before Release

**Required Tests:**
```bash
# Unit tests
mvn test

# Integration tests
mvn verify

# Security scans
mvn dependency-check:check

# Code coverage (aim for 80%+)
mvn jacoco:report
```

**Test Matrix:**
- Java versions: 17, 21, 25
- Operating systems: Linux, macOS, Windows
- Database versions (if applicable)

### 6. Documentation

**Update Before Release:**
- [ ] README.md (version badges, examples)
- [ ] CHANGELOG.md (new features, fixes, breaking changes)
- [ ] Migration guides (for major versions)
- [ ] API documentation (Javadoc)
- [ ] Example projects

**CHANGELOG.md Format:**
```markdown
## [1.0.8] - 2025-10-21

### Added
- New caching mechanism for improved performance

### Changed
- Updated dependency versions for security

### Fixed
- Resolved NPE in UserManager (#456)

### Security
- Fixed SQL injection vulnerability (CVE-2024-XXXXX)
```

### 7. Security Practices

**Artifact Signing:**
```bash
# GPG sign all artifacts
mvn clean deploy -Possrh-deploy,artagon-oss-release
# Automatically signs with GPG
```

**Checksums:**
```bash
# SHA-256 checksums for verification
sha256sum target/*.jar
```

**Vulnerability Scanning:**
```bash
# Regular dependency scans
mvn dependency-check:check

# OWASP security baseline
./scripts/security/update-baseline.sh
```

---

## Automation

### GitHub Actions Workflows

**Continuous Integration:**
```yaml
# .github/workflows/ci.yml
name: CI

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  ci:
    uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@v1
    secrets: inherit
```

**SNAPSHOT Deployment:**
```yaml
# .github/workflows/snapshot.yml
name: Deploy Snapshot

on:
  push:
    branches: [main]

jobs:
  deploy:
    uses: artagon/artagon-workflows/.github/workflows/maven-deploy.yml@v1
    secrets: inherit
```

**Release:**
```yaml
# .github/workflows/release.yml
name: Release

on:
  workflow_dispatch:

jobs:
  release:
    uses: artagon/artagon-workflows/.github/workflows/maven-release.yml@v1
    secrets: inherit
```

### Maven Profiles

**Standard Profiles:**
```xml
<profiles>
  <!-- OSSRH Deployment -->
  <profile>
    <id>ossrh-deploy</id>
    <properties>
      <altDeploymentRepository>
        ossrh::default::https://s01.oss.sonatype.org/content/repositories/snapshots
      </altDeploymentRepository>
    </properties>
  </profile>

  <!-- OSS Release (GPG signing) -->
  <profile>
    <id>artagon-oss-release</id>
    <build>
      <plugins>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-gpg-plugin</artifactId>
          <executions>
            <execution>
              <goals>
                <goal>sign</goal>
              </goals>
            </execution>
          </executions>
        </plugin>
      </plugins>
    </build>
  </profile>
</profiles>
```

---

## Troubleshooting

### Common Issues

#### "Must run from release-* branch"

**Cause:** Attempting to release from main or other branch

**Solution:**
```bash
git checkout -b release-1.0.8 main
git push origin release-1.0.8
```

#### "Version must be SNAPSHOT"

**Cause:** Release branch doesn't have SNAPSHOT version

**Solution:** Release branch should be created from commit with SNAPSHOT version
```bash
# Find commit with SNAPSHOT version
git log --all --grep="1.0.8-SNAPSHOT"

# Create release branch from that commit
git checkout -b release-1.0.8 <commit-sha>
```

#### "GPG signing failed"

**Cause:** GPG key not configured or incorrect passphrase

**Solution:**
```bash
# List available keys
gpg --list-secret-keys

# Test signing
echo "test" | gpg --clearsign

# If no key, generate one
gpg --gen-key
gpg --keyserver keys.openpgp.org --send-keys <KEY_ID>
```

#### "OSSRH authentication failed"

**Cause:** Invalid credentials

**Solution:**
```bash
# Verify credentials
cat ~/.m2/settings.xml | grep ossrh

# Test credentials
curl -u username:password https://s01.oss.sonatype.org/service/local/status

# Use environment variables
export OSSRH_USERNAME="your-username"
export OSSRH_PASSWORD="your-password"
```

#### "SNAPSHOT dependencies found"

**Cause:** Release depends on SNAPSHOT versions

**Solution:**
```bash
# Find SNAPSHOT dependencies
mvn dependency:tree | grep SNAPSHOT

# Options:
# 1. Update to released version
# 2. Release the dependency first
# 3. Remove the dependency
```

#### "Nexus staging repository not found"

**Cause:** Deployment may have failed or repository already released

**Solution:**
```bash
# List all staging repositories
mvn nexus-staging:rc-list -Possrh-deploy

# Drop failed repository
mvn nexus-staging:drop -Possrh-deploy

# Re-deploy
mvn clean deploy -Possrh-deploy,artagon-oss-release
```

---

## References

### Standards
- [Semantic Versioning 2.0.0](https://semver.org/)
- [Maven Central Publishing Guide](https://central.sonatype.org/publish/)
- [Maven Release Plugin](https://maven.apache.org/maven-release/maven-release-plugin/)

### Industry Examples
- [Spring Framework Release Process](https://github.com/spring-projects/spring-framework/wiki/Release-Process)
- [Apache Maven Release Process](https://maven.apache.org/developers/release/maven-project-release-procedure.html)
- [Apache Kafka Release Process](https://kafka.apache.org/documentation/#releaseguide)

### Artagon Resources
- [Artagon Workflows Repository](https://github.com/artagon/artagon-workflows)
- [Artagon Common Scripts](https://github.com/artagon/artagon-common/tree/main/scripts)
- [Maven Central Releases](https://search.maven.org/search?q=g:org.artagon)

---

## Appendix: Quick Reference

### Version Bump Commands

```bash
# Patch version (1.0.8 → 1.0.9-SNAPSHOT)
mvn versions:set -DnewVersion=1.0.9-SNAPSHOT -DgenerateBackupPoms=false

# Minor version (1.0.8 → 1.1.0-SNAPSHOT)
mvn versions:set -DnewVersion=1.1.0-SNAPSHOT -DgenerateBackupPoms=false

# Major version (1.0.8 → 2.0.0-SNAPSHOT)
mvn versions:set -DnewVersion=2.0.0-SNAPSHOT -DgenerateBackupPoms=false
```

### Common Maven Commands

```bash
# Build without tests
mvn clean install -DskipTests

# Run tests only
mvn test

# Deploy SNAPSHOT
mvn clean deploy -Possrh-deploy

# Deploy release
mvn clean deploy -Possrh-deploy,artagon-oss-release

# Check for updates
mvn versions:display-dependency-updates

# Security scan
mvn dependency-check:check
```

### Git Commands

```bash
# Create release branch
git checkout -b release-1.0.8 <commit-sha>
git push origin release-1.0.8

# Create tag
git tag -a v1.0.8 -m "Release 1.0.8"
git push origin v1.0.8

# Cherry-pick hotfix
git checkout main
git cherry-pick <commit-sha>

# Delete tag (if needed)
git tag -d v1.0.8
git push origin :refs/tags/v1.0.8
```
