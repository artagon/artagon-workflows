# Proposal: Maven SBOM + Sigstore Attestation

**Issue**: #13

## Why

Maven Central added Sigstore signature validation in January 2025. Modern supply chain security requires:
- Complete dependency inventory (SBOM)
- Cryptographic signing and provenance
- Industry-standard formats for interoperability

## What Changes

Enhance Maven workflows with:

1. **SBOM Generation**
   - CycloneDX v1.6 (JSON + XML)
   - Optional SPDX 2.3 conversion
   - Per-module and aggregate SBOMs
   - Schema validation

2. **Sigstore Attestation**
   - Keyless Cosign signing (OIDC)
   - In-toto attestations
   - Signature verification

3. **Release/Snapshot Support**
   - First-class SBOM for both build types
   - Appropriate retention policies

## Impact

- **Existing workflow**: `maven_sbom_generate.yml` - already implemented
- **Enhancement needed**: Verify completeness vs issue requirements
- **Status**: Partially complete - review needed

## Scope

### In Scope

- CycloneDX v1.6 generation (DONE)
- SPDX conversion (DONE)
- Cosign signing (DONE)
- Signature verification (DONE)
- Schema validation (DONE)

### Out of Scope

- Vulnerability scanning (separate issue)
- License compliance checking
- SLSA provenance (future enhancement)

## Status Review

The `maven_sbom_generate.yml` workflow already implements:
- [x] CycloneDX v1.6 JSON/XML generation
- [x] SPDX conversion option
- [x] Cosign keyless signing
- [x] Signature verification
- [x] Schema validation
- [x] Release/snapshot detection

**Recommendation**: Close issue #13 as completed, or document remaining gaps.
