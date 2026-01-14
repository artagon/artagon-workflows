# OpenSpec Instructions

Instructions for AI coding assistants using OpenSpec for spec-driven development of GitHub Actions workflows.

## TL;DR Quick Checklist

- Search existing work: `openspec spec list --long`, `openspec list` (use `rg` only for full-text search)
- Decide scope: new capability vs modify existing capability
- Pick a unique `change-id`: kebab-case, verb-led (`add-`, `update-`, `remove-`, `refactor-`)
- Scaffold: `proposal.md`, `tasks.md`, `design.md` (only if needed), and delta specs per affected capability
- Write deltas: use `## ADDED|MODIFIED|REMOVED|RENAMED Requirements`; include at least one `#### Scenario:` per requirement
- Validate: `openspec validate [change-id] --strict` and fix issues
- Request approval: Do not start implementation until proposal is approved

## Project-Specific Context

**CRITICAL**: This project provides reusable GitHub Actions workflows. All changes must follow strict security guidelines:

1. **Action Pinning**: ALL GitHub Actions must be pinned to commit SHAs (40 characters)
2. **Permissions**: ALL jobs must have explicit `permissions:` blocks with least-privilege
3. **Input Validation**: ALL user-controlled inputs must be validated before shell execution
4. **Secret Handling**: NEVER put secrets in command-line arguments; use config files
5. **Binary Downloads**: ALWAYS verify checksums for downloaded binaries

See `openspec/specs/workflow-security/spec.md` for detailed security requirements.

## Three-Stage Workflow

### Stage 1: Creating Changes

Create proposal when you need to:

- Add new workflows or workflow features
- Modify security patterns or permissions
- Change workflow naming conventions
- Update action versions or dependencies
- Add new build system support

Triggers (examples):

- "Help me create a change proposal"
- "Help me plan a change"
- "I want to add a new workflow"
- "I want to update security patterns"

Skip proposal for:

- Bug fixes (restore intended behavior)
- Typos, formatting, comments
- Minor documentation updates
- Adding test fixtures only

**Workflow**

1. Review `openspec/project.md`, `openspec list`, and `openspec list --specs` to understand current context.
2. Choose a unique verb-led `change-id` and scaffold `proposal.md`, `tasks.md`, optional `design.md`, and spec deltas under `openspec/changes/<id>/`.
3. Draft spec deltas using `## ADDED|MODIFIED|REMOVED Requirements` with at least one `#### Scenario:` per requirement.
4. Run `openspec validate <id> --strict` and resolve any issues before sharing the proposal.

### Stage 2: Implementing Changes

Track these steps as TODOs and complete them one by one.

1. **Read proposal.md** - Understand what's being built
2. **Read design.md** (if exists) - Review technical decisions
3. **Read tasks.md** - Get implementation checklist
4. **Implement tasks sequentially** - Complete in order
5. **Confirm completion** - Ensure every item in `tasks.md` is finished before updating statuses
6. **Update checklist** - After all work is done, set every task to `- [x]` so the list reflects reality
7. **Approval gate** - Do not start implementation until the proposal is reviewed and approved

### Stage 3: Archiving Changes

After deployment, create separate PR to:

- Move `changes/[name]/` to `changes/archive/YYYY-MM-DD-[name]/`
- Update `specs/` if capabilities changed
- Use `openspec archive <change-id> --skip-specs --yes` for tooling-only changes
- Run `openspec validate --strict` to confirm the archived change passes checks

## Security Patterns (COPY-PASTE READY)

### Pattern 1: Pin Action to SHA

```yaml
# actions/checkout@v4.2.2
- uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683
```

### Pattern 2: Add Permissions (CI)

```yaml
jobs:
  ci:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: read
    steps: [...]
```

### Pattern 3: Add Permissions (Release)

```yaml
jobs:
  release:
    runs-on: ubuntu-latest
    permissions:
      contents: write
      packages: write
    steps: [...]
```

### Pattern 4: Input Validation

```yaml
- name: Validate inputs
  run: |
    INPUT="${{ inputs.user-input }}"
    if ! echo "$INPUT" | grep -qE '^[ALLOWED_PATTERN]*$'; then
      echo "Invalid input"
      exit 1
    fi
    echo "Validation passed"
```

### Pattern 5: GPG Settings (Not CLI)

```yaml
- name: Configure GPG
  env:
    GPG_PASSPHRASE: ${{ secrets.GPG_PASSPHRASE }}
  run: |
    mkdir -p ~/.m2
    cat > ~/.m2/settings.xml <<EOF
    <settings>
      <profiles><profile><id>gpg</id>
        <properties>
          <gpg.passphrase>${GPG_PASSPHRASE}</gpg.passphrase>
        </properties>
      </profile></profiles>
    </settings>
    EOF
    chmod 600 ~/.m2/settings.xml
```

## Before Any Task

**Context Checklist:**

- [ ] Read relevant specs in `specs/[capability]/spec.md`
- [ ] Check pending changes in `changes/` for conflicts
- [ ] Read [@/openspec/project.md](./project.md) for conventions
- [ ] Read [@/openspec/contributing.md](./contributing.md) for project contribution guidelines
- [ ] Run `openspec list` to see active changes
- [ ] Run `openspec list --specs` to see existing capabilities

**Before Creating Specs:**

- Always check if capability already exists
- Prefer modifying existing specs over creating duplicates
- Use `openspec show [spec]` to review current state
- If request is ambiguous, ask 1-2 clarifying questions before scaffolding

## Directory Structure

```
openspec/
├── AGENTS.md               # This file - AI instructions
├── project.md              # Project conventions
├── contributing.md         # Contribution guidelines
├── specs/                  # Current truth - what IS built
│   ├── workflow-security/
│   │   └── spec.md         # Security requirements
│   ├── maven-workflows/
│   │   └── spec.md         # Maven workflow specs
│   ├── cmake-workflows/
│   │   └── spec.md         # CMake workflow specs
│   └── bazel-workflows/
│       └── spec.md         # Bazel workflow specs
├── changes/                # Proposals - what SHOULD change
│   ├── [change-name]/
│   │   ├── proposal.md     # Why, what, impact
│   │   ├── tasks.md        # Implementation checklist
│   │   ├── design.md       # Technical decisions (optional)
│   │   └── specs/          # Delta changes
│   │       └── [capability]/
│   │           └── spec.md # ADDED/MODIFIED/REMOVED
│   └── archive/            # Completed changes
```

## CLI Commands

```bash
# Essential commands
openspec list                  # List active changes
openspec list --specs          # List specifications
openspec show [item]           # Display change or spec
openspec validate [item]       # Validate changes or specs
openspec archive <change-id> [--yes|-y]   # Archive after deployment

# Debugging
openspec show [change] --json --deltas-only
openspec validate [change] --strict
```

## Spec File Format

### Critical: Scenario Formatting

**CORRECT** (use #### headers):

```markdown
#### Scenario: Workflow executes successfully

- **WHEN** valid inputs provided
- **THEN** build completes without errors
```

**WRONG** (don't use bullets or bold):

```markdown
- **Scenario: User login**
**Scenario**: User login

### Scenario: User login
```

Every requirement MUST have at least one scenario.

### Requirement Wording

- Use SHALL/MUST for normative requirements (avoid should/may unless intentionally non-normative)

### Delta Operations

- `## ADDED Requirements` - New capabilities
- `## MODIFIED Requirements` - Changed behavior
- `## REMOVED Requirements` - Deprecated features
- `## RENAMED Requirements` - Name changes

## Common Mistakes to Avoid

1. **Using mutable action tags**
   ```yaml
   # NEVER do this:
   uses: actions/checkout@v4
   uses: aquasecurity/trivy-action@master
   ```

2. **Omitting permissions blocks**
   ```yaml
   # WRONG - no permissions specified
   jobs:
     build:
       runs-on: ubuntu-latest
       steps: [...]
   ```

3. **Putting secrets in command-line arguments**
   ```yaml
   # WRONG - visible in ps, logs, core dumps
   run: mvn deploy -Dpassword="${{ secrets.PASSWORD }}"
   ```

4. **Skipping input validation**
   ```yaml
   # WRONG - command injection risk
   run: mvn clean install ${{ inputs.maven-args }}
   ```

5. **Downloading binaries without checksum**
   ```bash
   # WRONG - no verification
   wget https://example.com/binary
   chmod +x binary
   ```

## Best Practices

### Simplicity First

- Default to minimal workflow changes
- Single-purpose workflows
- Avoid framework proliferation
- Choose boring, proven patterns

### Clear References

- Use `workflow.yml:42` format for code locations
- Reference specs as `specs/workflow-security/spec.md`
- Link related changes and PRs

### Workflow Naming Convention

```
<buildsystem>_[lang]_<category>.yml

Examples:
- maven_ci.yml              (language-specific system)
- maven_release.yml
- cmake_c_ci.yml            (multi-language system + language)
- cmake_cpp_release.yml
- bazel_multi_ci.yml        (multi-language, no specific lang)
```

## Tool Selection Guide

| Task                  | Tool | Why                      |
| --------------------- | ---- | ------------------------ |
| Find files by pattern | Glob | Fast pattern matching    |
| Search code content   | Grep | Optimized regex search   |
| Read specific files   | Read | Direct file access       |
| Explore unknown scope | Task | Multi-step investigation |

## Quick Reference

### Stage Indicators

- `changes/` - Proposed, not yet built
- `specs/` - Built and deployed
- `archive/` - Completed changes

### File Purposes

- `proposal.md` - Why and what
- `tasks.md` - Implementation steps
- `design.md` - Technical decisions
- `spec.md` - Requirements and behavior

### CLI Essentials

```bash
openspec list              # What's in progress?
openspec show [item]       # View details
openspec validate --strict # Is it correct?
openspec archive <change-id> [--yes|-y]  # Mark complete
```

Remember: Specs are truth. Changes are proposals. Keep them in sync.
