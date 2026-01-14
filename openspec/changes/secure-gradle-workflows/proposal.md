# Proposal: Secure Gradle Workflows with Supply-Chain Hardening

**Issue**: #14

## Why

Gradle projects need comprehensive supply-chain security:
- Dependency verification prevents tampering
- Dependency locking ensures reproducibility
- SBOM generation provides transparency
- Attestation enables verification

This follows the same pattern as our Maven SBOM implementation.

## What Changes

Implement secure Gradle build workflows with:

1. **Artifact Integrity**
   - Dependency Verification (SHA-256, optional PGP)
   - Dependency Locking for all configurations
   - Gradle Wrapper pinning with `distributionSha256Sum`
   - Plugin version pinning (no dynamic `+` ranges)

2. **Repository Policy**
   - Single trusted mirror routing
   - Disallow ad-hoc repositories
   - `FAIL_ON_PROJECT_REPOS` mode

3. **SBOM + Attestation**
   - CycloneDX v1.6 per-module + aggregate
   - Schema validation
   - Optional SPDX conversion
   - Cosign attestation (keyless OIDC)

4. **Hardened Downloads**
   - HTTPS-only, TLS 1.2+
   - SHA-256 verification
   - Retries with backoff

## Impact

- **New workflow**: `gradle_secure_build.yml`
- **Templates**: Gradle configuration examples
- **Documentation**: Usage and trust model

## Scope

### In Scope

- Reusable secure Gradle workflow
- Dependency verification enforcement
- Lockfile drift detection
- SBOM generation and validation
- Cosign attestation for releases
- Configuration templates
- Documentation

### Out of Scope

- Modifying existing `gradle_build.yml` / `gradle_release.yml`
- Artifactory/Nexus setup
- PGP key management
