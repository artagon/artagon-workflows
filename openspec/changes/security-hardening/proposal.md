# Proposal: Security Hardening (P1)

## Why

The security audit identified **P1 high-priority security gaps** that need to be addressed:

1. **Missing permissions blocks** - `bazel_multi_release.yml` has 5 jobs without explicit permissions
2. **Input validation missing** - 8 workflows interpolate user inputs directly into shell commands
3. **Binary downloads without checksums** - 2 workflows download binaries without SHA256 verification

These issues violate the `workflow-security` spec and expose the repository to:
- Privilege escalation (missing permissions)
- Command injection attacks (unvalidated inputs)
- Supply chain attacks (unverified binaries)

## What Changes

1. Add explicit `permissions:` blocks to all jobs in `bazel_multi_release.yml`
2. Add input validation steps to 8 workflows before shell execution
3. Add SHA256 checksum verification to binary downloads

## Impact

- Affected workflows: 10 workflows total
- Breaking changes: None (invalid inputs will now fail fast)
- Security improvement: Significant reduction in attack surface

## Scope

### In Scope

- Add permissions blocks following least-privilege
- Add input validation with allowlist patterns
- Add checksum verification for binary downloads
- Update `test_security.yml` to enforce these checks

### Out of Scope

- P0 secrets fixes (separate change)
- Documentation updates (separate P2 change)
- New workflow functionality
