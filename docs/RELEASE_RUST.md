# Rust Release Strategy

> **Status: PARTIAL** - Rust CI workflow (`rust_ci.yml`) is available.
> Release workflow (`rust_release.yml`) is planned for future implementation.
> See [WORKFLOWS_USAGE.md](WORKFLOWS_USAGE.md) for available workflows.

**Language:** Rust
**Build System:** Cargo
**Package Registry:** crates.io
**Based on:** Tokio, Serde, ripgrep, rustc, Actix-web patterns

---

## Table of Contents

- [Release Strategy Overview](#release-strategy-overview)
- [Branching Model](#branching-model)
- [Version Management](#version-management)
- [Cargo and crates.io](#cargo-and-cratesio)
- [Release Process](#release-process)
- [MSRV Policy](#msrv-policy)
- [Breaking Changes and Editions](#breaking-changes-and-editions)
- [Best Practices](#best-practices)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)

---

## Release Strategy Overview

Artagon Rust projects use a **simple trunk-based strategy** with strong SemVer adherence, following Rust ecosystem conventions from Tokio, Serde, and the broader Rust community.

### Core Principles

- ✅ **`main` branch**: Development (always compilable)
- ✅ **Tags**: Primary release mechanism (e.g., `v1.2.3`)
- ✅ **Semantic Versioning**: Strictly enforced by cargo
- ✅ **MSRV Policy**: Documented minimum supported Rust version
- ✅ **Edition-based breaking changes**: Use Rust editions for major changes
- ✅ **LTS branches (optional)**: For frameworks (like Tokio LTS)

### Why This Strategy?

**Rust Ecosystem Characteristics:**
- Strong type system catches most issues at compile time
- Cargo enforces semantic versioning
- Edition system allows breaking changes without major version bumps
- Fast compilation (compared to C++)
- Strong backward compatibility culture

**Industry Alignment:**
- **Serde**: Simple main branch, stable 1.x since 2016
- **Tokio**: Main + LTS branches, monthly releases
- **ripgrep**: Main-only, tags for releases
- **rustc**: 6-week train model (nightly → beta → stable)
- **Actix-web**: Pre-releases for testing, careful SemVer

---

## Branching Model

### Branch Types

#### Main Branch
```
main
  ↓
  Always compiles
  Latest stable Rust
  Feature development
  Tagged for releases
```

**Purpose:** Primary development line

**Rules:**
- Must compile with `cargo check`
- All tests pass: `cargo test`
- Clippy clean: `cargo clippy -- -D warnings`
- Formatted: `cargo fmt --check`
- Documentation builds: `cargo doc --no-deps`

**MSRV Compliance:**
- Must compile with MSRV Rust version
- CI tests both stable and MSRV

#### LTS Branches (Optional, for frameworks)
```
lts-1.x
  ↓
  Long-term support
  Security fixes
  Critical bug fixes
  No new features
```

**Purpose:** Extended support for enterprise users

**Example (Tokio pattern):**
- `main`: 1.35.0 (latest)
- `tokio-1.x`: 1.28.x (LTS)
- Support: 6 months active, 18 months security

#### Feature Branches
```
feat/async-io-improvements
  ↓
  Merged to main via PR
```

**Purpose:** Isolated feature development

---

## Version Management

### Semantic Versioning (SemVer)

Rust enforces SemVer strictly:

```toml
# Cargo.toml
[package]
name = "myproject"
version = "1.2.3"  # MAJOR.MINOR.PATCH
```

**Rules (enforced by cargo):**

**Patch (1.2.X):**
- Bug fixes
- Documentation updates
- Performance improvements
- Internal refactoring
- **No API changes**

**Minor (1.X.0):**
- New public APIs
- New features
- Deprecations (with warnings)
- **Backward-compatible**
- Can add trait implementations

**Major (X.0.0):**
- Breaking API changes
- Removed public APIs
- Changed function signatures
- Changed trait definitions
- Bumped MSRV (controversial, but common)

### Version Declaration

**Cargo.toml:**
```toml
[package]
name = "myproject"
version = "1.2.3"
edition = "2021"
rust-version = "1.70"  # MSRV

authors = ["Your Name <email@example.com>"]
description = "A fantastic Rust library"
license = "MIT OR Apache-2.0"
repository = "https://github.com/artagon/myproject"
homepage = "https://artagon.github.io/myproject"
documentation = "https://docs.rs/myproject"
readme = "README.md"
keywords = ["async", "io", "performance"]
categories = ["asynchronous", "network-programming"]

[dependencies]
tokio = { version = "1.35", features = ["full"] }
serde = { version = "1.0", features = ["derive"] }

[dev-dependencies]
criterion = "0.5"
```

**Build-time version access:**
```rust
// lib.rs
pub const VERSION: &str = env!("CARGO_PKG_VERSION");

pub fn version() -> &'static str {
    VERSION
}

// At compile time
fn main() {
    println!("Version: {}", env!("CARGO_PKG_VERSION"));
}
```

---

## Cargo and crates.io

### Publishing to crates.io

**Prerequisites:**
1. Account on crates.io
2. API token: `cargo login`
3. Valid Cargo.toml metadata
4. Documentation (docs.rs will build it)
5. License files

**First-time setup:**
```bash
# Login to crates.io
cargo login

# Verify package
cargo package --list

# Check for warnings
cargo publish --dry-run
```

### Package Best Practices

**Include files:**
```toml
[package]
# ...
include = [
    "src/**/*",
    "Cargo.toml",
    "LICENSE-*",
    "README.md",
    "CHANGELOG.md",
]
```

**Exclude files:**
```toml
[package]
# ...
exclude = [
    ".github/**",
    "tests/**",
    "benches/**",
    "examples/**",  # Only if they're large
]
```

### Dependencies

**Use caret requirements (default):**
```toml
[dependencies]
serde = "1.0"           # ^1.0 = >=1.0.0, <2.0.0
tokio = "1.35"          # ^1.35 = >=1.35.0, <2.0.0
```

**Use tilde for patch-only:**
```toml
[dependencies]
special-dep = "~1.2.3"  # >=1.2.3, <1.3.0
```

**Pin exact versions (avoid in libraries):**
```toml
[dependencies]
# Only for binaries, not libraries
pinned = "=1.2.3"
```

---

## Release Process

### Prerequisites

#### 1. Pre-Release Checklist

- [ ] All tests pass: `cargo test --all-features`
- [ ] Clippy clean: `cargo clippy --all-features -- -D warnings`
- [ ] Formatted: `cargo fmt --check`
- [ ] Documentation builds: `cargo doc --all-features --no-deps`
- [ ] Examples compile: `cargo build --examples`
- [ ] Benchmarks compile: `cargo bench --no-run`
- [ ] MSRV verified: `cargo +1.70 check --all-features`
- [ ] CHANGELOG.md updated
- [ ] README.md updated
- [ ] Migration guide (for breaking changes)
- [ ] Security audit: `cargo audit`

#### 2. CI Passing

**GitHub Actions matrix:**
- Rust versions: stable, beta, MSRV
- Platforms: Linux, macOS, Windows
- Features: default, all-features, no-default-features
- Cross-compilation targets (if applicable)

### Release Workflow

#### Step 1: Update Version

**Edit Cargo.toml:**
```toml
[package]
version = "1.2.3"  # Update version
```

**Update version references in docs:**
```bash
# Update README.md
sed -i 's/myproject = "1.2.2"/myproject = "1.2.3"/' README.md

# Update lib.rs examples
vim src/lib.rs
```

**Commit version bump:**
```bash
git add Cargo.toml README.md
git commit -m "chore: bump version to 1.2.3"
```

#### Step 2: Update CHANGELOG

**CHANGELOG.md format (Keep a Changelog):**
```markdown
# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.2.3] - 2025-10-21

### Added
- New `async_process` function for asynchronous operations
- Support for custom error types with `#[derive(Error)]`
- New `Config::builder()` pattern for ergonomic configuration

### Changed
- Improved performance of hash table lookups by 40%
- Updated MSRV to 1.70 (for GATs and async fn in traits)
- Migrated to Rust 2021 edition

### Deprecated
- `old_function()` - use `new_function()` instead (will be removed in 2.0.0)

### Fixed
- Fixed panic when parsing empty strings (#123)
- Corrected race condition in concurrent iterator (#456)

### Security
- Fixed potential DoS in parser (RUSTSEC-2024-0001)

## [1.2.2] - 2025-09-15

...

[Unreleased]: https://github.com/artagon/myproject/compare/v1.2.3...HEAD
[1.2.3]: https://github.com/artagon/myproject/compare/v1.2.2...v1.2.3
[1.2.2]: https://github.com/artagon/myproject/compare/v1.2.1...v1.2.2
```

#### Step 3: Run Full Test Suite

```bash
# Clean build
cargo clean

# Check all features
cargo check --all-features
cargo check --no-default-features

# Run tests
cargo test --all-features
cargo test --no-default-features

# Run tests with all combinations
cargo test --all-targets --all-features

# Check documentation
cargo doc --all-features --no-deps

# Run clippy
cargo clippy --all-features -- -D warnings

# Check formatting
cargo fmt --check

# Security audit
cargo audit

# Verify MSRV
cargo +1.70 check --all-features
```

#### Step 4: Dry Run Publish

```bash
# Verify package contents
cargo package --list

# Dry run publish
cargo publish --dry-run

# Check package size
du -sh target/package/myproject-1.2.3.crate
```

#### Step 5: Create Git Tag

```bash
# Create annotated tag
git tag -a v1.2.3 -m "Release version 1.2.3

New features:
- Async operations support
- Builder pattern for Config

Improvements:
- 40% faster hash table lookups
- Updated to Rust 2021 edition

Bug fixes:
- Fixed panic on empty input
- Corrected race condition

See CHANGELOG.md for full details."

# Verify tag
git show v1.2.3
```

#### Step 6: Publish to crates.io

```bash
# Publish (no going back!)
cargo publish

# Wait for crates.io to process (usually ~1 minute)
# Verify at: https://crates.io/crates/myproject/1.2.3
```

**Note:** Once published to crates.io, versions are **permanent** and **immutable**. You cannot unpublish or modify released versions (only yank them).

#### Step 7: Push Tag to GitHub

```bash
# Push commits and tag
git push origin main
git push origin v1.2.3
```

#### Step 8: Create GitHub Release

```bash
gh release create v1.2.3 \
    --title "v1.2.3" \
    --notes-file RELEASE_NOTES.md \
    --verify-tag
```

#### Step 9: Announce Release

**Rust community channels:**
- This Week in Rust (submit PR)
- Reddit: r/rust (if significant)
- Rust Users Forum
- Project Discord/Zulip
- Twitter/Mastodon with #rustlang

---

## MSRV Policy

### Minimum Supported Rust Version

**Define in Cargo.toml:**
```toml
[package]
rust-version = "1.70"  # Enforced since Rust 1.56
```

**Common MSRV policies:**

**Conservative (libraries):**
- MSRV: 6-12 months behind stable
- Update only when necessary for features
- Document MSRV bumps as **minor** version changes

**Moderate (frameworks):**
- MSRV: 3-6 months behind stable
- Update quarterly or as needed
- Document MSRV bumps clearly

**Aggressive (applications):**
- MSRV: Latest stable
- Update frequently for new features
- Users expected to update Rust regularly

### MSRV Bumping

**When to bump:**
- Need new language features (GATs, async fn in traits, etc.)
- Need new std library features
- Major dependency requires newer Rust

**How to bump:**
```toml
# Cargo.toml
[package]
rust-version = "1.75"  # Bumped from 1.70
```

**Document in CHANGELOG:**
```markdown
## [1.3.0] - 2025-11-01

### Changed
- **BREAKING**: MSRV bumped to 1.75 (for async fn in traits)
```

**CI verification:**
```yaml
# .github/workflows/ci.yml
jobs:
  test:
    strategy:
      matrix:
        rust: [stable, beta, '1.75']  # Include MSRV
```

---

## Breaking Changes and Editions

### Rust Editions

Rust uses **editions** for breaking changes:
- 2015 (default, legacy)
- 2018 (modern)
- 2021 (current)
- 2024 (upcoming)

**Edition migration:**
```bash
# Update Cargo.toml
[package]
edition = "2021"

# Run migration
cargo fix --edition

# Test
cargo test
```

**Benefits:**
- Breaking changes without major version bump
- Gradual migration
- Backward compatibility (crates from different editions can interoperate)

### Deprecation Strategy

**Mark deprecated items:**
```rust
#[deprecated(since = "1.2.0", note = "use `new_function` instead")]
pub fn old_function() -> i32 {
    42
}

// Better: provide migration path
#[deprecated(since = "1.2.0", note = "use `new_function` instead")]
pub fn old_function() -> i32 {
    new_function()  // Forward to new implementation
}

pub fn new_function() -> i32 {
    42
}
```

**Deprecation timeline:**
```
1.2.0: Mark as deprecated
1.3.0: Keep deprecated (with warnings)
1.4.0: Keep deprecated (with warnings)
2.0.0: Remove deprecated items
```

### Breaking Changes Checklist

Before major version bump:
- [ ] All deprecations in 1.x have migration paths
- [ ] Migration guide written
- [ ] Pre-release versions tested (2.0.0-beta.1)
- [ ] Major users notified
- [ ] Examples updated
- [ ] Documentation updated

---

## Best Practices

### 1. API Design

**Use type-state pattern for safety:**
```rust
pub struct Connection<S> {
    state: S,
    // ...
}

pub struct Disconnected;
pub struct Connected;

impl Connection<Disconnected> {
    pub fn connect(self) -> Result<Connection<Connected>, Error> {
        // Connection logic
        Ok(Connection { state: Connected, /* ... */ })
    }
}

impl Connection<Connected> {
    pub fn send(&mut self, data: &[u8]) -> Result<(), Error> {
        // Can only send when connected
        Ok(())
    }
}
```

**Builder pattern for configuration:**
```rust
#[derive(Debug, Clone)]
pub struct Config {
    timeout: Duration,
    retry_count: usize,
    // ...
}

pub struct ConfigBuilder {
    timeout: Option<Duration>,
    retry_count: Option<usize>,
}

impl Config {
    pub fn builder() -> ConfigBuilder {
        ConfigBuilder {
            timeout: None,
            retry_count: None,
        }
    }
}

impl ConfigBuilder {
    pub fn timeout(mut self, duration: Duration) -> Self {
        self.timeout = Some(duration);
        self
    }

    pub fn retry_count(mut self, count: usize) -> Self {
        self.retry_count = Some(count);
        self
    }

    pub fn build(self) -> Config {
        Config {
            timeout: self.timeout.unwrap_or(Duration::from_secs(30)),
            retry_count: self.retry_count.unwrap_or(3),
        }
    }
}
```

### 2. Error Handling

**Use thiserror for library errors:**
```rust
use thiserror::Error;

#[derive(Error, Debug)]
pub enum MyError {
    #[error("IO error: {0}")]
    Io(#[from] std::io::Error),

    #[error("Parse error: {0}")]
    Parse(String),

    #[error("Invalid configuration: {field} = {value}")]
    InvalidConfig { field: String, value: String },
}

pub type Result<T> = std::result::Result<T, MyError>;
```

**Use anyhow for application errors:**
```rust
use anyhow::{Context, Result};

fn main() -> Result<()> {
    let config = load_config()
        .context("Failed to load configuration")?;

    process(config)
        .context("Processing failed")?;

    Ok(())
}
```

### 3. Feature Flags

**Cargo.toml features:**
```toml
[features]
default = ["std"]
std = []
async = ["tokio"]
serde = ["dep:serde", "serde/derive"]
full = ["std", "async", "serde"]

[dependencies]
tokio = { version = "1.35", optional = true }
serde = { version = "1.0", optional = true }
```

**Conditional compilation:**
```rust
#[cfg(feature = "async")]
pub async fn async_process() {
    // Async implementation
}

#[cfg(not(feature = "async"))]
pub fn sync_process() {
    // Sync implementation
}
```

### 4. Documentation

**Comprehensive docs:**
```rust
//! # MyProject
//!
//! A high-performance Rust library for amazing things.
//!
//! ## Quick Start
//!
//! ```
//! use myproject::Config;
//!
//! let config = Config::builder()
//!     .timeout(Duration::from_secs(30))
//!     .build();
//!
//! let result = myproject::process(&config)?;
//! # Ok::<(), Box<dyn std::error::Error>>(())
//! ```
//!
//! ## Features
//!
//! - **async**: Enable async support (requires tokio)
//! - **serde**: Enable serialization support

/// Process data with the given configuration.
///
/// # Examples
///
/// ```
/// # use myproject::Config;
/// let config = Config::default();
/// let result = myproject::process(&config)?;
/// # Ok::<(), Box<dyn std::error::Error>>(())
/// ```
///
/// # Errors
///
/// Returns `Err` if the input is invalid or processing fails.
///
/// # Panics
///
/// Panics if the configuration is malformed (debug builds only).
pub fn process(config: &Config) -> Result<Output> {
    // Implementation
}
```

### 5. Testing

**Comprehensive test coverage:**
```rust
#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_basic_functionality() {
        let config = Config::default();
        let result = process(&config).unwrap();
        assert_eq!(result.status, Status::Success);
    }

    #[test]
    #[should_panic(expected = "invalid input")]
    fn test_panics_on_invalid_input() {
        let bad_config = Config { /* ... */ };
        process(&bad_config).unwrap();
    }

    #[test]
    fn test_error_handling() {
        let result = process_with_error();
        assert!(result.is_err());
        assert_eq!(
            result.unwrap_err().to_string(),
            "Parse error: invalid syntax"
        );
    }
}
```

**Doc tests:**
```rust
/// Add two numbers together.
///
/// ```
/// # use myproject::add;
/// assert_eq!(add(2, 2), 4);
/// ```
///
/// ```should_panic
/// # use myproject::add;
/// add(i32::MAX, 1);  // Panics on overflow
/// ```
pub fn add(a: i32, b: i32) -> i32 {
    a.checked_add(b).expect("overflow")
}
```

---

## Testing

### Test Organization

```
myproject/
├── src/
│   ├── lib.rs
│   ├── module.rs
│   └── tests.rs         # Unit tests (optional)
├── tests/
│   ├── integration_test.rs
│   └── common/
│       └── mod.rs
├── benches/
│   └── benchmarks.rs
└── examples/
    └── example.rs
```

### CI Configuration

Use the `rust_ci.yml` reusable workflow for CI:

```yaml
# .github/workflows/ci.yml
name: CI

on:
  push:
    branches: [main]
  pull_request:

jobs:
  # Test with stable Rust
  ci-stable:
    uses: artagon/artagon-workflows/.github/workflows/rust_ci.yml@main
    with:
      rust-version: 'stable'
      enable-coverage: true
    secrets: inherit

  # Test with MSRV
  ci-msrv:
    uses: artagon/artagon-workflows/.github/workflows/rust_ci.yml@main
    with:
      rust-version: '1.70'
      enable-coverage: false
    secrets: inherit

  # Test with beta (optional)
  ci-beta:
    uses: artagon/artagon-workflows/.github/workflows/rust_ci.yml@main
    with:
      rust-version: 'beta'
      enable-coverage: false
    secrets: inherit
```

The reusable workflow includes:
- Build and test with cargo
- Clippy linting (`-D warnings`)
- Rustfmt format checking
- Code coverage with cargo-tarpaulin
- Cargo caching for faster builds
- Nix flake detection

---

## Troubleshooting

### Common Issues

#### "Failed to publish: version already exists"

**Cause:** Version already published to crates.io

**Solution:**
```bash
# Bump version and try again
# Edit Cargo.toml, increment version
git add Cargo.toml
git commit -m "chore: bump version to 1.2.4"
cargo publish
```

#### "MSRV check failed"

**Cause:** Code uses features not available in declared MSRV

**Solution:**
```bash
# Install MSRV version
rustup install 1.70

# Test with MSRV
cargo +1.70 check --all-features

# Fix errors or bump MSRV
```

#### "Cargo.lock conflicts"

**Cause:** Different dependency resolution

**Solution:**
```bash
# For libraries: don't commit Cargo.lock
echo "Cargo.lock" >> .gitignore

# For binaries: do commit Cargo.lock
git add Cargo.lock
```

#### "Documentation build failed on docs.rs"

**Cause:** Missing features or dependencies

**Solution:**
```toml
# Cargo.toml
[package.metadata.docs.rs]
all-features = true
rustdoc-args = ["--cfg", "docsrs"]
```

---

## References

### Standards
- [Semantic Versioning](https://semver.org/)
- [Cargo Book](https://doc.rust-lang.org/cargo/)
- [Rust API Guidelines](https://rust-lang.github.io/api-guidelines/)
- [Keep a Changelog](https://keepachangelog.com/)

### Industry Examples
- [Tokio Release Process](https://github.com/tokio-rs/tokio/blob/master/RELEASING.md)
- [Serde Releases](https://github.com/serde-rs/serde/releases)
- [rust-lang Release Process](https://forge.rust-lang.org/release/process.html)

### Tools
- [cargo-release](https://github.com/crate-ci/cargo-release) - Automate releases
- [cargo-audit](https://github.com/rustsec/rustsec/tree/main/cargo-audit) - Security audits
- [cargo-semver-checks](https://github.com/obi1kenobi/cargo-semver-checks) - SemVer verification
- [cargo-tarpaulin](https://github.com/xd009642/tarpaulin) - Code coverage

---

## Quick Reference

### Common Commands

```bash
# Development
cargo check
cargo test
cargo clippy
cargo fmt

# Release preparation
cargo test --all-features
cargo clippy --all-features -- -D warnings
cargo doc --all-features --no-deps
cargo audit

# Publishing
cargo package --list
cargo publish --dry-run
cargo publish

# Git
git tag -a v1.2.3 -m "Release 1.2.3"
git push origin v1.2.3

# Automation
cargo install cargo-release
cargo release patch  # 1.2.3 → 1.2.4
cargo release minor  # 1.2.3 → 1.3.0
cargo release major  # 1.2.3 → 2.0.0
```

### Version Constraints

```toml
# Caret (default): ^1.2.3 = >=1.2.3, <2.0.0
dep = "1.2.3"

# Tilde: ~1.2.3 = >=1.2.3, <1.3.0
dep = "~1.2.3"

# Wildcard: 1.* = >=1.0.0, <2.0.0
dep = "1.*"

# Comparison: >=1.2.3, <1.5.0
dep = ">=1.2.3, <1.5.0"

# Exact (avoid): =1.2.3
dep = "=1.2.3"
```
