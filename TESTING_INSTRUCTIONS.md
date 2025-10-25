# Comprehensive Workflow Testing Guide

This guide explains how to exercise the reusable workflows in this repository against real open-source projects across Maven/Java, C (CMake), C++ (CMake), Bazel (multi-language), and Rust (Cargo) so that we maintain confidence in cross-language coverage.

---

## 1. Test Matrix Overview

| Language / Build Tool | Recommended Workflow | Sample Open-Source Project | Notes |
|-----------------------|----------------------|-----------------------------|-------|
| Java (Maven)          | `.github/workflows/maven_ci.yml`<br>`.github/workflows/maven_release.yml` | [spring-projects/spring-petclinic](https://github.com/spring-projects/spring-petclinic) | Comprehensive Maven app with unit & integration tests. |
| C (CMake)             | `.github/workflows/cmake_c_ci.yml`<br>`.github/workflows/cmake_c_release.yml` | [curl/curl](https://github.com/curl/curl) | Mature CMake build; good for sanitizer coverage. |
| C++ (CMake)           | `.github/workflows/cmake_cpp_ci.yml`<br>`.github/workflows/cmake_cpp_release.yml` | [fmtlib/fmt](https://github.com/fmtlib/fmt) | Actively maintained C++ project with CI expectations. |
| Bazel (Polyglot)      | `.github/workflows/bazel_multi_ci.yml` | [google/flatbuffers](https://github.com/google/flatbuffers) | Bazel build targets in multiple languages. |
| Rust (Cargo)          | `.github/workflows/maven_security_scan.yml` (for Java security baseline)<br>`examples/rust` (planned) | [rust-lang/rustlings](https://github.com/rust-lang/rustlings) | Use as future fixture when Rust workflows land. |

> **Tip:** Fork each project under the Artagon organization (or a designated test org) so we can add workflow references without affecting upstream repositories.

---

## 2. Preparing Test Repositories

1. **Fork projects** into `github.com/artagon-test-labs/<project>`.
2. **Add reusable workflow references** in each fork:
   ```yaml
   # .github/workflows/ci.yml in the fork
   name: CI
   on:
     pull_request:
     push:
       branches: [main]
   jobs:
     main:
       uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@<ref>
       secrets: inherit
       with:
         java-version: '21'
   ```
   Replace the workflow path and inputs based on build system.
3. **Pin to the commit SHA** of the branch you are validating (e.g., `@<current-sha>`), not just `@main`.

---

## 3. Test Plan per Workflow Group

### Maven Workflows
1. In the forked Maven project, create a branch `workflow-test/maven-ci`.
2. Update the local CI pipeline to consume `maven_ci.yml`.
3. Trigger workflow runs:
   - Push commits (build + tests).
   - Enable code coverage upload if relevant.
4. Validate success:
   - Build/test steps complete.
   - Input validation fails gracefully if unsafe `maven-args` is supplied.
   - Permissions remain least-privilege (no unexpected repository write).
5. Repeat for release flows using dry-run tags (use `workflow_dispatch` with `maven_release.yml` against staging artifacts).

### C / C++ (CMake) Workflows
1. Configure project to use `cmake_c_ci.yml` or `cmake_cpp_ci.yml`.
2. Run matrices for Linux, macOS, Windows, sanitizers, and coverage.
3. Confirm:
   - Input validation catches malformed `cmake-options`.
   - Artifacts upload successfully.
   - Sanitizer jobs surface failures if you intentionally introduce defects.
4. For release workflows, create a temporary tag (e.g., `v0-test`) and ensure release artifacts upload to GitHub releases (use a sandbox repo to avoid polluting production).

### Bazel Multi-Language Workflow
1. Integrate `bazel_multi_ci.yml` into a Bazel project fork.
2. Trigger runs with multiple configs (`release,debug,asan,tsan`).
3. Confirm:
   - Input validation rejects unsafe target strings.
   - Cache usage and coverage steps behave across Nix/non-Nix paths.
   - Buildifier and bazel query jobs produce expected artifacts.

### Rust Workflows (Future)
1. Once reusable Rust workflows ship, mirror the process using a Cargo-based project (e.g., `rust-lang/rustlings`).
2. Ensure coverage, clippy, and security scanning run as planned.

---

## 4. Automating Regression Runs

- Create a GitHub Actions workflow inside **this repository** (`.github/workflows/fixture-smoke-tests.yml`) that:
  1. Checks out a test matrix file (YAML) listing `<repository, workflow-file, ref>`.
  2. Uses `gh workflow run` to trigger the reusable workflow in each fork.
  3. Polls run status via GitHub CLI and reports pass/fail in a summary table.
- Schedule the automation weekly and on demand (manual dispatch).

---

## 5. Documenting Results

For each test cycle:
1. Record the workflow SHAs tested and repository commit SHA.
2. Capture outcomes (success/failure, failure logs).
3. File issues or PRs in this repo when failures trace back to workflow changes.
4. Update this document with new fixtures or language support as additional workflows are introduced (e.g., Cargo release, Python).

---

Maintaining an external grid of real-world fixtures ensures our reusable workflows stay production-ready across languages. Integrate this testing cadence into release criteria for any workflow changes.***
