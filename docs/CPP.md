# C/C++ Workflows

Reusable GitHub Actions workflows for C/C++ projects using CMake.

## Available Workflows

### c-ci.yml - C Project CI

Continuous integration for C projects.

**Usage:**
```yaml
jobs:
  ci:
    uses: artagon/artagon-workflows/.github/workflows/c-ci.yml@v1
    with:
      cmake-options: '-DCMAKE_BUILD_TYPE=Release'
```

### cpp-ci.yml - C++ Project CI

Continuous integration for C++ projects.

**Usage:**
```yaml
jobs:
  ci:
    uses: artagon/artagon-workflows/.github/workflows/cpp-ci.yml@v1
    with:
      cmake-options: '-DCMAKE_BUILD_TYPE=Release'
```

### c-release.yml - C Project Release

Release workflow for C projects.

**Usage:**
```yaml
jobs:
  release:
    uses: artagon/artagon-workflows/.github/workflows/c-release.yml@v1
    with:
      release-version: '1.0.0'
```

### cpp-release.yml - C++ Project Release

Release workflow for C++ projects.

**Usage:**
```yaml
jobs:
  release:
    uses: artagon/artagon-workflows/.github/workflows/cpp-release.yml@v1
    with:
      release-version: '1.0.0'
```

## Examples

See [examples/cpp/](../examples/cpp/) for complete workflow examples.
