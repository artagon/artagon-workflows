# Tasks: Documentation and Consistency

## 1. Add Usage Documentation to Workflows

- [ ] 1.1 Add usage docs to `gradle_build.yml`
- [ ] 1.2 Add usage docs to `gradle_release.yml`
- [ ] 1.3 Add usage docs to `maven_build.yml`
- [ ] 1.4 Add usage docs to `maven_bump_version.yml`
- [ ] 1.5 Add usage docs to `maven_deploy.yml`
- [ ] 1.6 Add usage docs to `maven_release_branch.yml`
- [ ] 1.7 Add usage docs to `maven_release_tag.yml`
- [ ] 1.8 Add usage docs to `maven_release.yml`
- [ ] 1.9 Add usage docs to `maven_sbom_generate.yml`
- [ ] 1.10 Add usage docs to `maven_security_scan.yml`

**Acceptance**: All reusable workflows have usage documentation

## 2. Create Gradle Documentation

- [ ] 2.1 Create `docs/GRADLE.md`
- [ ] 2.2 Document `gradle_build.yml` usage and inputs
- [ ] 2.3 Document `gradle_release.yml` usage and inputs
- [ ] 2.4 Add examples for common use cases
- [ ] 2.5 Link from README.md

**Acceptance**: Gradle workflows fully documented

## 3. Fix Naming Convention

- [ ] 3.1 Rename `copilot-setup-steps.yml` to `copilot_setup_steps.yml`
- [ ] 3.2 Update any references to the old filename
- [ ] 3.3 Verify CI still works after rename

**Acceptance**: All workflows use snake_case naming

## 4. Clean Up Orphan Documentation

- [ ] 4.1 Review `RELEASE_RUST.md` content
- [ ] 4.2 Either remove or mark as "planned" if Rust workflows coming
- [ ] 4.3 Verify all docs reference existing workflows

**Acceptance**: No orphan documentation

## 5. Verification

- [ ] 5.1 Run actionlint on renamed workflow
- [ ] 5.2 Verify README links work
- [ ] 5.3 Verify docs/ folder is consistent

**Acceptance**: All checks pass
