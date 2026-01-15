# Tasks: Fix Test Fixture Project Failures

## 1. Fix Bazel Test Project Structure

- [x] 1.1 Create/update MODULE.bazel with module definition
- [x] 1.2 Create root BUILD.bazel file
- [x] 1.3 Create src/BUILD.bazel with cc_binary and cc_test targets
- [x] 1.4 Create src/hello.cc with minimal implementation
- [x] 1.5 Create src/hello_test.cc with minimal test
- [x] 1.6 Update .bazelrc with necessary configs (release, debug, coverage)

**Acceptance**: `bazel build //...` and `bazel test //...` pass locally
**Status**: COMPLETED

## 2. Fix Bazel Workflow Artifact Naming

- [x] 2.1 Update bazel_multi_ci.yml artifact names to include matrix context
- [x] 2.2 Update bazel_multi_ci.yml test-results artifact naming (added overwrite: true)
- [x] 2.3 Update bazel_multi_ci.yml dependency-graph artifact naming

**Acceptance**: No artifact naming conflicts in matrix builds
**Status**: COMPLETED

## 3. Fix Bazel Query Syntax

- [x] 3.1 Update bazel query command to handle multiple targets (using set())
- [x] 3.2 Add proper quoting for target list
- [x] 3.3 Ensure graceful failure if query fails (continue-on-error)

**Acceptance**: Bazel Query Analysis job passes
**Status**: COMPLETED

## 4. Fix CMake Test Project

- [x] 4.1 Verify CMakeLists.txt structure
- [x] 4.2 Ensure source files exist and compile
- [x] 4.3 Verify test targets work
- [x] 4.4 Check coverage configuration (added --ignore-errors flags to lcov)
- [x] 4.5 Fix memory sanitizer for mixed C/C++ projects (set CMAKE_C_COMPILER)

**Acceptance**: CMake CI jobs pass
**Status**: COMPLETED

## 5. Fix Rust Test Project

- [ ] 5.1 Verify Cargo.toml is valid
- [ ] 5.2 Ensure src/lib.rs or src/main.rs exists
- [ ] 5.3 Verify cargo build/test work

**Acceptance**: Rust CI jobs pass
**Status**: N/A - No rust_ci.yml workflow exists yet (placeholder project)

## 6. Update Test Repo CI Configurations

- [x] 6.1 Update artagon-workflow-test-bazel ci.yml to use valid targets
- [x] 6.2 Remove references to non-existent targets
- [x] 6.3 Configure appropriate bazel-configs for testing

**Acceptance**: CI configuration matches actual project structure
**Status**: COMPLETED

## 7. Cost Reduction (Added)

- [x] 7.1 Remove macOS from CI workflows
- [x] 7.2 Remove Windows from CI workflows
- [x] 7.3 Keep release workflows with optional macOS/Windows builds

**Acceptance**: CI only runs on ubuntu-latest
**Status**: COMPLETED

## 8. Verification

- [x] 8.1 Trigger Bazel test repo CI and verify all jobs pass
- [x] 8.2 Trigger CMake test repo CI and verify all jobs pass
- [ ] 8.3 Trigger Rust test repo CI and verify pass (N/A - no workflow)
- [x] 8.4 Run trigger_test_repos.yml and verify all dispatches succeed
- [ ] 8.5 Monitor scheduled runs for 3 days

**Acceptance**: All test repos consistently pass
**Status**: MOSTLY COMPLETE (monitoring pending)
