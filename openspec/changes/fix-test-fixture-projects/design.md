# Design: Fix Test Fixture Project Failures

## Overview

Create minimal but complete test fixture projects that properly exercise the reusable workflows without introducing unnecessary complexity.

## Architecture

### Test Repository Structure

```
artagon-workflow-test-bazel/
├── .github/workflows/ci.yml     # Calls bazel_multi_ci.yml
├── MODULE.bazel                  # Bazel module definition
├── BUILD.bazel                   # Root BUILD file
└── src/
    ├── BUILD.bazel              # Package BUILD file
    ├── hello.cc                 # Simple C++ source
    └── hello_test.cc            # Simple test

artagon-workflow-test-cmake/
├── .github/workflows/ci.yml     # Calls cmake_c_ci.yml, cmake_cpp_ci.yml
├── CMakeLists.txt               # Root CMake config
├── src/
│   ├── CMakeLists.txt
│   ├── hello.c                  # C source
│   └── hello.cpp                # C++ source
└── test/
    ├── CMakeLists.txt
    └── test_hello.c             # Test source

artagon-workflow-test-rust/
├── .github/workflows/ci.yml     # Calls rust_ci.yml
├── Cargo.toml
└── src/
    ├── lib.rs                   # Minimal Rust library
    └── main.rs                  # Binary entry point
```

## Component Design

### 1. Bazel Test Project

**MODULE.bazel**:
```starlark
module(name = "test_project", version = "0.1.0")
```

**src/BUILD.bazel**:
```starlark
cc_binary(
    name = "hello",
    srcs = ["hello.cc"],
)

cc_test(
    name = "hello_test",
    srcs = ["hello_test.cc"],
)
```

### 2. Artifact Naming Fix

The `bazel_multi_ci.yml` workflow has artifact naming conflicts when multiple matrix configurations run. Fix by including more context in artifact names:

```yaml
# Current (causes conflicts):
name: dependency-graph

# Fixed:
name: dependency-graph-${{ matrix.os }}-${{ matrix.config }}
```

### 3. Bazel Query Fix

The query syntax fails with multiple targets. Fix in `bazel_multi_ci.yml`:

```yaml
# Current (fails with space-separated targets):
bazel query --output=graph 'deps(${{ inputs.targets }})' > deps.dot

# Fixed (quote properly):
bazel query --output=graph "deps(${{ inputs.targets }})" > deps.dot || true
```

### 4. CMake Test Project

Minimal C/C++ project with:
- Working build configuration
- At least one executable target
- At least one test target
- Coverage-compatible structure

### 5. Rust CI Workflow (rust_ci.yml)

Create reusable Rust CI workflow with:

**Inputs**:
```yaml
inputs:
  rust-version:
    description: 'Rust toolchain version'
    type: string
    default: 'stable'
  enable-clippy:
    description: 'Run clippy lints'
    type: boolean
    default: true
  enable-rustfmt:
    description: 'Check formatting'
    type: boolean
    default: true
  enable-coverage:
    description: 'Generate code coverage'
    type: boolean
    default: true
  cargo-features:
    description: 'Cargo features to enable'
    type: string
    default: ''
```

**Jobs**:
1. `validate-inputs` - Validate workflow inputs
2. `build-test` - Build and run tests on ubuntu-latest
3. `clippy` - Run clippy lints (if enabled)
4. `rustfmt` - Check formatting (if enabled)
5. `coverage` - Generate coverage with cargo-tarpaulin (if enabled)

**Key Features**:
- Nix flake detection (like other workflows)
- Cargo caching for faster builds
- Support for workspace projects
- MSRV (Minimum Supported Rust Version) testing option

### 6. Rust Test Project

Update test repo CI to call reusable workflow:
```yaml
jobs:
  test-stable:
    uses: artagon/artagon-workflows/.github/workflows/rust_ci.yml@main
    with:
      rust-version: 'stable'
    secrets: inherit

  test-nightly:
    uses: artagon/artagon-workflows/.github/workflows/rust_ci.yml@main
    with:
      rust-version: 'nightly'
    secrets: inherit
```

## Security Considerations

- Test projects should not contain secrets
- Use minimal dependencies
- No network access required during build

## Testing Strategy

1. Fix test projects via GitHub API
2. Trigger CI runs
3. Verify all jobs pass
4. Monitor daily scheduled runs

## Rollback Plan

- Revert test project changes via GitHub API
- Test projects are isolated from main workflow repo
