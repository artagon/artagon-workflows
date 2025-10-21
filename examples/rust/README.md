# Rust Workflow Examples

Example GitHub Actions workflows for Rust projects using trunk-based development.

## Quick Start

Copy the desired workflow files to your project's `.github/workflows/` directory and customize as needed.

## Available Workflows

### [ci.yml](ci.yml)
**Purpose:** Continuous Integration

Runs on every push and pull request.

**Features:**
- Check, test, fmt, clippy
- Multi-platform (Linux, macOS, Windows)
- MSRV (Minimum Supported Rust Version) verification
- Documentation build
- Security audit with cargo-audit

**Usage:**
```bash
cp ci.yml <your-project>/.github/workflows/
```

### [release.yml](release.yml)
**Purpose:** Publish to crates.io and create GitHub release

**Features:**
- Pre-release validation
- Publish to crates.io
- Create GitHub release
- MSRV verification
- Security audit

**IMPORTANT:** Releases to crates.io are **permanent** and **immutable**.

## Trunk-Based Development

Rust projects use **simple trunk-based development**:

- **`main` branch**: Always compilable, development version
- **Tags**: Trigger releases to crates.io (e.g., `v1.2.3`)
- **`lts-*` branches** (optional): For frameworks requiring LTS

### Release Process

```bash
# 1. Update version in Cargo.toml
[package]
version = "1.2.3"

# 2. Update CHANGELOG.md
vim CHANGELOG.md

# 3. Commit
git add Cargo.toml CHANGELOG.md
git commit -m "chore: bump version to 1.2.3"

# 4. Create tag
git tag -a v1.2.3 -m "Release 1.2.3"

# 5. Push (this triggers the release workflow)
git push origin main --tags
```

The workflow will:
1. ✅ Run all pre-release checks
2. ✅ Publish to crates.io
3. ✅ Create GitHub release

## Required Secrets

Configure this secret in your repository:

- `CARGO_REGISTRY_TOKEN` - Token from crates.io (Account Settings → API Tokens)

## MSRV Policy

Define your Minimum Supported Rust Version in Cargo.toml:

```toml
[package]
rust-version = "1.70"  # MSRV
```

**Recommended policies:**
- **Libraries:** 6-12 months behind stable
- **Frameworks:** 3-6 months behind stable
- **Applications:** Latest stable

Bumping MSRV is considered a **minor** version change (not breaking).

## Customization

### MSRV in CI

Update the matrix in ci.yml:

```yaml
matrix:
  rust: [stable, beta, '1.70']  # Change 1.70 to your MSRV
```

### Skip Pre-release Checks (Not Recommended)

Remove steps from release.yml if needed, but all checks are recommended for crates.io releases.

## Feature Flags

Example Cargo.toml with features:

```toml
[features]
default = ["std"]
std = []
async = ["tokio"]
serde = ["dep:serde"]
full = ["std", "async", "serde"]

[dependencies]
tokio = { version = "1.35", optional = true }
serde = { version = "1.0", optional = true }
```

## Documentation

For complete Rust release strategy, see:
- **[Rust Release Strategy](../../docs/RELEASE_RUST.md)**

## Tools

Useful Cargo tools for release management:

```bash
# Automate releases
cargo install cargo-release
cargo release patch  # 1.2.3 → 1.2.4

# SemVer verification
cargo install cargo-semver-checks
cargo semver-checks

# Security audit
cargo install cargo-audit
cargo audit
```

## Support

- [artagon-workflows Issues](https://github.com/artagon/artagon-workflows/issues)
- [Rust Release Documentation](../../docs/RELEASE_RUST.md)
