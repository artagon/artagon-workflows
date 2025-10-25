# SBOM Workflow Implementation Plan

This document outlines how to implement the comprehensive SBOM generation and security workflows requested in `SBOM_REQ.md`. It breaks down requirements, tooling choices, workflow structure, and delivery artifacts for Java (Maven BOM), C (CMake/Make), C++ (CMake), and Rust (Cargo) projects.

---

## 1. Objectives

1. Generate reproducible SBOMs in SPDX 2.3 and CycloneDX 1.5 formats using Syft.
2. Perform vulnerability, license, and compliance checks with Trivy, Grype, and OSV-Scanner.
3. Produce signed SBOMs, SLSA level 3 attestations, and in-toto metadata via Cosign.
4. Store and distribute SBOMs via GitHub artifacts/releases, OCI (ghcr.io), AWS S3, Dependency-Track, and GitHub dependency submission API.
5. Automate across push, PR, release, and scheduled triggers with performance constraints (<5 minutes, 16-core self-hosted runners, 10 GB cache).
6. Support air-gapped execution and incremental rebuilds.

---

## 2. Tooling & Dependencies

| Purpose | Tool | Notes |
|---------|------|-------|
| SBOM generation | Syft CLI | Preferred generator; supports SPDX/CycloneDX. |
| Vulnerability scans | Trivy, Grype, OSV-Scanner | Multi-scanner approach; produce SARIF. |
| License checks | Trivy license mode, Licensee (optional) | Generate license matrix, enforce policy. |
| Signing & attestations | Cosign, SLSA provenance generator (slsa-generic), in-toto | Use keyless where possible; support key-based for air-gapped. |
| Dependency submission | `gh api dependency-submission` | Upload manifests to GitHub. |
| Storage | GitHub artifacts/releases, OCI (ghcr), AWS S3 (aws-cli) | Configure buckets and retention policies. |
| Reporting | jq, yq, custom Python scripts | Aggregate results, produce comparison reports. |

Self-hosted runners must have docker/buildkit, Cosign, Syft, Trivy, Grype, OSV-Scanner, aws-cli, and the compilers/toolchains for each language. Containerized steps should primarily use hardened Chainguard images (for example `cgr.dev/chainguard/syft`, `cgr.dev/chainguard/trivy`, `cgr.dev/chainguard/grype`), with Google Distroless equivalents (`gcr.io/distroless/*`) as the secondary option when Chainguard images are unavailable.

---

## 3. Workflow Architecture (per language)

Each workflow follows similar multi-job structure with reusable composite actions where possible:

1. `setup-environment` – prepare toolchains, authenticate registries, restore caches.
2. `generate-sbom` – run Syft (and language-specific commands) to produce SPDX & CycloneDX.
3. `scan-vulnerabilities` – run Trivy/Grype/OSV scans; aggregate SARIF, fail on configurable severity.
4. `license-compliance` – analyze license data, build compatibility matrix, emit NOTICE.
5. `sign-and-attest` – sign SBOMs, create SLSA level 3 provenance & in-toto attestations.
6. `publish` – upload artifacts, push to ghcr, send to AWS S3 and Dependency-Track, call dependency submission API, generate diff reports.
7. `quality-gates` – run monitoring hooks, send notifications if thresholds exceeded.

Matrix strategy: OS (ubuntu, macos, windows) × architecture (x86_64, arm64) with conditional execution to remain under 5 minutes (parallel jobs on 16-core runners).

---

## 4. Language-Specific Details

### Java Maven BOM
- Use Maven Wrapper + Temurin 17.
- Generate module-specific and aggregate SBOMs: `mvn -pl ... -am dependency:tree`.
- Include Maven plugin dependencies via `mvn help:effective-pom`.
- Extract scopes (compile/runtime/test/etc.) and annotation processors.
- Capture Java vendor/version via `java -XshowSettings:properties`.
- Support Maven profiles (matrix over requested profiles).

### C (CMake/Make)
- Configure for GCC/Clang, Debug/Release, cross-compilation flags.
- Leverage vcpkg/Conan manifests; parse lockfiles for dependencies.
- Collect toolchain info (compiler version, linker, libc) with `gcc -v`, `ld --version`.
- Track static/dynamic libs via `ldd`, `otool`, `objdump`.
- Provide script to enumerate system packages (`dpkg`, `rpm`, `brew list`).

### C++ (CMake, C++20)
- Similar to C workflow with additional tracking for STL implementation, Boost, template libs.
- Support package managers: Conan, vcpkg, CPM by reading lockfiles or manifest caches.
- Capture compiler flags affecting ABI (from `CMAKE_CXX_FLAGS`, `compile_commands.json`).

### Rust (Cargo)
- Use `cargo metadata` with `--locked`.
- Handle workspaces: iterate over members, generate per-crate and aggregate SBOMs.
- Record `rustc --version --verbose` (includes LLVM).
- Track features (`cargo tree --features`), procedural macros, build.rs deps.
- Support target-specific dependency sets (matrix over `--target` values).

---

## 5. Caching & Performance

1. Cache language dependencies (Maven `.m2`, Cargo `target`, C/C++ build directories) with scoped keys per OS/arch/profile.
2. Cache Syft/Trivy vulnerability databases locally, update weekly.
3. Use incremental SBOM updates by diffing previous SBOM (stored in S3/ghcr) and regenerating only changed modules.
4. Run matrix jobs in parallel; limit artifact sizes via compression and retention settings.

---

## 6. Signing, Attestation, and Storage

- **Cosign key management**:
  - Support keyless (OIDC) for connected environments.
  - Support offline keys (KMS or hardware) for air-gapped; configure via secrets.
- **SLSA Level 3**:
  - Use `slsa-framework/slsa-github-generator` generic builder, reference repo digest.
- **in-toto attestations**:
  - Generate JSON attestations referencing SBOM digests.
- **Distribution**:
  - Upload to GitHub artifacts/releases (retention configurable).
  - Push SBOMs and signatures to `ghcr.io/<org>/<project>-sbom`.
  - Push to AWS S3 bucket (`s3://artagon-sbom/<project>/<version>/`), using server-side encryption.
  - Send to Dependency-Track via API (project token required).
  - Submit to GitHub dependency submission API with manifest metadata.
  - Generate diff reports comparing previous SBOM (from ghcr/S3) vs new one (using `syft diff`).

---

## 7. Compliance & Policy Enforcement

- Enforce NIST SSDF and EU CRA requirements through documented control mappings.
- Integrate checks for license allow/deny lists (copyleft, proprietary).
- Provide configuration knobs for failure thresholds (critical vulnerabilities, license violations) via workflow inputs or repository variables.
- Include monitoring hooks (GitHub Actions workflow summary, Slack/email notifications via reusable action).

---

## 8. Documentation Deliverables

For each language-specific workflow:
1. **Workflow file**: `.github/workflows/sbom-<language>.yml`.
2. **Configuration**: `.trivyignore`, `grype.yaml`, `syft.yaml`, license policy file, `cyclonedx-config.json`.
3. **Scripts**: 
   - `scripts/sbom/generate_<language>.sh` (language-specific steps).
   - `scripts/sbom/sign_and_publish.sh`.
   - `scripts/sbom/build_diff.py` for comparison reports.
4. **Container/Dockerfile**: standardized SBOM environment built on Chainguard base images (fall back to Google Distroless if needed) with required tooling, plus instructions for self-hosted and air-gapped runners.
5. **Documentation**:
   - Setup & configuration guide per language.
   - Example outputs (SPDX, CycloneDX, SARIF, NOTICE).
   - Troubleshooting (tool installation, performance, common failures).
   - Integration guide for downstream consumers (CI/CD, release process, DevSecOps dashboards).
   - Compliance appendix mapping steps to NTIA minimum elements, NIST SSDF, EU CRA, SLSA requirements.

---

## 9. Implementation Phases

1. **Design & Tooling Validation**
   - Verify tool availability on self-hosted runners.
   - Draft Docker image with required tooling, based on Chainguard hardened images (include Distroless fallback).
   - Prototype Syft/Trivy/Grype flows for each language using sample projects.
2. **Workflow Development**
   - Build base reusable composite actions for SBOM generation, scanning, signing, publishing.
   - Implement language-specific workflows referencing composites.
   - Add matrix configurations and caching.
3. **Testing & Benchmarking**
   - Use sample open-source projects (see `TESTING_INSTRUCTIONS.md`) to validate runtime <5 minutes.
   - Simulate air-gapped mode (no network) by relying on cached artifacts and internal mirrors.
4. **Documentation & Rollout**
   - Produce complete documentation set.
   - Hold security review; map to compliance standards.
   - Tag workflows and update repository README/WORKFLOWS_USAGE.

---

## 10. Dependencies & Open Questions

- **Secrets**: Need storage for Cosign keys (or OIDC trust), AWS credentials, Dependency-Track API keys.
- **Self-hosted runner configuration**: Confirm installed tool versions and provide bootstrap script.
- **Chainguard/Distroless registry access**: Ensure self-hosted runners can pull required Chainguard images (and mirror Google Distroless alternatives for air-gapped mode).
- **Air-gapped support**: Determine internal mirrors for Maven, Cargo, package managers, vulnerability DBs. Document synchronization process.
- **Incremental SBOM updates**: Define acceptable diff tooling (Syft vs proprietary). Clarify retention policy.

---

## 11. Success Criteria Checklist

- [ ] Workflows complete <5 minutes on 16-core runners.
- [ ] SBOMs generated in SPDX/CycloneDX, pass validation.
- [ ] Vulnerability scans, license checks, SARIF uploads, and issue creation functioning.
- [ ] Cosign signatures, SLSA level 3 provenance, in-toto attestations verified.
- [ ] SBOMs stored in GitHub artifacts/releases, ghcr, AWS S3, Dependency-Track; GitHub dependency submissions generated.
- [ ] Documentation finalized with setup, configuration, examples, troubleshooting, compliance mapping.
- [ ] Air-gapped execution path validated.
- [ ] Incremental SBOM diff workflow operational.

---

## 12. Next Steps

1. Confirm secrets and infrastructure availability (self-hosted runner images, S3 buckets, Dependency-Track URL).
2. Prototype Docker image built from Chainguard base images (with Distroless fallback) with Syft/Trivy/Grype/OSV/Cosign plus language toolchains.
3. Draft reusable composite actions (generate, scan, sign, publish).
4. Implement Java Maven BOM workflow first (highest complexity), then parallelize C, C++, Rust.
5. Build automation for regression testing using sample projects.
6. Conduct internal security compliance review before production rollout.

---

Prepared for implementation in alignment with `SBOM_REQ.md` requirements. Update this document as architecture decisions evolve.
