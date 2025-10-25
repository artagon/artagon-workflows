# Test Repository Strategy

This document describes the separate test repository approach for validating artagon-workflows reusable workflows.

## Overview

Instead of embedding unit tests within the artagon-workflows repository, we use separate test repositories that consume the reusable workflows as external consumers would. This provides more realistic testing and keeps the main workflow repository focused.

## Test Repositories

### 1. artagon-workflow-test-maven
**URL**: https://github.com/artagon/artagon-workflow-test-maven
**Purpose**: Test Maven CI workflow (`maven_ci.yml`)

**Test Matrix**:
- Java versions: 17, 21, 25
- Maven arguments: default, custom
- Test execution: with/without tests, with integration tests
- Coverage: optional upload

**Triggers**:
- Push/PR to test repo
- Daily schedule (2 AM UTC)
- Repository dispatch from main repo
- Manual workflow dispatch

### 2. artagon-workflow-test-cmake
**URL**: https://github.com/artagon/artagon-workflow-test-cmake
**Purpose**: Test CMake C and C++ CI workflows (`cmake_c_ci.yml`, `cmake_cpp_ci.yml`)

**Test Matrix**:
- C standards: C11, C17, C23
- C++ standards: C++17, C++20, C++23
- CMake options: default, custom
- Test execution: with tests

**Triggers**:
- Push/PR to test repo
- Daily schedule (2 AM UTC)
- Repository dispatch from main repo
- Manual workflow dispatch

### 3. artagon-workflow-test-bazel
**URL**: https://github.com/artagon/artagon-workflow-test-bazel
**Purpose**: Test Bazel CI workflow (`bazel_multi_ci.yml`)

**Test Matrix**:
- Bazel versions: latest, 7.x
- Configurations: default, release, debug
- Targets: all, custom
- Test execution: with/without tests

**Triggers**:
- Push/PR to test repo
- Daily schedule (2 AM UTC)
- Repository dispatch from main repo
- Manual workflow dispatch

### 4. artagon-workflow-test-rust
**URL**: https://github.com/artagon/artagon-workflow-test-rust
**Purpose**: Test Rust CI workflow (when available)
**Status**: Placeholder - awaiting `rust_ci.yml` in artagon-workflows

**Planned Test Matrix**:
- Rust versions: stable, beta, nightly
- Features: default, custom
- Checks: clippy, rustfmt
- Test execution: with/without tests

**Triggers**:
- Push/PR to test repo
- Daily schedule (2 AM UTC)
- Repository dispatch from main repo
- Manual workflow dispatch

## Triggering Strategy

### Automatic Triggers

Test repositories are automatically triggered when:

1. **Workflow Changes** (via `trigger_test_repos.yml`):
   - Any `.yml`/`.yaml` file in `.github/workflows/` is pushed to main
   - Sends `repository_dispatch` event type: `workflow-updated`

2. **Releases**:
   - When a release is published in artagon-workflows
   - Sends `repository_dispatch` event type: `release-published`

3. **Daily Schedule**:
   - Each test repo runs at 2 AM UTC daily
   - Catches breaking changes from dependencies

### Manual Triggers

Test repositories can be manually triggered via:

1. **Main Repository Dispatch**:
   ```bash
   gh workflow run trigger_test_repos.yml
   ```

2. **Individual Test Repository**:
   ```bash
   gh workflow run ci.yml --repo artagon/artagon-workflow-test-maven
   gh workflow run ci.yml --repo artagon/artagon-workflow-test-cmake
   gh workflow run ci.yml --repo artagon/artagon-workflow-test-bazel
   gh workflow run ci.yml --repo artagon/artagon-workflow-test-rust
   ```

3. **GitHub UI**:
   - Navigate to repository → Actions → CI workflow → Run workflow

## Benefits of Separate Repositories

### 1. Realistic Testing
- Tests workflows exactly as external consumers use them
- No special accommodations or mocking needed
- Validates the `uses: artagon/artagon-workflows/.github/workflows/...@main` pattern

### 2. Isolation
- Test failures don't affect main repository CI
- Can test breaking changes without breaking main repo
- Independent versioning and maintenance

### 3. Simplicity
- Main workflows remain simple (no `working-directory` inputs needed)
- No complex test infrastructure in main repo
- Clear separation of concerns

### 4. Flexibility
- Each test repo can have its own release cycle
- Can test against multiple workflow versions
- Easy to add new test scenarios

### 5. Documentation
- Test repos serve as usage examples
- Demonstrate best practices for consuming workflows
- Living documentation that's always up-to-date

## Required Secrets

The main artagon-workflows repository requires:

- **WORKFLOW_DISPATCH_TOKEN**: GitHub PAT with `repo` scope to trigger repository_dispatch events in test repos

To create:
```bash
# Create a fine-grained PAT with:
# - Repository access: artagon/artagon-workflow-test-*
# - Permissions: Actions (read/write)

# Add to artagon/artagon-workflows repository secrets
gh secret set WORKFLOW_DISPATCH_TOKEN --repo artagon/artagon-workflows
```

## Maintenance

### Adding a New Test Repository

1. **Create Repository**:
   ```bash
   gh repo create artagon/artagon-workflow-test-<name> --public
   ```

2. **Add Test Project**:
   - Copy appropriate fixture from `test/fixtures/`
   - Add `.github/workflows/ci.yml` calling reusable workflow
   - Add README documenting test matrix

3. **Update Trigger Workflow**:
   - Add new job to `.github/workflows/trigger_test_repos.yml`
   - Include in summary

4. **Document**:
   - Update this file with new repository details
   - Update `test/README.md`
   - Update issue #7

### Updating Test Matrix

To add new test configurations to existing test repos:

1. Edit `.github/workflows/ci.yml` in the test repository
2. Add new job with different inputs
3. Update README test matrix table
4. Update `test-summary` job dependencies

### Removing a Test Repository

1. Archive the repository on GitHub
2. Remove trigger job from `trigger_test_repos.yml`
3. Update documentation

## Monitoring

### Health Checks

- **Daily Runs**: All test repos run daily at 2 AM UTC
- **Success Rate**: Should maintain >95% pass rate
- **Failure Alerts**: Failed runs should be investigated promptly

### Key Metrics

Track in test repositories:
- Build success rate
- Test pass rate
- Average build time
- Artifact upload success

## Troubleshooting

### Test Repo Not Triggering

1. Check `WORKFLOW_DISPATCH_TOKEN` is set and valid
2. Verify token has access to target repository
3. Check workflow run logs in main repo
4. Manually trigger to verify test repo CI works

### Test Failures

1. Check if failure is in test repo or reusable workflow
2. Verify workflow version (`@main` vs `@vX.Y.Z`)
3. Check for breaking changes in recent workflow updates
4. Review GitHub Actions changelog for platform changes

### Daily Schedule Not Running

1. Verify cron syntax in test repo CI
2. Check repository activity (inactive repos may pause scheduled workflows)
3. Manually trigger to verify workflow is valid

## Related Documents

- [Testing Strategy](TESTING_STRATEGY.md) - Overall testing approach (5 phases)
- [Test Fixtures](../test/README.md) - Local test fixture documentation
- [Security Implementation](SECURITY_IMPLEMENTATION_PLAN.md) - Security testing approach

## Future Enhancements

- [ ] Add workflow version matrix testing (@main, @vX.Y.Z, @develop)
- [ ] Create test result aggregation dashboard
- [ ] Add performance benchmarking
- [ ] Implement automatic rollback on test failures
- [ ] Add integration tests between multiple workflows
- [ ] Create test coverage reporting
