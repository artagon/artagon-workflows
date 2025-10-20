# Artagon Workflows – Codex Context

## Overview

Repository of reusable GitHub Actions workflows for Artagon projects. Provides standardized CI/CD pipelines for Maven, C/C++, and Bazel builds that can be called from any Artagon project.

### Key Components

- `.github/workflows/` - Reusable workflow files
  - Maven workflows (9 files): CI, build, deploy, release, security scanning
  - C/C++ workflows (4 files): CMake-based CI and release
  - Bazel workflows (2 files): CI and release
  - Utility workflows: submodule updates
- `docs/` - Comprehensive workflow documentation
  - `MAVEN.md` - Maven workflow reference
  - `CPP.md` - C/C++ workflow reference
  - `BAZEL.md` - Bazel workflow reference
- `examples/` - Usage examples for each workflow type
  - `maven/` - Maven CI, release, deploy examples
  - `cpp/` - C/C++ CI and release examples
  - `bazel/` - Bazel CI and release examples
- `.agents/` - Agent preferences and context (Claude, Codex)
- `.legal/artagon-license` - License submodule

## Workflow Categories

### Maven Workflows (9 workflows)
- **maven-ci.yml** - Full CI pipeline (build, test, verify)
- **maven-build.yml** - Build only, no deployment
- **maven-deploy.yml** - Deploy snapshots to OSSRH
- **maven-release.yml** - Complete release process
- **maven_release_tag.yml** - Release from git tag
- **maven_release_branch.yml** - Release from release branch
- **maven-central-release.yml** - Maven Central deployment
- **maven-github-release.yml** - GitHub release creation
- **maven_security_scan.yml** - Security vulnerability scanning

### C/C++ Workflows (4 workflows)
- **c-ci.yml** - C project CI with CMake
- **cpp-ci.yml** - C++ project CI with CMake
- **c-release.yml** - C project release
- **cpp-release.yml** - C++ project release

### Bazel Workflows (2 workflows)
- **bazel-ci.yml** - Bazel multi-config CI
- **bazel-release.yml** - Bazel project release

### Utility Workflows (1 workflow)
- **update-submodule.yml** - Automated submodule updates

## Workflow Design Principles

### Reusability
- All workflows use `workflow_call` trigger
- Configurable via inputs and secrets
- Support multiple versions of tools (Java 17/21/25, etc.)

### Consistency
- Standardized parameter naming
- Common caching strategies
- Uniform error handling

### Security
- No hardcoded credentials
- All secrets via GitHub Secrets
- GPG signing for releases
- Dependency vulnerability scanning

### Performance
- Automatic dependency caching (Maven, Bazel, CMake)
- Parallel job execution where possible
- Incremental builds

## Usage Pattern

Projects call workflows from their own `.github/workflows/` files:

```yaml
# Calling project's .github/workflows/ci.yml
jobs:
  ci:
    uses: artagon/artagon-workflows/.github/workflows/maven-ci.yml@v1
    secrets: inherit
```

## Versioning Strategy

- **Tags**: v1, v1.0.0, v1.1.0, etc.
- **Major versions** (v1, v2): Breaking changes
- **Minor versions** (v1.1.0): New features, backward compatible
- **Patch versions** (v1.0.1): Bug fixes

Projects should pin to major version (`@v1`) for stability with automatic updates, or specific version (`@v1.2.0`) for absolute stability.

## Development Workflow

### Adding New Workflows
1. Create issue describing the workflow need
2. Create semantic branch: `feat/<issue>-<description>`
3. Create workflow in `.github/workflows/`
4. Add documentation to `docs/`
5. Add usage example to `examples/`
6. Test in separate repository
7. Create PR and get review
8. Merge and tag release

### Modifying Existing Workflows
1. Check if change is breaking (requires major version bump)
2. Create issue and branch
3. Update workflow file
4. Update documentation
5. Update examples if needed
6. Test with existing projects
7. Provide migration guide if breaking
8. Create PR, review, merge, and tag

### Testing Workflows
- Never test workflows directly in artagon-workflows
- Clone workflow to test repository
- Test with real project builds
- Verify all input combinations
- Check error handling

## Backward Compatibility

### Within Major Versions
- Maintain backward compatibility
- Don't remove or rename required inputs
- Don't change default behavior breaking existing uses
- Deprecate before removing (minimum 1 minor version)

### Breaking Changes
- Increment major version (v1 → v2)
- Document breaking changes in CHANGELOG.md
- Provide migration guide
- Update all examples

## Documentation Standards

### Workflow Files
- Comment complex logic
- Document all inputs with descriptions
- Document all outputs
- List required secrets

### README.md
- Keep workflow list current
- Update quick start examples
- Maintain version recommendations

### Workflow Documentation (docs/)
- Document all parameters (inputs, outputs, secrets)
- Provide usage examples
- List supported versions
- Include troubleshooting tips

## Common Patterns

### Caching
```yaml
- uses: actions/cache@v4
  with:
    path: ~/.m2/repository
    key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
```

### Multi-Version Support
```yaml
inputs:
  java-version:
    type: string
    default: '25'
```

### Secret Handling
```yaml
secrets:
  OSSRH_USERNAME:
    required: true
```

## Maintenance Notes

- Test workflow changes with multiple projects before releasing
- Update documentation when adding/modifying workflows
- Keep examples current with workflow changes
- Tag releases following semver
- Maintain CHANGELOG.md for all releases
- Monitor workflow execution times and optimize caching

## Pitfalls / Reminders

- **Never test in main branch** - Use separate test repository
- **Backward compatibility** - Breaking changes require major version bump
- **Documentation** - Update docs with every workflow change
- **Examples** - Keep examples working and current
- **Secrets** - Never hardcode credentials in workflows
- **Caching** - Always use caching for dependencies to improve performance
- **Error handling** - Workflows should fail gracefully with clear messages
- **Multi-project testing** - Test workflows with different project types

## Recent Changes

### Initial Repository Setup (October 2025)
- Created artagon-workflows repository
- Moved 16 reusable workflows from artagon-common
- Added comprehensive documentation (MAVEN.md, CPP.md, BAZEL.md)
- Created usage examples for all workflow types
- Set up agent preferences (.agents/claude/, .agents/codex/)
- Added artagon-license submodule for dual licensing

## File Organization

```
artagon-workflows/
├── .agents/                      # Agent preferences
│   ├── claude/preferences.md
│   └── codex/
│       ├── preferences.md
│       └── project-context.md
├── .github/workflows/           # Reusable workflows
│   ├── maven-*.yml              # Maven workflows (9 files)
│   ├── {c,cpp}-*.yml           # C/C++ workflows (4 files)
│   ├── bazel-*.yml             # Bazel workflows (2 files)
│   └── update-submodule.yml    # Utility workflows
├── .legal/artagon-license/     # License submodule
├── docs/                        # Documentation
│   ├── MAVEN.md                # Maven workflow reference
│   ├── CPP.md                  # C/C++ workflow reference
│   └── BAZEL.md                # Bazel workflow reference
├── examples/                    # Usage examples
│   ├── maven/                  # Maven examples
│   ├── cpp/                    # C/C++ examples
│   └── bazel/                  # Bazel examples
├── LICENSE                      # Dual license (AGPL-3.0 / Commercial)
├── README.md                    # Main documentation
├── CONTRIBUTING.md              # Contribution guidelines
└── CHANGELOG.md                 # Release notes

```

## Related Repositories

- **artagon-common** - Project templates, configs, scripts
- **artagon-license** - License management

## Development Process Summary

For all changes:
1. Create/find issue with appropriate label
2. Create semantic branch
3. Make changes, test in separate repository
4. Update documentation and examples
5. Create PR for review
6. After merge, tag release if needed

No exceptions - this workflow is mandatory for traceability and quality.
