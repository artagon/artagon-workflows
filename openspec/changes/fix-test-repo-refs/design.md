# Design: Fix Test Repository Workflow References

## Current State

Test repositories reference a deleted branch:
```yaml
uses: artagon/artagon-workflows/.github/workflows/bazel_multi_ci.yml@feature/update-cache-and-sync-ci
```

This branch was merged/deleted, breaking all test repository workflows.

## Target State

All test repositories reference `@main`:
```yaml
uses: artagon/artagon-workflows/.github/workflows/bazel_multi_ci.yml@main
```

## Changes by Repository

### 1. artagon-workflow-test-bazel

**File**: `.github/workflows/ci.yml`
**Lines**: 33, 41, 50, 59, 69, 79 (6 occurrences)

```diff
-    uses: artagon/artagon-workflows/.github/workflows/bazel_multi_ci.yml@feature/update-cache-and-sync-ci
+    uses: artagon/artagon-workflows/.github/workflows/bazel_multi_ci.yml@main
```

### 2. artagon-workflow-test-cmake

**File**: `.github/workflows/ci.yml`
**Lines**: 34, 41, 48, 55, 62, 71, 78, 85, 92 (9 occurrences)

```diff
-    uses: artagon/artagon-workflows/.github/workflows/cmake_c_ci.yml@feature/update-cache-and-sync-ci
+    uses: artagon/artagon-workflows/.github/workflows/cmake_c_ci.yml@main

-    uses: artagon/artagon-workflows/.github/workflows/cmake_cpp_ci.yml@feature/update-cache-and-sync-ci
+    uses: artagon/artagon-workflows/.github/workflows/cmake_cpp_ci.yml@main
```

### 3. artagon-workflow-test-rust

**File**: `.github/workflows/ci.yml`
**Lines**: 70, 77, 84 (3 commented TODOs)

```diff
-  #   uses: artagon/artagon-workflows/.github/workflows/rust_ci.yml@feature/update-cache-and-sync-ci
+  #   uses: artagon/artagon-workflows/.github/workflows/rust_ci.yml@main
```

## Testing Plan

### Manual Trigger
```bash
gh workflow run ci.yml --repo artagon/artagon-workflow-test-bazel
gh workflow run ci.yml --repo artagon/artagon-workflow-test-cmake
gh workflow run ci.yml --repo artagon/artagon-workflow-test-rust
```

### Success Criteria
- All Bazel test jobs pass (6/6)
- All CMake C test jobs pass (5/5)
- All CMake C++ test jobs pass (4/4)
- Rust placeholder job continues to work
- No workflow errors in logs

## Future Prevention

1. Use semantic version tags instead of branch names
2. Configure Dependabot for workflow references
3. Add CI check to validate test repos reference valid branches
