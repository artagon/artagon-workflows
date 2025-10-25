# SBOM Workflow Implementation Request

## Context
I need you to create comprehensive SBOM (Software Bill of Materials) generation workflows for my projects. These workflows should run on GitHub Actions and generate SBOMs in both SPDX and CycloneDX formats, include vulnerability scanning, license compliance checking, and artifact signing.

## Project Types to Support

### 1. Java Maven BOM (Bill of Materials) Project
- Project structure: Multi-module Maven project with a parent POM that defines dependency management
- Build tool: Maven 3.9+
- Java version: 17+
- The parent POM acts as a BOM that other projects import
- Need to track both direct dependencies and transitive dependencies
- Should generate SBOMs for each module and an aggregate SBOM
- Include Maven plugins and their dependencies in the SBOM

### 2. C Project
- Build system: CMake and Make
- Compiler: GCC and Clang support
- Dependencies managed through: vcpkg, Conan, and system packages
- Need to capture: static libraries, dynamic libraries, header-only libraries
- Include compiler toolchain information
- Track both build-time and runtime dependencies

### 3. C++ Project
- Build system: CMake with modern C++20
- Package managers: Conan, vcpkg, and CPM
- Need to track: STL version, Boost libraries, third-party dependencies
- Include template libraries and header-only dependencies
- Support for both static and dynamic linking

### 4. Rust Project
- Build system: Cargo
- Include: cargo dependencies, build dependencies, dev dependencies
- Track: procedural macros, build scripts (build.rs)
- Support workspace projects with multiple crates
- Include rustc version and LLVM version information

## Requirements for Each Workflow

### Core Requirements:
1. **SBOM Generation**:
    - Generate SBOMs in SPDX 2.3 format
    - Generate SBOMs in CycloneDX 1.5 format
    - Include all direct and transitive dependencies
    - Capture dependency licenses
    - Include dependency versions with commit hashes where applicable
    - Generate both human-readable and machine-readable formats

2. **Vulnerability Scanning**:
    - Scan generated SBOMs for known vulnerabilities
    - Use multiple scanners (Trivy, Grype, OSV-Scanner)
    - Generate SARIF reports for GitHub Security tab
    - Fail builds on critical vulnerabilities (configurable)
    - Create issues for new vulnerabilities found

3. **License Compliance**:
    - Check for copyleft licenses (GPL, AGPL, LGPL)
    - Check for proprietary license restrictions
    - Generate license compatibility matrix
    - Fail on incompatible licenses (configurable)
    - Generate NOTICE file with all attributions

4. **Signing and Attestation**:
    - Sign SBOMs using Sigstore/Cosign
    - Generate SLSA provenance attestations
    - Create in-toto attestations
    - Store signatures in OCI registry
    - Verify signatures in downstream workflows

5. **Storage and Distribution**:
    - Upload SBOMs as GitHub artifacts
    - Attach SBOMs to GitHub releases
    - Push SBOMs to OCI registry (ghcr.io)
    - Store in SBOM-specific repository
    - Generate SBOM comparison reports between versions

6. **Automation**:
    - Run on: push to main, pull requests, releases, and weekly schedule
    - Cache dependencies for faster builds
    - Parallelize SBOM generation where possible
    - Incremental SBOM updates for large projects
    - Matrix builds for multiple OS/architecture combinations

### Language-Specific Requirements:

#### Java Maven BOM:
- Handle multi-module projects correctly
- Include Maven plugin dependencies
- Support for different scopes (compile, runtime, test, provided, system)
- Include Java vendor and version in SBOM
- Track annotation processors and code generators
- Support for Maven profiles
- Include build tool (Maven) version

#### C/C++:
- Track preprocessor definitions that affect dependencies
- Include compiler flags that affect ABI
- Support for different build configurations (Debug/Release)
- Track system libraries and their versions
- Handle header-only libraries correctly
- Include toolchain information (compiler, linker, stdlib)
- Support cross-compilation scenarios

#### Rust:
- Include feature flags used in compilation
- Track build.rs dependencies separately
- Support for workspace members
- Include procedural macro dependencies
- Track target-specific dependencies
- Include rustc and cargo versions
- Handle vendored dependencies

## Output Format

For each project type, provide:

1. **Complete GitHub Actions workflow file** (.github/workflows/sbom.yml)
2. **Configuration files** needed (e.g., .trivyignore, cyclonedx-config.json)
3. **Scripts** for complex SBOM processing (if needed)
4. **Docker/Container file** for consistent build environment
5. **Documentation** with:
    - Setup instructions
    - Configuration options
    - Example SBOM output
    - Troubleshooting guide
    - Integration guide for downstream consumers

## Additional Considerations

1. **Performance**: Workflows should complete within 10 minutes for medium-sized projects
2. **Caching**: Implement intelligent caching for dependencies and SBOM generation
3. **Incremental Updates**: Support incremental SBOM updates for large projects
4. **Reproducibility**: Ensure SBOMs are reproducible (same input = same SBOM)
5. **Extensibility**: Design workflows to be easily extended with custom checks
6. **Monitoring**: Include metrics and notifications for SBOM generation failures
7. **Compliance**: Ensure SBOMs meet NTIA minimum elements requirements
8. **Integration**: Provide examples of integrating with SBOM management platforms

## Example Structure Response
```yaml
# For each language, provide:
name: SBOM Generation for [Language]
on:
  # ... triggers
jobs:
  generate-sbom:
    # ... implementation
  scan-vulnerabilities:
    # ... implementation  
  check-licenses:
    # ... implementation
  sign-and-attest:
    # ... implementation
  publish:
    # ... implementation
```

## Success Criteria

The generated workflows should:
- Successfully generate valid SBOMs that can be validated by standard tools
- Detect all dependencies including transitive ones
- Identify known vulnerabilities with low false positive rate
- Correctly identify license conflicts
- Produce signed and verifiable SBOMs
- Be maintainable and well-documented
- Work across Linux, macOS, and Windows runners
- Support both x86_64 and ARM64 architectures

Please generate the complete implementation for all four project types with all configurations and documentation needed for production use.

Additional context for my projects:
- Java Maven BOM: We use Spring Boot 3.2, have 15 modules, and deploy to AWS
- C Project: Embedded system project using FreeRTOS, ARM Cortex-M target
- C++ Project: High-performance computing application using OpenMP and MPI
- Rust Project: Web service using Tokio, Actix-web, with 5 workspace members

Compliance requirements:
- Must meet NIST SSDF requirements
- Need to comply with EU Cyber Resilience Act
- Require SLSA Level 3 attestations
- Must support air-gapped environments

Output preferences:
- Prefer Syft over other SBOM generators
- Use GitHub's dependency submission API
- Store SBOMs in AWS S3 in addition to GitHub
- Integrate with Dependency-Track for SBOM management

Performance constraints:
- Workflows must complete in under 5 minutes
- Use self-hosted runners with 16 cores
- Cache size limit of 10GB
- Support incremental builds
