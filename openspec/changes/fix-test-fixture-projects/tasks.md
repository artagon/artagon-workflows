# Tasks: Fix Test Fixture Project Failures

## 1. Fix Bazel Test Project Structure

- [ ] 1.1 Create/update MODULE.bazel with module definition
- [ ] 1.2 Create root BUILD.bazel file
- [ ] 1.3 Create src/BUILD.bazel with cc_binary and cc_test targets
- [ ] 1.4 Create src/hello.cc with minimal implementation
- [ ] 1.5 Create src/hello_test.cc with minimal test
- [ ] 1.6 Update .bazelrc with necessary configs (release, debug, coverage)

**Acceptance**: `bazel build //...` and `bazel test //...` pass locally
**Status**: PENDING

## 2. Fix Bazel Workflow Artifact Naming

- [ ] 2.1 Update bazel_multi_ci.yml artifact names to include matrix context
- [ ] 2.2 Update bazel_multi_ci.yml test-results artifact naming
- [ ] 2.3 Update bazel_multi_ci.yml dependency-graph artifact naming

**Acceptance**: No artifact naming conflicts in matrix builds
**Status**: PENDING

## 3. Fix Bazel Query Syntax

- [ ] 3.1 Update bazel query command to handle multiple targets
- [ ] 3.2 Add proper quoting for target list
- [ ] 3.3 Ensure graceful failure if query fails

**Acceptance**: Bazel Query Analysis job passes
**Status**: PENDING

## 4. Fix CMake Test Project (if needed)

- [ ] 4.1 Verify CMakeLists.txt structure
- [ ] 4.2 Ensure source files exist and compile
- [ ] 4.3 Verify test targets work
- [ ] 4.4 Check coverage configuration

**Acceptance**: CMake CI jobs pass
**Status**: PENDING (may already work)

## 5. Fix Rust Test Project (if needed)

- [ ] 5.1 Verify Cargo.toml is valid
- [ ] 5.2 Ensure src/lib.rs or src/main.rs exists
- [ ] 5.3 Verify cargo build/test work

**Acceptance**: Rust CI jobs pass
**Status**: PENDING (placeholder project)

## 6. Update Test Repo CI Configurations

- [ ] 6.1 Update artagon-workflow-test-bazel ci.yml to use valid targets
- [ ] 6.2 Remove references to non-existent targets
- [ ] 6.3 Configure appropriate bazel-configs for testing

**Acceptance**: CI configuration matches actual project structure
**Status**: PENDING

## 7. Verification

- [ ] 7.1 Trigger Bazel test repo CI and verify all jobs pass
- [ ] 7.2 Trigger CMake test repo CI and verify all jobs pass
- [ ] 7.3 Trigger Rust test repo CI and verify pass
- [ ] 7.4 Run trigger_test_repos.yml and verify all dispatches succeed
- [ ] 7.5 Monitor scheduled runs for 3 days

**Acceptance**: All test repos consistently pass
**Status**: PENDING
