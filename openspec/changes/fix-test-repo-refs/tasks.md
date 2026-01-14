# Tasks: Fix Test Repository Workflow References

## 1. Fix artagon-workflow-test-bazel

- [ ] 1.1 Clone/checkout repository
- [ ] 1.2 Update 6 workflow references to @main
- [ ] 1.3 Commit and push changes
- [ ] 1.4 Trigger manual workflow run
- [ ] 1.5 Verify all 6 jobs pass

**Acceptance**: All Bazel test jobs succeed
**Status**: PENDING

## 2. Fix artagon-workflow-test-cmake

- [ ] 2.1 Clone/checkout repository
- [ ] 2.2 Update 9 workflow references to @main
- [ ] 2.3 Commit and push changes
- [ ] 2.4 Trigger manual workflow run
- [ ] 2.5 Verify all 9 jobs pass

**Acceptance**: All CMake C and C++ test jobs succeed
**Status**: PENDING

## 3. Fix artagon-workflow-test-rust

- [ ] 3.1 Clone/checkout repository
- [ ] 3.2 Update 3 commented TODO references to @main
- [ ] 3.3 Commit and push changes
- [ ] 3.4 Verify placeholder job still works

**Acceptance**: Rust repo ready for future workflow integration
**Status**: PENDING

## 4. Verification

- [ ] 4.1 Monitor Bazel repo daily runs for 3 days
- [ ] 4.2 Monitor CMake repo daily runs for 3 days
- [ ] 4.3 Document results in issue #19
- [ ] 4.4 Close issue #20

**Acceptance**: All daily runs succeed for 3 consecutive days
**Status**: PENDING
