# Tasks: Security Hardening

## 1. Add Permissions to bazel_multi_release.yml

- [ ] 1.1 Add `permissions:` to `create-release` job
- [ ] 1.2 Add `permissions:` to `build-linux` job
- [ ] 1.3 Add `permissions:` to `build-macos` job
- [ ] 1.4 Add `permissions:` to `build-windows` job
- [ ] 1.5 Add `permissions:` to `build-source` job
- [ ] 1.6 Verify each job has minimal required permissions

**Acceptance**: All jobs have explicit permissions blocks

## 2. Add Input Validation

- [ ] 2.1 Add validation to `update_submodule.yml`
- [ ] 2.2 Add validation to `bazel_multi_release.yml`
- [ ] 2.3 Add validation to `maven_deploy.yml`
- [ ] 2.4 Add validation to `maven_github_release.yml`
- [ ] 2.5 Add validation to `maven_sbom_generate.yml`
- [ ] 2.6 Add validation to `cmake_c_release.yml`
- [ ] 2.7 Add validation to `cmake_cpp_release.yml`
- [ ] 2.8 Add validation to `cmake_cpack_release.yml`

**Acceptance**: All user inputs validated before shell execution

## 3. Add Checksum Verification

- [ ] 3.1 Add SHA256 verification to CycloneDX CLI download (`maven_sbom_generate.yml`)
- [ ] 3.2 Add SHA256 verification to actionlint download (`test_lint.yml`)
- [ ] 3.3 Pin linuxdeploy to release version with checksum (`cmake_c_release.yml`, `cmake_cpp_release.yml`)

**Acceptance**: All binary downloads have checksum verification

## 4. Update Security Validation

- [ ] 4.1 Update `test_security.yml` to check for permissions blocks
- [ ] 4.2 Update `test_security.yml` to detect unvalidated inputs
- [ ] 4.3 Add check for binary downloads without checksums

**Acceptance**: Security validation catches these issues

## 5. Verification

- [ ] 5.1 Run actionlint on all modified workflows
- [ ] 5.2 Run test_security.yml and verify it passes
- [ ] 5.3 Test input validation rejects invalid inputs
- [ ] 5.4 Test checksum verification fails on mismatch

**Acceptance**: All security checks pass
