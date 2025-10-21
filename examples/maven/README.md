# Maven Workflow Examples

Example GitHub Actions workflows for Maven projects using Artagon's release branch strategy.

## Quick Start

Copy the desired workflow files to your project's `.github/workflows/` directory and customize as needed.

## Available Workflows

### [ci.yml](ci.yml)
**Purpose:** Continuous Integration

Runs on every push and pull request to main and release branches.

**Features:**
- Build and test with Maven
- Security scanning (dependency check, OSS Index, Trivy)
- Caching for faster builds

**Usage:**
```bash
cp ci.yml <your-project>/.github/workflows/
```

### [snapshot-deploy.yml](snapshot-deploy.yml)
**Purpose:** Deploy SNAPSHOT versions to OSSRH

Automatically deploys SNAPSHOT versions from main branch.

**Features:**
- Deploys to OSSRH snapshots repository
- GPG signing
- Runs tests before deployment

**Required Secrets:**
- `OSSRH_USERNAME`
- `OSSRH_PASSWORD`
- `GPG_PRIVATE_KEY`
- `GPG_PASSPHRASE`

### [release.yml](release.yml)
**Purpose:** Create releases using release branch strategy

**IMPORTANT:** Must be run from a `release-*` branch.

**Release Process:**
1. Ensure main is at next SNAPSHOT (e.g., `1.0.9-SNAPSHOT`)
2. Create release branch: `git checkout -b release-1.0.8 <commit-at-1.0.8-SNAPSHOT>`
3. Push branch: `git push origin release-1.0.8`
4. Run this workflow from release-1.0.8 branch
5. Workflow removes `-SNAPSHOT` and creates `v1.0.8` tag

**Features:**
- Auto-detects version from SNAPSHOT
- Validates release branch
- Deploys to Maven Central
- Creates GitHub release

### [release-branch.yml](release-branch.yml)
**Purpose:** Validate release branches

Runs automatically when pushing to `release-*` branches.

**Features:**
- Validates release branch state
- Optional staging deployment
- Pre-release checks

### [release-tag.yml](release-tag.yml)
**Purpose:** Publish releases from git tags

Alternative release method using tags instead of workflow dispatch.

**Usage:**
```bash
git tag -a v1.0.8 -m "Release 1.0.8"
git push origin v1.0.8
```

## Release Branch Strategy

Artagon Maven projects use a **release branch strategy**:

- **`main` branch**: Always has SNAPSHOT versions (e.g., `1.0.9-SNAPSHOT`)
- **`release-X.Y.Z` branches**: Have release versions without SNAPSHOT (e.g., `1.0.8`)
- **Tags**: Created on release branches (e.g., `v1.0.8`)

### Example Workflow

```bash
# 1. Ensure main is at next SNAPSHOT
git checkout main
mvn versions:set -DnewVersion=1.0.9-SNAPSHOT -DgenerateBackupPoms=false
git commit -am "chore: bump main to 1.0.9-SNAPSHOT"
git push origin main

# 2. Create release branch from commit at desired SNAPSHOT
git checkout -b release-1.0.8 <commit-at-1.0.8-SNAPSHOT>
git push origin release-1.0.8

# 3. Trigger release workflow from GitHub UI
# (Actions → Release → Run workflow → select release-1.0.8 branch)

# 4. Result:
# - main: 1.0.9-SNAPSHOT (unchanged)
# - release-1.0.8: 1.0.8 (frozen for hotfixes)
# - tag v1.0.8: created
```

## Required GitHub Secrets

Configure these secrets in your repository settings (Settings → Secrets and variables → Actions):

- `OSSRH_USERNAME` - Sonatype OSSRH username
- `OSSRH_PASSWORD` - Sonatype OSSRH password
- `GPG_PRIVATE_KEY` - GPG private key for artifact signing
- `GPG_PASSPHRASE` - GPG key passphrase

## Customization

### Java Version

Default is Java 21. To change:

```yaml
with:
  java-version: '17'  # Or 21, 25
```

### Security Scanning

To disable specific security scans:

```yaml
with:
  run-dependency-check: false
  run-ossindex-audit: false
  run-trivy-scan: false
```

### Skip Tests

Not recommended, but possible:

```yaml
with:
  skip-tests: true
```

## Documentation

For complete Maven/Java release strategy, see:
- **[Java Release Strategy](../../docs/RELEASE_JAVA.md)**
- **[General Release Process](../../RELEASE.md)**

## Support

For issues or questions:
- [artagon-workflows Issues](https://github.com/artagon/artagon-workflows/issues)
- [Release Documentation](../../docs/RELEASE_JAVA.md)
