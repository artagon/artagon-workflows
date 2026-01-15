# Proposal: Fix Test Fixture Project Failures

## Problem Statement

The test repositories (artagon-workflow-test-bazel, artagon-workflow-test-cmake, artagon-workflow-test-rust) are experiencing CI failures due to:

1. **Missing Bazel targets** - Test project references non-existent `//src:hello` and `//src:hello_test` targets
2. **Artifact naming conflicts** - Multiple matrix jobs upload artifacts with the same name
3. **Invalid Bazel query syntax** - Space-separated targets causing query parse errors
4. **Missing BUILD files** - No `src/` package defined in Bazel test project
5. **Missing Rust CI workflow** - No `rust_ci.yml` reusable workflow exists, so Rust test project cannot use reusable workflow pattern

## Impact

- Test repo CI runs fail even though reusable workflows are correct
- Cannot verify workflow changes are working properly
- False negatives in automated testing pipeline
- Reduces confidence in workflow quality

## Proposed Solution

Fix the test fixture projects to have proper project structure that exercises the reusable workflows correctly:

1. **Bazel test project**: Add minimal BUILD files with actual targets
2. **CMake test projects**: Ensure CMakeLists.txt and source files exist
3. **Artifact naming**: Fix matrix artifact uploads to use unique names
4. **Query syntax**: Fix Bazel query to handle multiple targets correctly
5. **Rust CI workflow**: Create `rust_ci.yml` reusable workflow with build, test, clippy, rustfmt, and coverage jobs
6. **Rust test project**: Update CI to call the new reusable workflow

## Success Criteria

- All test repository CI runs pass (green)
- Workflows are properly exercised (build, test, coverage)
- Automatic triggering produces passing results
- No false positives or negatives

## Priority

**HIGH** - Blocking ability to validate workflow changes

## Related Issues

- Discovered during issue #20 (fix-test-repo-refs) implementation
- Blocks full verification of trigger_test_repos.yml functionality
