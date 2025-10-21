# Bazel Workflow Examples

Example GitHub Actions workflows for Bazel projects.

## Quick Start

Copy the desired workflow files to your project's `.github/workflows/` directory and customize as needed.

## Available Workflows

### [ci.yml](ci.yml)
**Purpose:** Continuous Integration

Runs on every push and pull request.

**Features:**
- Build with Bazel
- Run tests
- Multiple configurations (release, debug)
- Caching for faster builds

**Usage:**
```bash
cp ci.yml <your-project>/.github/workflows/
```

### [release.yml](release.yml)
**Purpose:** Create releases from git tags

**Features:**
- Build release artifacts
- Run tests
- Create source tarballs
- Create GitHub release

## Release Strategy

Bazel projects can use either tag-based or release branch strategy depending on complexity.

### Simple Projects (Tag-Based)

```bash
# 1. Update version in MODULE.bazel or WORKSPACE
module(
    name = "myproject",
    version = "1.2.3",
)

# 2. Update CHANGELOG.md
vim CHANGELOG.md

# 3. Commit
git add MODULE.bazel CHANGELOG.md
git commit -m "chore: bump version to 1.2.3"

# 4. Create tag
git tag -a v1.2.3 -m "Release 1.2.3"

# 5. Push
git push origin main --tags
```

### Complex Projects (Release Branches)

Follow C++ release branch strategy for complex Bazel projects with multiple components.

## Customization

### Bazel Version

```yaml
with:
  bazel-version: '7.1.0'
```

### Build Configurations

```yaml
with:
  bazel-configs: 'release,debug,asan'
```

### Disable Caching

```yaml
with:
  cache-enabled: false
```

## Bazel Configurations

Example `.bazelrc` for different build configurations:

```
# .bazelrc
build:release --compilation_mode=opt
build:debug --compilation_mode=dbg
build:asan --features=asan
```

## Documentation

For Bazel-specific workflows, see:
- [Bazel Workflows Documentation](../../docs/BAZEL.md)

For language-specific release strategies:
- [C Release Strategy](../../docs/RELEASE_C.md) (for C projects using Bazel)
- [C++ Release Strategy](../../docs/RELEASE_CPP.md) (for C++ projects using Bazel)

## Support

- [artagon-workflows Issues](https://github.com/artagon/artagon-workflows/issues)
