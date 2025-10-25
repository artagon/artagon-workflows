# SBOM (Software Bill of Materials)

This repository includes comprehensive SBOM generation capabilities for both the artagon-workflows repository itself and as a reference for consumers implementing SBOM workflows in their projects.

## Repository SBOM Workflow

The [sbom.yml](.github/workflows/sbom.yml) workflow generates SBOMs for this repository:

- **Triggers**: Push to main, pull requests, releases, weekly schedule, manual dispatch
- **SBOM Formats**: SPDX 2.3 JSON, CycloneDX 1.5 JSON/XML
- **Vulnerability Scanning**: Trivy, Grype, OSV-Scanner
- **Security Integration**: SARIF upload to GitHub Security tab
- **Container**: Chainguard wolfi-base (hardened, minimal attack surface)

### Features

✅ **Multi-format SBOM Generation** - SPDX and CycloneDX for maximum compatibility  
✅ **Multi-scanner Security** - Triple validation with Trivy, Grype, and OSV-Scanner  
✅ **GitHub Security Integration** - SARIF reports uploaded to Security tab  
✅ **Performance Optimized** - Cached scanner databases for faster runs  
✅ **Hardened Runtime** - Chainguard wolfi-base container with pinned digest  
✅ **Comprehensive Artifacts** - All SBOMs and scan results preserved for 14 days  

### Workflow Output

The workflow generates:
- `sbom.spdx.json` - SPDX 2.3 format SBOM
- `sbom.cyclonedx.json` - CycloneDX 1.5 JSON format
- `sbom.cyclonedx.xml` - CycloneDX 1.5 XML format
- `trivy.sarif` - Trivy vulnerability scan (uploaded to Security tab)
- `grype.txt` - Grype vulnerability scan results
- `osv.json` - OSV-Scanner vulnerability data

## Consumer Reference Documentation

For projects looking to implement comprehensive SBOM workflows, we provide extensive documentation:

### 📖 Requirements & Planning
- **[.model-context/SBOM_REQ.md](.model-context/SBOM_REQ.md)** - Complete SBOM requirements for Java, C, C++, and Rust projects
  - Multi-format SBOM generation (SPDX, CycloneDX)
  - Vulnerability scanning with multiple tools
  - License compliance checking
  - Signing and attestation (Cosign, SLSA provenance)
  - Distribution strategies (GitHub, OCI, S3, Dependency-Track)
  - Compliance requirements (NTIA, NIST SSDF, EU CRA, SLSA Level 3)

### 🏗️ Implementation Guide
- **[.model-context/SBOM_IMPLEMENTATION.md](.model-context/SBOM_IMPLEMENTATION.md)** - Detailed implementation plan
  - Multi-job workflow architecture
  - Language-specific implementation details (Maven, C, C++, Rust, Bazel)
  - Tooling choices and rationale
  - Performance optimization strategies (<5 minutes on 16-core runners)
  - Air-gapped and offline support
  - Caching and incremental update strategies

### 🎓 Best Practices
- **[.model-context/skills/sbom-workflows.md](.model-context/skills/sbom-workflows.md)** - SBOM workflow best practices
  - Core principles and workflow structure
  - Language-specific tips and tricks
  - Tooling reference (Syft, Trivy, Grype, OSV-Scanner, Cosign)
  - Common pitfalls and troubleshooting
  - Testing and validation strategies

## Quick Start for Consumers

To implement SBOM generation in your project:

1. **Review Requirements** - Read [SBOM_REQ.md](.model-context/SBOM_REQ.md) to understand the full scope
2. **Choose Your Approach** - Decide which features you need (basic SBOM vs full compliance)
3. **Follow Implementation Plan** - Use [SBOM_IMPLEMENTATION.md](.model-context/SBOM_IMPLEMENTATION.md) as your guide
4. **Apply Best Practices** - Reference [sbom-workflows.md](.model-context/skills/sbom-workflows.md) throughout
5. **Start Simple** - Begin with basic SBOM generation (like our sbom.yml), then add advanced features

## Security & Compliance

The SBOM workflow follows security best practices:
- ✅ All actions pinned to immutable commit SHAs
- ✅ Container image pinned by digest (Chainguard wolfi-base)
- ✅ Explicit least-privilege permissions
- ✅ SARIF upload for GitHub Advanced Security integration
- ✅ Weekly automated scans for continuous monitoring

## Support & Feedback

For questions or issues with SBOM generation:
- Review the comprehensive documentation in `.model-context/`
- Check the [sbom.yml](.github/workflows/sbom.yml) workflow for a working example
- Consult [SBOM_IMPLEMENTATION.md](.model-context/SBOM_IMPLEMENTATION.md) for advanced features
- Open an issue if you need additional guidance
