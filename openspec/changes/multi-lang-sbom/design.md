# Design: Multi-Language SBOM Workflows

## Language Support Matrix

| Language | Build System | SBOM Tool | Status |
|----------|--------------|-----------|--------|
| Java | Maven | CycloneDX Maven Plugin | ✅ Done |
| Java | Gradle | CycloneDX Gradle Plugin | 🔄 Issue #14 |
| C/C++ | CMake | syft, cdxgen | ⏳ Planned |
| Rust | Cargo | cargo-sbom, syft | ⏳ Planned |
| Bazel | Bazel | Custom tooling | ⏳ Future |

## Workflow Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    SBOM Generation Flow                      │
├─────────────────────────────────────────────────────────────┤
│  1. Build project (language-specific)                       │
│  2. Generate SBOM (CycloneDX + SPDX)                        │
│  3. Validate SBOM against schema                            │
│  4. Scan for vulnerabilities                                │
│  5. Check license compliance                                │
│  6. Sign with Cosign (keyless)                              │
│  7. Generate attestations                                   │
│  8. Upload artifacts / Attach to release                    │
└─────────────────────────────────────────────────────────────┘
```

## C/C++ SBOM Design

### Tools
- **syft**: Container and filesystem SBOM generation
- **cdxgen**: CycloneDX generator with CMake support
- **conan**: Conan package manager integration

### Workflow: cmake_sbom_generate.yml
```yaml
inputs:
  source-dir: '.'
  build-dir: 'build'
  package-manager: 'system'  # system, vcpkg, conan
  output-format: 'all'       # cyclonedx, spdx, all
  sign-sbom: true
```

### Dependency Sources
1. CMake dependencies (find_package)
2. vcpkg manifest (vcpkg.json)
3. Conan dependencies (conanfile.txt/py)
4. System packages (apt, brew)

## Rust SBOM Design

### Tools
- **cargo-sbom**: Native Cargo SBOM generation
- **syft**: Filesystem analysis
- **cargo-audit**: Vulnerability database

### Workflow: rust_sbom_generate.yml
```yaml
inputs:
  manifest-path: 'Cargo.toml'
  include-dev-deps: false
  output-format: 'all'
  sign-sbom: true
```

### Dependency Sources
1. Cargo.lock (exact versions)
2. Build dependencies (build.rs)
3. Procedural macros

## Vulnerability Scanning

### Scanners
| Scanner | Database | Output |
|---------|----------|--------|
| Trivy | Multiple | SARIF |
| Grype | Anchore | SARIF |
| OSV-Scanner | OSV | SARIF |

### Integration
```yaml
- name: Scan SBOM
  run: |
    trivy sbom --format sarif --output trivy.sarif bom.json
    grype sbom:bom.json --output sarif > grype.sarif
```

## License Compliance

### Checks
- GPL/AGPL/LGPL detection (copyleft)
- Proprietary license restrictions
- License compatibility matrix
- NOTICE file generation

### Configuration
```yaml
inputs:
  fail-on-copyleft: false
  allowed-licenses: 'MIT,Apache-2.0,BSD-*'
  generate-notice: true
```

## Output Artifacts

| Artifact | Format | Signed |
|----------|--------|--------|
| bom.json | CycloneDX 1.6 JSON | ✓ |
| bom.xml | CycloneDX 1.6 XML | ✓ |
| bom.spdx.json | SPDX 2.3 JSON | ✓ |
| vulnerabilities.sarif | SARIF | - |
| NOTICE | Text | - |
