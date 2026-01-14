# Contributing to Artagon Workflows

Thank you for your interest in contributing! This repository uses **OpenSpec** for spec-driven development.

## Quick Start

1. Read [openspec/AGENTS.md](openspec/AGENTS.md) for the complete workflow
2. Check [openspec/contributing.md](openspec/contributing.md) for detailed guidelines
3. Review [openspec/specs/workflow-security/spec.md](openspec/specs/workflow-security/spec.md) for security requirements

## Code of Conduct

This project follows professional open-source development practices. Be respectful, constructive, and collaborative.

## Prerequisites

- GitHub account with access to Artagon organization
- Understanding of GitHub Actions and workflow syntax
- Familiarity with the build system you're working with (Maven, CMake, Bazel, Gradle)
- Test repository for workflow testing

## Development Workflow

### 1. Check OpenSpec Status

```bash
openspec list              # Active changes
openspec list --specs      # Capabilities
```

### 2. Create Issue or Proposal

For significant changes, create a spec issue:
- Use the **Spec** issue template for new capabilities
- Use the **Proposal** issue template for implementation approaches
- Use the **Bug** template for bug reports

### 3. Create Branch

```bash
# OpenSpec changes
feature/workflows(<issue#>)-<short-name>

# Other changes
<type>/<scope>-<description>

# Examples
feature/workflows(42)-add-gradle-workflows
fix/maven-cache-issue
security/pin-trivy-action
```

### 4. Make Changes

Follow the security requirements (see below).

### 5. Commit

```bash
git commit -m "feat(maven): add Java 26 support

- Implementation details
- References

Closes #42"
```

### 6. Create PR

Use the PR template which includes:
- Security checklist
- Spec compliance checklist
- Testing checklist

## Security Requirements (MANDATORY)

All workflow changes MUST comply:

### Action Pinning

```yaml
# CORRECT
# actions/checkout@v4.2.2
uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683

# WRONG
uses: actions/checkout@v4
```

### Permissions

```yaml
# CORRECT
jobs:
  build:
    permissions:
      contents: read
    steps: [...]

# WRONG - missing permissions
jobs:
  build:
    steps: [...]
```

### Input Validation

```yaml
# CORRECT
- name: Validate inputs
  run: |
    if ! echo "${{ inputs.value }}" | grep -qE '^[SAFE]*$'; then
      exit 1
    fi

# WRONG - direct use
run: mvn ${{ inputs.maven-args }}
```

### Secret Handling

```yaml
# CORRECT - in env/config
env:
  PASSWORD: ${{ secrets.PASSWORD }}

# WRONG - in CLI
run: cmd --password="${{ secrets.PASSWORD }}"
```

## Testing

**CRITICAL:** Never test workflows directly in this repository.

1. Create a test repository
2. Reference your branch: `uses: your-fork/.github/workflows/workflow.yml@your-branch`
3. Test all input combinations
4. Verify with real projects

See [docs/TESTING.md](docs/TESTING.md) for detailed testing procedures.

## Documentation

When adding or modifying workflows, update:

1. Workflow file comments
2. README.md if needed
3. Workflow-specific docs in `docs/`
4. Examples in `examples/`
5. OpenSpec specs if behavior changes

## Versioning

Workflows follow semantic versioning:
- **Major (v1 → v2):** Breaking changes
- **Minor (v1.0 → v1.1):** New features, backward compatible
- **Patch (v1.0.0 → v1.0.1):** Bug fixes

See [docs/MIGRATION_v2.md](docs/MIGRATION_v2.md) for migration guides.

## Resources

### OpenSpec
- [openspec/AGENTS.md](openspec/AGENTS.md) - Complete workflow
- [openspec/contributing.md](openspec/contributing.md) - Detailed guidelines
- [openspec/project.md](openspec/project.md) - Project context

### Documentation
- [docs/TESTING.md](docs/TESTING.md) - Testing guide
- [docs/MAVEN.md](docs/MAVEN.md) - Maven workflows
- [docs/CPP.md](docs/CPP.md) - C/C++ workflows
- [docs/BAZEL.md](docs/BAZEL.md) - Bazel workflows

### Support
- [GitHub Issues](https://github.com/artagon/artagon-workflows/issues)
- [Discussions](https://github.com/artagon/artagon-workflows/discussions)

## Questions?

See [openspec/AGENTS.md](openspec/AGENTS.md) or open an issue.

Thank you for contributing to Artagon Workflows!
