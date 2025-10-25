# Artagon Workflows

Reusable GitHub Actions workflows for Artagon projects providing standardized CI/CD pipelines for Maven, C/C++, and Bazel builds.

## About

**Artagon Workflows** is a comprehensive, production-ready collection of reusable GitHub Actions workflows designed to streamline software development across multiple languages and build systems. This repository provides enterprise-grade CI/CD pipelines with built-in security, compliance, and best practices for Maven (Java), CMake (C/C++), and Bazel projects.

### 🎯 Key Features

- **🔄 20+ Reusable Workflows** - Pre-built, tested workflows for build, test, release, and security scanning
- **🔒 Security-First Design** - All actions pinned to commit SHAs, TLS 1.3 enforcement, certificate validation, checksum verification
- **🚀 Multi-Language Support** - Java/Maven, C/C++/CMake, Bazel projects with language-specific optimizations
- **📦 Release Automation** - Complete release pipelines for Maven Central, GitHub Releases, Docker Hub, and custom registries
- **🛡️ Security Scanning** - CodeQL analysis, dependency review, vulnerability scanning, and license compliance
- **🤖 Bot Integration** - Auto-merge for Dependabot/Renovate PRs with configurable approval workflows
- **⚡ Performance Optimized** - Intelligent caching for Maven, Bazel, and CMake dependencies
- **🔧 Highly Configurable** - Extensive input parameters for project-specific customization
- **📊 PR Validation** - Semantic PR titles, branch naming conventions, commit message validation
- **✅ Testing Support** - Python pytest, shell script testing, multi-version matrix testing
- **📝 Comprehensive Documentation** - Detailed guides, examples, and best practices for every workflow

### 🏗️ Build Systems Supported

- **Maven** - Full lifecycle support (compile, test, package, deploy, release)
- **CMake** - Cross-platform C/C++ builds with multi-OS support (Linux, macOS, Windows)
- **Bazel** - Modern build system with remote caching and hermetic builds

### 🔐 Security Features

- **Supply Chain Security** - All GitHub Actions pinned to immutable commit SHAs
- **Secure Downloads** - TLS 1.3, certificate validation, SHA256 checksums for all binary downloads
- **Vulnerability Scanning** - Automated dependency and code security analysis
- **License Compliance** - Configurable allow/deny lists for dependency licenses
- **Secret Management** - Secure handling via GitHub Secrets, no hardcoded credentials
- **CodeQL Analysis** - Semantic code analysis for 8+ programming languages
- **Dependency Review** - PR-based security and license scanning for dependency changes

### 🎨 Use Cases

- **Open Source Projects** - Complete Maven Central release pipelines with GPG signing and attestation
- **Enterprise Applications** - Private repository releases with security scanning and compliance
- **Multi-Module Projects** - Support for complex Maven/Bazel multi-module builds
- **Cross-Platform Development** - C/C++ builds across Linux, macOS, and Windows
- **Microservices** - Docker image builds with multi-platform support
- **Library Development** - Release automation for reusable libraries and frameworks
- **Security-Critical Software** - Built-in vulnerability scanning and secure build practices

### 🏆 Benefits

- **Consistency** - Standardized CI/CD across all projects eliminates configuration drift
- **Time Savings** - Pre-built workflows reduce setup time from hours to minutes
- **Security** - Built-in best practices prevent common security vulnerabilities
- **Maintainability** - Centralized workflow updates propagate to all consuming projects
- **Reliability** - Production-tested workflows with extensive error handling
- **Flexibility** - Configurable inputs allow project-specific customization
- **Documentation** - Comprehensive guides and examples accelerate onboarding

### 🏷️ GitHub Topics

This repository is tagged with the following topics for discoverability:

`github-actions` `workflow` `reusable-workflows` `ci-cd` `continuous-integration` `continuous-deployment` `devops` `automation` `build-automation` `release-automation` `maven` `cmake` `bazel` `java` `cpp` `c` `cxx` `security-scanning` `vulnerability-scanning` `codeql` `dependency-management` `supply-chain-security` `testing` `pytest` `shellcheck` `semantic-versioning` `semver` `pr-validation` `auto-merge` `dependabot` `renovate` `maven-central` `ossrh` `docker` `multi-platform` `cross-platform` `linux` `macos` `windows` `gpg-signing` `artifact-attestation` `sbom` `license-compliance`

### 📊 Workflow Categories

**Build & Test** - CI workflows for continuous integration and testing
**Release & Deploy** - Automated release pipelines for multiple targets
**Security** - Vulnerability scanning, dependency review, and CodeQL analysis
**Validation** - PR validation, semantic commit checking, and branch naming
**Automation** - Auto-merge, submodule updates, and maintenance workflows

## Overview

This repository contains production-ready, reusable GitHub Actions workflows that can be called from any Artagon project. These workflows provide:

- **Consistent CI/CD** - Standardized build, test, and deployment pipelines
- **Version Control** - Pin to specific workflow versions for stability
- **Security** - Built-in security scanning and best practices
- **Flexibility** - Configurable inputs for project-specific needs

## Available Workflows

### Maven Workflows

- **[maven_ci.yml](.github/workflows/maven_ci.yml)** - Continuous integration (build, test, verify)
- **[maven-build.yml](.github/workflows/maven-build.yml)** - Build without deploy
- **[maven-deploy.yml](.github/workflows/maven-deploy.yml)** - Deploy snapshots to OSSRH
- **[maven-release.yml](.github/workflows/maven-release.yml)** - Full release process
- **[maven_release_tag.yml](.github/workflows/maven_release_tag.yml)** - Release from git tag
- **[maven_release_branch.yml](.github/workflows/maven_release_branch.yml)** - Release from release branch
- **[maven-central-release.yml](.github/workflows/maven-central-release.yml)** - Maven Central deployment
- **[maven-github-release.yml](.github/workflows/maven-github-release.yml)** - GitHub release creation
- **[maven_security_scan.yml](.github/workflows/maven_security_scan.yml)** - Security vulnerability scanning

### C/C++ Workflows

- **[cmake_c_ci.yml](.github/workflows/cmake_c_ci.yml)** - C project CI with CMake
- **[cmake_cpp_ci.yml](.github/workflows/cmake_cpp_ci.yml)** - C++ project CI with CMake
- **[cmake_c_release.yml](.github/workflows/cmake_c_release.yml)** - C project release
- **[cmake_cpp_release.yml](.github/workflows/cmake_cpp_release.yml)** - C++ project release
- **[cmake_cpack_release.yml](.github/workflows/cmake_cpack_release.yml)** - Multi-format packages (DEB, RPM, TGZ)

### Bazel Workflows

- **[bazel_multi_ci.yml](.github/workflows/bazel_multi_ci.yml)** - Bazel project CI
- **[bazel_multi_release.yml](.github/workflows/bazel_multi_release.yml)** - Bazel project release

### Utility Workflows

- **[update-submodule.yml](.github/workflows/update-submodule.yml)** - Automated submodule updates

## Quick Start

### Maven CI Example

```yaml
# .github/workflows/ci.yml
name: CI

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  ci:
    uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@v1
    secrets: inherit
```

### C++ CI Example

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
    uses: artagon/artagon-workflows/.github/workflows/cmake_cpp_ci.yml@v1
    with:
      cmake-options: '-DCMAKE_BUILD_TYPE=Release'
    secrets: inherit
```

### Bazel CI Example

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
    uses: artagon/artagon-workflows/.github/workflows/bazel_multi_ci.yml@v1
    with:
      bazel-configs: 'release,debug'
    secrets: inherit
```

## Versioning

Workflows are versioned using git tags. Pin to a specific version for stability:

```yaml
# Pin to major version (recommended)
uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@v1

# Pin to specific release
uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@v1.2.0

# Use latest (not recommended for production)
uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@main
```

## Release Strategy

Artagon projects follow a **release branch strategy** for stable, predictable releases:

### Branch Structure

- **`main` branch**: Always has SNAPSHOT versions (e.g., `1.0.9-SNAPSHOT`)
- **`release-X.Y.Z` branches**: Have release versions without SNAPSHOT (e.g., `1.0.8`)
- **Tags**: Created on release branches (e.g., `v1.0.8`)

### Release Process

```bash
# 1. Ensure main is at next SNAPSHOT version
main: 1.0.9-SNAPSHOT

# 2. Create release branch from commit at desired SNAPSHOT
git checkout -b release-1.0.8 <commit-at-1.0.8-SNAPSHOT>
git push origin release-1.0.8

# 3. Trigger release workflow from release branch
# The workflow removes -SNAPSHOT and creates v1.0.8 tag

# 4. Result
main:          1.0.9-SNAPSHOT (unchanged)
release-1.0.8: 1.0.8          (frozen for hotfixes)
tag v1.0.8:    created
```

### Key Principles

- ✅ Main branch **always** has SNAPSHOT versions
- ✅ Release branches **never** have SNAPSHOT versions
- ✅ Releases are **only** created from `release-*` branches
- ✅ Release branches are **kept** for hotfixes (not deleted)
- ✅ Tags are created on release branches

For detailed instructions, see **[RELEASE.md](docs/RELEASE.md)**.

## Documentation

### Release Process

- **[RELEASE.md](docs/RELEASE.md)** - Maven release process and language-specific guide index
- **[Java Release Strategy](docs/RELEASE_JAVA.md)** - Maven, Gradle, SNAPSHOT versions, Maven Central
- **[C Release Strategy](docs/RELEASE_C.md)** - CMake, Autotools, tag-based releases, ABI stability
- **[C++ Release Strategy](docs/RELEASE_CPP.md)** - CMake, Bazel, LTS support, ABI/API management
- **[Rust Release Strategy](docs/RELEASE_RUST.md)** - Cargo, crates.io, MSRV policy, SemVer
- **[OSS Release Strategies Analysis](docs/OSS_RELEASE_STRATEGIES.md)** - Industry research and best practices

### Workflows

- **[Maven Workflows](docs/MAVEN.md)** - Detailed Maven workflow documentation
- **[C/C++ Workflows](docs/CPP.md)** - C/C++ workflow documentation
- **[Bazel Workflows](docs/BAZEL.md)** - Bazel workflow documentation
- **[Examples](examples/)** - Complete workflow usage examples

## Features

### Multi-Version Support

All workflows support multiple language versions:
- Java: 17, 21, 25 (default)
- CMake: 3.20+
- Bazel: 7.x (default)

### Caching

Automatic dependency caching for faster builds:
- Maven dependencies
- Bazel cache
- CMake build cache

### Security

- Dependency vulnerability scanning
- GPG signing for releases
- Secret management via GitHub Secrets

### Platform Support

- Linux (ubuntu-latest)
- macOS (optional)
- Windows (optional for some workflows)

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines on adding or modifying workflows.

## License

Dual-licensed under AGPL-3.0 and Commercial licenses. See [LICENSE](LICENSE) for details.

## Related Repositories

- **[artagon-common](https://github.com/artagon/artagon-common)** - Project templates, configs, and scripts
- **[artagon-license](https://github.com/artagon/artagon-license)** - License management

## Support

For issues, questions, or contributions:
- GitHub Issues: https://github.com/artagon/artagon-workflows/issues
- Documentation: https://github.com/artagon/artagon-workflows/tree/main/docs
