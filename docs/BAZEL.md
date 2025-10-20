# Bazel Workflows

Reusable GitHub Actions workflows for Bazel projects.

## Available Workflows

### bazel-ci.yml - Bazel CI

Continuous integration with multiple configurations.

**Usage:**
```yaml
jobs:
  ci:
    uses: artagon/artagon-workflows/.github/workflows/bazel-ci.yml@v1
    with:
      bazel-configs: 'release,debug,asan'
```

**Inputs:**
- `bazel-configs` - Comma-separated list of Bazel configurations
- `bazel-version` - Bazel version (default: 'latest')

### bazel-release.yml - Bazel Release

Release workflow for Bazel projects.

**Usage:**
```yaml
jobs:
  release:
    uses: artagon/artagon-workflows/.github/workflows/bazel-release.yml@v1
    with:
      release-version: '1.0.0'
```

## Examples

See [examples/bazel/](../examples/bazel/) for complete workflow examples.
