# CPack Packaging Guide

This guide explains how to configure CPack in your CMake project to generate packages (DEB, RPM, TGZ, etc.) using the `cmake_cpack_release.yml` workflow.

## Table of Contents

- [Overview](#overview)
- [Quick Start](#quick-start)
- [CPack Configuration](#cpack-configuration)
- [Package Formats](#package-formats)
- [Workflow Usage](#workflow-usage)
- [Advanced Configuration](#advanced-configuration)
- [Examples](#examples)

---

## Overview

CPack is CMake's built-in packaging system that can generate multiple package formats from a single configuration. The `cmake_cpack_release.yml` workflow automates building, verifying, signing, and publishing these packages.

### Supported Package Formats

- **DEB** - Debian/Ubuntu packages (.deb)
- **RPM** - Fedora/RHEL/CentOS packages (.rpm)
- **TGZ** - Gzipped tarballs (.tar.gz)
- **TBZ2** - Bzip2 tarballs (.tar.bz2)
- **TXZ** - XZ tarballs (.tar.xz)
- **ZIP** - Zip archives (.zip)
- **STGZ** - Self-extracting gzipped tarballs

---

## Quick Start

### 1. Add CPack Configuration to CMakeLists.txt

Add this to the end of your root `CMakeLists.txt`:

```cmake
# CPack configuration
set(CPACK_PACKAGE_NAME "${PROJECT_NAME}")
set(CPACK_PACKAGE_VERSION "${PROJECT_VERSION}")
set(CPACK_PACKAGE_DESCRIPTION_SUMMARY "Your project description")
set(CPACK_PACKAGE_VENDOR "Your Organization")
set(CPACK_PACKAGE_CONTACT "Your Name <your.email@example.com>")

# Include CPack
include(CPack)
```

### 2. Create Workflow File

Create `.github/workflows/release.yml`:

```yaml
name: Release Packages

on:
  push:
    tags:
      - 'v*'

jobs:
  package:
    uses: artagon/artagon-workflows/.github/workflows/cmake_cpack_release.yml@main
    with:
      package-formats: 'DEB;RPM;TGZ'
      sign-packages: true
    secrets:
      gpg-private-key: ${{ secrets.GPG_PRIVATE_KEY }}
      gpg-passphrase: ${{ secrets.GPG_PASSPHRASE }}
```

### 3. Trigger Release

```bash
git tag -a v1.0.0 -m "Release 1.0.0"
git push origin v1.0.0
```

---

## CPack Configuration

### Basic Configuration

```cmake
# Project metadata
set(CPACK_PACKAGE_NAME "myproject")
set(CPACK_PACKAGE_VERSION "1.0.0")
set(CPACK_PACKAGE_DESCRIPTION_SUMMARY "A brief description of your project")
set(CPACK_PACKAGE_DESCRIPTION_FILE "${CMAKE_CURRENT_SOURCE_DIR}/README.md")
set(CPACK_PACKAGE_VENDOR "Your Organization")
set(CPACK_PACKAGE_CONTACT "Maintainer <maintainer@example.com>")

# License
set(CPACK_RESOURCE_FILE_LICENSE "${CMAKE_CURRENT_SOURCE_DIR}/LICENSE")
set(CPACK_RESOURCE_FILE_README "${CMAKE_CURRENT_SOURCE_DIR}/README.md")

# Package naming
set(CPACK_PACKAGE_FILE_NAME "${CPACK_PACKAGE_NAME}-${CPACK_PACKAGE_VERSION}-${CMAKE_SYSTEM_PROCESSOR}")

# Include CPack module
include(CPack)
```

### DEB-Specific Configuration

```cmake
# Debian package configuration
set(CPACK_DEBIAN_PACKAGE_MAINTAINER "Your Name <your.email@example.com>")
set(CPACK_DEBIAN_PACKAGE_SECTION "libs")
set(CPACK_DEBIAN_PACKAGE_PRIORITY "optional")
set(CPACK_DEBIAN_PACKAGE_HOMEPAGE "https://github.com/yourorg/myproject")

# Dependencies
set(CPACK_DEBIAN_PACKAGE_DEPENDS "libc6 (>= 2.34)")

# Library package naming (includes soversion)
set(CPACK_DEBIAN_PACKAGE_NAME "libmyproject1")

# Automatic dependency detection
set(CPACK_DEBIAN_PACKAGE_SHLIBDEPS ON)

# Package architecture (auto-detected)
set(CPACK_DEBIAN_PACKAGE_ARCHITECTURE "amd64")

# Control scripts (optional)
set(CPACK_DEBIAN_PACKAGE_CONTROL_EXTRA
    "${CMAKE_CURRENT_SOURCE_DIR}/debian/postinst"
    "${CMAKE_CURRENT_SOURCE_DIR}/debian/prerm"
)
```

### RPM-Specific Configuration

```cmake
# RPM package configuration
set(CPACK_RPM_PACKAGE_GROUP "Development/Libraries")
set(CPACK_RPM_PACKAGE_LICENSE "MIT")
set(CPACK_RPM_PACKAGE_URL "https://github.com/yourorg/myproject")
set(CPACK_RPM_PACKAGE_DESCRIPTION "Detailed description of your project.")

# Dependencies
set(CPACK_RPM_PACKAGE_REQUIRES "glibc >= 2.34")

# Build requirements
set(CPACK_RPM_PACKAGE_BUILD_REQUIRES "cmake >= 3.20, gcc")

# Automatic dependency detection
set(CPACK_RPM_PACKAGE_AUTOREQ ON)
set(CPACK_RPM_PACKAGE_AUTOPROV ON)

# Package architecture
set(CPACK_RPM_PACKAGE_ARCHITECTURE "x86_64")

# Changelog
set(CPACK_RPM_CHANGELOG_FILE "${CMAKE_CURRENT_SOURCE_DIR}/ChangeLog")

# Post-install scripts (optional)
set(CPACK_RPM_POST_INSTALL_SCRIPT_FILE "${CMAKE_CURRENT_SOURCE_DIR}/rpm/postinstall.sh")
set(CPACK_RPM_PRE_UNINSTALL_SCRIPT_FILE "${CMAKE_CURRENT_SOURCE_DIR}/rpm/preuninstall.sh")
```

### Component-Based Packaging

For splitting into runtime and development packages:

```cmake
# Enable component packaging
set(CPACK_COMPONENTS_ALL runtime development)

# Component descriptions
set(CPACK_COMPONENT_RUNTIME_DISPLAY_NAME "Runtime Libraries")
set(CPACK_COMPONENT_RUNTIME_DESCRIPTION "Shared libraries for myproject")
set(CPACK_COMPONENT_DEVELOPMENT_DISPLAY_NAME "Development Files")
set(CPACK_COMPONENT_DEVELOPMENT_DESCRIPTION "Headers and static libraries")

# Dependencies between components
set(CPACK_COMPONENT_DEVELOPMENT_DEPENDS runtime)

# DEB component packaging
set(CPACK_DEB_COMPONENT_INSTALL ON)
set(CPACK_DEBIAN_RUNTIME_PACKAGE_NAME "libmyproject1")
set(CPACK_DEBIAN_DEVELOPMENT_PACKAGE_NAME "libmyproject-dev")

# RPM component packaging
set(CPACK_RPM_COMPONENT_INSTALL ON)
set(CPACK_RPM_RUNTIME_PACKAGE_NAME "myproject-libs")
set(CPACK_RPM_DEVELOPMENT_PACKAGE_NAME "myproject-devel")

# Install directives with components
install(TARGETS myproject
    RUNTIME DESTINATION bin COMPONENT runtime
    LIBRARY DESTINATION lib COMPONENT runtime
    ARCHIVE DESTINATION lib COMPONENT development
)

install(DIRECTORY include/
    DESTINATION include
    COMPONENT development
)
```

---

## Package Formats

### DEB Packages (Debian/Ubuntu)

**Generated file:** `myproject-1.0.0-x86_64.deb`

**Features:**
- Automatic shared library dependency detection (if `CPACK_DEBIAN_PACKAGE_SHLIBDEPS=ON`)
- Supports pre/post install scripts
- Generates proper library package names (e.g., `libmyproject1`)
- Includes control file metadata

**Best for:** Debian, Ubuntu, Linux Mint, Pop!_OS

### RPM Packages (Fedora/RHEL/CentOS)

**Generated file:** `myproject-1.0.0.x86_64.rpm`

**Features:**
- Automatic dependency detection (if `AUTOREQ=ON`)
- Supports scriptlets (%post, %pre, %preun, %postun)
- Proper library naming (e.g., `myproject-libs`, `myproject-devel`)
- Includes spec file metadata

**Best for:** Fedora, RHEL, CentOS, Rocky Linux, AlmaLinux, openSUSE

### TGZ/TBZ2/TXZ Archives

**Generated files:**
- `myproject-1.0.0-x86_64.tar.gz`
- `myproject-1.0.0-x86_64.tar.bz2`
- `myproject-1.0.0-x86_64.tar.xz`

**Features:**
- Universal source distribution
- Includes all installed files
- Can be extracted anywhere
- No dependency management

**Best for:** Alpine Linux, Arch Linux, source distributions, manual installation

---

## Workflow Usage

### Basic Usage

```yaml
name: Release

on:
  push:
    tags:
      - 'v*'

jobs:
  package:
    uses: artagon/artagon-workflows/.github/workflows/cmake_cpack_release.yml@main
    with:
      package-formats: 'DEB;RPM;TGZ'
```

### With GPG Signing

```yaml
jobs:
  package:
    uses: artagon/artagon-workflows/.github/workflows/cmake_cpack_release.yml@main
    with:
      package-formats: 'DEB;RPM;TGZ'
      sign-packages: true
    secrets:
      gpg-private-key: ${{ secrets.GPG_PRIVATE_KEY }}
      gpg-passphrase: ${{ secrets.GPG_PASSPHRASE }}
```

### Custom CMake Options

```yaml
jobs:
  package:
    uses: artagon/artagon-workflows/.github/workflows/cmake_cpack_release.yml@main
    with:
      package-formats: 'DEB;RPM'
      cmake-options: '-DBUILD_SHARED_LIBS=ON -DBUILD_TESTING=OFF'
      build-type: 'Release'
```

### Disable Linting

```yaml
jobs:
  package:
    uses: artagon/artagon-workflows/.github/workflows/cmake_cpack_release.yml@main
    with:
      package-formats: 'DEB;RPM'
      run-linting: false
```

### Custom Release Tag

```yaml
jobs:
  package:
    uses: artagon/artagon-workflows/.github/workflows/cmake_cpack_release.yml@main
    with:
      package-formats: 'DEB;RPM'
      release-tag: 'v1.2.3'
```

---

## Advanced Configuration

### Multi-Architecture Builds

Create matrix builds for multiple architectures:

```yaml
jobs:
  package:
    strategy:
      matrix:
        arch: [x86_64, aarch64]
    uses: artagon/artagon-workflows/.github/workflows/cmake_cpack_release.yml@main
    with:
      package-formats: 'DEB;RPM'
      architecture: ${{ matrix.arch }}
```

### Version from Git Tag

Extract version from git tag automatically:

```cmake
# Get version from git tag
find_package(Git QUIET)
if(GIT_FOUND)
    execute_process(
        COMMAND ${GIT_EXECUTABLE} describe --tags --abbrev=0
        WORKING_DIRECTORY ${CMAKE_SOURCE_DIR}
        OUTPUT_VARIABLE GIT_TAG
        OUTPUT_STRIP_TRAILING_WHITESPACE
    )
    string(REGEX REPLACE "^v" "" PROJECT_VERSION "${GIT_TAG}")
endif()

project(myproject VERSION ${PROJECT_VERSION})
```

### Including Additional Files

```cmake
# Install documentation
install(FILES
    README.md
    LICENSE
    CHANGELOG.md
    DESTINATION share/doc/myproject
)

# Install man pages
install(FILES
    docs/myproject.1
    DESTINATION share/man/man1
)

# Install pkg-config file
configure_file(myproject.pc.in myproject.pc @ONLY)
install(FILES ${CMAKE_BINARY_DIR}/myproject.pc
    DESTINATION lib/pkgconfig
)

# Install CMake config
install(EXPORT myprojectTargets
    FILE myprojectTargets.cmake
    NAMESPACE myproject::
    DESTINATION lib/cmake/myproject
)
```

---

## Examples

### Complete C Library Example

```cmake
cmake_minimum_required(VERSION 3.20)
project(mylib VERSION 1.2.3 LANGUAGES C)

# Build library
add_library(mylib SHARED src/mylib.c)
set_target_properties(mylib PROPERTIES
    VERSION ${PROJECT_VERSION}
    SOVERSION 1
    PUBLIC_HEADER include/mylib.h
)

# Install targets
install(TARGETS mylib
    LIBRARY DESTINATION lib
    PUBLIC_HEADER DESTINATION include
)

# Install pkg-config file
configure_file(mylib.pc.in mylib.pc @ONLY)
install(FILES ${CMAKE_BINARY_DIR}/mylib.pc
    DESTINATION lib/pkgconfig
)

# CPack configuration
set(CPACK_PACKAGE_NAME "mylib")
set(CPACK_PACKAGE_VERSION "${PROJECT_VERSION}")
set(CPACK_PACKAGE_DESCRIPTION_SUMMARY "My C library for doing awesome things")
set(CPACK_PACKAGE_VENDOR "My Organization")
set(CPACK_PACKAGE_CONTACT "Maintainer <maintainer@example.com>")
set(CPACK_RESOURCE_FILE_LICENSE "${CMAKE_CURRENT_SOURCE_DIR}/LICENSE")

# DEB package
set(CPACK_DEBIAN_PACKAGE_MAINTAINER "Maintainer <maintainer@example.com>")
set(CPACK_DEBIAN_PACKAGE_SECTION "libs")
set(CPACK_DEBIAN_PACKAGE_NAME "libmylib1")
set(CPACK_DEBIAN_PACKAGE_SHLIBDEPS ON)
set(CPACK_DEBIAN_PACKAGE_HOMEPAGE "https://github.com/yourorg/mylib")

# RPM package
set(CPACK_RPM_PACKAGE_GROUP "System Environment/Libraries")
set(CPACK_RPM_PACKAGE_LICENSE "MIT")
set(CPACK_RPM_PACKAGE_URL "https://github.com/yourorg/mylib")
set(CPACK_RPM_PACKAGE_AUTOREQ ON)

include(CPack)
```

### Complete C++ Library with Components

```cmake
cmake_minimum_required(VERSION 3.20)
project(mylib VERSION 2.0.0 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)

# Build library
add_library(mylib src/mylib.cpp)
target_include_directories(mylib
    PUBLIC
        $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/include>
        $<INSTALL_INTERFACE:include>
)

# Install - Runtime component
install(TARGETS mylib
    EXPORT mylibTargets
    LIBRARY DESTINATION lib
        COMPONENT runtime
    ARCHIVE DESTINATION lib
        COMPONENT development
    INCLUDES DESTINATION include
)

# Install - Development component
install(DIRECTORY include/
    DESTINATION include
    COMPONENT development
)

install(EXPORT mylibTargets
    FILE mylibTargets.cmake
    NAMESPACE mylib::
    DESTINATION lib/cmake/mylib
    COMPONENT development
)

# CPack configuration
set(CPACK_PACKAGE_NAME "mylib")
set(CPACK_PACKAGE_VERSION "${PROJECT_VERSION}")
set(CPACK_PACKAGE_DESCRIPTION_SUMMARY "Modern C++ library")
set(CPACK_PACKAGE_VENDOR "My Organization")
set(CPACK_PACKAGE_CONTACT "Dev Team <dev@example.com>")
set(CPACK_RESOURCE_FILE_LICENSE "${CMAKE_CURRENT_SOURCE_DIR}/LICENSE")

# Component packaging
set(CPACK_COMPONENTS_ALL runtime development)
set(CPACK_COMPONENT_RUNTIME_DISPLAY_NAME "Runtime Libraries")
set(CPACK_COMPONENT_DEVELOPMENT_DISPLAY_NAME "Development Files")
set(CPACK_COMPONENT_DEVELOPMENT_DEPENDS runtime)

# DEB packages
set(CPACK_DEB_COMPONENT_INSTALL ON)
set(CPACK_DEBIAN_RUNTIME_PACKAGE_NAME "libmylib2")
set(CPACK_DEBIAN_DEVELOPMENT_PACKAGE_NAME "libmylib-dev")
set(CPACK_DEBIAN_RUNTIME_PACKAGE_SECTION "libs")
set(CPACK_DEBIAN_DEVELOPMENT_PACKAGE_SECTION "libdevel")
set(CPACK_DEBIAN_PACKAGE_SHLIBDEPS ON)

# RPM packages
set(CPACK_RPM_COMPONENT_INSTALL ON)
set(CPACK_RPM_RUNTIME_PACKAGE_NAME "mylib")
set(CPACK_RPM_DEVELOPMENT_PACKAGE_NAME "mylib-devel")
set(CPACK_RPM_RUNTIME_PACKAGE_GROUP "System Environment/Libraries")
set(CPACK_RPM_DEVELOPMENT_PACKAGE_GROUP "Development/Libraries")
set(CPACK_RPM_PACKAGE_LICENSE "Apache-2.0")

include(CPack)
```

---

## Verification

After building packages locally, verify them:

### DEB Package

```bash
# List contents
dpkg-deb -c mylib-1.2.3-x86_64.deb

# Show info
dpkg-deb -I mylib-1.2.3-x86_64.deb

# Lint
lintian mylib-1.2.3-x86_64.deb

# Test install
sudo dpkg -i mylib-1.2.3-x86_64.deb
```

### RPM Package

```bash
# List contents
rpm -qlp mylib-1.2.3.x86_64.rpm

# Show info
rpm -qip mylib-1.2.3.x86_64.rpm

# Check dependencies
rpm -qRp mylib-1.2.3.x86_64.rpm

# Lint
rpmlint mylib-1.2.3.x86_64.rpm

# Test install
sudo rpm -ivh mylib-1.2.3.x86_64.rpm
```

---

## Troubleshooting

### "No such file or directory" errors

**Cause:** Files not installed properly

**Solution:** Add proper `install()` directives in CMakeLists.txt

### Empty packages

**Cause:** Missing install targets

**Solution:** Ensure all libraries/executables have `install()` commands

### Wrong package architecture

**Cause:** CPack auto-detection issue

**Solution:** Set explicitly:
```cmake
set(CPACK_DEBIAN_PACKAGE_ARCHITECTURE "amd64")
set(CPACK_RPM_PACKAGE_ARCHITECTURE "x86_64")
```

### Lintian/rpmlint errors

**Cause:** Missing metadata or improper file permissions

**Solution:** Review package guidelines:
- Debian Policy: https://www.debian.org/doc/debian-policy/
- RPM Packaging Guide: https://rpm-packaging-guide.github.io/

---

## References

- [CPack Documentation](https://cmake.org/cmake/help/latest/module/CPack.html)
- [CPack DEB Generator](https://cmake.org/cmake/help/latest/cpack_gen/deb.html)
- [CPack RPM Generator](https://cmake.org/cmake/help/latest/cpack_gen/rpm.html)
- [Debian Policy Manual](https://www.debian.org/doc/debian-policy/)
- [RPM Packaging Guide](https://rpm-packaging-guide.github.io/)
