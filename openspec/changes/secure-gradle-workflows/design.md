# Design: Secure Gradle Workflows

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                  gradle_secure_build.yml                     │
├─────────────────────────────────────────────────────────────┤
│  1. Wrapper Verification (SHA-256)                          │
│  2. Dependency Verification (verification-metadata.xml)      │
│  3. Lockfile Enforcement (*.lockfile)                       │
│  4. Repository Policy Check                                  │
│  5. Build & Test                                            │
│  6. SBOM Generation (CycloneDX v1.6)                        │
│  7. SBOM Validation                                         │
│  8. Cosign Attestation (releases only)                      │
└─────────────────────────────────────────────────────────────┘
```

## Workflow Inputs

```yaml
inputs:
  java-version:
    default: '21'
  gradle-version:
    default: '8.5'
  mirror-url:
    description: 'Trusted repository mirror'
  verify-dependencies:
    default: true
  enforce-lockfiles:
    default: true
  generate-sbom:
    default: true
  attest-sbom:
    default: false  # true for releases
  wrapper-sha256:
    description: 'Expected Gradle wrapper checksum'
```

## Security Features

### 1. Wrapper Verification
```bash
# Verify wrapper before execution
echo "$WRAPPER_SHA256  gradle/wrapper/gradle-wrapper.jar" | sha256sum -c
```

### 2. Dependency Verification
```bash
./gradlew --verify-metadata build
```

Requires `gradle/verification-metadata.xml` with:
- SHA-256 checksums for all artifacts
- Optional PGP signatures
- Trusted keys configuration

### 3. Lockfile Enforcement
```bash
./gradlew dependencies --write-locks
git diff --exit-code *.lockfile || exit 1
```

### 4. Repository Policy
```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("$MIRROR_URL") }
    }
}
```

### 5. SBOM Generation
```kotlin
// build.gradle.kts
plugins {
    id("org.cyclonedx.bom") version "1.8.2"
}

cyclonedxBom {
    schemaVersion.set("1.6")
    outputFormat.set("all")
    includeConfigs.set(listOf("runtimeClasspath"))
}
```

### 6. Cosign Attestation
```bash
cosign attest-blob \
  --bundle="sbom.sigstore.json" \
  --yes \
  build/reports/bom.json
```

## Templates

### settings.gradle.kts
- Dependency verification configuration
- Dependency locking setup
- Repository allow-listing

### build.gradle.kts
- CycloneDX plugin configuration
- Plugin pinning examples

### gradle.properties
- Verification mode settings
- Mirror URL configuration

## CI Behavior

| Event | Verify | Lock Check | SBOM | Attest |
|-------|--------|------------|------|--------|
| PR | ✓ | ✓ | ✓ | - |
| Push to main | ✓ | ✓ | ✓ | - |
| Release tag | ✓ | ✓ | ✓ | ✓ |

## Failure Modes

- Verification mismatch → FAIL
- Lockfile drift → FAIL
- SBOM validation error → FAIL
- Unapproved repository → FAIL
- Wrapper checksum mismatch → FAIL
