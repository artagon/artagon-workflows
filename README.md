# Artagon Workflows

Reusable GitHub Actions workflows for Artagon projects providing standardized CI/CD pipelines for Maven, C/C++, and Bazel builds.

## Overview

This repository contains production-ready, reusable GitHub Actions workflows that can be called from any Artagon project. These workflows provide:

- **Consistent CI/CD** - Standardized build, test, and deployment pipelines
- **Version Control** - Pin to specific workflow versions for stability
- **Security** - Built-in security scanning and best practices
- **Flexibility** - Configurable inputs for project-specific needs

## Available Workflows

### Maven Workflows

- **[maven-ci.yml](.github/workflows/maven-ci.yml)** - Continuous integration (build, test, verify)
- **[maven-build.yml](.github/workflows/maven-build.yml)** - Build without deploy
- **[maven-deploy.yml](.github/workflows/maven-deploy.yml)** - Deploy snapshots to OSSRH
- **[maven-release.yml](.github/workflows/maven-release.yml)** - Full release process
- **[maven_release_tag.yml](.github/workflows/maven_release_tag.yml)** - Release from git tag
- **[maven_release_branch.yml](.github/workflows/maven_release_branch.yml)** - Release from release branch
- **[maven-central-release.yml](.github/workflows/maven-central-release.yml)** - Maven Central deployment
- **[maven-github-release.yml](.github/workflows/maven-github-release.yml)** - GitHub release creation
- **[maven_security_scan.yml](.github/workflows/maven_security_scan.yml)** - Security vulnerability scanning

### C/C++ Workflows

- **[c-ci.yml](.github/workflows/c-ci.yml)** - C project CI with CMake
- **[cpp-ci.yml](.github/workflows/cpp-ci.yml)** - C++ project CI with CMake
- **[c-release.yml](.github/workflows/c-release.yml)** - C project release
- **[cpp-release.yml](.github/workflows/cpp-release.yml)** - C++ project release

### Bazel Workflows

- **[bazel-ci.yml](.github/workflows/bazel-ci.yml)** - Bazel project CI
- **[bazel-release.yml](.github/workflows/bazel-release.yml)** - Bazel project release

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
    uses: artagon/artagon-workflows/.github/workflows/maven-ci.yml@v1
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
    uses: artagon/artagon-workflows/.github/workflows/cpp-ci.yml@v1
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
    uses: artagon/artagon-workflows/.github/workflows/bazel-ci.yml@v1
    with:
      bazel-configs: 'release,debug'
    secrets: inherit
```

## Versioning

Workflows are versioned using git tags. Pin to a specific version for stability:

```yaml
# Pin to major version (recommended)
uses: artagon/artagon-workflows/.github/workflows/maven-ci.yml@v1

# Pin to specific release
uses: artagon/artagon-workflows/.github/workflows/maven-ci.yml@v1.2.0

# Use latest (not recommended for production)
uses: artagon/artagon-workflows/.github/workflows/maven-ci.yml@main
```

## Documentation

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
