# Add New Workflow

Step-by-step guide for adding a new reusable workflow.

## Prerequisites

- Understand the target build system
- Review existing workflows for patterns
- Check `openspec/specs/` for requirements

## Steps

### 1. Create OpenSpec Proposal (if significant)

```bash
# For new capabilities, create a proposal
CHANGE=add-gradle-ci
mkdir -p openspec/changes/$CHANGE/specs/gradle-workflows

# Write proposal.md, tasks.md, and spec delta
```

### 2. Choose Filename

Follow naming convention:

```
<buildsystem>_[lang]_<category>.yml

Examples:
- gradle_build.yml
- cmake_c_security_scan.yml
- maven_sbom_generate.yml
```

### 3. Create Workflow File

```yaml
name: Descriptive Name

on:
  workflow_call:
    inputs:
      input-name:
        description: 'Input description'
        required: false
        type: string
        default: 'default-value'
    secrets:
      SECRET_NAME:
        description: 'Secret description'
        required: true

jobs:
  job-name:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: read

    steps:
      # actions/checkout@v4.2.2
      - uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683

      - name: Validate inputs
        run: |
          INPUT="${{ inputs.input-name }}"
          if ! echo "$INPUT" | grep -qE '^[SAFE_PATTERN]*$'; then
            echo "Invalid input"
            exit 1
          fi

      # ... rest of workflow
```

### 4. Security Checklist

- [ ] All actions pinned to commit SHAs
- [ ] Version comment above each SHA
- [ ] Permissions block with least privilege
- [ ] Input validation for user inputs
- [ ] Secrets handled via env, not CLI
- [ ] Binary downloads have checksums

### 5. Create Example

Add example in `examples/<buildsystem>/`:

```yaml
# examples/gradle/ci.yml
name: CI

on:
  push:
    branches: [main]

jobs:
  ci:
    uses: artagon/artagon-workflows/.github/workflows/gradle_build.yml@v1
    secrets: inherit
```

### 6. Update Documentation

- Add to README.md workflow list
- Create/update docs/<BUILDSYSTEM>.md
- Add inline workflow comments

### 7. Test

```bash
# Run linting
actionlint .github/workflows/new_workflow.yml
yamllint .github/workflows/new_workflow.yml

# Push and verify CI passes
git add .
git commit -m "feat(<system>): add new workflow"
git push
```

### 8. PR and Review

- Create PR with description
- Reference issue if applicable
- Request security review for new patterns
- Ensure CI passes

## Template: Basic CI Workflow

```yaml
name: Build System CI

on:
  workflow_call:
    inputs:
      version:
        description: 'Tool version'
        required: false
        type: string
        default: 'latest-lts'
    secrets:
      REGISTRY_TOKEN:
        description: 'Registry authentication token'
        required: false

jobs:
  build:
    runs-on: ubuntu-latest
    permissions:
      contents: read

    steps:
      # actions/checkout@v4.2.2
      - name: Checkout code
        uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683

      - name: Validate inputs
        run: |
          VERSION="${{ inputs.version }}"
          if ! echo "$VERSION" | grep -qE '^[a-zA-Z0-9.-]+$'; then
            echo "Invalid version format"
            exit 1
          fi
          echo "Version validated: $VERSION"

      - name: Setup environment
        run: |
          # Setup build tools
          echo "Setting up..."

      - name: Build
        run: |
          # Build commands
          echo "Building..."

      - name: Test
        run: |
          # Test commands
          echo "Testing..."
```
