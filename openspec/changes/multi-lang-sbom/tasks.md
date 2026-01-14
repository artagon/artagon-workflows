# Tasks: Multi-Language SBOM Workflows

## 1. Maven SBOM (DONE)

- [x] 1.1 Create `maven_sbom_generate.yml`
- [x] 1.2 CycloneDX v1.6 generation
- [x] 1.3 SPDX conversion
- [x] 1.4 Cosign signing
- [x] 1.5 Schema validation

**Acceptance**: Maven SBOM workflow complete
**Status**: ✅ COMPLETE

## 2. C/C++ SBOM

- [ ] 2.1 Create `cmake_sbom_generate.yml`
- [ ] 2.2 Implement syft/cdxgen integration
- [ ] 2.3 Support vcpkg dependencies
- [ ] 2.4 Support Conan dependencies
- [ ] 2.5 Support system package detection
- [ ] 2.6 Add CycloneDX output
- [ ] 2.7 Add SPDX output
- [ ] 2.8 Add Cosign signing

**Acceptance**: C/C++ SBOM generation works
**Status**: PENDING

## 3. Rust SBOM

- [ ] 3.1 Create `rust_sbom_generate.yml`
- [ ] 3.2 Implement cargo-sbom integration
- [ ] 3.3 Handle workspace projects
- [ ] 3.4 Include build dependencies
- [ ] 3.5 Add CycloneDX output
- [ ] 3.6 Add SPDX output
- [ ] 3.7 Add Cosign signing

**Acceptance**: Rust SBOM generation works
**Status**: PENDING

## 4. Vulnerability Scanning

- [ ] 4.1 Add Trivy scanning to all SBOM workflows
- [ ] 4.2 Add Grype scanning option
- [ ] 4.3 Add OSV-Scanner option
- [ ] 4.4 Generate SARIF reports
- [ ] 4.5 Upload to GitHub Security tab
- [ ] 4.6 Configure severity thresholds

**Acceptance**: Vulnerability scanning integrated
**Status**: PENDING

## 5. License Compliance

- [ ] 5.1 Add license detection to SBOM workflows
- [ ] 5.2 Implement copyleft detection
- [ ] 5.3 Add allowed-licenses configuration
- [ ] 5.4 Generate NOTICE files
- [ ] 5.5 Add license compatibility checking

**Acceptance**: License compliance checking works
**Status**: PENDING

## 6. Documentation

- [ ] 6.1 Create docs/SBOM.md
- [ ] 6.2 Document each language workflow
- [ ] 6.3 Document vulnerability scanning
- [ ] 6.4 Document license compliance
- [ ] 6.5 Add troubleshooting guide

**Acceptance**: Complete SBOM documentation
**Status**: PENDING

## 7. Testing

- [ ] 7.1 Test Maven SBOM with sample project
- [ ] 7.2 Test C/C++ SBOM with CMake project
- [ ] 7.3 Test Rust SBOM with Cargo project
- [ ] 7.4 Validate SBOM formats
- [ ] 7.5 Verify signature validity

**Acceptance**: All SBOM workflows validated
**Status**: PENDING
