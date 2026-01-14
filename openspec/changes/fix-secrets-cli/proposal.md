# Proposal: Fix Secrets on CLI (P0 Security)

## Why

The security audit identified **P0 critical vulnerabilities** where secrets are passed as command-line arguments. This exposes sensitive credentials in:
- Process listings (`ps aux`)
- GitHub Actions logs (even with masking, CLI args can leak)
- Shell history

This violates the `workflow-security` spec requirement for secret handling.

## What Changes

Remove secrets from command-line arguments in two workflows:

1. **`maven_github_release.yml:121-126`**
   - Current: `-Dtoken=${{ secrets.GITHUB_TOKEN }}`
   - Fix: Pass via environment variable

2. **`cmake_cpack_release.yml:120-126`**
   - Current: `echo "${{ secrets.gpg-private-key }}"`
   - Fix: Write to file with restricted permissions

## Impact

- Affected workflows: `maven_github_release.yml`, `cmake_cpack_release.yml`
- Breaking changes: None (internal implementation change)
- Security improvement: Eliminates credential exposure risk

## Scope

### In Scope

- Remove secrets from CLI arguments
- Use environment variables or config files
- Add `::add-mask::` where needed
- Verify secrets don't appear in logs

### Out of Scope

- Other security improvements (separate P1 change)
- New functionality
