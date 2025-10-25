# AI Model Instructions for Artagon Workflows

**Purpose**: General instructions for AI models working on this project
**Audience**: Claude, GPT-4, and other AI coding assistants
**Last Updated**: 2025-10-25

---

## 🎯 Quick Start for AI Models

When you start working on this project:

### Step 1: Read Context (REQUIRED)
```
1. Read .model-context/context.md first (main entry point - contains status)
2. Check SECURITY_IMPLEMENTATION_PLAN.md for detailed roadmap
3. Review relevant specialized docs as needed
```

### Step 2: Understand Current State
```
✅ 75% Complete - Production Ready
✅ All critical security vulnerabilities fixed
✅ All actions pinned to commit SHAs
⏳ 25% remaining (permissions, validation, test fixtures)
```

### Step 3: Apply Security Patterns
```
- Pin all actions to SHAs (.model-context/skills/github-workflows.md)
- Add explicit permissions (see templates in context.md)
- Validate all user inputs (see examples in context.md)
- Follow mono-repo strategy (REPOSITORY_STRATEGY.md)
```

---

## 📋 Project-Specific Rules

### MANDATORY Rules (Always Apply)

#### 1. **No Redoing Completed Work**
```
✅ COMPLETED (Do NOT redo):
- Action pinning (17/17 workflows) ✅
- Trivy @master fix ✅
- GPG passphrase fix ✅
- Buildifier checksum ✅
- Test infrastructure ✅
- All documentation ✅

⏳ REMAINING (Can work on):
- Permissions blocks (8 workflows)
- Input validation (5 workflows)
- Test fixtures
```

**Check Before Starting**:
```bash
# Always verify if task is already done by checking context.md
grep "your-task" .model-context/context.md
```

#### 2. **Repository Structure: MONO-REPO**
```
✅ DO: Keep all workflows in single repository
✅ DO: Use build-system-specific labels
✅ DO: Use CODEOWNERS for ownership
❌ DON'T: Split into artagon-workflow-maven, etc.

Reason: Saves 230 hours/year, unified versioning, atomic updates
Reference: .model-context/REPOSITORY_STRATEGY.md
```

#### 3. **Security First (Non-Negotiable)**
```
✅ Pin ALL actions to commit SHAs (40 characters)
✅ Add explicit permissions to ALL jobs
✅ Validate ALL user inputs
✅ Use settings.xml for GPG passphrases
✅ Verify checksums for binary downloads

Reference: .model-context/skills/github-workflows.md
```

#### 4. **No Claude Attribution in Commits**
```
❌ WRONG:
feat: add feature

🤖 Generated with Claude Code
Co-Authored-By: Claude <noreply@anthropic.com>

✅ CORRECT:
feat: add feature

- Implement new functionality
- Add tests
- Update documentation

Reason: Per .agents/claude/preferences.md
```

---

## 🔒 Security Patterns (COPY-PASTE READY)

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
      echo "❌ Invalid input"
      exit 1
    fi
    echo "✅ Validation passed"
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

---

## 📝 Workflow Templates

### New Workflow Template
```yaml
name: Description

on:
  workflow_call:
    inputs:
      user-input:
        description: 'Description'
        required: false
        type: string
        default: 'value'
    secrets:
      SECRET_NAME:
        description: 'Description'
        required: true

jobs:
  job-name:
    runs-on: ubuntu-latest
    permissions:
      contents: read       # Adjust based on needs
      packages: read

    steps:
      - name: Checkout code
        # actions/checkout@v4.2.2
        uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683

      - name: Validate inputs
        run: |
          INPUT="${{ inputs.user-input }}"
          if ! echo "$INPUT" | grep -qE '^[SAFE_PATTERN]*$'; then
            echo "❌ Invalid input"
            exit 1
          fi

      # ... rest of workflow
```

---

## 🎯 Common Tasks

### Task: Add Permissions to Workflow

**Location**: `.github/workflows/*.yml`

**Steps**:
1. Identify workflow type (CI, release, security)
2. Find the job definition
3. Add permissions before `strategy:` or `steps:`
4. Use appropriate template from above

**Example**:
```yaml
# Before
jobs:
  build:
    runs-on: ubuntu-latest
    steps: [...]

# After
jobs:
  build:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: read
    steps: [...]
```

**Workflows Needing Permissions** (8 remaining):
- cmake_c_ci.yml (6 more jobs)
- cmake_cpp_ci.yml
- cmake_c_release.yml
- cmake_cpp_release.yml
- maven_bump_version.yml
- maven_release_branch.yml
- maven_release_tag.yml
- update_submodule.yml

---

### Task: Add Input Validation

**Location**: After input is received, before use

**Steps**:
1. Identify user-controlled inputs
2. Add validation step early in workflow
3. Use regex allowlist pattern
4. Quote variables properly in shell

**Example**:
```yaml
# Add after checkout, before using input
- name: Validate inputs
  run: |
    MAVEN_ARGS="${{ inputs.maven-args }}"

    # Only allow safe characters
    if ! echo "$MAVEN_ARGS" | grep -qE '^[-A-Za-z0-9=.,_:/ ]*$'; then
      echo "❌ Invalid maven-args: contains disallowed characters"
      echo "Allowed: alphanumeric, hyphens, equals, dots, commas, underscores, colons, slashes, spaces"
      exit 1
    fi

    echo "✅ Input validation passed"

# Later when using:
- name: Build
  run: |
    MAVEN_ARGS="${{ inputs.maven-args }}"
    mvn clean verify "$MAVEN_ARGS"
```

**Workflows Needing Validation** (5 remaining):
- maven_build.yml (maven-args)
- maven_release.yml (deploy-profile)
- cmake_c_ci.yml (cmake-options)
- cmake_cpp_ci.yml (cmake-options)
- bazel_multi_ci.yml (bazel-configs)

---

### Task: Create Test Fixture

**Location**: `test/fixtures/<buildsystem>/`

**Structure**:
```
test/fixtures/
├── maven/
│   ├── pom.xml
│   └── src/test/java/SimpleTest.java
├── cmake_c/
│   ├── CMakeLists.txt
│   └── src/main.c
├── cmake_cpp/
│   ├── CMakeLists.txt
│   └── src/main.cpp
└── bazel/
    ├── WORKSPACE
    ├── BUILD.bazel
    └── src/main.cc
```

**Requirements**:
- Minimal viable project (compiles and runs)
- Includes at least one test
- Uses standard conventions
- Self-contained (no external dependencies if possible)

---

## 🚫 Common Mistakes (DO NOT DO)

### Mistake 1: Using Mutable Action Tags
```yaml
# ❌ WRONG
uses: actions/checkout@v4
uses: aquasecurity/trivy-action@master

# ✅ CORRECT
# actions/checkout@v4.2.2
uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683
```

### Mistake 2: Missing Permissions
```yaml
# ❌ WRONG - No permissions specified
jobs:
  build:
    runs-on: ubuntu-latest
    steps: [...]

# ✅ CORRECT - Explicit permissions
jobs:
  build:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: read
    steps: [...]
```

### Mistake 3: Unvalidated User Input
```yaml
# ❌ WRONG - Direct use in shell
run: mvn verify ${{ inputs.maven-args }}

# ✅ CORRECT - Validated first
- name: Validate inputs
  run: |
    if ! echo "${{ inputs.maven-args }}" | grep -qE '^[SAFE]*$'; then
      exit 1
    fi
- run: mvn verify "${{ inputs.maven-args }}"
```

### Mistake 4: Secrets in CLI
```yaml
# ❌ WRONG
run: mvn deploy -Dgpg.passphrase="${{ secrets.GPG_PASSPHRASE }}"

# ✅ CORRECT - Use config file
- run: |
    cat > ~/.m2/settings.xml <<EOF
    <settings>
      <profiles><profile>
        <properties>
          <gpg.passphrase>${{ secrets.GPG_PASSPHRASE }}</gpg.passphrase>
        </properties>
      </profile></profiles>
    </settings>
    EOF
```

### Mistake 5: Splitting Repository
```
# ❌ WRONG - Creates maintenance nightmare
artagon-workflow-maven/
artagon-workflow-cmake/
artagon-workflow-bazel/

# ✅ CORRECT - Single repository
artagon-workflows/
  .github/workflows/
    maven_*.yml
    cmake_*.yml
    bazel_*.yml
```

---

## 📊 Status Checking

### Before Starting Work

**Check what's completed**:
```bash
cd /path/to/artagon-workflows
cat .model-context/context.md | grep "✅"
```

**Check what remains**:
```bash
cat .model-context/SECURITY_IMPLEMENTATION_PLAN.md | grep "⏳"
```

**Verify current state**:
```bash
# Count pinned actions (should be 17)
grep -l "11bd71901bbe5b1630ceea73d27597364c9af683" .github/workflows/*.yml | wc -l

# Check for unpinned actions (should be 0)
grep -rn "uses:.*@v[0-9]$" .github/workflows/

# List workflows missing permissions
for f in .github/workflows/*.yml; do
  grep -q "permissions:" "$f" || echo "$f"
done
```

---

## 🔄 Workflow for AI Models

### Standard Operating Procedure

```
1. READ
   ├─ .model-context/context.md (overview and status)
   ├─ .model-context/SECURITY_IMPLEMENTATION_PLAN.md (roadmap)
   └─ Relevant specialized docs as needed

2. VERIFY
   ├─ Task not already completed
   ├─ Understand requirements
   └─ Check for existing patterns

3. IMPLEMENT
   ├─ Use templates from this file
   ├─ Follow security patterns
   └─ Match existing code style

4. TEST
   ├─ Run actionlint (if possible)
   ├─ Verify no unpinned actions
   └─ Check permissions present

5. DOCUMENT
   ├─ Update context.md or SECURITY_IMPLEMENTATION_PLAN.md
   ├─ Note any decisions
   └─ Update relevant docs

6. COMMIT
   ├─ Semantic commit message
   ├─ Reference issue numbers
   └─ NO Claude attribution
```

---

## 💡 Tips for Success

### Do's ✅
- Start with .model-context/context.md
- Use templates from this file
- Check context.md for current status before starting
- Apply security patterns consistently
- Keep documentation updated
- Test changes when possible
- Ask for clarification when uncertain

### Don'ts ❌
- Don't redo completed work
- Don't use unpinned actions
- Don't skip input validation
- Don't omit permissions blocks
- Don't split the repository
- Don't add Claude attribution
- Don't ignore security guidelines

---

## 📚 Quick Reference

### Key Documents
| Document | Purpose | When to Read |
|----------|---------|--------------|
| context.md | Main entry point + status | Always first |
| SECURITY_IMPLEMENTATION_PLAN.md | Roadmap and remaining work | Before starting any task |
| SECURITY_AUDIT.md | Vulnerability details | For security work |
| ACTION_VERSIONS.md | Action SHA reference | When pinning actions |
| TESTING_STRATEGY.md | Testing framework | When writing tests |
| skills/github-workflows.md | Security best practices | For all workflow changes |

### File Locations
```
.model-context/
├── context.md                       # Main entry point + status
├── instructions.md                  # This file
├── ACTION_VERSIONS.md               # Action SHA reference
├── REPOSITORY_STRATEGY.md           # Mono-repo decision
├── SECURITY_AUDIT.md                # Vulnerability audit
├── SECURITY_IMPLEMENTATION_PLAN.md  # Implementation roadmap
├── TESTING_STRATEGY.md              # Testing framework
└── skills/
    ├── github-workflows.md          # Security best practices
    └── development-workflow.md      # Development workflow guide
```

### Common Commands
```bash
# Check status
cat .model-context/context.md

# Find remaining work
grep "⏳" .model-context/SECURITY_IMPLEMENTATION_PLAN.md

# Verify action pinning
grep -rn "uses:.*@v[0-9]$" .github/workflows/

# List workflows without permissions
for f in .github/workflows/*.yml; do
  grep -q "permissions:" "$f" || echo "$f"
done
```

---

## 🎓 Learning & Improvement

### After Completing Tasks

**Update Status**:
```
1. Edit .model-context/context.md or SECURITY_IMPLEMENTATION_PLAN.md
2. Change status from ⏳ to ✅
3. Update completion percentages
4. Note any issues encountered
```

**Document Decisions**:
```
1. If you made a significant decision, document it
2. Add to .model-context/context.md under "Key Decisions"
3. Include rationale and alternatives considered
```

**Improve Documentation**:
```
1. If instructions were unclear, improve them
2. Add examples for future AI models
3. Update templates based on learnings
```

---

## ✅ Success Criteria

You're doing it right when:
- [ ] You read context.md before starting
- [ ] You check context.md for current status
- [ ] You use security templates consistently
- [ ] All actions are pinned to SHAs
- [ ] All jobs have explicit permissions
- [ ] All user inputs are validated
- [ ] You don't redo completed work
- [ ] You update documentation as you go
- [ ] Commits follow semantic format
- [ ] No Claude attribution in commits

---

## 🆘 When Things Go Wrong

### If You're Unsure
```
1. Check context.md for guidance
2. Review similar existing patterns
3. Ask user for clarification
4. Document the uncertainty
5. Propose options rather than guessing
```

### If You Make a Mistake
```
1. Acknowledge it clearly
2. Explain what went wrong
3. Propose how to fix it
4. Learn from it for next time
5. Update documentation to prevent recurrence
```

### If Requirements Conflict
```
1. Prioritize security over convenience
2. Follow least-privilege principle
3. Prefer mono-repo over splitting
4. Document the trade-off
5. Ask user for final decision
```

---

## 📞 Support

### For More Information
- **Security**: .model-context/skills/github-workflows.md
- **Status**: .model-context/context.md
- **Roadmap**: .model-context/SECURITY_IMPLEMENTATION_PLAN.md
- **Decisions**: .model-context/REPOSITORY_STRATEGY.md
- **Testing**: .model-context/TESTING_STRATEGY.md
- **Actions**: .model-context/ACTION_VERSIONS.md

### Contact
- **Issues**: GitHub Issues for bug reports
- **Questions**: Ask the user for clarification
- **Feedback**: Document in .model-context/ files

---

**Last Updated**: 2025-10-25
**Version**: 1.0
**Status**: Active

---

**For AI Models**: Follow these instructions carefully. When in doubt, prioritize security and ask for clarification. You're part of a security-first development process. Your adherence to these guidelines protects not just this project, but all downstream consumers.

**Remember**: Every workflow you create or modify will be used by real projects. Security, reliability, and maintainability are paramount. Take your time, use the templates, and do it right the first time.

Good luck! 🚀
