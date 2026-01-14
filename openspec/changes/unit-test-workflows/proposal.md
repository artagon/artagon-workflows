# Proposal: Unit Test Workflows

**Issue**: #7

## Why

Individual workflow behavior needs validation:
- Verify correct behavior with various configurations
- Catch regressions in workflow changes
- Ensure outputs and artifacts are correct
- Test edge cases and error handling

## What Changes

Create unit test workflows for each reusable workflow family:
- Maven CI workflows
- CMake C/C++ CI workflows
- Bazel CI workflows
- Rust workflows (when available)

Each test covers:
- Minimal configuration (defaults)
- Custom configuration (non-default options)
- Edge cases (skip tests, custom args)
- Output verification
- Artifact validation

## Impact

- **New files**: `test/workflows/test_*.yml`
- **Test fixtures**: `test/fixtures/`
- **CI integration**: Tests run on PR and main

## Scope

### In Scope

- Unit tests for maven_ci.yml
- Unit tests for cmake_c_ci.yml
- Unit tests for cmake_cpp_ci.yml
- Unit tests for bazel_multi_ci.yml
- Configuration matrix testing
- Output/artifact verification

### Out of Scope

- Integration testing (issue #19)
- Test infrastructure (issue #10)
- New workflow development
