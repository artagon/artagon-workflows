# Workflow Security

Security requirements for all GitHub Actions workflows in the Artagon ecosystem.

## Overview

This specification defines the security requirements that ALL workflows in this repository MUST follow. These requirements are based on OSSF guidelines, supply chain security best practices, and lessons learned from real-world attacks.

---

### Requirement: Action Pinning

ALL GitHub Actions MUST be pinned to immutable commit SHAs to prevent supply chain attacks.

#### Scenario: Action pinned to commit SHA

- **WHEN** a workflow uses a GitHub Action
- **THEN** the action MUST be referenced by its full 40-character commit SHA
- **AND** a comment with the semantic version MUST appear above the action reference

#### Scenario: Mutable tags rejected

- **WHEN** a workflow references an action using a mutable tag (e.g., `@v4`, `@main`, `@master`)
- **THEN** the workflow SHALL fail security validation
- **AND** the reviewer SHALL reject the PR

---

### Requirement: Explicit Permissions

ALL jobs MUST declare explicit permissions following the principle of least privilege.

#### Scenario: CI job permissions

- **WHEN** a CI job only needs to read code and run tests
- **THEN** the job SHALL declare `permissions: { contents: read }`
- **AND** no write permissions SHALL be granted

#### Scenario: Release job permissions

- **WHEN** a release job needs to publish artifacts or create tags
- **THEN** the job SHALL declare only the specific write permissions needed
- **AND** all other permissions SHALL remain at read or none

#### Scenario: Security scanning permissions

- **WHEN** a security scanning job uploads SARIF results
- **THEN** the job SHALL declare `security-events: write`
- **AND** contents SHALL be read-only

---

### Requirement: Input Validation

ALL user-controlled inputs MUST be validated before use in shell commands to prevent command injection.

#### Scenario: Input validation before shell execution

- **WHEN** a workflow receives user input that will be used in a shell command
- **THEN** the input MUST be validated against an allowlist pattern
- **AND** the validation MUST occur before any shell execution

#### Scenario: Invalid input rejection

- **WHEN** user input contains characters outside the allowlist
- **THEN** the workflow SHALL exit with error code 1
- **AND** the error message SHALL explain which characters are disallowed

#### Scenario: Safe input patterns

- **WHEN** validating Maven arguments
- **THEN** the allowlist pattern SHALL be `^[-A-Za-z0-9=.,_:/ ]*$`
- **WHEN** validating version strings
- **THEN** the allowlist pattern SHALL be `^[0-9]+\.[0-9]+\.[0-9]+(-[a-zA-Z0-9]+)?$`

---

### Requirement: Secret Handling

Secrets MUST NEVER be exposed in logs, process arguments, or error messages.

#### Scenario: GPG passphrase handling

- **WHEN** a workflow needs to use a GPG passphrase
- **THEN** the passphrase SHALL be written to a config file (e.g., `settings.xml`)
- **AND** the config file SHALL have permissions 600
- **AND** the passphrase SHALL NOT appear in command-line arguments

#### Scenario: Secret masking

- **WHEN** a secret value may appear in output
- **THEN** the workflow SHALL use `::add-mask::` to prevent exposure
- **AND** the secret SHALL NOT be echoed directly

#### Scenario: Environment variable secrets

- **WHEN** passing secrets to a step
- **THEN** the secret SHALL be passed via the `env:` block
- **AND** referenced using shell variable syntax (e.g., `$GPG_PASSPHRASE`)

---

### Requirement: Binary Download Verification

ALL binary downloads MUST be verified using checksums.

#### Scenario: Checksum verification

- **WHEN** a workflow downloads a binary from an external source
- **THEN** the download MUST be verified against a known SHA256 checksum
- **AND** the workflow SHALL fail if the checksum does not match

#### Scenario: HTTPS enforcement

- **WHEN** downloading files from external sources
- **THEN** the URL MUST use HTTPS
- **AND** HTTP URLs SHALL be rejected

#### Scenario: Version pinning

- **WHEN** downloading a tool binary
- **THEN** the version SHALL be explicitly specified
- **AND** floating versions (e.g., `latest`) SHALL NOT be used

---

### Requirement: Workflow Naming Convention

Workflows MUST follow a consistent naming convention for discoverability and organization.

#### Scenario: Build system prefix

- **WHEN** creating a new workflow
- **THEN** the filename SHALL start with the build system name (e.g., `maven_`, `cmake_`, `bazel_`)

#### Scenario: Language suffix for multi-language systems

- **WHEN** a build system supports multiple languages
- **THEN** the language SHALL be included in the filename (e.g., `cmake_c_ci.yml`, `cmake_cpp_ci.yml`)

#### Scenario: Category suffix

- **WHEN** naming a workflow
- **THEN** the filename SHALL end with the workflow category (e.g., `_ci.yml`, `_release.yml`, `_security_scan.yml`)

---

### Requirement: Dependabot Configuration

The repository MUST have Dependabot configured to automatically update action versions.

#### Scenario: GitHub Actions updates

- **WHEN** a new version of a pinned action is released
- **THEN** Dependabot SHALL create a PR to update the commit SHA
- **AND** the PR SHALL include the changelog

#### Scenario: Weekly schedule

- **WHEN** checking for action updates
- **THEN** Dependabot SHALL run on a weekly schedule
- **AND** updates SHALL be grouped by action owner

---

### Requirement: Security Validation in CI

The repository MUST have automated security validation for all workflow changes.

#### Scenario: Unpinned action detection

- **WHEN** a PR modifies workflow files
- **THEN** CI SHALL check for unpinned actions
- **AND** the PR SHALL fail if unpinned actions are found

#### Scenario: Missing permissions detection

- **WHEN** a PR modifies workflow files
- **THEN** CI SHALL check for missing permissions blocks
- **AND** a warning SHALL be issued for missing permissions

#### Scenario: actionlint validation

- **WHEN** a PR modifies workflow files
- **THEN** CI SHALL run actionlint
- **AND** the PR SHALL fail if actionlint reports errors
