# Tasks: Maven SBOM + Sigstore Attestation

## 1. Review Existing Implementation

- [ ] 1.1 Verify `maven_sbom_generate.yml` meets all requirements
- [ ] 1.2 Test SBOM generation with sample project
- [ ] 1.3 Test Cosign signing flow
- [ ] 1.4 Test SPDX conversion
- [ ] 1.5 Test schema validation

**Acceptance**: All features work as documented
**Status**: PENDING - Review needed

## 2. Gap Analysis

- [ ] 2.1 Compare implementation to issue #13 requirements
- [ ] 2.2 Document any missing features
- [ ] 2.3 Create follow-up issues if needed

**Acceptance**: Clear understanding of completion status
**Status**: PENDING

## 3. Documentation

- [ ] 3.1 Update docs/MAVEN.md with SBOM section
- [ ] 3.2 Add usage examples for maven_sbom_generate.yml
- [ ] 3.3 Document pom.xml plugin configuration

**Acceptance**: SBOM workflow fully documented
**Status**: PENDING

## 4. Testing

- [ ] 4.1 Add SBOM test to artagon-workflow-test-maven
- [ ] 4.2 Verify SBOM output format
- [ ] 4.3 Verify signature validity
- [ ] 4.4 Test with multi-module project

**Acceptance**: SBOM workflow validated in test infrastructure
**Status**: PENDING

## 5. Issue Resolution

- [ ] 5.1 Document completion status in issue #13
- [ ] 5.2 Close issue #13 if complete
- [ ] 5.3 Or create specific follow-up issues

**Acceptance**: Issue properly resolved
**Status**: PENDING
