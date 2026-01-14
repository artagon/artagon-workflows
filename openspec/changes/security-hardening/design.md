# Design: Security Hardening

## 1. Permissions Blocks

### Pattern for Release Jobs

```yaml
jobs:
  create-release:
    runs-on: ubuntu-latest
    permissions:
      contents: write  # Create releases and tags

  build-linux:
    runs-on: ubuntu-latest
    permissions:
      contents: read   # Checkout code only

  build-macos:
    runs-on: macos-latest
    permissions:
      contents: read

  build-windows:
    runs-on: windows-latest
    permissions:
      contents: read
```

### Permission Reference

| Permission | Use Case |
|------------|----------|
| `contents: read` | Checkout code, read files |
| `contents: write` | Create releases, tags, push commits |
| `packages: write` | Push to GitHub Packages |
| `id-token: write` | OIDC authentication (Sigstore) |
| `security-events: write` | Upload SARIF results |
| `attestations: write` | Create build attestations |

---

## 2. Input Validation

### Reusable Validation Step

```yaml
- name: Validate inputs
  run: |
    set -euo pipefail

    validate_input() {
      local name="$1"
      local value="$2"
      local pattern="$3"

      if [[ ! "$value" =~ $pattern ]]; then
        echo "::error::Invalid input '$name': must match pattern $pattern"
        echo "::error::Received: $value"
        exit 1
      fi
      echo "::debug::Input '$name' validated successfully"
    }

    # Maven arguments: alphanumeric, dashes, dots, equals, spaces
    validate_input "maven-args" "${{ inputs.maven-args }}" '^[-A-Za-z0-9=.,_:/ ]*$'

    # Version strings: semver with optional prerelease
    validate_input "version" "${{ inputs.version }}" '^[0-9]+\.[0-9]+\.[0-9]+(-[a-zA-Z0-9.]+)?$'

    # Java version: number or number with dots
    validate_input "java-version" "${{ inputs.java-version }}" '^[0-9]+(\.[0-9]+)*$'

    # Distribution: alphanumeric only
    validate_input "java-distribution" "${{ inputs.java-distribution }}" '^[a-zA-Z0-9]+$'
```

### Validation Patterns by Input Type

| Input Type | Pattern | Example Valid |
|------------|---------|---------------|
| Maven args | `^[-A-Za-z0-9=.,_:/ ]*$` | `-DskipTests -Pprofile` |
| CMake options | `^[-A-Za-z0-9=.,_:/ ]*$` | `-DCMAKE_BUILD_TYPE=Release` |
| Version | `^[0-9]+\.[0-9]+\.[0-9]+(-[a-zA-Z0-9.]+)?$` | `1.2.3`, `1.0.0-beta.1` |
| Java version | `^[0-9]+(\.[0-9]+)*$` | `21`, `17.0.1` |
| Branch name | `^[a-zA-Z0-9/_.-]+$` | `main`, `feature/foo` |
| Bazel configs | `^[a-z,_-]+$` | `release,opt` |

---

## 3. Checksum Verification

### Pattern for Binary Downloads

```yaml
- name: Download and verify binary
  run: |
    set -euo pipefail

    TOOL_VERSION="1.7.4"
    TOOL_SHA256="fc0a6886bbb9a23a39eeec4b176193cadb54ddbe77cdbb19b637933919545395"
    TOOL_URL="https://github.com/owner/repo/releases/download/v${TOOL_VERSION}/tool_${TOOL_VERSION}_linux_amd64.tar.gz"

    # Download
    curl -sSLO "${TOOL_URL}"

    # Verify checksum
    echo "${TOOL_SHA256}  tool_${TOOL_VERSION}_linux_amd64.tar.gz" | sha256sum --check

    # Extract and install
    tar xzf "tool_${TOOL_VERSION}_linux_amd64.tar.gz"
    chmod +x tool
    sudo mv tool /usr/local/bin/
```

### Binaries Requiring Checksums

| Workflow | Binary | Version | SHA256 |
|----------|--------|---------|--------|
| `maven_sbom_generate.yml` | cyclonedx-cli | TBD | TBD |
| `test_lint.yml` | actionlint | 1.7.4 | `fc0a6886...` |
| `cmake_c_release.yml` | linuxdeploy | TBD | TBD |
| `cmake_cpp_release.yml` | linuxdeploy | TBD | TBD |

---

## 4. Security Validation Updates

### Enhanced test_security.yml

```yaml
- name: Check for missing permissions
  run: |
    MISSING=""
    for f in .github/workflows/*.yml; do
      # Check if any job lacks permissions
      if grep -q "jobs:" "$f"; then
        JOBS=$(yq '.jobs | keys | .[]' "$f" 2>/dev/null || true)
        for job in $JOBS; do
          if ! yq ".jobs.${job}.permissions" "$f" 2>/dev/null | grep -q .; then
            MISSING="${MISSING}${f}:${job}\n"
          fi
        done
      fi
    done

    if [ -n "$MISSING" ]; then
      echo "::error::Jobs missing permissions blocks:"
      echo -e "$MISSING"
      exit 1
    fi

- name: Check for unvalidated inputs
  run: |
    # Look for inputs used directly in run: blocks without validation
    grep -rn 'inputs\.' .github/workflows/*.yml | \
      grep -v "validate" | \
      grep "run:" && echo "::warning::Potential unvalidated inputs found"
```

---

## Implementation Order

1. **Phase 1**: Add permissions to `bazel_multi_release.yml` (quick win)
2. **Phase 2**: Add input validation to workflows (highest security impact)
3. **Phase 3**: Add checksum verification (supply chain protection)
4. **Phase 4**: Update `test_security.yml` (prevent regression)
