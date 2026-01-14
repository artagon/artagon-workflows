# Tasks: Secure Gradle Workflows

## 1. Create Workflow

- [ ] 1.1 Create `gradle_secure_build.yml` reusable workflow
- [ ] 1.2 Implement wrapper SHA-256 verification
- [ ] 1.3 Implement dependency verification check
- [ ] 1.4 Implement lockfile drift detection
- [ ] 1.5 Implement repository policy enforcement
- [ ] 1.6 Add SBOM generation step
- [ ] 1.7 Add SBOM validation step
- [ ] 1.8 Add Cosign attestation for releases

**Acceptance**: Workflow enforces all security checks
**Status**: PENDING

## 2. Create Templates

- [ ] 2.1 Create `settings.gradle.kts` template
- [ ] 2.2 Create `build.gradle.kts` template with CycloneDX
- [ ] 2.3 Create `gradle.properties` template
- [ ] 2.4 Create `verification-metadata.xml` example
- [ ] 2.5 Create lockfile generation script

**Acceptance**: Templates are reusable and documented
**Status**: PENDING

## 3. Documentation

- [ ] 3.1 Create `docs/GRADLE_SECURITY.md`
- [ ] 3.2 Document trust model
- [ ] 3.3 Document verification setup
- [ ] 3.4 Document lockfile workflow
- [ ] 3.5 Add troubleshooting guide
- [ ] 3.6 Add migration guide for existing projects

**Acceptance**: Complete documentation
**Status**: PENDING

## 4. Testing

- [ ] 4.1 Create test Gradle project
- [ ] 4.2 Test wrapper verification
- [ ] 4.3 Test dependency verification
- [ ] 4.4 Test lockfile enforcement
- [ ] 4.5 Test SBOM generation
- [ ] 4.6 Test Cosign attestation
- [ ] 4.7 Test failure modes

**Acceptance**: All security features validated
**Status**: PENDING

## 5. Integration

- [ ] 5.1 Add to artagon-workflow-test-gradle repo (create if needed)
- [ ] 5.2 Configure daily validation runs
- [ ] 5.3 Document in main README

**Acceptance**: Workflow integrated into test infrastructure
**Status**: PENDING
