# Artagon Workflows Test Suite

This directory contains the testing infrastructure for artagon-workflows, implementing Phase 1 (Foundation) of the [Testing Strategy](../.model-context/TESTING_STRATEGY.md).

## Structure

```
test/
├── fixtures/           # Minimal test projects for each build system
│   ├── maven/         # Maven test fixture (Java 17, JUnit 5)
│   ├── cmake_c/       # CMake C test fixture (C17)
│   ├── cmake_cpp/     # CMake C++ test fixture (C++20)
│   ├── bazel/         # Bazel test fixture (C++)
│   └── rust/          # Rust test fixture (Cargo)
└── workflows/         # Reserved for unit test workflows (Phase 2)
```

## Test Fixtures

Each test fixture is a minimal, self-contained project that:
- Compiles successfully with standard configurations
- Includes at least one test that passes
- Follows language/build system conventions
- Has no external dependencies (where possible)

### Maven Fixture
- **Language**: Java 17
- **Framework**: JUnit 5.10.0
- **Build**: Standard Maven layout with `pom.xml`
- **Tests**: 4 test cases in `HelloTest.java`

### CMake C Fixture
- **Language**: C17
- **Build**: CMake 3.20+
- **Tests**: Simple assertion-based tests

### CMake C++ Fixture
- **Language**: C++20
- **Build**: CMake 3.20+
- **Tests**: Simple assertion-based tests

### Bazel Fixture
- **Language**: C++
- **Build**: Bazel with WORKSPACE and BUILD.bazel
- **Tests**: cc_test target

### Rust Fixture
- **Language**: Rust 2021 edition
- **Build**: Cargo
- **Tests**: Standard Rust test framework

## Test Workflows

The following GitHub Actions workflows validate the reusable workflows:

### Level 1: Syntax and Schema Validation

**Workflow**: `.github/workflows/test_lint.yml`

- **actionlint**: Validates workflow syntax and GitHub Actions usage
- **yamllint**: Validates YAML formatting and structure

Runs on: Every push to main, every PR that modifies workflows

### Security Testing

**Workflow**: `.github/workflows/test_security.yml`

Three security validation jobs:

1. **Secret Scanning**
   - Detects hardcoded secrets (passwords, tokens, keys)
   - Checks for GitHub PATs, AWS keys
   - Verifies secrets use `${{ secrets.* }}` syntax

2. **Permission Validation**
   - Ensures workflows have explicit permission blocks
   - Detects overly permissive `write-all` permissions
   - Enforces least-privilege principle

3. **Action Pinning Validation**
   - Verifies all actions are pinned to commit SHAs
   - Detects unpinned actions (major versions, branches)
   - Validates SHA format (40 characters)

Runs on: Every push to main, every PR that modifies workflows

### Level 2: Unit Test Workflows

**Workflows**: `.github/workflows/test_*_ci.yml`

Individual workflow validation tests:

1. **Maven CI Tests** (`test_maven_ci.yml`)
   - Test with Java 17 and 21
   - Test with/without running tests
   - Test with custom Maven arguments
   - Verify build artifacts

2. **CMake C CI Tests** (`test_cmake_c_ci.yml`)
   - Test with C standards: 11, 17, 23
   - Test with custom CMake options
   - Verify build and test execution

3. **CMake C++ CI Tests** (`test_cmake_cpp_ci.yml`)
   - Test with C++ standards: 17, 20, 23
   - Test with custom CMake options
   - Verify build and test execution

4. **Bazel CI Tests** (`test_bazel_ci.yml`)
   - Test minimal configuration
   - Test with release/debug configurations
   - Verify build and test targets

Each unit test workflow:
- Tests multiple configurations (minimal, custom, edge cases)
- Includes verification job that validates all tests passed
- Generates test result summary
- Fails if any test configuration fails

Runs on: Every push to main, every PR that modifies corresponding workflow or test fixtures

## Running Tests Locally

### Lint Workflows
```bash
# Install actionlint
bash <(curl https://raw.githubusercontent.com/rhysd/actionlint/main/scripts/download-actionlint.bash)

# Run actionlint
./actionlint -color -verbose

# Install yamllint
pip install yamllint

# Run yamllint
yamllint .github/workflows/
```

### Test Maven Fixture
```bash
cd test/fixtures/maven
mvn clean test
```

### Test CMake C Fixture
```bash
cd test/fixtures/cmake_c
mkdir build && cd build
cmake ..
cmake --build .
ctest
```

### Test CMake C++ Fixture
```bash
cd test/fixtures/cmake_cpp
mkdir build && cd build
cmake ..
cmake --build .
ctest
```

### Test Bazel Fixture
```bash
cd test/fixtures/bazel
bazel test //...
```

### Test Rust Fixture
```bash
cd test/fixtures/rust
cargo test
```

## Implementation Status

- [x] Phase 1: Foundation
  - [x] Test fixtures for all build systems
  - [x] Syntax/schema validation (actionlint, yamllint)
  - [x] Security testing workflows
    - [x] Secret scanning
    - [x] Permission validation
    - [x] Action pinning validation

- [x] Phase 2: Unit Tests
  - [x] Unit test workflows for each reusable workflow
    - [x] Maven CI (`test_maven_ci.yml`)
    - [x] CMake C CI (`test_cmake_c_ci.yml`)
    - [x] CMake C++ CI (`test_cmake_cpp_ci.yml`)
    - [x] Bazel CI (`test_bazel_ci.yml`)
  - [x] Test verification jobs with result summaries
  - [x] Multiple configuration testing (minimal, custom, edge cases)

- [ ] Phase 3: Integration Tests
  - [ ] Matrix test workflows
  - [ ] Test all input combinations
  - [ ] Weekly test schedule

- [ ] Phase 4: Security & Performance
  - [ ] Performance benchmarks
  - [ ] Cache effectiveness tests

- [ ] Phase 5: E2E & Monitoring
  - [ ] External test repositories
  - [ ] Health dashboard
  - [ ] Alerting configuration

## Next Steps

1. Implement Phase 2: Unit Tests
   - Create test workflows for each reusable workflow
   - Test minimal, custom, and edge-case configurations
   - Verify outputs and artifacts

2. Set up CI pipeline
   - Integrate all test workflows
   - Require tests to pass before merge

3. Document expected behaviors
   - Create test case documentation
   - Define success criteria

## References

- [Testing Strategy](../.model-context/TESTING_STRATEGY.md) - Complete testing strategy
- [Security Implementation Plan](../.model-context/SECURITY_IMPLEMENTATION_PLAN.md) - Security roadmap
- [GitHub Actions Best Practices](https://docs.github.com/en/actions/learn-github-actions/workflow-syntax-for-github-actions)
- [actionlint](https://github.com/rhysd/actionlint) - Workflow linting tool

## Contributing

When adding new test fixtures or workflows:

1. Follow existing patterns in this directory
2. Ensure tests are minimal but complete
3. Update this README with new fixtures/workflows
4. Run all tests locally before committing
5. Follow security best practices from `.model-context/`
