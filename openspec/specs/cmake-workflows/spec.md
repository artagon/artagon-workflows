# CMake Workflows

Specifications for CMake C/C++ project workflows.

## Overview

This specification defines the requirements for CMake workflows that build, test, and release C and C++ projects.

---

### Requirement: CMake C CI Workflow

The C CI workflow SHALL build and test C projects using CMake.

#### Scenario: Successful C build

- **WHEN** a C project triggers the CI workflow
- **THEN** the workflow SHALL configure with CMake
- **AND** the workflow SHALL build the project
- **AND** the workflow SHALL run CTest

#### Scenario: Multi-platform build

- **WHEN** cross-platform builds are requested
- **THEN** the workflow SHALL build on Linux, macOS, and Windows
- **AND** each platform SHALL use appropriate compilers

#### Scenario: CMake options validation

- **WHEN** custom CMake options are provided via `cmake-options` input
- **THEN** the options SHALL be validated against allowlist
- **AND** invalid options SHALL cause the workflow to fail

---

### Requirement: CMake C++ CI Workflow

The C++ CI workflow SHALL build and test C++ projects using CMake.

#### Scenario: Successful C++ build

- **WHEN** a C++ project triggers the CI workflow
- **THEN** the workflow SHALL configure with CMake
- **AND** the workflow SHALL build with C++ compiler
- **AND** the workflow SHALL run tests

#### Scenario: C++ standard selection

- **WHEN** a specific C++ standard is required
- **THEN** the workflow SHALL support C++11, C++14, C++17, C++20, and C++23
- **AND** the standard SHALL be configurable via input

---

### Requirement: CMake C Release Workflow

The C release workflow SHALL create releases for C projects.

#### Scenario: Release artifact creation

- **WHEN** a C release is triggered
- **THEN** the workflow SHALL build release binaries
- **AND** binaries SHALL be optimized for production

#### Scenario: Release packaging

- **WHEN** creating release artifacts
- **THEN** the workflow SHALL create tarballs and zip archives
- **AND** artifacts SHALL include license and readme files

---

### Requirement: CMake C++ Release Workflow

The C++ release workflow SHALL create releases for C++ projects.

#### Scenario: Release artifact creation

- **WHEN** a C++ release is triggered
- **THEN** the workflow SHALL build release binaries
- **AND** binaries SHALL be built with release configuration

#### Scenario: Multi-platform releases

- **WHEN** releasing C++ projects
- **THEN** releases SHALL be built for multiple platforms
- **AND** platform-specific binaries SHALL be clearly labeled

---

### Requirement: CMake CPack Release Workflow

The CPack workflow SHALL create distribution packages using CPack.

#### Scenario: DEB package generation

- **WHEN** DEB packaging is enabled
- **THEN** the workflow SHALL generate Debian packages
- **AND** packages SHALL include proper metadata

#### Scenario: RPM package generation

- **WHEN** RPM packaging is enabled
- **THEN** the workflow SHALL generate RPM packages
- **AND** packages SHALL include proper metadata

#### Scenario: TGZ archive generation

- **WHEN** TGZ packaging is enabled
- **THEN** the workflow SHALL generate compressed tarballs
- **AND** archives SHALL be portable across systems
