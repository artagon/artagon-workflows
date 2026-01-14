# Security Guardrails

Non-negotiable security constraints for all workflow changes.

## Critical Rules

### 1. Action Pinning (MANDATORY)

**Rule**: ALL GitHub Actions MUST be pinned to commit SHAs.

```yaml
# CORRECT
# actions/checkout@v4.2.2
uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683

# VIOLATION - Will be rejected
uses: actions/checkout@v4
uses: actions/checkout@main
uses: aquasecurity/trivy-action@master
```

**Rationale**: Prevents supply chain attacks. Tags can be moved by attackers.

### 2. Explicit Permissions (MANDATORY)

**Rule**: ALL jobs MUST have explicit `permissions:` blocks.

```yaml
# CORRECT
jobs:
  build:
    permissions:
      contents: read
      packages: read
    steps: [...]

# VIOLATION - Missing permissions
jobs:
  build:
    steps: [...]
```

**Rationale**: Default permissions are too permissive. Least privilege principle.

### 3. Input Validation (MANDATORY)

**Rule**: ALL user-controlled inputs MUST be validated before shell execution.

```yaml
# CORRECT
- name: Validate inputs
  run: |
    INPUT="${{ inputs.value }}"
    if ! echo "$INPUT" | grep -qE '^[SAFE_PATTERN]*$'; then
      exit 1
    fi

# VIOLATION - Direct use without validation
run: mvn ${{ inputs.maven-args }}
```

**Rationale**: Prevents command injection attacks (CWE-77).

### 4. Secret Handling (MANDATORY)

**Rule**: Secrets MUST NOT appear in command-line arguments.

```yaml
# CORRECT - Secret in config file
env:
  GPG_PASSPHRASE: ${{ secrets.GPG_PASSPHRASE }}
run: |
  cat > ~/.m2/settings.xml <<EOF
  <settings>
    <profiles><profile>
      <properties>
        <gpg.passphrase>${GPG_PASSPHRASE}</gpg.passphrase>
      </properties>
    </profile></profiles>
  </settings>
  EOF

# VIOLATION - Secret in CLI
run: mvn -Dgpg.passphrase="${{ secrets.GPG_PASSPHRASE }}"
```

**Rationale**: CLI arguments are visible in process listings and logs.

### 5. Binary Download Verification (MANDATORY)

**Rule**: ALL binary downloads MUST be verified with checksums.

```yaml
# CORRECT
- run: |
    wget -O /tmp/tool https://example.com/tool
    echo "EXPECTED_SHA256  /tmp/tool" | sha256sum --check

# VIOLATION - No verification
- run: wget https://example.com/tool && chmod +x tool
```

**Rationale**: Prevents MITM attacks and compromised downloads.

## Prohibited Actions

1. **Never use mutable tags** (`@v4`, `@main`, `@master`)
2. **Never echo secrets** or include them in logs
3. **Never skip input validation** for user inputs
4. **Never omit permissions blocks**
5. **Never download binaries without checksums**
6. **Never hardcode secrets** in workflow files

## Validation Checklist

Before submitting workflow changes:

- [ ] All actions pinned to commit SHAs
- [ ] Semantic version comment above each SHA
- [ ] Permissions block on every job
- [ ] User inputs validated with allowlist
- [ ] Secrets passed via env, not CLI
- [ ] Binary downloads have checksums
- [ ] actionlint passes
- [ ] yamllint passes

## Escalation

If you encounter a situation where these guardrails cannot be followed:

1. Document the constraint
2. Propose an alternative approach
3. Request security review
4. Do NOT proceed without explicit approval

## References

- [OSSF Scorecard](https://github.com/ossf/scorecard)
- [SLSA Framework](https://slsa.dev/)
- [GitHub Actions Security](https://docs.github.com/en/actions/security-guides/security-hardening-for-github-actions)
