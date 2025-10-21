# C Release Strategy

**Language:** C
**Build Systems:** CMake, Autotools, Make
**Package Managers:** System package managers, source tarballs
**Based on:** Linux Kernel, Git, Redis, SQLite, curl patterns

---

## Table of Contents

- [Release Strategy Overview](#release-strategy-overview)
- [Branching Model](#branching-model)
- [Version Management](#version-management)
- [Build System Integration](#build-system-integration)
- [Release Process](#release-process)
- [Hotfix Process](#hotfix-process)
- [Best Practices](#best-practices)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)

---

## Release Strategy Overview

Artagon C projects use a **conservative tag-based release strategy** with a focus on stability, following patterns from proven C projects like Git, curl, and SQLite.

### Core Principles

- ✅ **`main` branch**: Stable development (always buildable)
- ✅ **Tags**: Primary release mechanism (e.g., `v1.2.3`)
- ✅ **Stable branches**: For long-term maintenance (e.g., `maint-1.2`, `stable`)
- ✅ **Minimal branching**: Simple, predictable model
- ✅ **Semantic Versioning**: MAJOR.MINOR.PATCH

### Why This Strategy?

**C Ecosystem Characteristics:**
- High stability requirements
- Long-term binary compatibility concerns
- System-level integration (often vendored)
- Conservative user base
- Minimal dependencies

**Industry Alignment:**
- **Linux Kernel**: Tag-based with separate stable tree
- **Git**: Four-tier integration (pu → next → master → maint)
- **SQLite**: Trunk-based with extensive testing
- **curl**: Simple main + tags model
- **Redis**: Unstable/stable branch split

---

## Branching Model

### Branch Types

#### Main Branch (Development)
```
main
  ↓
  Always buildable
  All tests pass
  Feature development
  Tagged for releases
```

**Purpose:** Primary development line

**Rules:**
- Must compile without warnings (`-Wall -Werror`)
- All tests must pass
- Code review required for merges
- Clean commit history preferred

**Stability Level:** Beta quality
- Safe for developers
- May have incomplete features (use `#ifdef` guards)
- Not guaranteed ABI stable

#### Stable/Maintenance Branches
```
maint-1.2
  ↓
  Cherry-pick fixes only
  No new features
  ABI frozen
```

**Purpose:** Long-term support and hotfixes

**Rules:**
- Created when maintenance period begins
- Only bug fixes and security patches
- No API/ABI changes
- Minimal changes only

**Naming Conventions:**
- `maint-MAJOR.MINOR` (e.g., `maint-1.2`)
- `stable` (for single active stable line)
- `v1.x` (version-specific, like Redis)

#### Integration Branches (Optional, Git Pattern)
```
next (integration testing)
  ↑
  pu (proposed updates)
  ↑
  topic branches
```

**Purpose:** Multi-tier testing before main

**Use When:**
- Large project with many contributors
- Need integration testing
- Want to test combinations of features

---

## Version Management

### Semantic Versioning for C

```
MAJOR.MINOR.PATCH

Examples:
- 1.0.0  (initial stable release)
- 1.0.1  (bug fix)
- 1.1.0  (new features, backward-compatible)
- 2.0.0  (breaking changes)
```

### Version Bumping Rules

**Patch (X.Y.Z):**
- Bug fixes
- Security patches
- Documentation updates
- Build system fixes
- **ABI compatible**
- **API compatible**

**Minor (X.Y.0):**
- New functions
- New features
- Deprecations (with warnings)
- **ABI compatible** (preferred)
- **API backward-compatible**

**Major (X.0.0):**
- Breaking API changes
- ABI changes
- Removed functions
- Struct layout changes
- Requires recompilation

### Version Declaration

**In header file:**
```c
// version.h
#define PROJECT_VERSION_MAJOR 1
#define PROJECT_VERSION_MINOR 2
#define PROJECT_VERSION_PATCH 3
#define PROJECT_VERSION "1.2.3"

// Numeric version for comparisons
#define PROJECT_VERSION_NUMBER \
    ((PROJECT_VERSION_MAJOR * 10000) + \
     (PROJECT_VERSION_MINOR * 100) + \
     (PROJECT_VERSION_PATCH))
```

**In CMakeLists.txt:**
```cmake
project(MyProject VERSION 1.2.3 LANGUAGES C)

configure_file(
    "${CMAKE_SOURCE_DIR}/include/version.h.in"
    "${CMAKE_BINARY_DIR}/include/version.h"
)
```

**Version check at runtime:**
```c
#include "version.h"

const char* get_version(void) {
    return PROJECT_VERSION;
}

int get_version_number(void) {
    return PROJECT_VERSION_NUMBER;
}
```

---

## Build System Integration

### CMake Configuration

**Root CMakeLists.txt:**
```cmake
cmake_minimum_required(VERSION 3.20)

project(MyProject
    VERSION 1.2.3
    DESCRIPTION "High-performance C library"
    LANGUAGES C
)

# Version information
set(PROJECT_VERSION_MAJOR ${PROJECT_VERSION_MAJOR})
set(PROJECT_VERSION_MINOR ${PROJECT_VERSION_MINOR})
set(PROJECT_VERSION_PATCH ${PROJECT_VERSION_PATCH})

# SO version (for ABI compatibility)
set(PROJECT_SOVERSION ${PROJECT_VERSION_MAJOR})

# Configure version header
configure_file(
    "${CMAKE_SOURCE_DIR}/include/version.h.in"
    "${CMAKE_BINARY_DIR}/include/version.h"
    @ONLY
)

# Library with version
add_library(myproject SHARED src/myproject.c)
set_target_properties(myproject PROPERTIES
    VERSION ${PROJECT_VERSION}
    SOVERSION ${PROJECT_SOVERSION}
)
```

### Autotools Configuration

**configure.ac:**
```autoconf
AC_INIT([myproject], [1.2.3], [bugs@example.com])
AC_CONFIG_SRCDIR([src/myproject.c])
AC_CONFIG_HEADERS([config.h])

AM_INIT_AUTOMAKE([foreign -Wall -Werror])

# Version info for libtool (current:revision:age)
# See: https://www.gnu.org/software/libtool/manual/html_node/Updating-version-info.html
MYPROJECT_LT_VERSION="2:3:1"
AC_SUBST(MYPROJECT_LT_VERSION)

AC_PROG_CC
AM_PROG_AR
LT_INIT

AC_CONFIG_FILES([
    Makefile
    src/Makefile
    tests/Makefile
])
AC_OUTPUT
```

**Makefile.am:**
```makefile
lib_LTLIBRARIES = libmyproject.la

libmyproject_la_SOURCES = \
    src/myproject.c \
    src/util.c

libmyproject_la_LDFLAGS = \
    -version-info $(MYPROJECT_LT_VERSION) \
    -no-undefined

include_HEADERS = \
    include/myproject.h \
    include/version.h
```

---

## Release Process

### Prerequisites

#### 1. Pre-Release Checklist

- [ ] All tests pass on all platforms (Linux, macOS, BSD)
- [ ] No compiler warnings (`-Wall -Werror`)
- [ ] Valgrind clean (no memory leaks)
- [ ] Static analysis clean (cppcheck, clang-analyzer)
- [ ] Documentation updated (man pages, README)
- [ ] CHANGELOG updated
- [ ] Version numbers updated
- [ ] ABI compatibility verified (if applicable)

#### 2. Testing Requirements

**Minimum test matrix:**
- Compilers: gcc, clang
- Platforms: Linux (x86_64, ARM), macOS, FreeBSD
- Configurations: Debug, Release, MinSize
- Sanitizers: AddressSanitizer, UndefinedBehaviorSanitizer

### Release Workflow

#### Step 1: Update Version Numbers

**Update version.h.in:**
```c
// include/version.h.in
#define @PROJECT_NAME@_VERSION_MAJOR @PROJECT_VERSION_MAJOR@
#define @PROJECT_NAME@_VERSION_MINOR @PROJECT_VERSION_MINOR@
#define @PROJECT_NAME@_VERSION_PATCH @PROJECT_VERSION_PATCH@
#define @PROJECT_NAME@_VERSION "@PROJECT_VERSION@"
```

**Update CMakeLists.txt:**
```cmake
project(MyProject VERSION 1.2.3 LANGUAGES C)
```

**Commit version bump:**
```bash
git add CMakeLists.txt include/version.h.in
git commit -m "chore: bump version to 1.2.3"
```

#### Step 2: Update CHANGELOG

**CHANGELOG.md format:**
```markdown
# Changelog

All notable changes to this project will be documented in this file.

## [1.2.3] - 2025-10-21

### Added
- New `process_data_async()` function for asynchronous processing
- Support for custom memory allocators

### Changed
- Improved performance of hash table lookups (30% faster)
- Updated documentation with more examples

### Fixed
- Fixed memory leak in error handling path (issue #123)
- Corrected off-by-one error in buffer management (CVE-2024-XXXXX)

### Security
- Fixed buffer overflow in string parsing (CVE-2024-YYYYY)

## [1.2.2] - 2025-09-15
...
```

#### Step 3: Run Full Test Suite

```bash
# Clean build
rm -rf build
mkdir build && cd build

# Configure with all warnings
cmake .. \
    -DCMAKE_BUILD_TYPE=Release \
    -DCMAKE_C_FLAGS="-Wall -Wextra -Werror -pedantic"

# Build
cmake --build . --parallel

# Run tests
ctest --output-on-failure

# Memory checks
valgrind --leak-check=full --error-exitcode=1 ./tests/test_suite

# Static analysis
cppcheck --enable=all --error-exitcode=1 ../src/
```

#### Step 4: Create Release Tag

```bash
# Create annotated tag
git tag -a v1.2.3 -m "Release version 1.2.3

Changes:
- New async processing API
- Performance improvements
- Security fixes for CVE-2024-XXXXX and CVE-2024-YYYYY

Full changelog: https://github.com/artagon/project/blob/v1.2.3/CHANGELOG.md"

# Verify tag
git show v1.2.3

# Push tag
git push origin v1.2.3
```

#### Step 5: Create Release Tarball

**Generate source tarball:**
```bash
# Using git archive
git archive --format=tar.gz \
    --prefix=myproject-1.2.3/ \
    -o myproject-1.2.3.tar.gz \
    v1.2.3

# Or using make dist (autotools)
make dist
```

**Create checksums:**
```bash
# SHA-256
sha256sum myproject-1.2.3.tar.gz > myproject-1.2.3.tar.gz.sha256

# GPG signature
gpg --armor --detach-sign myproject-1.2.3.tar.gz
```

**Verify tarball:**
```bash
# Extract and build
tar xzf myproject-1.2.3.tar.gz
cd myproject-1.2.3
mkdir build && cd build
cmake ..
cmake --build .
ctest
```

#### Step 6: Create GitHub Release

```bash
gh release create v1.2.3 \
    --title "Release 1.2.3" \
    --notes-file CHANGELOG-1.2.3.md \
    myproject-1.2.3.tar.gz \
    myproject-1.2.3.tar.gz.sha256 \
    myproject-1.2.3.tar.gz.asc
```

#### Step 7: Create Stable Branch (if LTS)

For long-term support releases:
```bash
# Create maintenance branch
git checkout -b maint-1.2 v1.2.3
git push origin maint-1.2
```

---

## Hotfix Process

### Critical Bug Fix Workflow

#### Step 1: Identify Target Branch

**For latest release:**
```bash
git checkout main
```

**For older LTS version:**
```bash
git checkout maint-1.2
```

#### Step 2: Apply Fix

```bash
# Create fix
vim src/vulnerable_code.c

# Test thoroughly
cmake --build build
ctest --test-dir build

# Commit
git add src/vulnerable_code.c
git commit -m "fix: resolve buffer overflow in parse_input()

CVE-2024-XXXXX: Buffer overflow when parsing malformed input

The issue was caused by missing bounds check in parse_input().
Added proper validation to prevent buffer overrun.

Fixes: #456
Security: CVE-2024-XXXXX"
```

#### Step 3: Create Hotfix Release

```bash
# Update version (1.2.3 → 1.2.4)
# Edit CMakeLists.txt
sed -i 's/VERSION 1.2.3/VERSION 1.2.4/' CMakeLists.txt

git add CMakeLists.txt
git commit -m "chore: bump version to 1.2.4 for security release"

# Create tag
git tag -a v1.2.4 -m "Security release 1.2.4

Critical security fix for CVE-2024-XXXXX

All users should upgrade immediately."

# Push
git push origin maint-1.2 --tags
```

#### Step 4: Cherry-pick to Main (if needed)

```bash
git checkout main
git cherry-pick <fix-commit-sha>

# If main has diverged significantly, manual port may be needed
git push origin main
```

#### Step 5: Coordinate Disclosure

**For security issues:**
1. Notify package maintainers first (embargo period)
2. Coordinate with distributions
3. Publish CVE details after fix is available
4. Send security advisory

---

## Best Practices

### 1. API Stability

**Public API headers:**
```c
// myproject.h - Public API
#ifndef MYPROJECT_H
#define MYPROJECT_H

#ifdef __cplusplus
extern "C" {
#endif

// Version check
#define MYPROJECT_VERSION_CHECK(major, minor, patch) \
    (MYPROJECT_VERSION_NUMBER >= \
     ((major) * 10000 + (minor) * 100 + (patch)))

// Stable API
int myproject_init(void);
void myproject_cleanup(void);
int myproject_process(const char* input, char* output, size_t size);

// New in 1.2.0
#if MYPROJECT_VERSION_CHECK(1, 2, 0)
int myproject_process_async(const char* input, void (*callback)(void*));
#endif

#ifdef __cplusplus
}
#endif

#endif // MYPROJECT_H
```

**Deprecation strategy:**
```c
// Mark deprecated functions
#ifndef MYPROJECT_DEPRECATED
#  ifdef __GNUC__
#    define MYPROJECT_DEPRECATED __attribute__((deprecated))
#  elif defined(_MSC_VER)
#    define MYPROJECT_DEPRECATED __declspec(deprecated)
#  else
#    define MYPROJECT_DEPRECATED
#  endif
#endif

// Deprecated function (removed in 2.0.0)
MYPROJECT_DEPRECATED
int myproject_old_function(void);
```

### 2. ABI Compatibility

**Maintain ABI between minor versions:**

❌ **Don't:**
- Change struct layout
- Remove functions
- Change function signatures
- Reorder virtual function tables
- Change enum values

✅ **Do:**
- Add new functions
- Add new structs
- Use reserved fields for expansion
- Use opaque pointers

**Opaque pointer pattern:**
```c
// Public header
typedef struct myproject_context myproject_context_t;

myproject_context_t* myproject_create(void);
void myproject_destroy(myproject_context_t* ctx);

// Private implementation
struct myproject_context {
    int internal_field1;
    char* internal_field2;
    // Can change freely without breaking ABI
};
```

### 3. Testing Strategy

**Comprehensive test coverage:**
```c
// test_myproject.c
#include <assert.h>
#include "myproject.h"

void test_init_cleanup(void) {
    assert(myproject_init() == 0);
    myproject_cleanup();
}

void test_process_valid_input(void) {
    char output[256];
    int result = myproject_process("test", output, sizeof(output));
    assert(result == 0);
    assert(strcmp(output, "expected") == 0);
}

void test_process_null_input(void) {
    char output[256];
    int result = myproject_process(NULL, output, sizeof(output));
    assert(result == -1); // Should handle gracefully
}

void test_process_buffer_too_small(void) {
    char output[4];
    int result = myproject_process("longstring", output, sizeof(output));
    assert(result == -1); // Should detect overflow
}

int main(void) {
    test_init_cleanup();
    test_process_valid_input();
    test_process_null_input();
    test_process_buffer_too_small();

    printf("All tests passed!\n");
    return 0;
}
```

**Test automation with CMake:**
```cmake
# tests/CMakeLists.txt
enable_testing()

add_executable(test_suite test_myproject.c)
target_link_libraries(test_suite myproject)

add_test(NAME test_suite COMMAND test_suite)

# Memory leak tests
find_program(VALGRIND valgrind)
if(VALGRIND)
    add_test(NAME test_memcheck
        COMMAND ${VALGRIND}
            --leak-check=full
            --error-exitcode=1
            $<TARGET_FILE:test_suite>
    )
endif()
```

### 4. Documentation

**Man pages:**
```troff
.TH MYPROJECT 3 "2025-10-21" "1.2.3" "MyProject Manual"
.SH NAME
myproject_init \- initialize MyProject library
.SH SYNOPSIS
.B #include <myproject.h>
.PP
.BI "int myproject_init(void);"
.SH DESCRIPTION
The
.BR myproject_init ()
function initializes the MyProject library.
Must be called before any other library functions.
.SH RETURN VALUE
Returns 0 on success, -1 on error.
.SH EXAMPLE
.EX
#include <myproject.h>

int main(void) {
    if (myproject_init() != 0) {
        fprintf(stderr, "Failed to initialize\\n");
        return 1;
    }

    // Use library...

    myproject_cleanup();
    return 0;
}
.EE
.SH SEE ALSO
.BR myproject_cleanup (3),
.BR myproject_process (3)
```

### 5. Build System Best Practices

**CMake feature detection:**
```cmake
include(CheckCSourceCompiles)
include(CheckFunctionExists)
include(CheckIncludeFile)

# Check for required functions
check_function_exists(pthread_create HAVE_PTHREAD)
check_include_file(sys/mman.h HAVE_SYS_MMAN_H)

# Configure header
configure_file(
    "${CMAKE_SOURCE_DIR}/config.h.in"
    "${CMAKE_BINARY_DIR}/config.h"
)
```

**Portable code:**
```c
// Use configure-time detection
#include "config.h"

#ifdef HAVE_PTHREAD
#include <pthread.h>
#define THREAD_LOCAL __thread
#elif defined(_WIN32)
#include <windows.h>
#define THREAD_LOCAL __declspec(thread)
#else
#define THREAD_LOCAL /* not supported */
#endif
```

---

## Testing

### Test Pyramid for C

**Unit Tests (70%):**
- Individual functions
- Edge cases
- Error conditions
- Fast execution

**Integration Tests (20%):**
- Module interactions
- End-to-end workflows
- Configuration scenarios

**System Tests (10%):**
- Platform-specific behavior
- Performance benchmarks
- Stress tests

### Continuous Integration

```yaml
# .github/workflows/cmake_c_ci.yml
name: C CI

on: [push, pull_request]

jobs:
  test:
    strategy:
      matrix:
        os: [ubuntu-latest, macos-latest]
        compiler: [gcc, clang]
        build_type: [Debug, Release]

    runs-on: ${{ matrix.os }}

    steps:
      - uses: actions/checkout@v4

      - name: Install dependencies
        run: |
          if [ "$RUNNER_OS" == "Linux" ]; then
            sudo apt-get update
            sudo apt-get install -y valgrind cppcheck
          fi

      - name: Configure
        env:
          CC: ${{ matrix.compiler }}
        run: |
          cmake -B build \
            -DCMAKE_BUILD_TYPE=${{ matrix.build_type }} \
            -DCMAKE_C_FLAGS="-Wall -Wextra -Werror"

      - name: Build
        run: cmake --build build --parallel

      - name: Test
        run: ctest --test-dir build --output-on-failure

      - name: Memory Check (Linux only)
        if: runner.os == 'Linux'
        run: |
          valgrind --leak-check=full \
            --error-exitcode=1 \
            ./build/tests/test_suite
```

---

## Troubleshooting

### Build Issues

**Missing dependencies:**
```bash
# CMake can't find libraries
cmake .. -DCMAKE_PREFIX_PATH=/usr/local

# Check what CMake found
cmake .. --debug-find
```

**Compiler warnings:**
```bash
# Treat warnings as errors during development
cmake .. -DCMAKE_C_FLAGS="-Wall -Wextra -Werror -pedantic"

# Disable specific warnings if needed
cmake .. -DCMAKE_C_FLAGS="-Wall -Wno-unused-parameter"
```

### ABI Compatibility Check

```bash
# Using abi-compliance-checker
abi-compliance-checker -l myproject \
    -old old_version/myproject.so \
    -new new_version/myproject.so

# Using abi-dumper
abi-dumper myproject.so -o ABI-1.dump -lver 1
abi-dumper myproject.so -o ABI-2.dump -lver 2
abi-compliance-checker -l myproject -old ABI-1.dump -new ABI-2.dump
```

---

## References

### Standards
- [Semantic Versioning](https://semver.org/)
- [Library Interface Versioning (GNU Libtool)](https://www.gnu.org/software/libtool/manual/html_node/Versioning.html)
- [POSIX API Standards](https://pubs.opengroup.org/onlinepubs/9699919799/)

### Industry Examples
- [Linux Kernel Release Process](https://www.kernel.org/category/releases.html)
- [Git Maintenance and Release Process](https://github.com/git/git/blob/master/Documentation/howto/maintain-git.txt)
- [SQLite Release Checklist](https://www.sqlite.org/testing.html)
- [curl Release Procedure](https://curl.se/dev/release-procedure.html)

### Tools
- [CMake Documentation](https://cmake.org/documentation/)
- [Valgrind](https://valgrind.org/)
- [Cppcheck](http://cppcheck.sourceforge.net/)
- [ABI Compliance Checker](https://lvc.github.io/abi-compliance-checker/)

---

## Appendix: Quick Reference

### Common Commands

```bash
# CMake build
cmake -B build -DCMAKE_BUILD_TYPE=Release
cmake --build build --parallel
ctest --test-dir build

# Autotools build
./autogen.sh
./configure
make -j$(nproc)
make check

# Create release
git tag -a v1.2.3 -m "Release 1.2.3"
git push origin v1.2.3
git archive --format=tar.gz --prefix=proj-1.2.3/ v1.2.3 > proj-1.2.3.tar.gz

# Verify
sha256sum proj-1.2.3.tar.gz
gpg --armor --detach-sign proj-1.2.3.tar.gz
```
