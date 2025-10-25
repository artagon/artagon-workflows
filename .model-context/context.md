# AI Model Context: Artagon Workflows

**Project**: artagon-workflows
**Type**: Reusable GitHub Actions Workflows Library
**Status**: Production-ready, 75% security hardening complete
**Last Updated**: 2025-10-25

---

## 🎯 Project Overview

### Purpose
Centralized repository of production-ready, reusable GitHub Actions workflows for Artagon projects across multiple languages and build systems.

### Supported Build Systems
- **Maven** - Java/JVM projects (9 workflows)
- **CMake** - C and C++ projects (4 workflows)
- **Bazel** - Multi-language projects (2 workflows)
- **Rust/Cargo** - Documented, workflows planned

### Repository Structure
```
artagon-workflows/
├── .github/
│   └── workflows/            # 24 reusable workflow files
├── .model-context/           # AI model instructions (THIS FOLDER)
├── docs/                     # Build & release documentation
├── examples/                 # Usage examples for each build system
├── WORKFLOWS_USAGE.md        # Consumer guide for reusable workflows
└── MIGRATION_v2.md           # Migration guidance for v2 workflow naming
```

---

## 📚 Documentation Index

This folder contains comprehensive AI model instructions:

### 🔒 Security & Roadmap (READ FIRST)
1. **instructions.md** ⭐ **START HERE**
   - Quick-start rules for AI assistants
   - Escalation paths and security requirements

2. **context.md**
   - Current repository status
   - Key decisions and remaining work
   - Quick links to supporting docs

3. **SECURITY_IMPLEMENTATION_PLAN.md**
   - 10-week prioritized roadmap
   - Task breakdown with time estimates
   - Validation checklists

4. **SECURITY_AUDIT.md**
   - 10 vulnerabilities identified (2 CRITICAL, 5 HIGH, 3 MEDIUM)
   - All CRITICAL and HIGH issues resolved
   - Detailed remediation steps

### 📖 Reference Documentation
5. **ACTION_VERSIONS.md**
   - Complete action pinning reference
   - Trust levels for all third-party actions
   - Commit SHA mappings
   - Maintenance schedule

6. **TESTING_STRATEGY.md**
   - 4-level testing pyramid
   - Implementation examples
   - 10-week testing roadmap

7. **REPOSITORY_STRATEGY.md**
   - **DECISION: KEEP MONO-REPO**
   - Detailed analysis (mono-repo vs multi-repo)
   - Saves ~230 hours/year

8. **skills/github-workflows.md**
   - Security templates for permissions and input validation
   - Action pinning and checksum patterns
   - Review checklists for workflow changes

9. **skills/development-workflow.md**
   - Day-to-day development workflow guidance
   - Commit hygiene and review expectations

---

## 🚨 CRITICAL CONTEXT FOR AI MODELS

### ✅ COMPLETED TASKS (Do NOT redo these)

1. **All GitHub Actions Pinned** ✅
   - ALL 24 workflows updated
   - All action references pinned to immutable commit SHAs
   - Trivy @master vulnerability FIXED

2. **Critical Security Fixes** ✅
   - GPG passphrase exposure FIXED
   - Buildifier checksum verification ADDED
   - SARIF permissions FIXED

3. **Documentation** ✅
   - 65KB of comprehensive documentation
   - All reference materials complete

4. **Testing Infrastructure** ✅
   - test_lint.yml created with security validation
   - .yamllint.yml configured

5. **Permissions Hardening** ✅
   - All 24 workflows now declare least-privilege permissions
   - Release and automation workflows only request the scopes they use

6. **Input Validation Reinforced** ✅
   - High-risk workflows validate user-controlled inputs before shell execution
   - Patterns from `maven_ci.yml` reused across build and release pipelines

### ⏳ REMAINING TASKS (25% - 4 hours)

1. **Test Fixtures** (planned)
   - Create minimal test projects
   - Directory structure defined

---

## 🎯 AI Model Instructions

### When Working on This Project

#### 1. **ALWAYS Read Context First**
```
1. Read this context.md file
2. Review instructions.md for the quick-start checklist
3. Check SECURITY_IMPLEMENTATION_PLAN.md for roadmap details
```

#### 2. **Repository Strategy: MONO-REPO**
- ✅ **DO**: Keep all workflows in single repository
- ✅ **DO**: Use build-system-specific labels and CODEOWNERS
- ❌ **DO NOT**: Split into separate repos (artagon-workflow-maven, etc.)
- **Reason**: Saves 230 hours/year, unified versioning, atomic updates
- **Reference**: REPOSITORY_STRATEGY.md

#### 3. **Security Requirements (MANDATORY)**

**Action Pinning**:
```yaml
# ✅ CORRECT - Pin to commit SHA with comment
# actions/checkout@v4.2.2
uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683

# ❌ WRONG - Never use mutable tags
uses: actions/checkout@v4
uses: actions/checkout@main
uses: aquasecurity/trivy-action@master
```

**Permissions**:
```yaml
# ✅ ALWAYS add explicit permissions
jobs:
  ci:
    runs-on: ubuntu-latest
    permissions:
      contents: read      # Minimal access
      packages: read
```

**Input Validation**:
```yaml
# ✅ ALWAYS validate user inputs
- name: Validate inputs
  run: |
    INPUT="${{ inputs.user-provided-value }}"
    if ! echo "$INPUT" | grep -qE '^[SAFE_PATTERN]*$'; then
      echo "❌ Invalid input"
      exit 1
    fi
```

**GPG Secrets**:
```yaml
# ✅ CORRECT - Use settings.xml
- name: Configure Maven GPG settings
  env:
    GPG_PASSPHRASE: ${{ secrets.GPG_PASSPHRASE }}
  run: |
    cat > ~/.m2/settings.xml <<EOF
    <settings>
      <profiles><profile><id>gpg</id>
        <properties>
          <gpg.passphrase>${GPG_PASSPHRASE}</gpg.passphrase>
        </properties>
      </profile></profiles>
    </settings>
    EOF

# ❌ WRONG - Never in command line
run: mvn deploy -Dgpg.passphrase="$GPG_PASSPHRASE"
```

#### 4. **Workflow Naming Convention (v2.0.0)**

**Current Standard**:
```
<buildsystem>_[lang]_<category>.yml

Examples:
- maven_ci.yml              (language-specific system)
- maven_release.yml
- cmake_c_ci.yml            (multi-language system + language)
- cmake_cpp_release.yml
- bazel_multi_ci.yml        (multi-language, no specific lang)
```

**Breaking Change Planned (v3.0.0)**:
- Rename `maven-*.yml` → `maven_*.yml` (hyphen to underscore)
- Not implemented yet - keep current names for now

#### 5. **Testing Requirements**

**Before Committing**:
- [ ] Run actionlint (automated in test_lint.yml)
- [ ] Run yamllint (automated in test_lint.yml)
- [ ] Verify no unpinned actions
- [ ] Verify no hardcoded secrets
- [ ] Check permissions blocks exist

**Automated Tests** (trigger on push):
```bash
# Push changes triggers:
# - .github/workflows/test_lint.yml
#   - actionlint
#   - yamllint
#   - security validation
```

#### 6. **Commit Message Format**

**Security Fixes**:
```
security: pin all GitHub Actions to commit SHAs

- Pin actions/checkout, setup-java, cache, upload-artifact
- Fix Trivy @master vulnerability
- Prevents supply chain attacks

Ref: SECURITY_AUDIT.md Issue #1 (CRITICAL)
```

**Feature Additions**:
```
feat(maven): add Java 26 support

- Update maven_ci.yml with Java 26 option
- Test with temurin-26
- Update documentation

Closes #123
```

**Documentation**:
```
docs: update security guidelines

- Add input validation examples
- Document action pinning policy
- Update TESTING_STRATEGY.md
```

---

## 🔑 Key Decisions & Rationale

### Decision 1: Keep Mono-Repo ✅
**Date**: 2025-10-25
**Rationale**:
- Unified versioning (no compatibility matrix)
- Single source of truth for docs
- Saves ~230 hours/year vs 3-4 separate repos
- Easier for polyglot projects
- Atomic cross-cutting updates

**Implementation**:
- Use CODEOWNERS for ownership
- Use labels for organization
- Organize CHANGELOG by build system

**Reference**: REPOSITORY_STRATEGY.md

### Decision 2: Pin All Actions to SHAs ✅
**Date**: 2025-10-25
**Rationale**:
- Prevents supply chain attacks
- Mutable tags can be moved by attackers
- Critical after Trivy @master vulnerability

**Status**: ✅ 100% complete (24/24 workflows)

**Reference**: SECURITY_AUDIT.md Issue #1, ACTION_VERSIONS.md

### Decision 3: Release Branch Strategy ✅
**Date**: Pre-existing
**Rationale**:
- Main branch always has SNAPSHOT versions
- Release branches for stable releases
- Branches kept for hotfixes

**Pattern**:
```
main: 1.0.9-SNAPSHOT
release-1.0.8: 1.0.8 (no SNAPSHOT)
tag v1.0.8: created on release branch
```

**Reference**: RELEASE.md, docs/RELEASE_*.md

### Decision 4: Explicit Permissions ✅
**Date**: 2025-10-25
**Rationale**:
- Default permissions too permissive
- Least-privilege security principle
- Required for SARIF upload

**Status**: 🔄 In progress (17/24 workflows complete, 7 remaining)

**Reference**: SECURITY_AUDIT.md Issue #3

---

## 📋 Common Tasks & Templates

### Task 1: Add Permissions to Workflow

**For CI Workflows** (read-only):
```yaml
jobs:
  ci:
    runs-on: ubuntu-latest
    permissions:
      contents: read       # Checkout code
      packages: read       # Download artifacts (if needed)
    steps: [...]
```

**For Release Workflows** (write access):
```yaml
jobs:
  release:
    runs-on: ubuntu-latest
    permissions:
      contents: write      # Create tags, releases, push commits
      packages: write      # Deploy/publish artifacts
    steps: [...]
```

**For Security Workflows**:
```yaml
jobs:
  security-scan:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      security-events: write  # Upload SARIF to Security tab
    steps: [...]
```

### Task 2: Add Input Validation

**Template** (copy from maven_ci.yml:88-100):
```yaml
- name: Validate inputs
  run: |
    INPUT="${{ inputs.YOUR_INPUT_NAME }}"

    # Validate input contains only safe characters
    # Adjust regex pattern based on expected input
    if ! echo "$INPUT" | grep -qE '^[-A-Za-z0-9=.,_:/ ]*$'; then
      echo "❌ Invalid input: contains disallowed characters"
      echo "Allowed: alphanumeric, hyphens, equals, dots, commas, underscores, colons, slashes, spaces"
      echo "Provided: $INPUT"
      exit 1
    fi

    echo "✅ Input validation passed"
```

**Common Patterns**:
- Maven args: `^[-A-Za-z0-9=.,_:/ ]*$`
- CMake options: `^[-A-Za-z0-9=.,_:/ ]*$`
- Bazel configs: `^[a-z,_-]+$`
- Version strings: `^[0-9]+\.[0-9]+\.[0-9]+$`

### Task 3: Pin New Action

1. **Find the latest release**:
```bash
gh release list --repo actions/checkout --limit 1
```

2. **Get commit SHA for tag**:
```bash
gh api repos/actions/checkout/git/ref/tags/v4.2.2 --jq '.object.sha'
```

3. **Update workflow**:
```yaml
# actions/checkout@v4.2.2
uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683
```

4. **Document in ACTION_VERSIONS.md**

### Task 4: Add New Workflow

**Checklist**:
- [ ] Use build-system-first naming (`<system>_[lang]_<category>.yml`)
- [ ] Pin all actions to commit SHAs
- [ ] Add explicit permissions block
- [ ] Validate all user inputs
- [ ] Add inline documentation
- [ ] Create example in `examples/`
- [ ] Document in README.md
- [ ] Add to test suite

---

## 🚫 Common Mistakes to Avoid

### ❌ DO NOT:
1. **Use mutable action tags**
   ```yaml
   # NEVER do this:
   uses: actions/checkout@v4
   uses: aquasecurity/trivy-action@master
   ```

2. **Omit permissions blocks**
   ```yaml
   # WRONG - no permissions specified
   jobs:
     build:
       runs-on: ubuntu-latest
       steps: [...]
   ```

3. **Put secrets in command-line arguments**
   ```yaml
   # WRONG - visible in ps, logs, core dumps
   run: mvn deploy -Dpassword="${{ secrets.PASSWORD }}"
   ```

4. **Skip input validation**
   ```yaml
   # WRONG - command injection risk
   run: mvn clean install ${{ inputs.maven-args }}
   ```

5. **Split repository into separate repos**
   ```
   # WRONG - creates maintenance nightmare
   artagon-workflow-maven/
   artagon-workflow-cmake/
   artagon-workflow-bazel/
   ```

6. **Use different naming conventions**
   ```yaml
   # WRONG - inconsistent
   maven-ci.yml        # hyphens
   maven_release.yml   # underscores
   ```

7. **Download binaries without checksum**
   ```bash
   # WRONG - no verification
   wget https://example.com/binary
   chmod +x binary
   ```

8. **Add claude.com attribution in commits**
   ```
   # WRONG - per .agents/claude/preferences.md
   feat: add feature

   🤖 Generated with Claude Code
   ```

---

## 📊 Current Status Summary

### Security Posture
| Category | Status | Details |
|----------|--------|---------|
| **Risk Level** | LOW ✅ | Down from MEDIUM-HIGH |
| **Actions Pinned** | 100% ✅ | 24/24 workflows, 400+ refs |
| **Critical Issues** | 0 ✅ | All resolved |
| **High Issues** | 0 ✅ | All resolved |
| **Permissions** | 100% ✅ | 24/24 workflows covered with least-privilege scopes |
| **Input Validation** | 100% ✅ | High-risk workflows validate user inputs before shell execution |
| **Test Coverage** | 50% 🔄 | Linting automated |

### Documentation Status
- ✅ Security audit complete
- ✅ Action versions documented
- ✅ Testing strategy defined
- ✅ Repository strategy decided
- ✅ Implementation plan created
- ✅ Progress tracking active

### Workflow Status
## 🔐 Security Snapshot

- ✅ Critical fixes completed (GPG passphrase handling, Buildifier checksum verification, SARIF permissions).
- ✅ Action pinning enforced across all 24 workflows.
- ✅ Dedicated security workflows available (`maven_security_scan.yml`, `codeql.yml`, `dependency_review.yml`).

**Pending Hardening**

- **Focus** – Build out workflow test fixtures to exercise reusable pipelines end-to-end.

---

## 🎯 AI Model Workflow

When asked to work on this project:

### Step 1: Assess Request
```
1. Check if task is in "COMPLETED" section → Don't redo
2. Check if task is in "REMAINING" section → Can proceed
3. Check if task is new → Evaluate against guidelines
```

### Step 2: Read Relevant Docs
```
- For security tasks → SECURITY_AUDIT.md, SECURITY_IMPLEMENTATION_PLAN.md
- For testing → TESTING_STRATEGY.md
- For action versions → ACTION_VERSIONS.md
- For current status → context.md (this file) and instructions.md
```

### Step 3: Apply Guidelines
```
- Use templates from this context.md
- Follow naming conventions
- Pin all actions
- Add permissions
- Validate inputs
- Test changes
```

### Step 4: Update Documentation
```
- Update SECURITY_IMPLEMENTATION_PLAN.md or this context.md with progress
- Note any new decisions in context.md
- Update supporting documentation as needed
```

### Step 5: Commit Properly
```
- Use semantic commit format
- Reference issue numbers
- Link to security audit if applicable
- NO Claude attribution (per .agents/claude/preferences.md)
```

---

## 🔗 Quick Reference Links

### Within This Folder (.model-context/)
- **instructions.md** - Quick-start rules and escalation guidance
- **context.md** - Current status, key decisions, outstanding work
- **SECURITY_IMPLEMENTATION_PLAN.md** - Roadmap and remaining work
- **SECURITY_AUDIT.md** - All vulnerabilities and fixes
- **ACTION_VERSIONS.md** - Action version reference
- **TESTING_STRATEGY.md** - Testing framework
- **REPOSITORY_STRATEGY.md** - Mono-repo decision
- **skills/github-workflows.md** - Security templates for permissions, validation, pinning
- **skills/development-workflow.md** - Day-to-day engineering workflow

### Repository Root
- **README.md** - User-facing documentation
- **RELEASE.md** - Release process guide
- **CONTRIBUTING.md** - Contribution guidelines
- **.github/dependabot.yml** - Dependency automation
- **.github/workflows/test_lint.yml** - Automated testing
- **WORKFLOWS_USAGE.md** - Reusable workflow usage guide
- **MIGRATION_v2.md** - Workflow naming migration notes

### Documentation Folders
- **docs/** - Language-specific guides (RELEASE_JAVA.md, RELEASE_C.md, etc.)
- **examples/** - Usage examples for each build system

---

## 💡 Tips for AI Models

### When in Doubt
1. **Review instructions.md and this context first** - They are the sources of truth
2. **Check security templates** - Use the templates in this file
3. **Verify against checklists** - Use validation checklists before committing
4. **Ask before major changes** - Repository structure, breaking changes, etc.

### Performance Tips
1. **Batch updates** - Use scripts for repetitive tasks (e.g., pinning actions)
2. **Read once** - Keep context in memory, don't re-read unnecessarily
3. **Use templates** - Copy-paste from proven patterns

### Quality Tips
1. **Test locally** - Use actionlint, yamllint before committing
2. **Follow conventions** - Naming, commit messages, documentation
3. **Update status** - Keep this context.md and SECURITY_IMPLEMENTATION_PLAN.md current
4. **Be consistent** - Match existing patterns in the codebase

---

## 🎓 Learning Resources

### GitHub Actions Security
- [GitHub Actions Security Best Practices](https://docs.github.com/en/actions/security-guides/security-hardening-for-github-actions)
- [OSSF Scorecard](https://github.com/ossf/scorecard)
- [Supply Chain Levels for Software Artifacts (SLSA)](https://slsa.dev/)

### Workflow Development
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Reusable Workflows](https://docs.github.com/en/actions/using-workflows/reusing-workflows)
- [Workflow Syntax](https://docs.github.com/en/actions/learn-github-actions/workflow-syntax-for-github-actions)

### Testing
- [actionlint](https://github.com/rhysd/actionlint)
- [yamllint](https://yamllint.readthedocs.io/)
- [act - Local GitHub Actions](https://github.com/nektos/act)

---

## 📞 Contact & Support

### Getting Help
- **Security Questions**: Review SECURITY_AUDIT.md
- **Implementation Help**: See SECURITY_IMPLEMENTATION_PLAN.md
- **Action Versions**: Check ACTION_VERSIONS.md
- **Testing Strategy**: Read TESTING_STRATEGY.md
- **Repository Decision**: See REPOSITORY_STRATEGY.md

### Issue Tracking
- Create issues in GitHub for bugs and features
- Use semantic labels: `security`, `enhancement`, `bug`, `documentation`
- Reference this context in AI-related issues

---

## 🔄 Maintenance Schedule

### Weekly
- Review Dependabot PRs
- Check for security advisories
- Update ACTION_VERSIONS.md if needed

### Monthly
- Run full security scan
- Update action SHAs if security updates available
- Review and update documentation

### Quarterly
- Comprehensive security audit
- Update this context.md
- Review and refine testing strategy
- Consider version bump (if breaking changes)

---

## ✅ Pre-Commit Checklist

Before committing workflow changes:

- [ ] All actions pinned to commit SHAs
- [ ] Comment with semantic version above SHA
- [ ] Permissions block present
- [ ] Input validation added (if user inputs)
- [ ] GPG secrets handled securely
- [ ] Binary downloads have checksum verification
- [ ] Workflow tested (at minimum: linting)
- [ ] Documentation updated
- [ ] context.md and/or SECURITY_IMPLEMENTATION_PLAN.md updated
- [ ] Semantic commit message
- [ ] NO Claude attribution

---

**Last Updated**: 2025-10-25
**Next Review**: 2025-11-25
**Version**: 1.0
**Status**: Active - Phase 1 Security Hardening 75% Complete

---

**🎯 Remember**: This project is production-ready. All critical security vulnerabilities have been eliminated. The remaining 25% is enhancement work for complete coverage.

**For AI Models**: Always start with instructions.md and this context.md, apply the security templates from this folder, and update status as you work. Happy coding! 🚀
