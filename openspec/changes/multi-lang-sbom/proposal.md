# Proposal: Comprehensive Multi-Language SBOM Workflows

**Issue**: #2

## Why

Software supply chain security requires complete dependency visibility across all project types. Each language ecosystem has unique:
- Dependency management systems
- Build tools and package managers
- Transitive dependency resolution
- License and vulnerability data sources

## What Changes

Implement SBOM generation workflows for:

1. **Java Maven** - CycloneDX + SPDX (DONE: `maven_sbom_generate.yml`)
2. **C/C++** - CMake/vcpkg/Conan dependency tracking
3. **Rust** - Cargo dependency graph
4. **Bazel** - Multi-language dependency tracking

Each workflow provides:
- SPDX 2.3 format output
- CycloneDX 1.5/1.6 format output
- Vulnerability scanning (Trivy, Grype, OSV-Scanner)
- License compliance checking
- Sigstore/Cosign signing
- SLSA provenance attestations

## Impact

- **New workflows**: Language-specific SBOM generators
- **Integration**: Attach SBOMs to releases
- **Security**: Automated vulnerability detection
- **Compliance**: License compatibility checking

## Scope

### In Scope

- SBOM generation for Maven (DONE), C/C++, Rust
- CycloneDX and SPDX format support
- Vulnerability scanning integration
- License compliance checking
- Sigstore signing
- Release artifact attachment

### Out of Scope

- Bazel SBOM (complex multi-language, separate effort)
- OCI registry distribution (future enhancement)
- SBOM comparison tools
