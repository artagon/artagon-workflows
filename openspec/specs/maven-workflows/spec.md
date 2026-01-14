# Maven Workflows

Specifications for Maven/Java project workflows.

## Overview

This specification defines the requirements for Maven workflows that build, test, and release Java/JVM projects using Maven.

---

### Requirement: Maven CI Workflow

The CI workflow SHALL build and test Maven projects with configurable Java versions.

#### Scenario: Successful build with default Java version

- **WHEN** a Maven project triggers the CI workflow
- **THEN** the workflow SHALL build the project with Java 21 by default
- **AND** the workflow SHALL run `mvn verify`
- **AND** all tests SHALL pass

#### Scenario: Multi-version matrix testing

- **WHEN** a consumer specifies multiple Java versions
- **THEN** the workflow SHALL build against each version in the matrix
- **AND** results SHALL be reported for each version

#### Scenario: Maven argument validation

- **WHEN** custom Maven arguments are provided via `maven-args` input
- **THEN** the arguments SHALL be validated against allowlist `^[-A-Za-z0-9=.,_:/ ]*$`
- **AND** invalid arguments SHALL cause the workflow to fail

---

### Requirement: Maven Deploy Workflow

The deploy workflow SHALL deploy SNAPSHOT artifacts to OSSRH.

#### Scenario: SNAPSHOT deployment

- **WHEN** the deploy workflow runs on main branch
- **THEN** the workflow SHALL deploy SNAPSHOT versions to OSSRH
- **AND** GPG signing SHALL be used
- **AND** credentials SHALL be handled securely

#### Scenario: Deploy credentials

- **WHEN** deploying to OSSRH
- **THEN** OSSRH username and password SHALL be passed via environment variables
- **AND** credentials SHALL NOT appear in command-line arguments

---

### Requirement: Maven Release Workflow

The release workflow SHALL create releases with proper versioning and tagging.

#### Scenario: Release version removal

- **WHEN** releasing from a release branch
- **THEN** the workflow SHALL remove `-SNAPSHOT` from the version
- **AND** the pom.xml SHALL be committed with the release version

#### Scenario: Git tag creation

- **WHEN** a release is created
- **THEN** a git tag SHALL be created with format `v{version}`
- **AND** the tag SHALL be pushed to the remote

#### Scenario: GPG signing for releases

- **WHEN** deploying release artifacts
- **THEN** all artifacts SHALL be GPG signed
- **AND** the GPG passphrase SHALL be handled via settings.xml

---

### Requirement: Maven Central Release Workflow

The Maven Central release workflow SHALL deploy to Maven Central with proper attestation.

#### Scenario: Maven Central deployment

- **WHEN** deploying to Maven Central
- **THEN** the workflow SHALL use the Nexus staging plugin
- **AND** artifacts SHALL be staged, closed, and released

#### Scenario: SBOM generation

- **WHEN** releasing to Maven Central
- **THEN** an SBOM SHALL be generated
- **AND** the SBOM SHALL be attached to the release

#### Scenario: Attestation

- **WHEN** creating a Maven Central release
- **THEN** build provenance attestation SHALL be generated
- **AND** the attestation SHALL be signed

---

### Requirement: Maven Security Scan Workflow

The security scan workflow SHALL identify vulnerabilities in Maven dependencies.

#### Scenario: Dependency vulnerability scan

- **WHEN** the security scan workflow runs
- **THEN** dependencies SHALL be scanned for known vulnerabilities
- **AND** results SHALL be uploaded to GitHub Security tab

#### Scenario: SARIF upload

- **WHEN** vulnerabilities are found
- **THEN** results SHALL be uploaded in SARIF format
- **AND** the workflow SHALL require `security-events: write` permission

---

### Requirement: Maven SBOM Generation Workflow

The SBOM workflow SHALL generate Software Bill of Materials for Maven projects.

#### Scenario: CycloneDX SBOM generation

- **WHEN** the SBOM workflow runs
- **THEN** a CycloneDX SBOM SHALL be generated
- **AND** the SBOM SHALL include all dependencies

#### Scenario: SBOM artifact upload

- **WHEN** an SBOM is generated
- **THEN** the SBOM SHALL be uploaded as a workflow artifact
- **AND** the SBOM SHALL be attached to GitHub releases

---

### Requirement: Maven Version Bump Workflow

The version bump workflow SHALL increment project versions.

#### Scenario: Version increment

- **WHEN** the version bump workflow runs
- **THEN** the version in pom.xml SHALL be incremented
- **AND** the new version SHALL have `-SNAPSHOT` suffix

#### Scenario: Commit and push

- **WHEN** a version bump completes
- **THEN** the changes SHALL be committed
- **AND** the commit SHALL be pushed to the branch
