# C Workflow Examples

Example GitHub Actions workflows for C projects using tag-based release strategy.

## Quick Start

Copy the desired workflow files to your project's `.github/workflows/` directory and customize as needed.

## Available Workflows

### [ci.yml](ci.yml)
**Purpose:** Continuous Integration

Runs on every push and pull request.

**Features:**
- Build with CMake
- Run tests with CTest
- Memory leak detection with Valgrind
- Static analysis with cppcheck
- AddressSanitizer and UndefinedBehaviorSanitizer

**Usage:**
```bash
cp ci.yml <your-project>/.github/workflows/
```

### [release.yml](release.yml)
**Purpose:** Create releases from git tags

**Features:**
- Build release artifacts
- Create source tarballs
- Create binary packages (DEB, RPM)
- GPG signing
- Create GitHub release

## Tag-Based Release Strategy

C projects use a **simple tag-based strategy**:

- **`main` branch**: Development (always buildable)
- **Tags**: Primary release mechanism (e.g., `v1.2.3`)
- **`maint-*` branches**: For long-term support (e.g., `maint-1.2`)

### Release Process

```bash
# 1. Update version in CMakeLists.txt
project(MyProject VERSION 1.2.3 LANGUAGES C)

# 2. Update CHANGELOG.md
vim CHANGELOG.md

# 3. Commit changes
git add CMakeLists.txt CHANGELOG.md
git commit -m "chore: bump version to 1.2.3"

# 4. Create tag
git tag -a v1.2.3 -m "Release 1.2.3"

# 5. Push
git push origin main --tags
```

## Customization

### CMake Version

```yaml
with:
  cmake-version: '3.25'  # Or 3.20+
```

### Build Type

```yaml
with:
  build-type: 'Debug'  # Or Release, RelWithDebInfo, MinSizeRel
```

### Disable Sanitizers

```yaml
with:
  enable-sanitizers: false
  run-valgrind: false
```

## Documentation

For complete C release strategy, see:
- **[C Release Strategy](../../docs/RELEASE_C.md)**

## Support

- [artagon-workflows Issues](https://github.com/artagon/artagon-workflows/issues)
