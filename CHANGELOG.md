# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- C++26 standard support in C++ CI workflows
- CI status badges to README.md
- CHANGELOG.md for tracking project changes

### Changed
- Pin linuxdeploy to specific release (1-alpha-20250213-2) in C/C++ release workflows
- Update documentation references from `artagon/artagon-common` to `artagon/artagon-workflows`

### Fixed
- Resolved TODOs in cmake_cpp_release.yml and cmake_c_release.yml for linuxdeploy versioning

## [2.0.0] - 2024-10-20

### Breaking Changes
- Implemented build-system-first naming convention
  - Old: `maven-ci.yml`, `cpp-ci.yml`, `c-ci.yml`, `bazel-ci.yml`
  - New: `maven_ci.yml`, `cmake_cpp_ci.yml`, `cmake_c_ci.yml`, `bazel_multi_ci.yml`
  - **Action Required**: Update all workflow references to use new filenames

### Added
- Secure Gradle workflows with SBOM generation
- Reusable SBOM generation workflow (`maven_sbom_generate.yml`)
- Phase 1 testing infrastructure
- Comprehensive security documentation
- Language-specific release strategy guides:
  - Java/Maven (`docs/RELEASE_JAVA.md`)
  - C (`docs/RELEASE_C.md`)
  - C++ (`docs/RELEASE_CPP.md`)
  - Rust (`docs/RELEASE_RUST.md`)
- OSS release strategies analysis (`docs/OSS_RELEASE_STRATEGIES.md`)
- Naming conventions guide (`docs/NAMING_CONVENTIONS.md`)
- Workflows usage documentation (`docs/WORKFLOWS_USAGE.md`)
- CPack packaging guide (`docs/CPACK_PACKAGING.md`)
- Test repository trigger workflow
- CI workflow for PR validation
- Release branch strategy documentation

### Changed
- Enhanced supply chain security across all workflows
- Updated all GitHub Actions to commit SHA pinning
- Migrated from deprecated actions/cache v4.1.2 to v4.2.0
- Improved input validation in all workflows
- Updated workflow examples for all languages

### Fixed
- Corrected actions/cache SHA references to v4.2.0
- Fixed upload-artifact action SHA in maven_sbom_generate.yml
- Addressed PR review comments for Gradle workflows

## [1.0.0] - Initial Release

### Added
- Initial repository setup with reusable workflows
- Maven CI/CD workflows (9 workflows):
  - `maven_ci.yml` - Continuous integration
  - `maven_build.yml` - Build without deploy
  - `maven_deploy.yml` - Deploy snapshots
  - `maven_release.yml` - Full release process
  - `maven_release_tag.yml` - Release from tag
  - `maven_release_branch.yml` - Release from branch
  - `maven_central_release.yml` - Maven Central deployment
  - `maven_github_release.yml` - GitHub Packages release
  - `maven_security_scan.yml` - Security scanning
- C/C++ CMake workflows (5 workflows):
  - `cmake_c_ci.yml` - C project CI
  - `cmake_cpp_ci.yml` - C++ project CI
  - `cmake_c_release.yml` - C project release
  - `cmake_cpp_release.yml` - C++ project release
  - `cmake_cpack_release.yml` - Multi-format packaging
- Bazel workflows (2 workflows):
  - `bazel_multi_ci.yml` - Multi-language CI
  - `bazel_multi_release.yml` - Multi-language release
- Security workflows:
  - `codeql.yml` - CodeQL security analysis
  - `dependency_review.yml` - Dependency security review
- Validation workflows:
  - `pr_validation.yml` - PR title and commit validation
  - `shellcheck.yml` - Shell script linting
- Automation workflows:
  - `auto_merge.yml` - Dependabot auto-merge
  - `update_submodule.yml` - Submodule update automation
- Comprehensive documentation
- Usage examples for all build systems
- Dependabot configuration with action pinning
- Release branch strategy
- .gitignore configuration

### Security
- All GitHub Actions pinned to immutable commit SHAs
- Input validation with regex patterns
- Least-privilege permissions model
- TLS 1.3 enforcement for downloads
- SHA256 checksum verification
- Multiple security scanning tools integration:
  - CodeQL semantic analysis
  - Trivy vulnerability scanner
  - OWASP Dependency Check
  - Sonatype OSS Index
  - SpotBugs static analysis

---

## Version Strategy

- **Major version** (X.0.0): Breaking changes to workflow APIs, inputs, or behavior
- **Minor version** (x.Y.0): New workflows or backward-compatible features
- **Patch version** (x.y.Z): Bug fixes, documentation updates, security patches

## Migration Guides

### Migrating from v1.x to v2.x

**Workflow File Naming Changes:**

Update all workflow references in your projects:

```yaml
# Before (v1.x)
uses: artagon/artagon-workflows/.github/workflows/maven-ci.yml@v1

# After (v2.x)
uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@v2
```

**Complete Mapping:**

| v1.x Filename | v2.x Filename |
|---------------|---------------|
| `maven-ci.yml` | `maven_ci.yml` |
| `maven-build.yml` | `maven_build.yml` |
| `maven-deploy.yml` | `maven_deploy.yml` |
| `maven-release.yml` | `maven_release.yml` |
| `cpp-ci.yml` | `cmake_cpp_ci.yml` |
| `cpp-release.yml` | `cmake_cpp_release.yml` |
| `c-ci.yml` | `cmake_c_ci.yml` |
| `c-release.yml` | `cmake_c_release.yml` |
| `bazel-ci.yml` | `bazel_multi_ci.yml` |
| `bazel-release.yml` | `bazel_multi_release.yml` |

See [docs/NAMING_CONVENTIONS.md](docs/NAMING_CONVENTIONS.md) for rationale.

## Links

- [Documentation](https://github.com/artagon/artagon-workflows/tree/main/docs)
- [Contributing Guide](CONTRIBUTING.md)
- [Release Strategy](docs/RELEASE.md)
- [Issue Tracker](https://github.com/artagon/artagon-workflows/issues)
