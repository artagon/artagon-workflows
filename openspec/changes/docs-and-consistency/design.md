# Design: Documentation and Consistency

## 1. Workflow Usage Documentation Pattern

### Standard Header Format

```yaml
# Reusable [System] [Category] Workflow
#
# Usage in your project's .github/workflows/[name].yml:
#
# name: [Name]
# on: [trigger]
#
# jobs:
#   [job]:
#     uses: artagon/artagon-workflows/.github/workflows/[workflow].yml@main
#     with:
#       [key-inputs]
#     secrets: inherit

name: [Workflow Name] (Reusable)

on:
  workflow_call:
    # ...
```

### Example: gradle_build.yml

```yaml
# Reusable Gradle Build Workflow
#
# Usage in your project's .github/workflows/ci.yml:
#
# name: CI
# on: [push, pull_request]
#
# jobs:
#   build:
#     uses: artagon/artagon-workflows/.github/workflows/gradle_build.yml@main
#     with:
#       java-version: '21'
#       gradle-args: 'build test'
#     secrets: inherit

name: Gradle Build (Reusable)
```

---

## 2. GRADLE.md Structure

```markdown
# Gradle Workflows

## Overview

Reusable GitHub Actions workflows for Gradle/Java projects.

## Available Workflows

### gradle_build.yml

CI workflow for building and testing Gradle projects.

#### Inputs

| Input | Type | Default | Description |
|-------|------|---------|-------------|
| java-version | string | '21' | Java version |
| java-distribution | string | 'temurin' | Java distribution |
| gradle-args | string | 'build' | Gradle arguments |

#### Usage

\`\`\`yaml
jobs:
  build:
    uses: artagon/artagon-workflows/.github/workflows/gradle_build.yml@main
    with:
      java-version: '21'
    secrets: inherit
\`\`\`

### gradle_release.yml

Release workflow for Gradle projects with SBOM generation.

#### Inputs

| Input | Type | Default | Description |
|-------|------|---------|-------------|
| java-version | string | '21' | Java version |
| generate-sbom | boolean | true | Generate SBOM |
| sign-sbom | boolean | true | Sign SBOM with Cosign |

#### Usage

\`\`\`yaml
name: Release
on:
  push:
    tags: ['v*']

jobs:
  release:
    uses: artagon/artagon-workflows/.github/workflows/gradle_release.yml@main
    secrets: inherit
\`\`\`

## Examples

See [examples/gradle/](../examples/gradle/) for complete examples.
```

---

## 3. Naming Convention Fix

### Current
```
.github/workflows/copilot-setup-steps.yml  # kebab-case
```

### After
```
.github/workflows/copilot_setup_steps.yml  # snake_case
```

### Update Checklist

1. Rename file
2. Update any workflow references (none expected - internal workflow)
3. Update README.md if referenced
4. Verify CI triggers still work

---

## 4. Orphan Documentation

### RELEASE_RUST.md

Options:
1. **Remove** - No Rust workflows exist
2. **Keep as placeholder** - If Rust workflows planned
3. **Move to archive** - Keep for reference

**Recommendation**: Keep with "Planned" note, as Rust support may be added.

```markdown
# Rust Release Workflows

> **Status**: Planned - Not yet implemented

This document will cover Rust release workflows when available.

## Planned Workflows

- `rust_ci.yml` - Build and test with cargo
- `rust_release.yml` - Publish to crates.io
```

---

## Implementation Order

1. **Phase 1**: Add usage docs to workflows (high value, low risk)
2. **Phase 2**: Create GRADLE.md (fills documentation gap)
3. **Phase 3**: Rename copilot workflow (minor change)
4. **Phase 4**: Update RELEASE_RUST.md (cleanup)
