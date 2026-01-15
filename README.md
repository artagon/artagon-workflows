# Artagon Workflows

[![Workflow Validation](https://github.com/artagon/artagon-workflows/actions/workflows/test_lint.yml/badge.svg)](https://github.com/artagon/artagon-workflows/actions/workflows/test_lint.yml)
[![Security Scan](https://github.com/artagon/artagon-workflows/actions/workflows/test_security.yml/badge.svg)](https://github.com/artagon/artagon-workflows/actions/workflows/test_security.yml)
[![CodeQL](https://github.com/artagon/artagon-workflows/actions/workflows/codeql.yml/badge.svg)](https://github.com/artagon/artagon-workflows/actions/workflows/codeql.yml)
[![License](https://img.shields.io/badge/License-AGPL%203.0-blue.svg)](LICENSE)
[![GitHub release](https://img.shields.io/github/v/release/artagon/artagon-workflows?include_prereleases)](https://github.com/artagon/artagon-workflows/releases)

Reusable GitHub Actions workflows for Artagon projects providing standardized CI/CD pipelines for Maven, C/C++, Bazel, and Gradle builds.

## About

**Artagon Workflows** is a production-ready collection of reusable GitHub Actions workflows with enterprise-grade security, compliance, and best practices.

### Key Features

- **20+ Reusable Workflows** - Build, test, release, and security scanning
- **Security-First Design** - All actions pinned to commit SHAs, input validation, least-privilege permissions
- **Multi-Language Support** - Java/Maven, C/C++/CMake, Bazel, Gradle, Rust
- **Release Automation** - Maven Central, GitHub Releases, Docker Hub
- **Security Scanning** - CodeQL, dependency review, vulnerability scanning

### Build Systems Supported

| Build System | CI | Release | Security |
|--------------|-----|---------|----------|
| Maven | `maven_ci.yml` | `maven_release.yml` | `maven_security_scan.yml` |
| CMake (C) | `cmake_c_ci.yml` | `cmake_c_release.yml` | - |
| CMake (C++) | `cmake_cpp_ci.yml` | `cmake_cpp_release.yml` | - |
| Bazel | `bazel_multi_ci.yml` | `bazel_multi_release.yml` | - |
| Gradle | `gradle_build.yml` | `gradle_release.yml` | - |
| Rust | `rust_ci.yml` | Planned | - |

## Project Structure

```
artagon-workflows/
├── .github/
│   ├── workflows/              # 24+ reusable workflow files
│   ├── ISSUE_TEMPLATE/         # Issue templates (spec, proposal, bug)
│   ├── PULL_REQUEST_TEMPLATE.md
│   ├── CODEOWNERS
│   ├── copilot-instructions.md
│   └── copilot-review-instructions.md
├── openspec/                   # Spec-driven development
│   ├── AGENTS.md               # AI agent instructions
│   ├── project.md              # Project context
│   ├── contributing.md         # Contribution guidelines
│   ├── specs/                  # Capability specifications
│   │   ├── workflow-security/  # Security requirements
│   │   ├── maven-workflows/    # Maven workflow specs
│   │   ├── cmake-workflows/    # CMake workflow specs
│   │   └── bazel-workflows/    # Bazel workflow specs
│   └── changes/                # Change proposals and archive
├── .agents/                    # AI agent context
│   ├── context/                # Glossary and context
│   ├── policies/               # Security guardrails
│   └── workflows/              # Agent workflow guides
├── docs/                       # Operational documentation
├── examples/                   # Usage examples
├── templates/                  # Starter templates
└── test/                       # Test fixtures
```

## Quick Start

### Maven CI

```yaml
name: CI
on: [push, pull_request]

jobs:
  ci:
    uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@v1
    secrets: inherit
```

### CMake C++ CI

```yaml
name: CI
on: [push, pull_request]

jobs:
  ci:
    uses: artagon/artagon-workflows/.github/workflows/cmake_cpp_ci.yml@v1
    with:
      cmake-options: '-DCMAKE_BUILD_TYPE=Release'
    secrets: inherit
```

### Bazel CI

```yaml
name: CI
on: [push, pull_request]

jobs:
  ci:
    uses: artagon/artagon-workflows/.github/workflows/bazel_multi_ci.yml@v1
    with:
      bazel-configs: 'release,debug'
    secrets: inherit
```

### Rust CI

```yaml
name: CI
on: [push, pull_request]

jobs:
  ci:
    uses: artagon/artagon-workflows/.github/workflows/rust_ci.yml@main
    with:
      rust-version: 'stable'
    secrets: inherit
```

## Versioning

```yaml
# Pin to major version (recommended)
uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@v1

# Pin to specific release
uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@v1.2.0
```

## Security Requirements

All workflows follow strict security requirements:

1. **Action Pinning** - All actions pinned to commit SHAs
2. **Permissions** - Explicit least-privilege permissions on all jobs
3. **Input Validation** - All user inputs validated before shell execution
4. **Secret Handling** - Secrets in config files, never in CLI arguments
5. **Binary Verification** - Checksum verification for all downloads

See [openspec/specs/workflow-security/spec.md](openspec/specs/workflow-security/spec.md) for details.

## OpenSpec Workflow

This repository uses **OpenSpec** for spec-driven development.

### Key Concepts

- **Specs** (`openspec/specs/`) - Current truth: what IS built
- **Changes** (`openspec/changes/`) - Proposals: what SHOULD change
- **Archive** (`openspec/changes/archive/`) - History: completed changes

### Common Commands

```bash
openspec list              # Active changes
openspec list --specs      # Capabilities
openspec show [item]       # View details
openspec validate --strict # Validate
openspec archive <id> --yes # Archive after deployment
```

### Workflow

1. **Create** - Proposal in `openspec/changes/<change-id>/`
2. **Implement** - Follow `tasks.md`, reference spec issue
3. **Archive** - Move to archive after deployment
4. **Close** - Update specs, close GitHub issue

See [openspec/AGENTS.md](openspec/AGENTS.md) for complete workflow.

## Documentation

### Workflow Guides

- [Maven Workflows](docs/MAVEN.md)
- [C/C++ Workflows](docs/CPP.md)
- [Bazel Workflows](docs/BAZEL.md)
- [Workflow Usage Guide](docs/WORKFLOWS_USAGE.md)

### Release Process

- [Release Guide](docs/RELEASE.md)
- [Java Release Strategy](docs/RELEASE_JAVA.md)
- [C Release Strategy](docs/RELEASE_C.md)
- [C++ Release Strategy](docs/RELEASE_CPP.md)
- [Rust Release Strategy](docs/RELEASE_RUST.md)

### Testing

- [Testing Guide](docs/TESTING.md) - Workflow testing procedures

### Examples

- [Maven Examples](examples/maven/)
- [CMake C Examples](examples/cmake_c/)
- [CMake C++ Examples](examples/cmake_cpp/)
- [Bazel Examples](examples/bazel_multi/)
- [Rust Examples](examples/rust/)

## Contributing

This repository uses OpenSpec for contributions. See:

- [openspec/contributing.md](openspec/contributing.md) - Contribution workflow
- [CONTRIBUTING.md](CONTRIBUTING.md) - General guidelines

### Branch Naming

```bash
# OpenSpec changes
feature/workflows(<issue#>)-<short-name>

# Other changes
<type>/<scope>-<description>
```

### PR Requirements

- Reference spec issue for implementations
- Security checklist for workflow changes
- All actions pinned to SHAs
- Permissions declared on all jobs

## AI Agents

For AI assistants working on this repository:

- [AGENTS.md](AGENTS.md) - Entry point
- [CLAUDE.md](CLAUDE.md) - Claude-specific instructions
- [COPILOT.md](COPILOT.md) - Copilot context
- [openspec/AGENTS.md](openspec/AGENTS.md) - Detailed workflow
- [.agents/](/.agents/) - Context and policies

## License

Dual-licensed under AGPL-3.0 and Commercial licenses. See [LICENSE](LICENSE).

## Support

- [GitHub Issues](https://github.com/artagon/artagon-workflows/issues)
- [Documentation](docs/)
- [Discussions](https://github.com/artagon/artagon-workflows/discussions)
