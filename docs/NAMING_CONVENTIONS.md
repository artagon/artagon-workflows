# Naming Conventions

Analysis of current naming strategy and proposed improvements for workflows and documentation.

---

## Table of Contents

- [Current State Analysis](#current-state-analysis)
- [Proposed Naming Strategy](#proposed-naming-strategy)
- [Rationale](#rationale)
- [Migration Plan](#migration-plan)
- [Examples](#examples)

---

## Current State Analysis

### Current Workflow Naming

**Observed patterns:**

```
bazel_multi_ci.yml                 (hyphen, lang implicit)
bazel_multi_release.yml            (hyphen, lang implicit)
cmake_c_ci.yml                     (hyphen, buildsystem implicit)
cmake_c_release.yml                (hyphen, buildsystem implicit)
cmake_cpp_ci.yml                   (hyphen, buildsystem implicit)
cmake_cpp_release.yml              (hyphen, buildsystem implicit)
maven_build.yml              (underscore)
maven_bump_version.yml       (underscore)
maven_deploy.yml             (underscore)
maven_release_branch.yml     (underscore)
maven_release_tag.yml        (underscore)
maven_release.yml            (underscore)
maven_security_scan.yml      (underscore)
maven-central-release.yml    (hyphen)
maven_ci.yml                 (hyphen)
maven-github-release.yml     (hyphen)
update-submodule.yml         (hyphen, utility)
```

### Issues Identified

#### 1. **Inconsistent Delimiters**
- Maven workflows mix `_` (underscores) and `-` (hyphens)
- No clear rule for when to use which

#### 2. **Missing Build System Context**
- `cmake_c_ci.yml` doesn't indicate it uses CMake
- `cmake_cpp_ci.yml` doesn't indicate it uses CMake
- Users can't tell `cmake_c_ci.yml` from `bazel-cmake_c_ci.yml` without inspection

#### 3. **Redundancy Issues**
- Maven is Java-only → `maven_java_ci.yml` is redundant
- Cargo is Rust-only → `cargo_rust_ci.yml` is redundant

#### 4. **Ambiguity for Multi-Language Build Systems**
- Bazel supports Java, C, C++, Kotlin, Go, etc.
- `bazel_multi_ci.yml` - which language?
- CMake supports C, C++, Fortran, etc.

#### 5. **Documentation Naming**
- Uses `RELEASE_JAVA.md`, `RELEASE_C.md` (language-first)
- Different pattern from workflows (build-system-first or language-first?)

---

## Proposed Naming Strategy

### Format Rules

**Primary format:** `<primary>_<secondary>_<category>.yml`

**Delimiter rules:**
- Use **underscores** (`_`) for semantic separation
- Use **hyphens** (`-`) only within compound words (e.g., `pre-commit`, `post-merge`)

### Decision Tree

```
Is build system language-specific?
├─ YES (Maven→Java, Cargo→Rust, Gradle→JVM)
│  └─ Format: <buildsystem>_<category>.yml
│     Examples: maven_ci.yml, cargo_ci.yml, gradle_ci.yml
│
└─ NO (CMake, Bazel, Make, Autotools)
   └─ Format: <buildsystem>_<lang>_<category>.yml
      Examples: cmake_c_ci.yml, bazel_java_ci.yml, make_c_ci.yml
```

### Category Naming

**Standard categories:**
- `ci` - Continuous Integration
- `build` - Build only (no deploy)
- `test` - Testing workflows
- `release` - Full release process
- `release_tag` - Tag-triggered releases
- `release_branch` - Branch-triggered releases
- `deploy` - Deployment
- `deploy_snapshot` - Snapshot deployment
- `security` - Security scanning
- `security_scan` - Detailed security scanning
- `version_bump` - Version management

### Special Cases

**Utility workflows (no language/build system):**
- Format: `<description>.yml`
- Examples: `update_submodule.yml`, `auto_merge.yml`

**Legacy/deprecated workflows:**
- Add `legacy_` prefix
- Example: `legacy_maven_build.yml`

---

## Rationale

### Why This Strategy?

#### 1. **Clarity and Predictability**
```
✅ cmake_c_ci.yml          - Clear: CMake, C language, CI
✅ cmake_cpp_ci.yml        - Clear: CMake, C++, CI
✅ bazel_java_ci.yml       - Clear: Bazel, Java, CI
✅ maven_ci.yml            - Clear: Maven (Java implied), CI
❌ cmake_c_ci.yml                - Unclear: What build system?
```

#### 2. **Consistency**
- All workflows follow the same pattern
- Same delimiter throughout (`_`)
- Predictable file naming

#### 3. **Scalability**
```
# As project grows, easy to add:
bazel_kotlin_ci.yml
bazel_go_ci.yml
cmake_fortran_ci.yml
gradle_kotlin_ci.yml
```

#### 4. **Alphabetical Grouping**
Files naturally group by build system in directory listings:
```
bazel_cpp_ci.yml
bazel_cpp_release.yml
bazel_java_ci.yml
bazel_java_release.yml
cmake_c_ci.yml
cmake_c_release.yml
cmake_cpp_ci.yml
cmake_cpp_release.yml
maven_ci.yml
maven_deploy.yml
maven_release.yml
```

#### 5. **Alignment with Documentation**
Documentation uses language-first (`RELEASE_JAVA.md`), but workflows are action-first (CI, release, deploy). Different purposes warrant different approaches:
- **Workflows:** Action/tool-oriented → build system first
- **Docs:** Concept-oriented → language first

---

## Proposed File Names

### Complete Mapping

#### Current → Proposed

**Maven (Java) - Language-Specific Build System:**
```
maven_ci.yml                → maven_ci.yml                 ✅ (already exists as underscore variant)
maven_build.yml             → maven_build.yml              ✅ (keep)
maven-central-release.yml   → maven_central_release.yml    ✅ (compound word uses hyphen)
maven_deploy.yml            → maven_deploy_snapshot.yml    📝 (more specific)
maven-github-release.yml    → maven_github_release.yml     ✅ (compound word uses hyphen)
maven_release.yml           → maven_release.yml            ✅ (keep)
maven_release_branch.yml    → maven_release_branch.yml     ✅ (keep)
maven_release_tag.yml       → maven_release_tag.yml        ✅ (keep)
maven_security_scan.yml     → maven_security_scan.yml      ✅ (keep)
maven_bump_version.yml      → maven_version_bump.yml       📝 (consistent with category naming)
```

**CMake (C) - Multi-Language Build System:**
```
cmake_c_ci.yml                    → cmake_c_ci.yml               📝 (add build system)
cmake_c_release.yml               → cmake_c_release.yml          📝 (add build system)
```

**CMake (C++) - Multi-Language Build System:**
```
cmake_cpp_ci.yml                  → cmake_cpp_ci.yml             📝 (add build system)
cmake_cpp_release.yml             → cmake_cpp_release.yml        📝 (add build system)
```

**Bazel - Multi-Language Build System:**
```
bazel_multi_ci.yml                → bazel_multi_ci.yml           📝 (clarify: supports multiple languages)
bazel_multi_release.yml           → bazel_multi_release.yml      📝 (or create per-language variants)

# OR, better: Create language-specific variants
bazel_java_ci.yml           (NEW)
bazel_cpp_ci.yml            (NEW)
bazel_c_ci.yml              (NEW)
```

**Cargo (Rust) - Language-Specific Build System:**
```
# Future workflows
cargo_ci.yml                (NEW)
cargo_release.yml           (NEW)
cargo_publish.yml           (NEW)
```

**Utilities:**
```
update-submodule.yml        → update_submodule.yml         📝 (consistent delimiter)
```

---

## Documentation Naming

### Current Pattern

```
docs/RELEASE_JAVA.md
docs/RELEASE_C.md
docs/RELEASE_CPP.md
docs/RELEASE_RUST.md
docs/OSS_RELEASE_STRATEGIES.md
```

### Analysis

**Current pattern is GOOD:**
- ✅ Language-first makes sense for documentation (user perspective)
- ✅ All caps with underscores is clear and distinct
- ✅ Consistent pattern across all release docs

### Proposed Pattern (Keep Current + Additions)

**Release documentation (Language-Focused):**

These docs focus on **language ecosystems and release strategies**, independent of build system:
```
docs/RELEASE_JAVA.md              ✅ (Java ecosystem: Maven, Gradle, SNAPSHOT conventions)
docs/RELEASE_C.md                 ✅ (C ecosystem: tag-based, ABI stability, conservative)
docs/RELEASE_CPP.md               ✅ (C++ ecosystem: LTS, ABI/API, PIMPL patterns)
docs/RELEASE_RUST.md              ✅ (Rust ecosystem: crates.io, MSRV, SemVer)
docs/RELEASE_KOTLIN.md            📝 (future)
docs/RELEASE_GO.md                📝 (future)
```

**Why language-focused?** Release strategies are driven by language ecosystem conventions:
- Java: SNAPSHOT versions, Maven Central, semantic versioning
- C: Conservative releases, ABI compatibility, system packages
- C++: PIMPL for ABI, LTS support, complex dependencies
- Rust: Strict SemVer, MSRV policy, crates.io permanence

**Workflow-specific documentation:**
```
docs/WORKFLOWS_MAVEN.md           📝 (Maven-specific workflows)
docs/WORKFLOWS_CMAKE.md           📝 (CMake workflows for C and C++)
docs/WORKFLOWS_BAZEL.md           📝 (Bazel workflows, multi-language)
docs/WORKFLOWS_CARGO.md           📝 (Cargo workflows for Rust)
```

**Note:** Workflow docs are build-system-centric since one build system serves multiple languages:
- `WORKFLOWS_CMAKE.md` covers both C and C++ CMake workflows
- `WORKFLOWS_BAZEL.md` covers Java, C, C++, Kotlin, Go, etc.

### Documentation Naming Philosophy

**Two-tier documentation strategy:**

| Documentation Type | Naming Pattern | Focus | Example |
|-------------------|----------------|-------|---------|
| **Release Strategy** | `RELEASE_<LANGUAGE>.md` | Language ecosystem, conventions, philosophy | `RELEASE_CPP.md` covers C++ LTS, ABI, PIMPL |
| **Workflow Guide** | `WORKFLOWS_<BUILDSYSTEM>.md` | Build system usage, CI/CD, practical setup | `WORKFLOWS_CMAKE.md` covers CMake for C and C++ |

**Why this separation?**

1. **Different audiences:**
   - Release docs: Architects deciding strategy
   - Workflow docs: Engineers implementing CI/CD

2. **Different scopes:**
   - Release docs: Ecosystem-wide patterns (language-specific)
   - Workflow docs: Tool-specific usage (build-system-specific)

3. **Practical example:**
   ```
   Developer asks: "How should I release my C++ library?"
   ├─ Read RELEASE_CPP.md → Learn about LTS, ABI stability, SemVer
   └─ Read WORKFLOWS_CMAKE.md → Learn how to implement with CMake workflows

   Developer asks: "How do I use CMake workflows?"
   └─ Read WORKFLOWS_CMAKE.md → Works for both C and C++ projects
   ```

**Strategy documentation:**
```
docs/STRATEGIES_OSS_RELEASE.md    📝 (rename from OSS_RELEASE_STRATEGIES.md)
docs/STRATEGIES_VERSIONING.md     📝 (future)
docs/STRATEGIES_SECURITY.md       📝 (future)
```

**Reference documentation:**
```
docs/REFERENCE_NAMING.md          📝 (this document, renamed)
docs/REFERENCE_BEST_PRACTICES.md  📝 (future)
```

---

## Examples Directory Naming

### Current Structure

```
examples/
├── maven/
│   ├── README.md
│   ├── ci.yml
│   ├── release.yml
│   └── ...
├── c/
│   ├── README.md
│   ├── ci.yml
│   └── release.yml
├── cpp/
│   ├── README.md
│   ├── ci.yml
│   └── release.yml
└── bazel/
    ├── README.md
    ├── ci.yml
    └── release.yml
```

### Proposed Structure

**Option 1: Build-System-First (Recommended)**
```
examples/
├── maven/              # Language-specific build system
│   ├── README.md
│   ├── ci.yml
│   ├── release.yml
│   └── ...
├── cmake_c/            # Multi-language build system + language
│   ├── README.md
│   ├── ci.yml
│   └── release.yml
├── cmake_cpp/          # Multi-language build system + language
│   ├── README.md
│   ├── ci.yml
│   └── release.yml
├── bazel_java/         # Multi-language build system + language
│   ├── README.md
│   ├── ci.yml
│   └── release.yml
├── bazel_cpp/          # Multi-language build system + language
│   ├── README.md
│   ├── ci.yml
│   └── release.yml
└── cargo/              # Language-specific build system
    ├── README.md
    ├── ci.yml
    └── release.yml
```

**Option 2: Language-First (Alternative)**
```
examples/
├── java/
│   ├── maven/
│   │   ├── ci.yml
│   │   └── release.yml
│   └── bazel/
│       ├── ci.yml
│       └── release.yml
├── c/
│   ├── cmake/
│   │   ├── ci.yml
│   │   └── release.yml
│   └── bazel/
│       ├── ci.yml
│       └── release.yml
├── cpp/
│   ├── cmake/
│   │   ├── ci.yml
│   │   └── release.yml
│   └── bazel/
│       ├── ci.yml
│       └── release.yml
└── rust/
    └── cargo/
        ├── ci.yml
        └── release.yml
```

**Recommendation:** Use Option 1 (Build-System-First) for consistency with workflow naming.

---

## Migration Plan

### Phase 1: Document New Convention (DONE)
- ✅ Create this document
- ✅ Get team approval

### Phase 2: Standardize Delimiters
**Priority:** High
**Effort:** Low

```bash
# Rename workflows to use consistent underscores
mv maven_ci.yml maven_ci.yml
mv maven-central-release.yml maven_central_release.yml
mv maven-github-release.yml maven_github_release.yml
mv update-submodule.yml update_submodule.yml
mv bazel_multi_ci.yml bazel_multi_ci.yml
mv bazel_multi_release.yml bazel_multi_release.yml
```

### Phase 3: Add Build System Context
**Priority:** High
**Effort:** Medium

```bash
# Rename C/C++ workflows to include CMake
mv cmake_c_ci.yml cmake_c_ci.yml
mv cmake_c_release.yml cmake_c_release.yml
mv cmake_cpp_ci.yml cmake_cpp_ci.yml
mv cmake_cpp_release.yml cmake_cpp_release.yml
```

### Phase 4: Update Examples
**Priority:** Medium
**Effort:** Medium

```bash
# Rename example directories
mv examples/c examples/cmake_c
mv examples/cpp examples/cmake_cpp

# Update all references in README files
# Update all workflow references
```

### Phase 5: Update Documentation References
**Priority:** Medium
**Effort:** Low

- Update all documentation to reference new workflow names
- Update examples in release strategy guides
- Update README files

### Phase 6: Create Redirects/Aliases (Optional)
**Priority:** Low
**Effort:** Medium

For backward compatibility, could create:
- GitHub repository redirects
- Deprecation notices in old workflow files
- Migration guide

---

## Implementation Checklist

### Immediate Actions

- [ ] Review and approve this naming convention document
- [ ] Update CONTRIBUTING.md with naming requirements
- [ ] Create issue for workflow renaming
- [ ] Plan communication to users about changes

### Workflow Renaming

- [ ] Rename Maven workflows (hyphens → underscores)
- [ ] Rename C/C++ workflows (add cmake prefix)
- [ ] Rename Bazel workflows (clarify multi-language)
- [ ] Rename utility workflows (consistent delimiters)
- [ ] Update all workflow cross-references

### Examples Restructuring

- [ ] Rename example directories
- [ ] Update README files in examples
- [ ] Update workflow file references
- [ ] Test all example workflows

### Documentation Updates

- [ ] Update RELEASE.md
- [ ] Update RELEASE_JAVA.md
- [ ] Update RELEASE_C.md
- [ ] Update RELEASE_CPP.md
- [ ] Update RELEASE_RUST.md
- [ ] Update README.md
- [ ] Update CONTRIBUTING.md

### Communication

- [ ] Announce changes to team
- [ ] Update changelog
- [ ] Create migration guide for users
- [ ] Update any external references

---

## Naming Convention Rules Summary

### Workflows

✅ **DO:**
- Use underscores (`_`) for semantic separation
- Include build system when multi-language (CMake, Bazel)
- Use descriptive categories (`ci`, `release`, `deploy`)
- Follow pattern: `<buildsystem>_[lang]_<category>.yml`
- Group related workflows alphabetically

❌ **DON'T:**
- Mix hyphens and underscores for separation
- Omit build system for multi-language tools
- Use vague categories (`test1.yml`, `build2.yml`)
- Use language for single-language build systems (`maven_java_ci.yml`)

### Documentation

✅ **DO:**
- Use language-first for release strategies (`RELEASE_JAVA.md`)
- Use category-first for reference docs (`WORKFLOWS_MAVEN.md`)
- Use all caps with underscores for clarity
- Be descriptive and specific

❌ **DON'T:**
- Mix naming patterns within same category
- Use abbreviations unless very common
- Use generic names (`DOC1.md`, `GUIDE.md`)

### Examples

✅ **DO:**
- Match workflow naming pattern
- Use build system first for directories
- Include language when build system is multi-language
- Keep structure shallow and navigable

❌ **DON'T:**
- Use different pattern from workflows
- Create deep nesting
- Mix languages in same directory

---

## Quick Reference

### Workflow Naming Pattern

```
Language-Specific Build System:
  maven_ci.yml
  maven_release.yml
  cargo_ci.yml
  gradle_ci.yml

Multi-Language Build System:
  cmake_c_ci.yml
  cmake_cpp_ci.yml
  bazel_java_ci.yml
  bazel_cpp_release.yml
  make_c_ci.yml

Utilities:
  update_submodule.yml
  auto_merge.yml
```

### Documentation Naming Pattern

```
Release Strategies:
  RELEASE_JAVA.md
  RELEASE_C.md
  RELEASE_CPP.md

Workflow Guides:
  WORKFLOWS_MAVEN.md
  WORKFLOWS_CMAKE.md
  WORKFLOWS_BAZEL.md

Reference:
  NAMING_CONVENTIONS.md
  BEST_PRACTICES.md
```

---

## Questions & Answers

**Q: Why not use hyphens everywhere like `maven_ci.yml`?**
A: Hyphens are for compound words (e.g., `pre-commit`). Underscores separate semantic components. This provides visual distinction.

**Q: Why include build system for C/C++ but not Java?**
A: Maven is Java-exclusive. CMake/Bazel are multi-language. Including build system prevents ambiguity.

**Q: What about workflows that support multiple build systems?**
A: Create separate workflows. Example: `maven_ci.yml` and `gradle_ci.yml` rather than `java_ci.yml`.

**Q: How do we handle backward compatibility?**
A: Keep old workflow names as aliases that reference new ones, with deprecation warnings.

**Q: Should examples use the exact workflow names?**
A: No. Examples use generic names (`ci.yml`, `release.yml`) since they're templates to be copied.

---

## References

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Semantic Versioning](https://semver.org/)
- [Keep a Changelog](https://keepachangelog.com/)
