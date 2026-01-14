# Tasks: Security Hardening

## 1. Add Permissions to bazel_multi_release.yml

- [x] 1.1 Add `permissions:` to `create-release` job
- [x] 1.2 Add `permissions:` to `build-linux` job
- [x] 1.3 Add `permissions:` to `build-macos` job
- [x] 1.4 Add `permissions:` to `build-windows` job
- [x] 1.5 Add `permissions:` to `build-source` job
- [x] 1.6 Verify each job has minimal required permissions

**Acceptance**: All jobs have explicit permissions blocks
**Status**: ✅ COMPLETE - Added `contents: write` to all 5 jobs

## 2. Add Input Validation

- [x] 2.1 Add validation to `update_submodule.yml`
- [x] 2.2 Add validation to `bazel_multi_release.yml`
- [x] 2.3 Add validation to `maven_deploy.yml`
- [x] 2.4 Add validation to `maven_github_release.yml`
- [x] 2.5 Add validation to `maven_sbom_generate.yml`
- [x] 2.6 Add validation to `cmake_c_release.yml`
- [x] 2.7 Add validation to `cmake_cpp_release.yml`
- [x] 2.8 Add validation to `cmake_cpack_release.yml`

**Acceptance**: All user inputs validated before shell execution
**Status**: ✅ COMPLETE - Added "Validate inputs" step to all 8 workflows

## 3. Add Checksum Verification

- [x] 3.1 Add SHA256 verification to CycloneDX CLI download (`maven_sbom_generate.yml`)
- [x] 3.2 Add SHA256 verification to actionlint download (`test_lint.yml`)
- [x] 3.3 Pin linuxdeploy to release version with checksum (`cmake_c_release.yml`, `cmake_cpp_release.yml`)

**Acceptance**: All binary downloads have checksum verification
**Status**: ✅ COMPLETE - Added SHA256 verification to all binary downloads

## 4. Update Security Validation

- [x] 4.1 Update `test_lint.yml` security-validation to check for permissions blocks
- [ ] 4.2 Update `test_lint.yml` to detect unvalidated inputs (deferred - complex pattern matching)
- [ ] 4.3 Add check for binary downloads without checksums (deferred - complex pattern matching)

**Acceptance**: Security validation catches these issues
**Status**: ⏳ PARTIAL - Permissions check exists (warns), advanced checks deferred

## 5. Verification

- [ ] 5.1 Run actionlint on all modified workflows
- [ ] 5.2 Run test_lint.yml and verify it passes
- [ ] 5.3 Test input validation rejects invalid inputs
- [ ] 5.4 Test checksum verification fails on mismatch

**Acceptance**: All security checks pass
**Status**: ⏳ PENDING - Will be verified in CI
