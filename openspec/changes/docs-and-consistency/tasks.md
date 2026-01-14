# Tasks: Documentation and Consistency

## 1. Add Usage Documentation to Workflows

- [x] 1.1 Add usage docs to `gradle_build.yml`
- [x] 1.2 Add usage docs to `gradle_release.yml`
- [x] 1.3 Add usage docs to `maven_build.yml`
- [x] 1.4 Add usage docs to `maven_bump_version.yml`
- [x] 1.5 Add usage docs to `maven_deploy.yml`
- [x] 1.6 Add usage docs to `maven_release_branch.yml`
- [x] 1.7 Add usage docs to `maven_release_tag.yml`
- [x] 1.8 Add usage docs to `maven_release.yml`
- [x] 1.9 Add usage docs to `maven_sbom_generate.yml`
- [x] 1.10 Add usage docs to `maven_security_scan.yml`

**Acceptance**: All reusable workflows have usage documentation
**Status**: ✅ COMPLETE - Added usage comment blocks to all 10 workflows

## 2. Create Gradle Documentation

- [x] 2.1 Create `docs/GRADLE.md`
- [x] 2.2 Document `gradle_build.yml` usage and inputs
- [x] 2.3 Document `gradle_release.yml` usage and inputs
- [x] 2.4 Add examples for common use cases
- [ ] 2.5 Link from README.md

**Acceptance**: Gradle workflows fully documented
**Status**: ✅ COMPLETE - Created comprehensive GRADLE.md

## 3. Fix Naming Convention

- [x] 3.1 Rename `copilot-setup-steps.yml` to `copilot_setup_steps.yml`
- [x] 3.2 Update any references to the old filename
- [ ] 3.3 Verify CI still works after rename

**Acceptance**: All workflows use snake_case naming
**Status**: ✅ COMPLETE - Renamed workflow file

## 4. Clean Up Orphan Documentation

- [x] 4.1 Review `RELEASE_RUST.md` content
- [x] 4.2 Either remove or mark as "planned" if Rust workflows coming
- [ ] 4.3 Verify all docs reference existing workflows

**Acceptance**: No orphan documentation
**Status**: ✅ COMPLETE - Added "PLANNED" status notice to RELEASE_RUST.md

## 5. Verification

- [ ] 5.1 Run actionlint on renamed workflow
- [ ] 5.2 Verify README links work
- [ ] 5.3 Verify docs/ folder is consistent

**Acceptance**: All checks pass
**Status**: ⏳ PENDING - Will be verified in CI
