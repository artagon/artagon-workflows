# Project Context

## Purpose

Artagon Workflows is a centralized repository of production-ready, reusable GitHub Actions workflows for Artagon projects across multiple languages and build systems.

## Scope and Goals

- Provide standardized CI/CD pipelines for Maven, CMake, and Bazel projects
- Enforce security best practices across all workflows
- Maintain consistent workflow naming conventions
- Enable version pinning for stability
- Support multi-language and cross-platform builds

## Tech Stack

### Build Systems Supported

- **Maven** - Java/JVM projects (9+ workflows)
- **CMake** - C and C++ projects (5 workflows)
- **Bazel** - Multi-language projects (2 workflows)
- **Gradle** - Java/JVM projects (2 workflows)
- **Rust/Cargo** - Documented, workflows planned

### Core Technologies

- GitHub Actions (reusable workflows)
- YAML workflow definitions
- Shell scripting (bash)
- Security scanning (CodeQL, Trivy, Dependency Review)

### Tooling

- actionlint (workflow linting)
- yamllint (YAML validation)
- Dependabot (dependency updates)

## Project Structure

```
artagon-workflows/
├── .github/
│   └── workflows/            # 24+ reusable workflow files
├── openspec/                 # OpenSpec specifications
│   ├── AGENTS.md             # AI instructions
│   ├── project.md            # This file
│   ├── contributing.md       # Contribution guidelines
│   ├── specs/                # Capability specifications
│   └── changes/              # Change proposals and archive
├── .agents/                  # AI agent context and policies
│   ├── context/              # Runtime context
│   ├── policies/             # Security policies
│   └── workflows/            # Agent workflow guides
├── docs/                     # Build & release documentation
├── examples/                 # Usage examples per build system
├── templates/                # Starter templates
└── test/                     # Test fixtures
```

## Workflow Categories

### Build & Test

- `maven_ci.yml` - Maven continuous integration
- `cmake_c_ci.yml` - C project CI with CMake
- `cmake_cpp_ci.yml` - C++ project CI with CMake
- `bazel_multi_ci.yml` - Bazel project CI
- `gradle_build.yml` - Gradle build workflow

### Release & Deploy

- `maven_release.yml` - Full Maven release process
- `maven_release_tag.yml` - Release from git tag
- `maven_release_branch.yml` - Release from release branch
- `maven_central_release.yml` - Maven Central deployment
- `cmake_c_release.yml` - C project release
- `cmake_cpp_release.yml` - C++ project release
- `bazel_multi_release.yml` - Bazel project release
- `gradle_release.yml` - Gradle release workflow

### Security

- `maven_security_scan.yml` - Security vulnerability scanning
- `codeql.yml` - CodeQL analysis
- `dependency_review.yml` - Dependency security review

### Utility

- `update_submodule.yml` - Automated submodule updates
- `test_lint.yml` - Workflow validation
- `maven_sbom_generate.yml` - SBOM generation

## Naming Conventions

### Workflow Files

```
<buildsystem>_[lang]_<category>.yml

Examples:
- maven_ci.yml              # Maven CI (no lang needed)
- cmake_c_ci.yml            # CMake + C language
- cmake_cpp_release.yml     # CMake + C++ release
- bazel_multi_ci.yml        # Bazel multi-language
- gradle_build.yml          # Gradle build
```

### Branch Names

```
# For OpenSpec changes
feature/workflows(<issue#>)-<short-name>

# For other changes
<type>/<scope>-<description>

Types: feat, fix, docs, style, refactor, perf, test, chore, security
```

## Security Requirements

All workflows MUST follow these security requirements:

1. **Action Pinning**
   - ALL actions pinned to immutable commit SHAs (40 characters)
   - Semantic version comment above each pinned action
   - No `@v[0-9]`, `@master`, or `@main` references

2. **Permissions**
   - Explicit `permissions:` block on every job
   - Minimal permissions (read-only by default)
   - Write permissions only where needed

3. **Input Validation**
   - All user-controlled inputs validated
   - Regex allowlist patterns used
   - Validation failures exit with error

4. **Secret Handling**
   - No secrets in command-line arguments
   - Secrets in config files with restricted permissions
   - No secrets in echo statements

5. **Binary Downloads**
   - Checksum verification for all downloads
   - HTTPS URLs only
   - Versions pinned

## Repository Strategy

**Decision: MONO-REPO** (made 2025-10-25)

Rationale:
- Unified versioning (no compatibility matrix)
- Single source of truth for docs
- Saves ~230 hours/year vs separate repos
- Atomic cross-cutting updates
- Easier for polyglot projects

## Release Strategy

- Main branch: SNAPSHOT versions
- Release branches: Stable release versions
- Tags: Created on release branches
- Hotfixes: Applied to release branches

## External Dependencies

- GitHub Actions ecosystem
- Third-party actions (all pinned to SHAs)
- Dependabot for automated updates

## Constraints

- All workflows must work with GitHub-hosted runners
- Optional support for self-hosted runners
- Must maintain backward compatibility within major versions
- Security scanning required before releases
