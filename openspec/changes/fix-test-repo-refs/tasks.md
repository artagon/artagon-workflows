# Tasks: Fix Test Repository Workflow References

## 1. Fix artagon-workflow-test-bazel

- [x] 1.1 Clone/checkout repository
- [x] 1.2 Update 6 workflow references to @main
- [x] 1.3 Commit and push changes
- [x] 1.4 Trigger manual workflow run
- [x] 1.5 Verify validate-inputs passes (6/6 jobs pass validation)

**Acceptance**: All Bazel test validate-inputs jobs succeed
**Status**: COMPLETE

## 2. Fix artagon-workflow-test-cmake

- [x] 2.1 Clone/checkout repository
- [x] 2.2 Update 9 workflow references to @main
- [x] 2.3 Commit and push changes
- [x] 2.4 Trigger manual workflow run
- [x] 2.5 Verify workflow triggered successfully

**Acceptance**: CMake test repo references @main
**Status**: COMPLETE

## 3. Fix artagon-workflow-test-rust

- [x] 3.1 Clone/checkout repository
- [x] 3.2 Update 3 commented TODO references to @main
- [x] 3.3 Commit and push changes
- [x] 3.4 Verify placeholder job still works (passed)

**Acceptance**: Rust repo ready for future workflow integration
**Status**: COMPLETE

## 4. Additional Bug Fix

- [x] 4.1 Fixed regex bug in bazel_multi_ci.yml
      - Changed `'^[@/A-Za-z0-9_:.\\*\\- ]+$'` to `'^[@/A-Za-z0-9_:.* -]+$'`
      - Moved hyphen to end of character class to fix "Invalid range end" error
- [x] 4.2 Committed and pushed fix to main

**Status**: COMPLETE

## 5. Verification

- [x] 5.1 All 6 Bazel validate-inputs jobs pass
- [x] 5.2 Rust CI passes completely
- [ ] 5.3 Note: Remaining failures in Bazel/CMake are due to outdated action SHAs (separate issue)

**Notes**:
- The action SHA references (cachix/install-nix-action, bazelbuild/setup-bazelisk)
  are outdated and need updating in a separate issue
- Issue #20 scope (fix workflow refs to @main) is complete

**Status**: COMPLETE (primary objective achieved)
