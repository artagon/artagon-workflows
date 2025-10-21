# C++ Workflow Examples

Example GitHub Actions workflows for C++ projects using release branch strategy with LTS support.

## Quick Start

Copy the desired workflow files to your project's `.github/workflows/` directory and customize as needed.

## Available Workflows

### [ci.yml](ci.yml)
**Purpose:** Continuous Integration

Runs on every push and pull request.

**Features:**
- Build with CMake (C++17/20/23)
- Run tests with GoogleTest
- clang-tidy static analysis
- cppcheck
- Sanitizers (Address, Undefined, Thread)

**Usage:**
```bash
cp ci.yml <your-project>/.github/workflows/
```

### [release.yml](release.yml)
**Purpose:** Create releases with LTS support

**Features:**
- Build release artifacts
- ABI compatibility verification
- Benchmarks
- Source tarballs and binary packages
- Create GitHub release

## Release Branch Strategy

C++ projects use **release branches with optional LTS**:

- **`main` branch**: Development (C++20/23 features)
- **`release-X.Y` branches**: Stable releases (e.g., `release-2.1`)
- **`lts-X.Y` branches**: Long-term support (e.g., `lts-2.0`)
- **Tags**: Version tags (e.g., `v2.1.3`)

### Release Process

```bash
# 1. Create release branch
git checkout -b release-2.1

# 2. Update version in CMakeLists.txt
project(MyProject VERSION 2.1.0 LANGUAGES CXX)

# 3. Update CHANGELOG.md
vim CHANGELOG.md

# 4. Commit and push
git add CMakeLists.txt CHANGELOG.md
git commit -m "chore: release 2.1.0"
git push origin release-2.1

# 5. Create tag
git tag -a v2.1.0 -m "Release 2.1.0"
git push origin v2.1.0

# 6. (Optional) Create LTS branch for long-term support
git checkout -b lts-2.0 release-2.0
git push origin lts-2.0
```

## LTS Support

For projects requiring long-term support:

**Standard releases:**
- 1 year of bug fixes
- 18 months of security fixes

**LTS releases:**
- 3 years of bug fixes
- 5 years of security fixes

## Customization

### C++ Standard

```yaml
with:
  cxx-standard: '23'  # Or 17, 20
```

### CMake Version

```yaml
with:
  cmake-version: '3.25'
```

### ABI Verification

```yaml
with:
  verify-abi-compatibility: true  # Checks ABI stability
```

## Documentation

For complete C++ release strategy, see:
- **[C++ Release Strategy](../../docs/RELEASE_CPP.md)**

## Support

- [artagon-workflows Issues](https://github.com/artagon/artagon-workflows/issues)
