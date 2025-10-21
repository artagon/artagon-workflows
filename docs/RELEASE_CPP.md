# C++ Release Strategy

**Language:** C++
**Build Systems:** CMake, Bazel, Meson
**Package Managers:** Conan, vcpkg, system package managers
**Based on:** LLVM, TensorFlow, Chromium, Qt, Boost patterns

---

## Table of Contents

- [Release Strategy Overview](#release-strategy-overview)
- [Branching Model](#branching-model)
- [Version Management](#version-management)
- [ABI/API Stability](#abiapi-stability)
- [Build System Integration](#build-system-integration)
- [Release Process](#release-process)
- [LTS Strategy](#lts-strategy)
- [Best Practices](#best-practices)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)

---

## Release Strategy Overview

Artagon C++ projects use a **release branch strategy with LTS support**, following patterns from LLVM, Qt, and TensorFlow for enterprise-grade stability.

### Core Principles

- ✅ **`main` branch**: Development version (C++17/20/23 features)
- ✅ **`release-X.Y` branches**: Stable releases with maintenance
- ✅ **LTS releases**: Long-term support (3-5 years)
- ✅ **ABI stability**: Within minor versions
- ✅ **Semantic Versioning**: MAJOR.MINOR.PATCH

### Why This Strategy?

**C++ Ecosystem Characteristics:**
- Complex build systems and dependencies
- ABI compatibility critical
- Long compilation times
- Enterprise adoption (needs LTS)
- Header-only vs. library trade-offs

**Industry Alignment:**
- **LLVM**: 6-month release cycle, release branches
- **Qt**: LTS model (5 years commercial, 3 years open source)
- **TensorFlow**: ReleaseFlow with long-term support
- **Boost**: Develop/master split, quarterly releases
- **Chromium**: 4-channel train model (adapted for libraries)

---

## Branching Model

### Branch Types

#### Main Branch (Development)
```
main
  ↓
  C++20/23 features
  Active development
  May break ABI
  Tagged for releases
```

**Purpose:** Next major/minor version development

**Rules:**
- Use latest C++ standard features
- All tests pass
- Code review required
- May break ABI (document in CHANGELOG)
- Can change internal APIs

**C++ Standards:**
- Minimum: C++17
- Recommended: C++20
- Experimental: C++23 features (gated by macros)

#### Release Branches
```
release-2.1
  ↓
  ABI frozen
  Bug fixes only
  Backports from main
  LTS support
```

**Purpose:** Stable releases with maintenance

**Rules:**
- Created at release time
- ABI/API frozen
- Only bug fixes and security patches
- No new features (exceptions for LTS)
- Keep for support period

**Naming:** `release-MAJOR.MINOR`

#### Feature Branches
```
feature/vectorization-support
  ↓
  Experimental work
  Merged to main via PR
```

**Purpose:** Isolated feature development

---

## Version Management

### Semantic Versioning for C++

```
MAJOR.MINOR.PATCH

Examples:
- 1.0.0  (initial stable release)
- 1.1.0  (new features, ABI compatible)
- 1.1.1  (bug fixes)
- 2.0.0  (ABI/API breaking changes)
```

### Version Bumping Rules

**Patch (X.Y.Z):**
- Bug fixes
- Documentation updates
- Performance improvements (no API changes)
- **ABI stable**
- **API stable**
- **Can be drop-in replacement**

**Minor (X.Y.0):**
- New classes/functions
- New template specializations
- Optional features
- **ABI stable** (if careful)
- **API backward-compatible**
- **May require recompilation**

**Major (X.0.0):**
- Breaking API changes
- ABI changes
- Removed/renamed classes
- Template signature changes
- Minimum C++ standard bump
- **Requires code changes**

### Version Declaration

**version.hpp:**
```cpp
#ifndef MYPROJECT_VERSION_HPP
#define MYPROJECT_VERSION_HPP

#define MYPROJECT_VERSION_MAJOR 2
#define MYPROJECT_VERSION_MINOR 1
#define MYPROJECT_VERSION_PATCH 3

#define MYPROJECT_VERSION_STRING "2.1.3"

// Numeric version for comparisons
#define MYPROJECT_VERSION_NUMBER \
    ((MYPROJECT_VERSION_MAJOR * 10000) + \
     (MYPROJECT_VERSION_MINOR * 100) + \
     (MYPROJECT_VERSION_PATCH))

namespace myproject {

struct Version {
    static constexpr int major = MYPROJECT_VERSION_MAJOR;
    static constexpr int minor = MYPROJECT_VERSION_MINOR;
    static constexpr int patch = MYPROJECT_VERSION_PATCH;

    static constexpr const char* string() {
        return MYPROJECT_VERSION_STRING;
    }

    static constexpr int number() {
        return MYPROJECT_VERSION_NUMBER;
    }
};

} // namespace myproject

#endif // MYPROJECT_VERSION_HPP
```

**CMakeLists.txt:**
```cmake
project(MyProject
    VERSION 2.1.3
    DESCRIPTION "High-performance C++ library"
    LANGUAGES CXX
)

# C++ standard
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

# SO version for ABI
set(MYPROJECT_SOVERSION ${PROJECT_VERSION_MAJOR})

configure_file(
    "${CMAKE_SOURCE_DIR}/include/myproject/version.hpp.in"
    "${CMAKE_BINARY_DIR}/include/myproject/version.hpp"
    @ONLY
)
```

---

## ABI/API Stability

### ABI Compatibility Guidelines

#### What Breaks ABI

❌ **Never do in minor versions:**
- Change class size
- Reorder class members
- Change virtual function table
- Change template parameters
- Remove functions
- Change function signatures
- Change exception specifications

#### What's ABI Safe

✅ **Allowed in minor versions:**
- Add new classes
- Add new non-virtual functions
- Add new static functions
- Add template specializations
- Add namespaces
- Change private implementation (PIMPL)

### PIMPL Pattern for ABI Stability

**Public header (stable):**
```cpp
// myclass.hpp - Public API
#ifndef MYPROJECT_MYCLASS_HPP
#define MYPROJECT_MYCLASS_HPP

#include <memory>
#include <string>

namespace myproject {

class MyClass {
public:
    MyClass();
    ~MyClass();

    // Copyable
    MyClass(const MyClass& other);
    MyClass& operator=(const MyClass& other);

    // Movable
    MyClass(MyClass&& other) noexcept;
    MyClass& operator=(MyClass&& other) noexcept;

    // Public API
    void set_value(int value);
    int get_value() const;

private:
    class Impl;
    std::unique_ptr<Impl> pimpl_;
};

} // namespace myproject

#endif
```

**Implementation (can change freely):**
```cpp
// myclass.cpp
#include "myproject/myclass.hpp"
#include <vector>
#include <map>

namespace myproject {

class MyClass::Impl {
public:
    int value_{0};
    std::vector<int> data_;
    std::map<std::string, int> cache_;

    // Can add/remove fields without breaking ABI
    // Can change implementation freely
};

MyClass::MyClass() : pimpl_(std::make_unique<Impl>()) {}
MyClass::~MyClass() = default;

MyClass::MyClass(const MyClass& other)
    : pimpl_(std::make_unique<Impl>(*other.pimpl_)) {}

MyClass& MyClass::operator=(const MyClass& other) {
    if (this != &other) {
        *pimpl_ = *other.pimpl_;
    }
    return *this;
}

MyClass::MyClass(MyClass&& other) noexcept = default;
MyClass& MyClass::operator=(MyClass&& other) noexcept = default;

void MyClass::set_value(int value) {
    pimpl_->value_ = value;
}

int MyClass::get_value() const {
    return pimpl_->value_;
}

} // namespace myproject
```

### API Versioning with Inline Namespaces

```cpp
// Forward compatibility
namespace myproject {

// Version 2 API (current)
inline namespace v2 {

class MyClass {
    // Current API
};

} // namespace v2

// Version 1 API (deprecated, for compatibility)
namespace v1 {

class [[deprecated("Use v2::MyClass")]] MyClass {
    // Old API
};

} // namespace v1

} // namespace myproject

// Users can explicitly choose version:
// myproject::v1::MyClass old_way;
// myproject::v2::MyClass new_way;
// myproject::MyClass defaults_to_v2;
```

---

## Build System Integration

### CMake Configuration

**Root CMakeLists.txt:**
```cmake
cmake_minimum_required(VERSION 3.20)

project(MyProject
    VERSION 2.1.3
    DESCRIPTION "Modern C++ library"
    LANGUAGES CXX
)

# C++ standard
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

# Options
option(MYPROJECT_BUILD_TESTS "Build tests" ON)
option(MYPROJECT_BUILD_EXAMPLES "Build examples" ON)
option(MYPROJECT_BUILD_SHARED "Build shared library" ON)
option(MYPROJECT_ENABLE_LTO "Enable link-time optimization" OFF)

# Version configuration
set(MYPROJECT_SOVERSION ${PROJECT_VERSION_MAJOR})

configure_file(
    "${CMAKE_SOURCE_DIR}/include/myproject/version.hpp.in"
    "${CMAKE_BINARY_DIR}/include/myproject/version.hpp"
    @ONLY
)

# Library target
if(MYPROJECT_BUILD_SHARED)
    add_library(myproject SHARED)
else()
    add_library(myproject STATIC)
endif()

target_sources(myproject
    PRIVATE
        src/myclass.cpp
        src/algorithm.cpp
    PUBLIC
        FILE_SET HEADERS
        BASE_DIRS include
        FILES
            include/myproject/myclass.hpp
            include/myproject/algorithm.hpp
)

target_include_directories(myproject
    PUBLIC
        $<BUILD_INTERFACE:${CMAKE_SOURCE_DIR}/include>
        $<BUILD_INTERFACE:${CMAKE_BINARY_DIR}/include>
        $<INSTALL_INTERFACE:include>
)

target_compile_features(myproject PUBLIC cxx_std_20)

set_target_properties(myproject PROPERTIES
    VERSION ${PROJECT_VERSION}
    SOVERSION ${MYPROJECT_SOVERSION}
    CXX_VISIBILITY_PRESET hidden
    VISIBILITY_INLINES_HIDDEN YES
)

# Compiler warnings
if(MSVC)
    target_compile_options(myproject PRIVATE /W4 /WX)
else()
    target_compile_options(myproject PRIVATE -Wall -Wextra -Werror -pedantic)
endif()

# Installation
include(GNUInstallDirs)

install(TARGETS myproject
    EXPORT MyProjectTargets
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    ARCHIVE DESTINATION ${CMAKE_INSTALL_LIBDIR}
    RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
    FILE_SET HEADERS DESTINATION ${CMAKE_INSTALL_INCLUDEDIR}
)

install(EXPORT MyProjectTargets
    FILE MyProjectTargets.cmake
    NAMESPACE MyProject::
    DESTINATION ${CMAKE_INSTALL_LIBDIR}/cmake/MyProject
)

# Package config
include(CMakePackageConfigHelpers)

configure_package_config_file(
    "${CMAKE_SOURCE_DIR}/cmake/MyProjectConfig.cmake.in"
    "${CMAKE_BINARY_DIR}/MyProjectConfig.cmake"
    INSTALL_DESTINATION ${CMAKE_INSTALL_LIBDIR}/cmake/MyProject
)

write_basic_package_version_file(
    "${CMAKE_BINARY_DIR}/MyProjectConfigVersion.cmake"
    VERSION ${PROJECT_VERSION}
    COMPATIBILITY SameMajorVersion
)

install(FILES
    "${CMAKE_BINARY_DIR}/MyProjectConfig.cmake"
    "${CMAKE_BINARY_DIR}/MyProjectConfigVersion.cmake"
    DESTINATION ${CMAKE_INSTALL_LIBDIR}/cmake/MyProject
)
```

### Bazel Configuration

**WORKSPACE:**
```python
workspace(name = "myproject")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

# abseil-cpp
http_archive(
    name = "com_google_absl",
    urls = ["https://github.com/abseil/abseil-cpp/archive/refs/tags/20240116.0.tar.gz"],
    strip_prefix = "abseil-cpp-20240116.0",
)

# GoogleTest
http_archive(
    name = "com_google_googletest",
    urls = ["https://github.com/google/googletest/archive/refs/tags/v1.14.0.tar.gz"],
    strip_prefix = "googletest-1.14.0",
)
```

**BUILD.bazel:**
```python
load("@rules_cc//cc:defs.bzl", "cc_library", "cc_test", "cc_binary")

package(default_visibility = ["//visibility:public"])

cc_library(
    name = "myproject",
    srcs = [
        "src/myclass.cpp",
        "src/algorithm.cpp",
    ],
    hdrs = [
        "include/myproject/myclass.hpp",
        "include/myproject/algorithm.hpp",
        "include/myproject/version.hpp",
    ],
    includes = ["include"],
    deps = [
        "@com_google_absl//absl/container:flat_hash_map",
        "@com_google_absl//absl/strings",
    ],
    copts = [
        "-std=c++20",
        "-Wall",
        "-Wextra",
        "-Werror",
    ],
)

cc_test(
    name = "myproject_test",
    srcs = ["tests/myclass_test.cpp"],
    deps = [
        ":myproject",
        "@com_google_googletest//:gtest_main",
    ],
)
```

---

## Release Process

### Prerequisites

#### 1. Pre-Release Checklist

- [ ] All tests pass (unit, integration, end-to-end)
- [ ] No compiler warnings (-Wall -Wextra -Werror)
- [ ] Sanitizers clean (ASan, UBSan, TSan, MSan)
- [ ] Static analysis clean (clang-tidy, cppcheck)
- [ ] ABI compatibility verified (for minor releases)
- [ ] Documentation updated (Doxygen, README)
- [ ] CHANGELOG.md updated
- [ ] Migration guide (for major versions)
- [ ] Benchmarks run (no regressions)

#### 2. Test Matrix

**Compilers:**
- GCC: 11, 12, 13
- Clang: 15, 16, 17
- MSVC: 2019, 2022
- AppleClang: Latest

**Platforms:**
- Linux: Ubuntu 22.04, Fedora latest
- macOS: 12+
- Windows: 10, 11

**Configurations:**
- Debug / Release / RelWithDebInfo
- Shared / Static libraries
- C++17 / C++20 / C++23

### Release Workflow

#### Step 1: Update Version

**version.hpp.in:**
```cpp
#define MYPROJECT_VERSION_MAJOR @PROJECT_VERSION_MAJOR@
#define MYPROJECT_VERSION_MINOR @PROJECT_VERSION_MINOR@
#define MYPROJECT_VERSION_PATCH @PROJECT_VERSION_PATCH@
```

**CMakeLists.txt:**
```cmake
project(MyProject VERSION 2.1.3 LANGUAGES CXX)
```

**Commit:**
```bash
git add CMakeLists.txt include/myproject/version.hpp.in
git commit -m "chore: bump version to 2.1.3"
```

#### Step 2: Run Full Test Suite

```bash
# Clean build
rm -rf build
mkdir build && cd build

# Configure with all checks
cmake .. \
    -DCMAKE_BUILD_TYPE=Release \
    -DCMAKE_CXX_COMPILER=clang++ \
    -DCMAKE_CXX_FLAGS="-Wall -Wextra -Werror -pedantic" \
    -DMYPROJECT_BUILD_TESTS=ON \
    -DMYPROJECT_ENABLE_SANITIZERS=ON

# Build
cmake --build . --parallel

# Run tests
ctest --output-on-failure --parallel

# Run with AddressSanitizer
cmake .. -DCMAKE_CXX_FLAGS="-fsanitize=address -fno-omit-frame-pointer"
cmake --build . --parallel
ctest --output-on-failure

# Run with UndefinedBehaviorSanitizer
cmake .. -DCMAKE_CXX_FLAGS="-fsanitize=undefined"
cmake --build . --parallel
ctest --output-on-failure
```

#### Step 3: Verify ABI Compatibility (Minor Releases)

```bash
# Build both versions
cmake -B build-old -DCMAKE_BUILD_TYPE=Release
cmake --build build-old

cmake -B build-new -DCMAKE_BUILD_TYPE=Release
cmake --build build-new

# Check ABI compatibility
abi-compliance-checker -l myproject \
    -old build-old/libmyproject.so \
    -new build-new/libmyproject.so
```

#### Step 4: Create Release Branch

For new minor version:
```bash
git checkout -b release-2.1
git push origin release-2.1
```

#### Step 5: Tag Release

```bash
git tag -a v2.1.3 -m "Release 2.1.3

New features:
- Vectorized operations for improved performance
- Added SIMD support for ARM and x86

Improvements:
- 30% performance improvement in matrix operations
- Reduced memory footprint by 15%

Bug fixes:
- Fixed race condition in thread pool
- Corrected edge case in parser

See CHANGELOG.md for full details."

git push origin v2.1.3
```

#### Step 6: Build Distribution Packages

**Source tarball:**
```bash
git archive --format=tar.gz \
    --prefix=myproject-2.1.3/ \
    -o myproject-2.1.3.tar.gz \
    v2.1.3

# Checksums
sha256sum myproject-2.1.3.tar.gz > myproject-2.1.3.tar.gz.sha256
sha512sum myproject-2.1.3.tar.gz > myproject-2.1.3.tar.gz.sha512

# GPG signature
gpg --armor --detach-sign myproject-2.1.3.tar.gz
```

**Binary packages (optional):**
```bash
# Debian package
cd build
cpack -G DEB

# RPM package
cpack -G RPM

# Windows installer
cpack -G NSIS
```

#### Step 7: Create GitHub Release

```bash
gh release create v2.1.3 \
    --title "Release 2.1.3" \
    --notes-file RELEASE_NOTES.md \
    myproject-2.1.3.tar.gz \
    myproject-2.1.3.tar.gz.sha256 \
    myproject-2.1.3.tar.gz.asc
```

#### Step 8: Publish to Package Managers

**Conan:**
```bash
cd conan
conan create . myproject/2.1.3@
conan upload myproject/2.1.3@ -r artifactory
```

**vcpkg:**
```bash
# Create PR to vcpkg repository
# Update ports/myproject/portfile.cmake
# Update ports/myproject/vcpkg.json
```

---

## LTS Strategy

### LTS Release Cadence

**Standard Releases:**
- Every 3-4 months
- 1 year of bug fixes
- Security fixes for 18 months

**LTS Releases:**
- Once per year
- 3 years of bug fixes
- 5 years of security fixes
- Extra validation and testing

### LTS Designation

**Version Scheme:**
```
2.0.0 LTS (released Q1 2024, supported until Q1 2029)
2.1.0     (released Q2 2024, supported until Q2 2025)
2.2.0     (released Q3 2024, supported until Q3 2025)
2.3.0     (released Q4 2024, supported until Q4 2025)
3.0.0 LTS (released Q1 2025, supported until Q1 2030)
```

**LTS Branch Management:**
```bash
# Create LTS branch from release
git checkout -b lts-2.0 release-2.0
git push origin lts-2.0
```

### Backporting to LTS

```bash
# Cherry-pick fix from main
git checkout lts-2.0
git cherry-pick <commit-sha>

# Test
cmake -B build && cmake --build build
ctest --test-dir build

# Tag patch release
git tag -a v2.0.15 -m "LTS patch release 2.0.15"
git push origin lts-2.0 --tags
```

---

## Best Practices

### 1. Modern C++ Practices

**Use RAII:**
```cpp
class Resource {
public:
    Resource() : handle_(acquire_handle()) {}
    ~Resource() { release_handle(handle_); }

    // Delete copy, allow move
    Resource(const Resource&) = delete;
    Resource& operator=(const Resource&) = delete;

    Resource(Resource&& other) noexcept
        : handle_(std::exchange(other.handle_, nullptr)) {}

    Resource& operator=(Resource&& other) noexcept {
        if (this != &other) {
            release_handle(handle_);
            handle_ = std::exchange(other.handle_, nullptr);
        }
        return *this;
    }
};
```

**Use smart pointers:**
```cpp
// Ownership
std::unique_ptr<Widget> widget = std::make_unique<Widget>();

// Shared ownership
std::shared_ptr<Resource> resource = std::make_shared<Resource>();

// Avoid raw pointers for ownership
// Widget* widget = new Widget();  // Bad
```

### 2. API Design

**Header-only vs. Library:**

**Header-only (good for templates):**
```cpp
// algorithm.hpp
namespace myproject {

template<typename T>
T max(T a, T b) {
    return (a > b) ? a : b;
}

} // namespace myproject
```

**Library with PIMPL (good for ABI stability):**
```cpp
// Shown earlier in ABI section
```

### 3. Error Handling

**Use exceptions appropriately:**
```cpp
class FileError : public std::runtime_error {
public:
    explicit FileError(const std::string& msg)
        : std::runtime_error(msg) {}
};

void read_file(const std::string& path) {
    std::ifstream file(path);
    if (!file) {
        throw FileError("Failed to open: " + path);
    }
    // ... read file
}
```

**Or use std::expected (C++23) / Result types:**
```cpp
template<typename T, typename E>
class Result {
public:
    // Implementation of Rust-style Result<T, E>
};

Result<Data, Error> parse_data(const std::string& input) {
    if (input.empty()) {
        return Error{"Empty input"};
    }
    return Data{/* parsed */};
}
```

---

## Testing

### Unit Testing with GoogleTest

```cpp
// tests/myclass_test.cpp
#include <gtest/gtest.h>
#include "myproject/myclass.hpp"

namespace myproject {
namespace {

class MyClassTest : public ::testing::Test {
protected:
    void SetUp() override {
        obj_ = std::make_unique<MyClass>();
    }

    std::unique_ptr<MyClass> obj_;
};

TEST_F(MyClassTest, InitialValue) {
    EXPECT_EQ(obj_->get_value(), 0);
}

TEST_F(MyClassTest, SetAndGet) {
    obj_->set_value(42);
    EXPECT_EQ(obj_->get_value(), 42);
}

TEST_F(MyClassTest, CopyConstruction) {
    obj_->set_value(100);
    MyClass copy(*obj_);
    EXPECT_EQ(copy.get_value(), 100);
}

TEST_F(MyClassTest, MoveConstruction) {
    obj_->set_value(200);
    MyClass moved(std::move(*obj_));
    EXPECT_EQ(moved.get_value(), 200);
}

} // namespace
} // namespace myproject
```

### Benchmarking

```cpp
// benchmarks/algorithm_benchmark.cpp
#include <benchmark/benchmark.h>
#include "myproject/algorithm.hpp"

static void BM_Algorithm(benchmark::State& state) {
    std::vector<int> data(state.range(0));
    for (auto _ : state) {
        myproject::process(data);
        benchmark::DoNotOptimize(data);
    }
    state.SetItemsProcessed(state.iterations() * state.range(0));
}

BENCHMARK(BM_Algorithm)->Range(8, 8<<10);

BENCHMARK_MAIN();
```

---

## References

### Standards
- [ISO C++ Standards](https://isocpp.org/std/the-standard)
- [Semantic Versioning](https://semver.org/)
- [C++ Core Guidelines](https://isocpp.github.io/CppCoreGuidelines/CppCoreGuidelines)

### Industry Examples
- [LLVM Release Process](https://llvm.org/docs/HowToReleaseLLVM.html)
- [Qt Release Process](https://wiki.qt.io/Qt_Release_Process)
- [TensorFlow Release Process](https://github.com/tensorflow/tensorflow/blob/master/RELEASE.md)
- [Boost Release Process](https://www.boost.org/development/requirements.html)

### Tools
- [CMake](https://cmake.org/)
- [Bazel](https://bazel.build/)
- [Conan](https://conan.io/)
- [vcpkg](https://vcpkg.io/)
- [GoogleTest](https://github.com/google/googletest)
- [Google Benchmark](https://github.com/google/benchmark)

---

## Quick Reference

```bash
# CMake build
cmake -B build -DCMAKE_BUILD_TYPE=Release -DCMAKE_CXX_STANDARD=20
cmake --build build --parallel
ctest --test-dir build --output-on-failure

# Bazel build
bazel build //...
bazel test //...

# Release
git tag -a v2.1.3 -m "Release 2.1.3"
git archive --prefix=myproject-2.1.3/ v2.1.3 | gzip > myproject-2.1.3.tar.gz
gh release create v2.1.3 myproject-2.1.3.tar.gz

# ABI check
abi-compliance-checker -l myproject -old v2.1.2 -new v2.1.3
```
