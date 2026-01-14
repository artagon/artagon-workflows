# Workflow Testing Guide

Comprehensive guide for testing reusable workflows against real open-source projects.

## Overview

This guide explains how to exercise the reusable workflows in this repository against real open-source projects across Maven/Java, C (CMake), C++ (CMake), Bazel (multi-language), and Rust (Cargo) to maintain confidence in cross-language coverage.

## Test Matrix

| Language / Build Tool | CI Workflow | Release Workflow | Sample Project |
|-----------------------|-------------|------------------|----------------|
| Java (Maven) | `maven_ci.yml` | `maven_release.yml` | [spring-petclinic](https://github.com/spring-projects/spring-petclinic) |
| C (CMake) | `cmake_c_ci.yml` | `cmake_c_release.yml` | [curl/curl](https://github.com/curl/curl) |
| C++ (CMake) | `cmake_cpp_ci.yml` | `cmake_cpp_release.yml` | [fmtlib/fmt](https://github.com/fmtlib/fmt) |
| Bazel (Polyglot) | `bazel_multi_ci.yml` | `bazel_multi_release.yml` | [google/flatbuffers](https://github.com/google/flatbuffers) |
| Rust (Cargo) | Planned | Planned | [rust-lang/rustlings](https://github.com/rust-lang/rustlings) |

> **Tip:** Fork each project under a test organization so we can add workflow references without affecting upstream repositories.

## Local Validation

### Quick Checks

```bash
# Run actionlint on all workflows
actionlint .github/workflows/*.yml

# Run yamllint
yamllint .github/workflows/*.yml

# Check for unpinned actions
grep -rn "uses:.*@v[0-9]$" .github/workflows/

# Check for missing permissions
for f in .github/workflows/*.yml; do
  grep -q "permissions:" "$f" || echo "Missing: $f"
done
```

### Security Validation

```bash
# Check for @main/@master references
grep -rn "uses:.*@\(main\|master\)$" .github/workflows/

# Verify SHA format (40 characters)
grep -rn "uses:.*@[a-f0-9]\{40\}" .github/workflows/ | wc -l
```

## Preparing Test Repositories

1. **Fork projects** into `github.com/artagon-test-labs/<project>`

2. **Add reusable workflow references** in each fork:
   ```yaml
   # .github/workflows/ci.yml in the fork
   name: CI
   on:
     pull_request:
     push:
       branches: [main]
   jobs:
     main:
       uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@<ref>
       secrets: inherit
       with:
         java-version: '21'
   ```

3. **Pin to the commit SHA** of the branch you are validating (e.g., `@<current-sha>`), not just `@main`

## Test Plan by Workflow Group

### Maven Workflows

1. In the forked Maven project, create a branch `workflow-test/maven-ci`
2. Update the local CI pipeline to consume `maven_ci.yml`
3. Trigger workflow runs:
   - Push commits (build + tests)
   - Enable code coverage upload if relevant
4. Validate success:
   - Build/test steps complete
   - Input validation fails gracefully if unsafe `maven-args` is supplied
   - Permissions remain least-privilege (no unexpected repository write)
5. Repeat for release flows using dry-run tags

### CMake Workflows (C/C++)

1. Configure project to use `cmake_c_ci.yml` or `cmake_cpp_ci.yml`
2. Run matrices for Linux, macOS, Windows, sanitizers, and coverage
3. Confirm:
   - Input validation catches malformed `cmake-options`
   - Artifacts upload successfully
   - Sanitizer jobs surface failures if defects introduced
4. For release workflows, create temporary tag and verify artifacts upload

### Bazel Workflows

1. Integrate `bazel_multi_ci.yml` into a Bazel project fork
2. Trigger runs with multiple configs (`release,debug,asan,tsan`)
3. Confirm:
   - Input validation rejects unsafe target strings
   - Cache usage and coverage steps behave correctly
   - Buildifier and bazel query jobs produce expected artifacts

### Gradle Workflows

1. Configure project to use `gradle_build.yml`
2. Verify build and test steps complete
3. Test release workflow with dry-run

## Automating Regression Runs

Create a workflow in this repository (`.github/workflows/fixture-smoke-tests.yml`) that:

1. Checks out a test matrix file (YAML) listing `<repository, workflow-file, ref>`
2. Uses `gh workflow run` to trigger the reusable workflow in each fork
3. Polls run status via GitHub CLI and reports pass/fail in a summary table

Schedule the automation weekly and on demand (manual dispatch).

## Documenting Results

For each test cycle:

1. Record the workflow SHAs tested and repository commit SHA
2. Capture outcomes (success/failure, failure logs)
3. File issues or PRs in this repo when failures trace back to workflow changes
4. Update this document with new fixtures or language support

## CI Validation Workflows

The repository includes automated validation:

- **test_lint.yml** - actionlint and yamllint validation
- **test_security.yml** - Security checks
- **codeql.yml** - CodeQL analysis
- **copilot-setup-steps.yml** - Copilot environment validation

## Test Fixtures

Local test fixtures are available in `test/fixtures/`:

```
test/fixtures/
├── maven/          # Minimal Maven project
├── cmake_c/        # Minimal C project
├── cmake_cpp/      # Minimal C++ project
├── bazel/          # Minimal Bazel project
└── rust/           # Minimal Rust project
```

These fixtures are used for basic workflow validation and can be extended for comprehensive testing.

## See Also

- [Workflow Usage Guide](WORKFLOWS_USAGE.md)
- [Security Requirements](../openspec/specs/workflow-security/spec.md)
- [Contributing Guidelines](../openspec/contributing.md)
