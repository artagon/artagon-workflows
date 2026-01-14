# Proposal: Documentation and Consistency (P2)

## Why

The audit identified documentation gaps and minor consistency issues:

1. **10 workflows missing usage documentation** - Hard for consumers to use
2. **No Gradle documentation** - `GRADLE.md` missing from docs/
3. **Naming inconsistency** - `copilot-setup-steps.yml` uses kebab-case
4. **Orphan documentation** - `RELEASE_RUST.md` exists but no Rust workflows

## What Changes

1. Add usage documentation headers to 10 Maven/Gradle workflows
2. Create `docs/GRADLE.md` for Gradle workflow documentation
3. Rename `copilot-setup-steps.yml` to `copilot_setup_steps.yml`
4. Remove or update `RELEASE_RUST.md`

## Impact

- Affected files: 10 workflow files, 2 doc files
- Breaking changes: None (docs and internal naming only)
- User benefit: Better discoverability and consistency

## Scope

### In Scope

- Add `# Usage:` comment blocks to workflows
- Create Gradle documentation
- Fix naming convention
- Clean up orphan docs

### Out of Scope

- Security fixes (P0/P1 changes)
- New workflow functionality
- Spec updates
