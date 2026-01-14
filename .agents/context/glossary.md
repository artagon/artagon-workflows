# Glossary and Context

Terminology and context for AI agents working on Artagon Workflows.

## Technology Abbreviations

### GitHub Actions

- **GHA** - GitHub Actions
- **SHA** - Secure Hash Algorithm (40-character commit hash)
- **SARIF** - Static Analysis Results Interchange Format
- **OSSF** - Open Source Security Foundation
- **SLSA** - Supply-chain Levels for Software Artifacts

### Build Systems

- **Maven** - Apache Maven build tool for Java/JVM projects
- **CMake** - Cross-platform build system generator for C/C++
- **Bazel** - Google's build tool for multi-language projects
- **Gradle** - Build automation tool for Java/JVM projects
- **Cargo** - Rust package manager and build tool

### Security

- **CVE** - Common Vulnerabilities and Exposures
- **CWE** - Common Weakness Enumeration
- **OSSRH** - OSS Repository Hosting (Sonatype)
- **GPG** - GNU Privacy Guard (signing)
- **SBOM** - Software Bill of Materials

### Workflow Types

- **CI** - Continuous Integration (build, test)
- **CD** - Continuous Deployment (deploy, release)
- **PR** - Pull Request
- **CODEOWNERS** - GitHub file for ownership definitions

## Workflow Naming Convention

```
<buildsystem>_[lang]_<category>.yml

Examples:
- maven_ci.yml              # Maven CI
- cmake_c_ci.yml            # CMake + C language CI
- cmake_cpp_release.yml     # CMake + C++ release
- bazel_multi_ci.yml        # Bazel multi-language CI
- gradle_build.yml          # Gradle build
```

## Branch Naming Convention

```
# OpenSpec changes
feature/workflows(<issue#>)-<short-name>

# Other changes
<type>/<scope>-<description>

Types: feat, fix, docs, security, refactor, test, chore
```

## Security Patterns

### Action Pinning

```yaml
# Format: Comment + SHA
# actions/checkout@v4.2.2
uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683
```

### Permission Levels

| Scope | CI Jobs | Release Jobs | Security Jobs |
|-------|---------|--------------|---------------|
| contents | read | write | read |
| packages | read | write | - |
| security-events | - | - | write |

### Input Validation Patterns

| Input Type | Pattern |
|------------|---------|
| Maven args | `^[-A-Za-z0-9=.,_:/ ]*$` |
| CMake options | `^[-A-Za-z0-9=.,_:/ ]*$` |
| Bazel configs | `^[a-z,_-]+$` |
| Version strings | `^[0-9]+\.[0-9]+\.[0-9]+(-[a-zA-Z0-9]+)?$` |

## Current Repository State

### Workflow Count

- Maven workflows: 9+
- CMake workflows: 5
- Bazel workflows: 2
- Gradle workflows: 2
- Utility workflows: 3+

### Security Status

- Action pinning: 100% complete
- Permissions: 100% complete
- Input validation: 100% complete (high-risk workflows)
- Binary verification: 100% complete

### Decision: Mono-Repo

The repository uses a mono-repo strategy:
- All workflows in single repository
- Unified versioning
- Atomic cross-cutting updates
- Saves ~230 hours/year vs separate repos

## Common Tasks Reference

### Pin New Action

```bash
# Get SHA for tag
gh api repos/OWNER/REPO/git/ref/tags/TAG --jq '.object.sha'
```

### Validate Workflows

```bash
# Local validation
actionlint .github/workflows/*.yml
yamllint .github/workflows/*.yml

# Check for unpinned actions
grep -rn "uses:.*@v[0-9]$" .github/workflows/
```

### OpenSpec Commands

```bash
openspec list              # Active changes
openspec list --specs      # Capabilities
openspec show [item]       # View details
openspec validate --strict # Validate
openspec archive <id> --yes # Archive
```
