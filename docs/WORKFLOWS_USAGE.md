# Reusable Workflows Usage Guide

This repository provides secure, reusable GitHub Actions workflows for common CI/CD tasks across all Artagon projects.

## Table of Contents

1. [Quick Start](#quick-start)
2. [Available Workflows](#available-workflows)
   - [Build & Test Workflows](#build--test-workflows)
   - [Validation Workflows](#validation-workflows)
   - [Release Workflows](#release-workflows)
3. [Security Features](#security-features)
4. [Best Practices](#best-practices)
5. [Examples](#examples)

---

## Quick Start

To use a reusable workflow, add it to your repository's `.github/workflows/` directory:

```yaml
name: My Workflow

on:
  push:
    branches: [main]

jobs:
  my-job:
    uses: artagon/artagon-workflows/.github/workflows/<workflow-name>.yml@main
    with:
      # workflow-specific inputs
    secrets:
      # workflow-specific secrets
```

⚠️ **Security Note**: In production, pin to a specific commit SHA instead of `@main`:
```yaml
uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@<commit-sha>
```

---

## Available Workflows

### Build & Test Workflows

#### Maven CI (`maven_ci.yml`)

Runs Maven build, test, and code coverage for Java/Maven projects.

**When to use**: Pull requests and pushes to validate Maven projects.

**Usage**:
```yaml
jobs:
  maven-ci:
    uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@main
    with:
      java-version: '21'
      maven-args: '-DskipTests=false'
      upload-coverage: true
    secrets:
      CODECOV_TOKEN: ${{ secrets.CODECOV_TOKEN }}
```

**Inputs**:
| Input | Description | Required | Default |
|-------|-------------|----------|---------|
| `java-version` | Java version (11, 17, 21, 23) | No | `21` |
| `java-distribution` | Java distribution | No | `temurin` |
| `maven-args` | Additional Maven arguments | No | `''` |
| `upload-coverage` | Upload coverage to Codecov | No | `false` |

**Secrets**:
- `CODECOV_TOKEN`: Required if `upload-coverage: true`

---

#### CMake C CI (`cmake_c_ci.yml`)

Builds and tests C projects using CMake with matrix testing across multiple compilers and OSes.

**When to use**: CI for C projects using CMake.

**Usage**:
```yaml
jobs:
  cmake-ci:
    uses: artagon/artagon-workflows/.github/workflows/cmake_c_ci.yml@main
    with:
      c-standard: '17'
      cmake-options: '-DBUILD_TESTING=ON'
```

**Inputs**:
| Input | Description | Required | Default |
|-------|-------------|----------|---------|
| `c-standard` | C standard version (11, 17, 23) | No | `17` |
| `cmake-options` | Additional CMake options | No | `''` |
| `build-type` | Build type (Debug, Release) | No | `Debug` |

**Features**:
- Tests on Ubuntu, macOS, Windows
- Nix flake support (auto-detected)
- Multiple compiler support (GCC, Clang, MSVC)
- ASAN, UBSAN, TSAN sanitizer builds

---

#### CMake C++ CI (`cmake_cpp_ci.yml`)

Similar to CMake C CI but for C++ projects.

**Usage**:
```yaml
jobs:
  cmake-cpp-ci:
    uses: artagon/artagon-workflows/.github/workflows/cmake_cpp_ci.yml@main
    with:
      cxx-standard: '23'
      cmake-options: '-DENABLE_BENCHMARKS=ON'
```

**Inputs**:
| Input | Description | Required | Default |
|-------|-------------|----------|---------|
| `cxx-standard` | C++ standard (11, 14, 17, 20, 23, 26) | No | `23` |
| `cmake-options` | Additional CMake options | No | `''` |
| `build-type` | Build type | No | `Debug` |

---

#### Bazel Multi-Language CI (`bazel_multi_ci.yml`)

Builds and tests Bazel projects supporting C, C++, Java, Python, Go, Rust.

**When to use**: CI for polyglot projects using Bazel.

**Usage**:
```yaml
jobs:
  bazel-ci:
    uses: artagon/artagon-workflows/.github/workflows/bazel_multi_ci.yml@main
    with:
      bazel-configs: 'linux'
```

**Inputs**:
| Input | Description | Required | Default |
|-------|-------------|----------|---------|
| `bazel-configs` | Bazel config flags | No | `''` |

**Features**:
- Multi-OS support (Linux, macOS, Windows)
- Nix flake integration
- Buildifier formatting checks
- Test result artifacts

---

#### Python & Shell Tests (`python_shell_tests.yml`)

Runs pytest and shell script tests.

**When to use**: Testing Python scripts or shell utilities.

**Usage**:
```yaml
jobs:
  tests:
    uses: artagon/artagon-workflows/.github/workflows/python_shell_tests.yml@main
    with:
      python-version: '3.11'
      pytest-path: 'tests/'
      shell-test-script: 'tests/shell/run_tests.sh'
```

**Inputs**:
| Input | Description | Required | Default |
|-------|-------------|----------|---------|
| `python-version` | Python version | No | `3.11` |
| `pytest-args` | Additional pytest arguments | No | `''` |
| `pytest-path` | Path to pytest tests | No | `tests/` |
| `shell-test-script` | Shell test script path | No | `''` |
| `requirements-file` | Additional requirements | No | `''` |

---

### Validation Workflows

#### PR Validation (`pr_validation.yml`)

Validates pull requests for semantic conventions and quality standards.

**When to use**: On every PR to enforce code quality standards.

**Usage**:
```yaml
jobs:
  validate-pr:
    uses: artagon/artagon-workflows/.github/workflows/pr_validation.yml@main
    secrets:
      token: ${{ secrets.GITHUB_TOKEN }}
    with:
      require-issue-reference: false
      enable-auto-label: true
```

**Validates**:
- ✅ Semantic PR title (`feat:`, `fix:`, `docs:`, etc.)
- ✅ Branch naming (`type/issue-description`)
- ✅ Semantic commit messages
- ✅ Issue references (configurable)
- ✅ Auto-labels PRs

**Inputs**:
| Input | Description | Required | Default |
|-------|-------------|----------|---------|
| `require-issue-reference` | Fail if no issue ref | No | `false` |
| `enable-auto-label` | Enable auto-labeling | No | `true` |
| `labeler-config-path` | Labeler config path | No | `.github/labeler.yml` |

**Secrets**:
- `token`: GitHub token (required)

---

#### Shellcheck (`shellcheck.yml`)

Validates shell scripts using shellcheck.

**When to use**: Validating shell script quality.

**Usage**:
```yaml
jobs:
  shellcheck:
    uses: artagon/artagon-workflows/.github/workflows/shellcheck.yml@main
    with:
      severity: error
      ignore-paths: |
        .git
        node_modules
```

**Inputs**:
| Input | Description | Required | Default |
|-------|-------------|----------|---------|
| `severity` | Min severity (error, warning, info, style) | No | `warning` |
| `scan-dir` | Directory to scan | No | `.` |
| `ignore-paths` | Paths to ignore | No | `.git` |
| `upload-results` | Upload results artifact | No | `true` |

---

#### Workflow Linting (`test_lint.yml`)

Validates GitHub Actions workflows using actionlint and yamllint.

**When to use**: Validating your own workflows.

**Usage**:
```yaml
on:
  push:
    paths: ['.github/workflows/*.yml']

jobs:
  lint:
    uses: artagon/artagon-workflows/.github/workflows/test_lint.yml@main
```

**Checks**:
- ✅ YAML syntax (yamllint)
- ✅ Workflow structure (actionlint)
- ✅ Action pinning (security validation)
- ✅ Hardcoded secret detection

---

### Release Workflows

#### Maven Release (`maven_release.yml`)

Creates Maven releases with GPG signing and deployment.

**When to use**: Releasing Maven artifacts to Maven Central or GitHub Packages.

**Usage**:
```yaml
jobs:
  release:
    uses: artagon/artagon-workflows/.github/workflows/maven_release.yml@main
    with:
      java-version: '21'
      deploy-profile: 'ossrh'
    secrets:
      GPG_PRIVATE_KEY: ${{ secrets.GPG_PRIVATE_KEY }}
      GPG_PASSPHRASE: ${{ secrets.GPG_PASSPHRASE }}
      OSSRH_USERNAME: ${{ secrets.OSSRH_USERNAME }}
      OSSRH_TOKEN: ${{ secrets.OSSRH_TOKEN }}
```

**Inputs**:
| Input | Description | Required | Default |
|-------|-------------|----------|---------|
| `java-version` | Java version | No | `21` |
| `deploy-profile` | Maven profile for deployment | No | `ossrh` |
| `maven-args` | Additional Maven arguments | No | `''` |

**Secrets**:
- `GPG_PRIVATE_KEY`: GPG private key for signing
- `GPG_PASSPHRASE`: GPG passphrase
- `OSSRH_USERNAME`: Sonatype OSSRH username
- `OSSRH_TOKEN`: Sonatype OSSRH token

**Features**:
- ✅ GPG signing (secure settings.xml method)
- ✅ Automated version bumping
- ✅ Tag creation
- ✅ Artifact deployment

---

#### CMake Release - C/C++ (`cmake_c_release.yml`, `cmake_cpp_release.yml`)

Creates releases for CMake-based C/C++ projects with AppImage packaging.

**When to use**: Creating releases for C/C++ projects.

**Usage**:
```yaml
jobs:
  release:
    uses: artagon/artagon-workflows/.github/workflows/cmake_c_release.yml@main
    with:
      c-standard: '17'
    secrets:
      GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

**Features**:
- ✅ Creates AppImage packages (Linux)
- ✅ GitHub Release creation
- ✅ Asset upload
- ✅ Version tagging

---

#### CMake CPack Multi-Format Packages (`cmake_cpack_release.yml`)

Creates DEB, RPM, and tarball packages for C/C++ projects using CMake's CPack system.

**When to use**: Creating distribution packages for Linux (Debian, Ubuntu, Fedora, RHEL, etc.).

**Usage**:
```yaml
on:
  push:
    tags:
      - 'v*'

jobs:
  package:
    uses: artagon/artagon-workflows/.github/workflows/cmake_cpack_release.yml@main
    with:
      package-formats: 'DEB;RPM;TGZ'
      sign-packages: true
      run-linting: true
    secrets:
      gpg-private-key: ${{ secrets.GPG_PRIVATE_KEY }}
      gpg-passphrase: ${{ secrets.GPG_PASSPHRASE }}
```

**Inputs**:
| Input | Description | Required | Default |
|-------|-------------|----------|---------|
| `package-formats` | CPack generators (DEB;RPM;TGZ;TBZ2;TXZ;ZIP) | No | `DEB;RPM;TGZ` |
| `cmake-version` | CMake version | No | `3.20` |
| `build-type` | Build type (Release, Debug, RelWithDebInfo) | No | `Release` |
| `cmake-options` | Additional CMake options | No | `''` |
| `sign-packages` | Sign packages with GPG | No | `false` |
| `run-linting` | Run lintian/rpmlint verification | No | `true` |
| `upload-to-release` | Upload to GitHub Release | No | `true` |
| `release-tag` | Release tag (auto-detected for tag pushes) | No | `''` |

**Secrets**:
- `gpg-private-key`: GPG private key for signing (if `sign-packages: true`)
- `gpg-passphrase`: GPG key passphrase (if `sign-packages: true`)

**Features**:
- ✅ Multi-format packaging (DEB, RPM, TGZ, TBZ2, TXZ, ZIP)
- ✅ GPG signing with detached signatures (.asc)
- ✅ Package verification (lintian for DEB, rpmlint for RPM)
- ✅ SHA256 checksums for all packages
- ✅ Automatic dependency detection
- ✅ GitHub Release creation with all artifacts
- ✅ Component-based packaging (runtime/development)
- ✅ Multi-architecture support

**Package Outputs**:
- DEB packages for Debian/Ubuntu: `myproject-1.0.0-x86_64.deb`
- RPM packages for Fedora/RHEL: `myproject-1.0.0.x86_64.rpm`
- Source tarballs: `myproject-1.0.0-x86_64.tar.gz`, `.tar.bz2`, `.tar.xz`
- Checksums: `*.sha256` and `SHA256SUMS`
- GPG signatures: `*.asc` (if signing enabled)

**See also**: [CPack Packaging Guide](CPACK_PACKAGING.md) for CMake configuration details.

---

#### Bazel Multi-Language Release (`bazel_multi_release.yml`)

Creates releases for Bazel projects with Docker image support.

**When to use**: Releasing Bazel projects with container images.

**Usage**:
```yaml
jobs:
  release:
    uses: artagon/artagon-workflows/.github/workflows/bazel_multi_release.yml@main
    with:
      image-name: 'my-app'
      enable-docker: true
    secrets:
      GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

**Features**:
- ✅ Multi-architecture builds
- ✅ Docker image creation
- ✅ GitHub Container Registry push
- ✅ Release asset creation

---

## Security Features

All workflows implement security best practices:

### 1. Action Pinning
All actions pinned to immutable commit SHAs:
```yaml
# ✅ Secure - pinned to SHA
- uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683

# ❌ Insecure - mutable tag
- uses: actions/checkout@v4
```

### 2. Least Privilege Permissions
Explicit permissions for each job:
```yaml
jobs:
  build:
    permissions:
      contents: read      # Read source code
      packages: read      # Download packages
```

### 3. Secret Handling
- Secrets passed via `secrets` parameter
- GPG passphrases in settings.xml (not CLI)
- No hardcoded credentials

### 4. Input Validation
- Safe defaults
- Type-checked inputs
- Pattern validation

### 5. Binary Downloads
- TLS 1.3 enforcement
- Certificate validation
- SHA256 checksum verification

---

## Best Practices

### 1. Pin Workflow Versions

**Development**:
```yaml
uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@main
```

**Production**:
```yaml
uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@<commit-sha>
```

### 2. Use Matrix Testing

Test across multiple versions:
```yaml
jobs:
  test:
    strategy:
      matrix:
        java: ['17', '21']
        os: [ubuntu-latest, macos-latest, windows-latest]
    uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@main
    with:
      java-version: ${{ matrix.java }}
```

### 3. Separate CI and Release

```yaml
# ci.yml - Run on every push/PR
on:
  push:
    branches: [main, develop]
  pull_request:

jobs:
  ci:
    uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@main

# release.yml - Run on tags only
on:
  push:
    tags: ['v*']

jobs:
  release:
    uses: artagon/artagon-workflows/.github/workflows/maven_release.yml@main
```

### 4. Use Semantic Commits

Enable PR validation:
```yaml
# .github/workflows/pr-validation.yml
on:
  pull_request:
    types: [opened, edited, synchronize, reopened]

jobs:
  validate:
    uses: artagon/artagon-workflows/.github/workflows/pr_validation.yml@main
    secrets:
      token: ${{ secrets.GITHUB_TOKEN }}
```

### 5. Cache Dependencies

Workflows automatically cache:
- Maven dependencies (`~/.m2/repository`)
- Bazel artifacts (`~/.cache/bazel`)
- Python packages (pip cache)

---

## Examples

### Complete Maven Project

```yaml
# .github/workflows/ci.yml
name: CI

on:
  push:
    branches: [main, develop]
  pull_request:

jobs:
  validate-pr:
    if: github.event_name == 'pull_request'
    uses: artagon/artagon-workflows/.github/workflows/pr_validation.yml@main
    secrets:
      token: ${{ secrets.GITHUB_TOKEN }}

  maven-ci:
    uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@main
    with:
      java-version: '21'
      maven-args: '-Pcoverage'
      upload-coverage: true
    secrets:
      CODECOV_TOKEN: ${{ secrets.CODECOV_TOKEN }}

  security-scan:
    uses: artagon/artagon-workflows/.github/workflows/maven_security_scan.yml@main
```

```yaml
# .github/workflows/release.yml
name: Release

on:
  push:
    tags: ['v*']

jobs:
  release:
    uses: artagon/artagon-workflows/.github/workflows/maven_release.yml@main
    with:
      java-version: '21'
      deploy-profile: 'ossrh'
    secrets:
      GPG_PRIVATE_KEY: ${{ secrets.GPG_PRIVATE_KEY }}
      GPG_PASSPHRASE: ${{ secrets.GPG_PASSPHRASE }}
      OSSRH_USERNAME: ${{ secrets.OSSRH_USERNAME }}
      OSSRH_TOKEN: ${{ secrets.OSSRH_TOKEN }}
```

---

### Complete C++ Project

```yaml
# .github/workflows/ci.yml
name: CI

on:
  push:
    branches: [main]
  pull_request:

jobs:
  pr-validation:
    if: github.event_name == 'pull_request'
    uses: artagon/artagon-workflows/.github/workflows/pr_validation.yml@main
    secrets:
      token: ${{ secrets.GITHUB_TOKEN }}

  cmake-ci:
    uses: artagon/artagon-workflows/.github/workflows/cmake_cpp_ci.yml@main
    with:
      cxx-standard: '23'
      cmake-options: '-DBUILD_TESTING=ON -DENABLE_COVERAGE=ON'

  shellcheck:
    uses: artagon/artagon-workflows/.github/workflows/shellcheck.yml@main
    with:
      severity: error
```

```yaml
# .github/workflows/release.yml
name: Release

on:
  push:
    tags: ['v*']

jobs:
  release:
    uses: artagon/artagon-workflows/.github/workflows/cmake_cpp_release.yml@main
    with:
      cxx-standard: '23'
    secrets:
      GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

---

### Polyglot Bazel Project

```yaml
# .github/workflows/ci.yml
name: CI

on:
  push:
    branches: [main]
  pull_request:

jobs:
  pr-validation:
    if: github.event_name == 'pull_request'
    uses: artagon/artagon-workflows/.github/workflows/pr_validation.yml@main
    secrets:
      token: ${{ secrets.GITHUB_TOKEN }}

  bazel-ci:
    uses: artagon/artagon-workflows/.github/workflows/bazel_multi_ci.yml@main
    with:
      bazel-configs: 'linux'

  python-tests:
    uses: artagon/artagon-workflows/.github/workflows/python_shell_tests.yml@main
    with:
      python-version: '3.11'
      pytest-path: 'tests/'

  shellcheck:
    uses: artagon/artagon-workflows/.github/workflows/shellcheck.yml@main
```

---

## Troubleshooting

### Issue: Workflow not found

**Problem**: `error: workflow not found at path`

**Solution**: Ensure you're using the correct path and the workflow exists:
```yaml
uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@main
#                                     ^^^^^^^^^^^^^^^^^^^^^^^^^^^
#                                     Must match exact filename
```

### Issue: Secrets not accessible

**Problem**: Secrets are undefined in reusable workflow

**Solution**: Pass secrets explicitly:
```yaml
jobs:
  build:
    uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@main
    secrets:
      CODECOV_TOKEN: ${{ secrets.CODECOV_TOKEN }}  # Explicit pass
```

### Issue: Matrix not working

**Problem**: Can't use matrix with reusable workflows

**Solution**: Create matrix at the calling workflow level:
```yaml
jobs:
  test:
    strategy:
      matrix:
        java: ['17', '21']
    uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@main
    with:
      java-version: ${{ matrix.java }}
```

---

## Contributing

Found a bug or want to add a workflow? See [CONTRIBUTING.md](CONTRIBUTING.md).

## Support

- **Documentation**: `.model-context/` directory
- **Issues**: [GitHub Issues](https://github.com/artagon/artagon-workflows/issues)
- **Security**: See [SECURITY_AUDIT.md](.model-context/SECURITY_AUDIT.md)

---

**Last Updated**: 2025-10-25
**Version**: 2.0.0
