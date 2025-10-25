# SBOM Workflows Skill Guide

**Skill**: SBOM Automation and Supply-Chain Security
**Category**: Security, Compliance
**Level**: Advanced
**Last Updated**: 2025-10-25

---

## Overview

This guide summarizes best practices for designing, implementing, and operating Software Bill of Materials (SBOM) workflows as defined in `SBOM_REQ.md` and detailed in `SBOM_IMPLEMENTATION.md`.

Use alongside:
- `SBOM_REQ.md` for requirements
- `SBOM_IMPLEMENTATION.md` for implementation plan
- `SECURITY_IMPLEMENTATION_PLAN.md` for scheduling

---

## Core Principles

1. **Comprehensive Coverage**
   - Generate SPDX 2.3 and CycloneDX 1.5 SBOMs.
   - Include direct, transitive, build-time, and runtime dependencies.
   - Capture toolchain info (compiler, runtime, build scripts).

2. **Security & Compliance**
   - Run Trivy, Grype, OSV-Scanner against generated SBOMs.
   - Produce SARIF, fail on critical vulnerabilities (configurable).
   - Enforce license policies (copyleft, proprietary restrictions).
   - Map controls to NTIA minimum elements, NIST SSDF, EU CRA.

3. **Signing & Attestation**
   - Use Cosign for SBOM signing (Chainguard images first, Distroless fallback).
   - Produce SLSA level 3 provenance and in-toto attestations.
   - Support keyless (OIDC) and key-based modes for air-gapped setups.

4. **Distribution & Storage**
   - Upload artifacts to GitHub (artifacts, releases), ghcr, AWS S3.
   - Submit SBOMs to Dependency-Track and GitHub dependency submission API.
   - Maintain comparison reports (`syft diff`) between versions.

5. **Performance & Resilience**
   - Target <5-minute runtime on 16-core self-hosted runners.
   - Cache dependency trees, vulnerability databases (≤10 GB).
   - Support incremental SBOM updates and air-gapped operations.

---

## Workflow Structure

Recommended jobs (per language workflow):

1. `setup-environment`
   - Authenticate registries (Chainguard primary).
   - Restore caches, install language toolchains.

2. `generate-sbom`
   - Run Syft with language-specific commands.
   - Output SPDX + CycloneDX, both JSON and human-readable.

3. `scan-vulnerabilities`
   - Trivy, Grype, OSV-Scanner.
   - Aggregate SARIF; configurable failure thresholds.

4. `license-compliance`
   - License matrix, allow/deny enforcement, NOTICE generation.

5. `sign-and-attest`
   - Cosign signing, SLSA provenance (`slsa-generic`), in-toto statements.

6. `publish`
   - Upload to GitHub artifacts/releases, ghcr, S3.
   - Dependency submission API, Dependency-Track upload.
   - SBOM diff vs previous release.

7. `quality-gates`
   - Summaries, notifications, metrics export.

---

## Language-Specific Tips

### Java (Maven BOM)
- Gather dependency scopes, plugin dependencies, annotation processors.
- Capture Java vendor/version via `java -XshowSettings:properties`.
- Support profiles and multi-module aggregation.

### C / C++ (CMake)
- Collect compiler/linker versions (`gcc -v`, `clang --version`).
- Enumerate system packages, static/dynamic libs (`ldd`, `otool`).
- Incorporate vcpkg/Conan/CPM lockfiles.
- Handle cross-compilation flags and sanitizer builds.

### Rust (Cargo)
- Use `cargo metadata --locked` for workspace graphs.
- Track features, build.rs dependencies, procedural macros.
- Record `rustc --version --verbose` (includes LLVM info).

### Bazel/Polyglot
- Use Syft’s Bazel integration or `bazel query` for dependency graphs.
- Capture Nix vs non-Nix flows when applicable.

---

## Tooling Reference

| Function | Preferred Tool | Notes |
|----------|----------------|-------|
| SBOM generation | Syft (Chainguard image) | SPDX + CycloneDX |
| Vulnerability scan | Trivy, Grype, OSV-Scanner | Multi-scanner approach |
| Signing | Cosign | Support keyless + key-based |
| Provenance | `slsa-framework/slsa-github-generator` | Level 3 verifiable builds |
| Attestations | in-toto | Reference SBOM digests |
| License analysis | Trivy license mode, Licensee | Build compliance matrix |
| Diffing | `syft diff` | Compare across releases |

---

## Secrets & Infrastructure

- Cosign private keys or OIDC identities.
- AWS credentials (IAM role/user) for S3.
- Dependency-Track API key + project ID.
- ghcr credentials (PAT or GitHub token).
- Access to Chainguard registries (mirror for air-gapped).
- Self-hosted runner bootstrap scripts installing required CLIs.

---

## Testing Strategy

- Follow `TESTING_INSTRUCTIONS.md` for fixture repositories (Maven, C, C++, Bazel, Rust).
- Validate runtime and output formats.<n>This ensures no workflow regression slips through.
- Add smoke tests to run nightly/weekly via automation driver.

---

## Documentation Checklist

- Setup steps (secrets, runners, tool installation).
- Configuration options per workflow (inputs, thresholds).
- Example outputs (SPDX JSON, CycloneDX XML, SARIF, NOTICE, provenance).
- Troubleshooting (tool failures, performance tuning, air-gapped instructions).
- Compliance mapping table (NTIA, NIST SSDF, EU CRA, SLSA).

---

## Common Pitfalls

- Missing transitive dependencies: verify Syft context includes build artifacts.
- Large SBOMs exceeding artifact limits: compress, split per module.
- Scanner DB staleness: schedule weekly updates / warm caches.
- Signing failures due to incorrect OIDC audience: confirm Cosign environment vars.
- Air-gapped mode: ensure local mirrors for package managers and scanner DBs.

---

## References

- `SBOM_REQ.md`
- `SBOM_IMPLEMENTATION.md`
- `SECURITY_IMPLEMENTATION_PLAN.md` (Task 8)
- NTIA SBOM minimum elements
- NIST SP 800-218 (Secure Software Development Framework)
- EU Cyber Resilience Act (draft obligations)
- SLSA Framework v1.0

