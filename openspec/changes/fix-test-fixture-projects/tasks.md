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

## 5. Create Rust CI Workflow (rust_ci.yml)

- [x] 5.1 Create rust_ci.yml with workflow_call trigger
- [x] 5.2 Add validate-inputs job with input validation
- [x] 5.3 Add build-test job with cargo build/test
- [x] 5.4 Add clippy job for linting
- [x] 5.5 Add rustfmt job for format checking
- [x] 5.6 Add coverage job with cargo-tarpaulin
- [x] 5.7 Add Nix flake detection support
- [x] 5.8 Add Cargo caching

**Acceptance**: rust_ci.yml is callable from other repos
**Status**: COMPLETED

## 6. Fix Rust Test Project

- [x] 6.1 Verify Cargo.toml is valid
- [x] 6.2 Ensure src/lib.rs exists with tests
- [x] 6.3 Ensure src/main.rs exists
- [x] 6.4 Update CI to call rust_ci.yml reusable workflow
- [x] 6.5 Add test configurations (stable, MSRV)

**Acceptance**: Rust CI jobs pass
**Status**: COMPLETED

## 7. Update Test Repo CI Configurations

- [x] 7.1 Update artagon-workflow-test-bazel ci.yml to use valid targets
- [x] 7.2 Remove references to non-existent targets
- [x] 7.3 Configure appropriate bazel-configs for testing

**Acceptance**: CI configuration matches actual project structure
**Status**: COMPLETED

## 8. Cost Reduction

- [x] 8.1 Remove macOS from CI workflows
- [x] 8.2 Remove Windows from CI workflows
- [x] 8.3 Keep release workflows with optional macOS/Windows builds

**Acceptance**: CI only runs on ubuntu-latest
**Status**: COMPLETED

## 9. Verification

- [x] 9.1 Trigger Bazel test repo CI and verify all jobs pass
- [x] 9.2 Trigger CMake test repo CI and verify all jobs pass
- [x] 9.3 Trigger Rust test repo CI and verify pass
- [x] 9.4 Run trigger_test_repos.yml and verify all dispatches succeed
- [ ] 9.5 Monitor scheduled runs for 3 days

**Acceptance**: All test repos consistently pass
**Status**: MOSTLY COMPLETE (monitoring pending)
