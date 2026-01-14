# Design: Maven SBOM + Sigstore Attestation

## Current Implementation

The `maven_sbom_generate.yml` workflow already exists with:

### SBOM Generation
```yaml
- name: Generate SBOM
  run: mvn -B package ${{ inputs.maven-args }}
```

Uses CycloneDX Maven plugin configured in project pom.xml:
- Schema version: 1.6
- Output formats: JSON + XML
- Output directory: `target/sbom/`

### Cosign Signing
```yaml
- name: Sign SBOMs
  run: |
    cosign sign-blob "$file" --bundle="${file}.sigstore.json" --yes
```

Keyless OIDC signing via GitHub Actions identity.

### Verification
```yaml
- name: Verify signatures
  run: |
    cosign verify-blob "$file" \
      --bundle="$bundle" \
      --certificate-identity-regexp=".*" \
      --certificate-oidc-issuer-regexp=".*"
```

## Workflow Inputs

| Input | Default | Description |
|-------|---------|-------------|
| `java-version` | 25 | JDK version |
| `java-distribution` | temurin | JDK distribution |
| `maven-args` | -DskipTests | Additional Maven args |
| `validate-sbom` | true | Validate against schema |
| `fail-on-validation-error` | true | Fail if validation fails |
| `convert-to-spdx` | false | Convert to SPDX format |
| `sign-sbom` | true | Sign with Cosign |

## Workflow Outputs

| Output | Description |
|--------|-------------|
| `sbom-json` | Path to CycloneDX JSON |
| `sbom-xml` | Path to CycloneDX XML |
| `spdx-json` | Path to SPDX JSON (if converted) |
| `build-type` | "release" or "snapshot" |
| `version` | Version identifier |

## Gap Analysis vs Issue #13

| Requirement | Status |
|-------------|--------|
| CycloneDX v1.6 generation | ✅ Done |
| SPDX 2.3 conversion | ✅ Done |
| Sigstore/Cosign attestation | ✅ Done |
| Keyless OIDC signing | ✅ Done |
| Release/snapshot support | ✅ Done |
| Schema validation | ✅ Done |
| Signature verification | ✅ Done |
| Per-module SBOMs | ⚠️ Depends on pom.xml config |
| Aggregate SBOMs | ⚠️ Depends on pom.xml config |

## Recommendation

Issue #13 appears to be **substantially complete**. The `maven_sbom_generate.yml` workflow implements all core requirements. Consider:

1. Close issue #13 as completed
2. Or create follow-up issues for specific enhancements
