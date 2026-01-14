# Bazel Workflows

Specifications for Bazel multi-language project workflows.

## Overview

This specification defines the requirements for Bazel workflows that build, test, and release multi-language projects.

---

### Requirement: Bazel Multi-Language CI Workflow

The CI workflow SHALL build and test Bazel projects with configurable build configurations.

#### Scenario: Successful Bazel build

- **WHEN** a Bazel project triggers the CI workflow
- **THEN** the workflow SHALL run `bazel build`
- **AND** the workflow SHALL run `bazel test`
- **AND** all tests SHALL pass

#### Scenario: Multiple build configurations

- **WHEN** multiple Bazel configs are specified via `bazel-configs` input
- **THEN** the workflow SHALL build with each configuration
- **AND** results SHALL be reported for each configuration

#### Scenario: Bazel config validation

- **WHEN** custom Bazel configs are provided
- **THEN** the configs SHALL be validated against allowlist `^[a-z,_-]+$`
- **AND** invalid configs SHALL cause the workflow to fail

#### Scenario: Bazel cache usage

- **WHEN** running Bazel builds
- **THEN** the workflow SHALL cache Bazel outputs
- **AND** subsequent builds SHALL reuse cached artifacts

---

### Requirement: Bazel Multi-Language Release Workflow

The release workflow SHALL create releases for Bazel projects.

#### Scenario: Release artifact creation

- **WHEN** a Bazel release is triggered
- **THEN** the workflow SHALL build release targets
- **AND** artifacts SHALL be collected from Bazel outputs

#### Scenario: Multi-target releases

- **WHEN** multiple targets are specified for release
- **THEN** the workflow SHALL build all specified targets
- **AND** all artifacts SHALL be included in the release

#### Scenario: Release packaging

- **WHEN** creating release packages
- **THEN** the workflow SHALL use `bazel build --config=release`
- **AND** artifacts SHALL be optimized for distribution

---

### Requirement: Bazel Buildifier Integration

Bazel workflows SHALL integrate with Buildifier for BUILD file formatting.

#### Scenario: Buildifier download verification

- **WHEN** downloading Buildifier binary
- **THEN** the download SHALL be verified against SHA256 checksum
- **AND** the workflow SHALL fail if checksum does not match

#### Scenario: BUILD file formatting check

- **WHEN** CI runs on Bazel projects
- **THEN** BUILD files SHALL be checked for proper formatting
- **AND** improperly formatted files SHALL cause a warning
