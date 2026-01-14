# Proposal: Fix Test Repository Workflow References (P0 - URGENT)

**Issue**: #20

## Why

All test repositories are referencing an outdated branch (`feature/update-cache-and-sync-ci`) that no longer exists. This causes:
- Daily scheduled workflow runs failing since ~Oct 27
- No validation of workflow changes before merge
- Breaking changes go undetected
- CI/CD pipeline reliability compromised

This is **P0 URGENT** because the test infrastructure is the canary for all workflow changes.

## What Changes

Update workflow references in 3 test repositories from deleted feature branch to `@main`:

1. **artagon-workflow-test-bazel** - 6 occurrences
2. **artagon-workflow-test-cmake** - 9 occurrences
3. **artagon-workflow-test-rust** - 3 commented TODOs

Simple find/replace:
```diff
- @feature/update-cache-and-sync-ci
+ @main
```

## Impact

- **Affected repos**: 3 external test repositories
- **Breaking changes**: None
- **Immediate benefit**: Restore test coverage for all workflow changes

## Scope

### In Scope

- Update workflow refs in artagon-workflow-test-bazel
- Update workflow refs in artagon-workflow-test-cmake
- Update commented TODOs in artagon-workflow-test-rust
- Validate all test jobs pass after fix
- Monitor daily runs for 3 days

### Out of Scope

- Adding new test coverage (separate issue #19)
- Branch protection rules
- Dependabot configuration for workflow refs
