# Design: Fix Secrets on CLI

## Current State

### maven_github_release.yml

```yaml
# INSECURE - secret in CLI argument
- name: Deploy
  run: |
    mvn deploy -Dtoken=${{ secrets.GITHUB_TOKEN }}
```

### cmake_cpack_release.yml

```yaml
# INSECURE - secret echoed to CLI
- name: Import GPG Key
  run: |
    echo "${{ secrets.gpg-private-key }}" | gpg --import
```

---

## Proposed Solution

### maven_github_release.yml

```yaml
# SECURE - secret via environment variable
- name: Deploy
  env:
    GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
  run: |
    mvn deploy -Dtoken="${GITHUB_TOKEN}"
```

Or better, use settings.xml:

```yaml
- name: Configure Maven settings
  run: |
    mkdir -p ~/.m2
    cat > ~/.m2/settings.xml << 'EOF'
    <settings>
      <servers>
        <server>
          <id>github</id>
          <username>${env.GITHUB_ACTOR}</username>
          <password>${env.GITHUB_TOKEN}</password>
        </server>
      </servers>
    </settings>
    EOF
    chmod 600 ~/.m2/settings.xml

- name: Deploy
  env:
    GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
  run: mvn deploy
```

### cmake_cpack_release.yml

```yaml
# SECURE - write to file with restricted permissions
- name: Import GPG Key
  env:
    GPG_PRIVATE_KEY: ${{ secrets.gpg-private-key }}
  run: |
    echo "::add-mask::${GPG_PRIVATE_KEY}"

    # Write key to temp file
    KEY_FILE=$(mktemp)
    echo "${GPG_PRIVATE_KEY}" > "${KEY_FILE}"
    chmod 600 "${KEY_FILE}"

    # Import and cleanup
    gpg --batch --import "${KEY_FILE}"
    rm -f "${KEY_FILE}"
```

---

## Security Considerations

1. **Environment variables** are safer than CLI args because:
   - Not visible in `ps` output
   - Not logged by shell
   - GitHub Actions masks them automatically

2. **File-based secrets** should:
   - Use `mktemp` for unique filenames
   - Set `chmod 600` immediately
   - Be deleted after use
   - Never be committed

3. **Masking** with `::add-mask::` ensures the value is redacted even if accidentally logged.

---

## Testing

1. Run modified workflows in a test repository
2. Check workflow logs for any secret exposure
3. Verify `ps aux` during execution doesn't show secrets
4. Confirm actionlint passes
