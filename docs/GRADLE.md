# Gradle Workflows

Reusable GitHub Actions workflows for Gradle projects.

## Available Workflows

### gradle_build.yml - Continuous Integration Build

Builds Gradle projects with configurable Java version and comprehensive security checks.

**Features:**
- Wrapper integrity verification
- Dependency verification
- Lockfile drift detection
- Configurable caching

**Usage:**
```yaml
name: Build
on:
  push:
    branches: [main]
  pull_request:

jobs:
  build:
    uses: artagon/artagon-workflows/.github/workflows/gradle_build.yml@main
    with:
      java-version: '21'
      run-tests: true
      verify-wrapper: true
      verify-dependencies: true
```

**Inputs:**

| Input | Description | Default |
|-------|-------------|---------|
| `java-version` | Java version to use | `25` |
| `java-distribution` | Java distribution (temurin, corretto, etc.) | `temurin` |
| `gradle-args` | Additional Gradle arguments | `''` |
| `run-tests` | Whether to run tests | `true` |
| `verify-wrapper` | Verify Gradle wrapper integrity | `true` |
| `verify-dependencies` | Enforce dependency verification | `true` |
| `check-lockfiles` | Check for lockfile drift | `true` |
| `wrapper-sha256` | Expected SHA-256 of gradle-wrapper.jar | `''` |
| `cache-key-prefix` | Cache key prefix | `gradle` |

### gradle_release.yml - Release with SBOM

Builds, tests, and releases Gradle projects with SBOM attestation and GitHub release creation.

**Features:**
- SBOM generation (CycloneDX format)
- Sigstore signing and attestation
- GitHub release with artifacts
- Provenance metadata

**Usage:**
```yaml
name: Release
on:
  push:
    tags: ['v*']

jobs:
  release:
    uses: artagon/artagon-workflows/.github/workflows/gradle_release.yml@main
    with:
      java-version: '21'
    secrets: inherit
```

**Inputs:**

| Input | Description | Default |
|-------|-------------|---------|
| `java-version` | Java version to use | `25` |
| `java-distribution` | Java distribution | `temurin` |
| `gradle-args` | Additional Gradle arguments | `''` |
| `verify-wrapper` | Verify Gradle wrapper integrity | `true` |
| `wrapper-sha256` | Expected SHA-256 of gradle-wrapper.jar | `''` |
| `sign-artifacts` | Sign artifacts with Sigstore | `true` |
| `generate-sbom` | Generate SBOM | `true` |

## Security Features

### Wrapper Verification

The workflows verify Gradle wrapper integrity before execution:

```yaml
with:
  verify-wrapper: true
  wrapper-sha256: 'abc123...'  # Optional: pin specific SHA
```

### Dependency Verification

Enable dependency verification to ensure artifact integrity:

```yaml
with:
  verify-dependencies: true
```

Requires a `gradle/verification-metadata.xml` file in your project.

### Lockfile Checking

Detect lockfile drift to ensure reproducible builds:

```yaml
with:
  check-lockfiles: true
```

Requires lockfiles generated with:
```bash
./gradlew dependencies --write-locks
```

## Examples

### Basic Build

```yaml
jobs:
  build:
    uses: artagon/artagon-workflows/.github/workflows/gradle_build.yml@main
```

### Build with Custom Java Version

```yaml
jobs:
  build:
    uses: artagon/artagon-workflows/.github/workflows/gradle_build.yml@main
    with:
      java-version: '17'
      java-distribution: 'corretto'
```

### Build with Additional Gradle Arguments

```yaml
jobs:
  build:
    uses: artagon/artagon-workflows/.github/workflows/gradle_build.yml@main
    with:
      gradle-args: '--warning-mode all --stacktrace'
```

### Full Release Pipeline

```yaml
name: Release

on:
  push:
    tags: ['v*']

jobs:
  release:
    uses: artagon/artagon-workflows/.github/workflows/gradle_release.yml@main
    with:
      java-version: '21'
      sign-artifacts: true
      generate-sbom: true
    secrets: inherit
```

## Related Documentation

- [RELEASE_JAVA.md](RELEASE_JAVA.md) - Java release strategies
- [WORKFLOWS_USAGE.md](WORKFLOWS_USAGE.md) - General workflow usage
