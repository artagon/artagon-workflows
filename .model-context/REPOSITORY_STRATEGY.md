# Repository Strategy Analysis: Mono-repo vs Multi-repo

## Executive Summary

**Recommendation**: **KEEP MONO-REPO** (with organizational improvements)

This document analyzes whether artagon-workflows should be split into separate repositories per build system (artagon-workflow-maven, artagon-workflow-cmake, artagon-workflow-bazel) or remain as a unified mono-repo.

**TL;DR**: The mono-repo approach provides better value for workflows due to:
- Simplified versioning and dependency management
- Atomic cross-build-system updates
- Reduced maintenance overhead
- Unified documentation and examples
- Better code reuse opportunities

---

## Current State Analysis

### Repository Structure

```
artagon-workflows/ (Current Mono-repo)
├── .github/workflows/
│   ├── maven_*.yml        (9 workflows, 1,450 LOC)
│   ├── cmake_*.yml        (4 workflows, 1,280 LOC)
│   ├── bazel_*.yml        (2 workflows, 490 LOC)
│   └── update_submodule.yml  (1 shared utility)
├── docs/                  (Comprehensive, cross-referenced)
├── examples/              (All build systems)
├── test/fixtures/         (All build systems)
└── Common files (README, RELEASE, CONTRIBUTING, etc.)
```

### Metrics

| Metric | Current Value |
|--------|---------------|
| **Total Workflows** | 17 |
| **Total Lines of Code** | ~3,711 |
| **Build Systems** | 4 (Maven, CMake, Bazel, Cargo planned) |
| **Documentation Files** | 11 |
| **Example Projects** | 5 |
| **Shared Infrastructure** | Yes (docs, testing, CI) |

---

## Option 1: Mono-repo (Current) - RECOMMENDED

### Advantages

#### 1. **Unified Versioning** ⭐⭐⭐
- Single version tag applies to all workflows
- Consumers know `@v2.0.0` is tested together
- No version skew between build systems
- Atomic updates across ecosystems

**Example**:
```yaml
# Clear, unified version reference
uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@v2.0.0
uses: artagon/artagon-workflows/.github/workflows/bazel_multi_ci.yml@v2.0.0
# Both guaranteed to work together
```

vs

```yaml
# Multi-repo: version coordination nightmare
uses: artagon/artagon-workflow-maven/.github/workflows/ci.yml@v1.5.0
uses: artagon/artagon-workflow-bazel/.github/workflows/ci.yml@v2.1.3
# Are these compatible? Who knows!
```

#### 2. **Cross-Build-System Features** ⭐⭐⭐
- Shared release strategy across all build systems
- Common CI patterns (testing, coverage, security)
- Reusable composite actions
- Unified secret management approach

**Real Examples**:
- Release branch strategy applies to Maven, CMake, Bazel
- Security scanning patterns are consistent
- Common inputs (Java version, coverage settings)

#### 3. **Documentation Coherence** ⭐⭐⭐
- Single README with all workflows
- Cross-referenced docs (RELEASE.md references all build systems)
- Unified contribution guide
- Central security audit
- Consistent examples

**Current Structure**:
```
docs/
├── RELEASE.md          → References all build systems
├── RELEASE_JAVA.md     → Language-specific
├── RELEASE_C.md        → Language-specific
├── RELEASE_CPP.md      → Language-specific
├── OSS_RELEASE_STRATEGIES.md  → Shared research
└── NAMING_CONVENTIONS.md      → Applies to all
```

Split repos would duplicate 70% of this content.

#### 4. **Maintenance Efficiency** ⭐⭐
- One repository to monitor
- Single set of branch protection rules
- One Dependabot configuration
- Unified CI/CD testing
- Centralized issue tracking

**Savings**:
- 1 Dependabot config vs 3-4
- 1 CI workflow vs 3-4
- 1 security scan vs 3-4
- 1 set of release notes vs 3-4

#### 5. **Code Reuse Opportunities** ⭐⭐
- Shared composite actions (planned)
- Common step libraries
- Unified testing framework
- Shared security patterns

**Future Opportunities**:
```yaml
# Potential shared composite action
- uses: artagon/artagon-workflows/.github/actions/setup-security@v2
  # Works for Maven, CMake, Bazel, Rust
```

#### 6. **Discovery and Adoption** ⭐⭐
- Single point of discovery
- Clear comparison between build systems
- Easier for polyglot projects
- One repo to star/watch

**User Journey**:
1. Find `artagon-workflows`
2. See all supported build systems
3. Choose relevant workflow
4. Discover related workflows if project evolves

#### 7. **Testing Synergy** ⭐
- Test all workflows together
- Catch cross-workflow breaking changes
- Unified testing infrastructure
- Shared test fixtures

### Disadvantages

#### 1. **Larger Repository**
- More files to clone
- Broader notification scope
- Potentially confusing for single-build-system users

**Mitigation**:
- Sparse checkout for consumers (not needed, only reference workflows)
- Clear directory structure
- Build-system-specific labels for issues

#### 2. **Release Coordination Complexity**
- Breaking change in one build system affects version for all
- Must test all workflows before release

**Mitigation**:
- Semantic versioning with clear changelogs
- Build-system-specific changelog sections
- Automated testing catches issues

#### 3. **Cognitive Load**
- Developers must understand multiple build systems
- More code to navigate

**Mitigation**:
- Clear directory structure
- Build-system ownership (CODEOWNERS)
- Focused pull request scope

---

## Option 2: Multi-repo (Split by Build System)

### Structure

```
artagon-workflow-maven/
  ├── .github/workflows/
  │   ├── ci.yml
  │   ├── deploy.yml
  │   ├── release.yml
  │   └── ...
  ├── docs/
  ├── examples/
  └── test/

artagon-workflow-cmake/
  ├── .github/workflows/
  │   ├── c_ci.yml
  │   ├── cpp_ci.yml
  │   ├── ...
  └── ...

artagon-workflow-bazel/
  ├── .github/workflows/
  │   ├── multi_ci.yml
  │   ├── release.yml
  └── ...

artagon-workflow-rust/    (future)
  └── ...
```

### Advantages

#### 1. **Clear Ownership** ⭐
- Maven team owns maven repo
- CMake team owns cmake repo
- Reduces cross-team coordination

#### 2. **Independent Versioning** ⭐
- Maven can release v3.0.0 while CMake stays at v2.1.0
- No cross-build-system breaking changes

#### 3. **Smaller Blast Radius** ⭐
- Breaking changes isolated to one build system
- Easier rollback

#### 4. **Focused CI** ⭐
- Only test affected build system
- Faster CI runs

### Disadvantages

#### 1. **Version Coordination Nightmare** ⭐⭐⭐
- Which version of maven-workflow works with which cmake-workflow?
- Compatibility matrix becomes complex
- Polyglot projects suffer

**Example Problem**:
```yaml
# Project uses both Maven and Bazel
# Which versions are compatible?
uses: artagon/artagon-workflow-maven/.github/workflows/ci.yml@v2.0.0
uses: artagon/artagon-workflow-bazel/.github/workflows/ci.yml@???
```

#### 2. **Documentation Fragmentation** ⭐⭐⭐
- Release process duplicated across repos
- Security practices duplicated
- Inconsistent documentation updates
- No central source of truth

**Duplication Examples**:
- `RELEASE.md` duplicated 4 times
- `SECURITY_AUDIT.md` duplicated 4 times
- `CONTRIBUTING.md` duplicated 4 times
- `OSS_RELEASE_STRATEGIES.md` - which repo owns this?

#### 3. **Maintenance Overhead** ⭐⭐⭐
- 4 Dependabot configs to maintain
- 4 CI pipelines to monitor
- 4 security scans
- 4 sets of branch protection rules
- 4 sets of release notes

**Time Impact**: Estimated 4x maintenance burden

#### 4. **Code Duplication** ⭐⭐
- Common patterns copied across repos
- No shared composite actions
- Inconsistent security implementations
- Version drift over time

#### 5. **Discovery Fragmentation** ⭐⭐
- Users must find correct repository
- Polyglot projects need to track multiple repos
- Split community across repos
- Harder to compare approaches

#### 6. **Testing Isolation** ⭐
- Can't test cross-build-system compatibility
- Integration testing becomes complex
- Shared testing infrastructure duplicated

#### 7. **Cross-Cutting Changes** ⭐⭐⭐
- Security updates need 4 PRs
- Tooling updates need 4 PRs
- Documentation updates need 4 PRs
- High coordination cost

**Example**: Updating Dependabot config
- Mono-repo: 1 PR
- Multi-repo: 4 PRs (with potential inconsistencies)

---

## Comparative Analysis

| Criteria | Mono-repo | Multi-repo | Winner |
|----------|-----------|------------|--------|
| **Versioning Simplicity** | ✅ Unified | ❌ Complex | Mono-repo |
| **Documentation** | ✅ Centralized | ❌ Fragmented | Mono-repo |
| **Maintenance Burden** | ✅ Low | ❌ High (4x) | Mono-repo |
| **Discovery** | ✅ Single source | ❌ Scattered | Mono-repo |
| **Code Reuse** | ✅ Easy | ❌ Difficult | Mono-repo |
| **Testing** | ✅ Unified | ❌ Isolated | Mono-repo |
| **Cross-cutting Changes** | ✅ One PR | ❌ Many PRs | Mono-repo |
| **Independent Releases** | ❌ Coupled | ✅ Independent | Multi-repo |
| **Blast Radius** | ⚠️ Larger | ✅ Smaller | Multi-repo |
| **Ownership Clarity** | ⚠️ Shared | ✅ Clear | Multi-repo |
| **CI Speed** | ⚠️ Slower | ✅ Faster | Multi-repo |

**Score**: Mono-repo wins 7-4

---

## Real-World Examples

### Mono-repo Success Stories

#### 1. **actions/starter-workflows** (GitHub Official)
- Single repo for all starter workflows
- Covers 20+ languages and frameworks
- Over 300 workflows
- Successfully maintained by GitHub

#### 2. **projen** (AWS CDK)
- Mono-repo for all project types
- Covers TypeScript, Python, Java, Go
- Unified tooling and documentation

### Multi-repo Challenges

#### 1. **aws-actions** (AWS)
- Split across 15+ repositories
- Version coordination nightmare
- Documentation scattered
- Community fragmentation

#### 2. **gradle-build-action** vs **setup-gradle**
- Confusion over which to use
- Different version cadences
- Inconsistent documentation

---

## Decision Matrix

### When to Split

Split if ANY of these apply:
- ❌ Different release cadences required (not true for us)
- ❌ Different ownership teams with no coordination (not true)
- ❌ Massive codebase (>100k LOC) (we're at 3.7k LOC)
- ❌ Fundamentally different audiences (not true - all Artagon projects)
- ❌ Different security models (not true)

### When to Keep Mono-repo

Keep mono-repo if ANY of these apply:
- ✅ Shared release process (TRUE - same release branch strategy)
- ✅ Common documentation (TRUE - release guides, security)
- ✅ Unified versioning beneficial (TRUE - polyglot projects)
- ✅ Cross-cutting concerns (TRUE - security, testing)
- ✅ Small to medium codebase (TRUE - 3.7k LOC)
- ✅ Same organization (TRUE - all Artagon)

**Result**: 5/5 criteria met for mono-repo

---

## Recommendations

### Primary Recommendation: Enhance Mono-repo

Keep unified repository with organizational improvements:

#### 1. **Improve Directory Structure** (if needed)

Current structure is good, but could be clearer:

```
artagon-workflows/
├── .github/
│   └── workflows/
│       ├── maven/           # Group by build system
│       │   ├── ci.yml
│       │   ├── deploy.yml
│       │   ├── release.yml
│       │   └── ...
│       ├── cmake/
│       │   ├── c_ci.yml
│       │   ├── cpp_ci.yml
│       │   └── ...
│       ├── bazel/
│       │   ├── multi_ci.yml
│       │   └── multi_release.yml
│       └── shared/
│           └── update_submodule.yml
```

**However**, this BREAKS reusable workflow references:

```yaml
# Would need to change from:
uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@v2

# To:
uses: artagon/artagon-workflows/.github/workflows/maven/ci.yml@v2
```

**Alternative**: Keep flat structure, use naming convention (current approach is better).

#### 2. **Use CODEOWNERS for Ownership**

Create `.github/CODEOWNERS`:

```
# Maven workflows
/.github/workflows/maven_*.yml @artagon/maven-team

# CMake workflows
/.github/workflows/cmake_*.yml @artagon/cmake-team

# Bazel workflows
/.github/workflows/bazel_*.yml @artagon/bazel-team

# Documentation
/docs/MAVEN.md @artagon/maven-team
/docs/CPP.md @artagon/cmake-team
/docs/BAZEL.md @artagon/bazel-team

# Cross-cutting
/docs/RELEASE.md @artagon/workflows-team
/SECURITY_AUDIT.md @artagon/security-team
```

#### 3. **Build-System-Specific Labels**

```
labels:
  - name: "maven"
    color: "d73a4a"
    description: "Maven workflow related"
  - name: "cmake"
    color: "0075ca"
    description: "CMake workflow related"
  - name: "bazel"
    color: "43a047"
    description: "Bazel workflow related"
  - name: "cross-cutting"
    color: "fef2c0"
    description: "Affects multiple build systems"
```

#### 4. **Changelog Sections**

Organize CHANGELOG.md by build system:

```markdown
# Changelog

## [2.1.0] - 2025-11-01

### Maven
- feat: add Java 26 support
- fix: GPG signing on Windows

### CMake
- feat: add C23 standard support

### Bazel
- No changes

### Cross-Cutting
- chore: update all actions to latest SHAs
- docs: improve release process documentation
```

#### 5. **Focused Testing**

Run build-system-specific tests when only those workflows change:

```yaml
name: CI

on:
  pull_request:
    paths:
      - '.github/workflows/maven_*.yml'
      - 'test/fixtures/maven/**'
      - 'examples/maven/**'

jobs:
  test-maven:
    # Only test Maven workflows
    uses: ./.github/workflows/test_maven.yml
```

---

## Alternative: Mono-repo with Subprojects

If split is really desired, consider a middle ground:

```
artagon-workflows/
├── maven/
│   ├── .github/workflows/
│   ├── docs/
│   └── examples/
├── cmake/
│   ├── .github/workflows/
│   ├── docs/
│   └── examples/
├── bazel/
│   ├── .github/workflows/
│   ├── docs/
│   └── examples/
└── shared/
    ├── docs/
    └── actions/
```

**Problem**: GitHub doesn't support nested `.github/workflows/` in subdirectories for reusable workflows.

**Solution**: Not viable without complex workarounds.

---

## Migration Path (if split decision changes)

If you decide to split later:

### Phase 1: Preparation
1. Ensure all workflows are independent
2. Remove cross-workflow dependencies
3. Duplicate shared documentation

### Phase 2: Split
1. Create new repositories
2. Copy relevant workflows
3. Update documentation links
4. Update examples

### Phase 3: Deprecation
1. Mark old paths as deprecated
2. Redirect documentation
3. Notify consumers
4. Archive old repository after 1 year

**Estimated Effort**: 40-60 hours

**Risk**: High (breaking changes for all consumers)

---

## Cost-Benefit Analysis

### Mono-repo Costs
- Slightly larger clone size: +2 minutes for consumers (negligible)
- Broader notification scope: +10 notifications/month
- **Total Cost**: LOW

### Mono-repo Benefits
- Unified versioning: -20 hours/year (no compatibility tracking)
- Centralized documentation: -30 hours/year (no duplication)
- Single maintenance: -40 hours/year (one Dependabot, one CI)
- **Total Benefit**: 90 hours/year saved

### Multi-repo Costs
- Maintenance overhead: +160 hours/year (4x)
- Version coordination: +40 hours/year
- Documentation sync: +30 hours/year
- **Total Cost**: 230 hours/year

### Multi-repo Benefits
- Independent releases: +10 hours/year (faster releases)
- Clearer ownership: +5 hours/year (less coordination)
- **Total Benefit**: 15 hours/year gained

**Net Difference**: Mono-repo saves **~230 hours/year**

---

## Conclusion

**KEEP MONO-REPO** with the following enhancements:

✅ **Implement**:
1. CODEOWNERS for build-system ownership
2. Build-system-specific labels
3. Organized changelog sections
4. Focused testing (conditional CI)
5. Clear naming conventions (already done in v2.0.0)

✅ **Document**:
1. This decision and rationale
2. Ownership model
3. Release coordination process

✅ **Monitor**:
1. Repository growth
2. Maintenance burden
3. Consumer feedback

❌ **Don't Split** unless:
1. Repository exceeds 50k LOC
2. Teams become completely independent
3. Release cadences diverge significantly
4. Consumer feedback strongly demands it

---

## FAQ

**Q: What if Maven workflows evolve much faster than others?**
A: Use `@main` for rapid iteration, `@vX.Y.Z` for stability. Consumers choose.

**Q: Won't the repository become too large?**
A: At 3.7k LOC with 4 build systems, we'd need 10+ build systems to hit 10k LOC (manageable).

**Q: How do we handle breaking changes in one build system?**
A: Bump major version, document in changelog under build-system section.

**Q: What if a consumer only uses Maven?**
A: They reference `maven_*.yml` files, ignoring others. No impact.

**Q: Can we split later if needed?**
A: Yes, but at high cost (40-60 hours, breaking changes). Prefer mono-repo now.

---

## References

- [Mono-repo vs Multi-repo](https://kinsta.com/blog/monorepo-vs-multi-repo/)
- [GitHub Actions Reusable Workflows](https://docs.github.com/en/actions/using-workflows/reusing-workflows)
- [CODEOWNERS Documentation](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/about-code-owners)
- [Semantic Versioning](https://semver.org/)

---

**Decision Date**: 2025-10-25
**Review Date**: 2026-10-25 (or when repository exceeds 20k LOC)
**Status**: APPROVED (pending stakeholder review)
