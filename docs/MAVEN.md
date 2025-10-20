# Maven Workflows

Reusable GitHub Actions workflows for Maven projects.

## Available Workflows

### maven-ci.yml - Continuous Integration

Full CI pipeline with build, test, and verification.

**Usage:**
```yaml
jobs:
  ci:
    uses: artagon/artagon-workflows/.github/workflows/maven-ci.yml@v1
    secrets: inherit
```

### maven-build.yml - Build Only

Build without deployment.

**Usage:**
```yaml
jobs:
  build:
    uses: artagon/artagon-workflows/.github/workflows/maven-build.yml@v1
    with:
      java-version: '25'
```

### maven-deploy.yml - Snapshot Deployment

Deploy SNAPSHOT versions to OSSRH.

**Usage:**
```yaml
jobs:
  deploy:
    uses: artagon/artagon-workflows/.github/workflows/maven-deploy.yml@v1
    secrets: inherit
```

### maven-release.yml - Full Release

Complete release process with version management.

**Usage:**
```yaml
jobs:
  release:
    uses: artagon/artagon-workflows/.github/workflows/maven-release.yml@v1
    with:
      release-version: '1.0.0'
    secrets: inherit
```

## Examples

See [examples/maven/](../examples/maven/) for complete workflow examples.
