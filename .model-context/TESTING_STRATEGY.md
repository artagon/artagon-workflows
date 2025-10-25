# Workflow Testing Strategy

## Overview

This document defines the testing strategy for artagon-workflows, ensuring all reusable workflows are validated before deployment and remain reliable over time.

**Goal**: Achieve 100% workflow validation coverage with automated testing.

---

## Testing Philosophy

### Core Principles

1. **Test in Isolation**: Each workflow must be testable independently
2. **Test in Integration**: Workflows must be tested with real projects
3. **Fail Fast**: Tests should catch issues before they reach consumers
4. **Automated**: All tests run automatically on PR and main branch
5. **Documented**: Test failures must provide actionable error messages

### Testing Pyramid

```
        ┌─────────────────┐
        │   E2E Tests     │  Integration with real projects
        │   (Quarterly)   │
        ├─────────────────┤
        │ Integration     │  Test matrix combinations
        │ Tests (Weekly)  │
        ├─────────────────┤
        │  Workflow       │  Syntax, schema validation
        │  Unit Tests     │  (on every commit)
        │  (Immediate)    │
        └─────────────────┘
```

---

## Test Levels

### Level 1: Syntax and Schema Validation

**Purpose**: Catch YAML syntax errors and schema violations
**Frequency**: Every commit
**Tool**: `actionlint`, `yamllint`

**Implementation**:

Create `.github/workflows/test_lint.yml`:

```yaml
name: Workflow Linting

on:
  push:
    branches: [main]
    paths:
      - '.github/workflows/*.yml'
  pull_request:
    paths:
      - '.github/workflows/*.yml'

jobs:
  actionlint:
    name: Lint Workflows
    runs-on: ubuntu-latest
    permissions:
      contents: read

    steps:
      - name: Checkout
        uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683

      - name: Download actionlint
        run: |
          bash <(curl https://raw.githubusercontent.com/rhysd/actionlint/main/scripts/download-actionlint.bash)

      - name: Run actionlint
        run: ./actionlint -color -verbose

  yamllint:
    name: YAML Validation
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683

      - name: Install yamllint
        run: pip install yamllint

      - name: Run yamllint
        run: yamllint .github/workflows/
```

**Expected Catches**:
- Invalid YAML syntax
- Incorrect action usage
- Missing required inputs
- Type mismatches
- Deprecated syntax

---

### Level 2: Workflow Unit Tests

**Purpose**: Validate individual workflow behavior
**Frequency**: On every PR
**Tool**: Custom test workflows

**Implementation**:

Create test fixtures for each workflow type in `test/fixtures/`:

```
test/
├── fixtures/
│   ├── maven/
│   │   ├── pom.xml              # Minimal Maven project
│   │   └── src/test/java/...   # Simple test
│   ├── cmake_c/
│   │   ├── CMakeLists.txt       # Minimal C project
│   │   └── src/main.c
│   ├── cmake_cpp/
│   │   ├── CMakeLists.txt       # Minimal C++ project
│   │   └── src/main.cpp
│   ├── bazel/
│   │   ├── WORKSPACE
│   │   ├── BUILD.bazel
│   │   └── src/main.cc
│   └── rust/
│       ├── Cargo.toml
│       └── src/lib.rs
└── workflows/
    ├── test_maven_ci.yml
    ├── test_cmake_ci.yml
    ├── test_bazel_ci.yml
    └── ...
```

**Example Test Workflow** (`.github/workflows/test_maven_ci.yml`):

```yaml
name: Test Maven CI Workflow

on:
  pull_request:
    paths:
      - '.github/workflows/maven_ci.yml'
      - 'test/fixtures/maven/**'
  push:
    branches: [main]
    paths:
      - '.github/workflows/maven_ci.yml'

jobs:
  # Test with minimal configuration
  test-minimal:
    name: Test Minimal Configuration
    uses: ./.github/workflows/maven_ci.yml
    with:
      java-version: '21'
      run-tests: true
    permissions:
      contents: read

  # Test with custom options
  test-custom-options:
    name: Test Custom Maven Args
    uses: ./.github/workflows/maven_ci.yml
    with:
      java-version: '17'
      maven-args: '-DskipITs=true'
      run-tests: true

  # Test without tests
  test-skip-tests:
    name: Test Skip Tests
    uses: ./.github/workflows/maven_ci.yml
    with:
      run-tests: false

  # Verify test results
  verify:
    name: Verify Test Results
    needs: [test-minimal, test-custom-options, test-skip-tests]
    runs-on: ubuntu-latest
    if: always()

    steps:
      - name: Check test results
        run: |
          if [ "${{ needs.test-minimal.result }}" != "success" ]; then
            echo "❌ Minimal configuration test failed"
            exit 1
          fi
          if [ "${{ needs.test-custom-options.result }}" != "success" ]; then
            echo "❌ Custom options test failed"
            exit 1
          fi
          if [ "${{ needs.test-skip-tests.result }}" != "success" ]; then
            echo "❌ Skip tests configuration failed"
            exit 1
          fi
          echo "✅ All workflow tests passed"
```

---

### Level 3: Integration Matrix Tests

**Purpose**: Test workflow combinations and edge cases
**Frequency**: Weekly + on workflow changes
**Tool**: GitHub Actions matrix strategy

**Implementation**:

Create `.github/workflows/test_integration.yml`:

```yaml
name: Integration Tests

on:
  schedule:
    - cron: '0 2 * * 1'  # Every Monday at 2 AM
  pull_request:
    paths:
      - '.github/workflows/**'
  workflow_dispatch:

jobs:
  test-maven-matrix:
    name: Maven (${{ matrix.java }}, ${{ matrix.os }})
    strategy:
      fail-fast: false
      matrix:
        java: ['17', '21', '25']
        os: [ubuntu-latest, macos-latest, windows-latest]
        include:
          - java: '17'
            distribution: 'temurin'
          - java: '21'
            distribution: 'temurin'
          - java: '25'
            distribution: 'temurin'

    uses: ./.github/workflows/maven_ci.yml
    with:
      java-version: ${{ matrix.java }}
      java-distribution: ${{ matrix.distribution }}

  test-cmake-compilers:
    name: CMake C (${{ matrix.compiler }})
    strategy:
      fail-fast: false
      matrix:
        compiler: [gcc, clang]
        c-standard: ['11', '17', '23']

    uses: ./.github/workflows/cmake_c_ci.yml
    with:
      c-standard: ${{ matrix.c-standard }}

  test-bazel-configs:
    name: Bazel (${{ matrix.config }})
    strategy:
      matrix:
        config: ['release', 'debug', 'asan', 'tsan', 'ubsan']

    uses: ./.github/workflows/bazel_multi_ci.yml
    with:
      bazel-configs: ${{ matrix.config }}
```

**Expected Catches**:
- Platform-specific failures
- Version compatibility issues
- Configuration conflicts
- Race conditions

---

### Level 4: End-to-End Tests

**Purpose**: Validate workflows with real-world projects
**Frequency**: Quarterly (manual) + before major releases
**Tool**: External test repositories

**Implementation**:

Create separate test repositories that consume workflows:

```
artagon-workflows-test-maven/
  .github/workflows/ci.yml:
    uses: artagon/artagon-workflows/.github/workflows/maven_ci.yml@main

artagon-workflows-test-cmake/
  .github/workflows/ci.yml:
    uses: artagon/artagon-workflows/.github/workflows/cmake_c_ci.yml@main

artagon-workflows-test-bazel/
  .github/workflows/ci.yml:
    uses: artagon/artagon-workflows/.github/workflows/bazel_multi_ci.yml@main
```

**Test Scenarios**:
1. **Fresh Clone**: Clone and build from scratch
2. **Incremental Build**: Build with existing cache
3. **Dependency Changes**: Update dependencies and rebuild
4. **Release Process**: Full release cycle from branch to tag
5. **Hotfix**: Apply hotfix to release branch
6. **Multi-Module**: Test with multi-module projects
7. **Large Codebases**: Test with 10k+ files

---

## Security Testing

### Secret Scanning

Test that workflows don't leak secrets:

```yaml
name: Secret Scanning Test

on:
  pull_request:
    paths:
      - '.github/workflows/**'

jobs:
  test-secret-exposure:
    name: Verify No Secret Leakage
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683

      - name: Scan for hardcoded secrets
        run: |
          # Check for common secret patterns
          ! grep -rE '(password|token|key).*=.*["\x27][^"\x27]{10,}' .github/workflows/
          ! grep -rE 'ghp_[a-zA-Z0-9]{36}' .github/workflows/
          ! grep -rE 'AKIA[0-9A-Z]{16}' .github/workflows/

      - name: Verify secrets are from GitHub Secrets
        run: |
          # All secrets should use ${{ secrets.* }} syntax
          grep -r 'password\|token\|key' .github/workflows/ | while read line; do
            if ! echo "$line" | grep -q '\${{ secrets\.'; then
              echo "❌ Potential hardcoded secret: $line"
              exit 1
            fi
          done
```

### Permission Validation

Verify workflows use minimal permissions:

```yaml
name: Permission Audit

on:
  pull_request:
    paths:
      - '.github/workflows/**'

jobs:
  audit-permissions:
    name: Audit Workflow Permissions
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683

      - name: Check for permission blocks
        run: |
          for workflow in .github/workflows/*.yml; do
            if ! grep -q "permissions:" "$workflow"; then
              echo "⚠️  Missing permissions block: $workflow"
              echo "   Add explicit permissions to follow least-privilege principle"
            fi
          done

      - name: Verify no write-all permissions
        run: |
          if grep -r "permissions:.*write-all" .github/workflows/; then
            echo "❌ Found write-all permissions - use specific permissions instead"
            exit 1
          fi
```

### Action Pinning Validation

Verify all actions are pinned to commit SHAs:

```yaml
name: Action Pinning Validation

on:
  pull_request:
    paths:
      - '.github/workflows/**'

jobs:
  validate-pinning:
    name: Validate Action Pinning
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683

      - name: Check for unpinned actions
        run: |
          UNPINNED=$(grep -rn "uses:.*@v[0-9]" .github/workflows/ || true)
          UNPINNED_MAJOR=$(grep -rn "uses:.*@v[0-9]$" .github/workflows/ || true)
          UNPINNED_MASTER=$(grep -rn "uses:.*@master\|uses:.*@main" .github/workflows/ || true)

          if [ -n "$UNPINNED" ] || [ -n "$UNPINNED_MAJOR" ] || [ -n "$UNPINNED_MASTER" ]; then
            echo "❌ Found unpinned actions:"
            echo "$UNPINNED"
            echo "$UNPINNED_MAJOR"
            echo "$UNPINNED_MASTER"
            echo ""
            echo "All actions must be pinned to commit SHAs for security."
            echo "See ACTION_VERSIONS.md for reference."
            exit 1
          fi

          echo "✅ All actions are pinned to commit SHAs"
```

---

## Performance Testing

### Workflow Execution Time

Monitor workflow performance over time:

```yaml
name: Performance Benchmark

on:
  schedule:
    - cron: '0 0 * * 0'  # Weekly

jobs:
  benchmark-maven:
    name: Benchmark Maven CI
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683

      - name: Run Maven CI and measure time
        id: benchmark
        run: |
          START_TIME=$(date +%s)
          # Trigger workflow and wait for completion
          # (implementation depends on workflow dispatch)
          END_TIME=$(date +%s)
          DURATION=$((END_TIME - START_TIME))

          echo "duration=$DURATION" >> $GITHUB_OUTPUT

      - name: Record metrics
        run: |
          echo "Maven CI completed in ${{ steps.benchmark.outputs.duration }} seconds"
          # Send to monitoring system (e.g., CloudWatch, Datadog)

      - name: Check against baseline
        run: |
          BASELINE=300  # 5 minutes
          if [ ${{ steps.benchmark.outputs.duration }} -gt $BASELINE ]; then
            echo "⚠️  Workflow slower than baseline: ${{ steps.benchmark.outputs.duration }}s > ${BASELINE}s"
          fi
```

### Cache Effectiveness

Measure cache hit rates:

```yaml
name: Cache Effectiveness Test

on:
  schedule:
    - cron: '0 0 * * 1'

jobs:
  test-cache:
    name: Test Cache Hit Rate
    runs-on: ubuntu-latest

    steps:
      - name: First run (cold cache)
        uses: ./.github/workflows/maven_ci.yml

      - name: Second run (warm cache)
        uses: ./.github/workflows/maven_ci.yml

      - name: Analyze cache metrics
        run: |
          # Parse cache hit/miss from logs
          # Calculate hit rate percentage
          # Alert if below threshold (e.g., 80%)
          echo "Cache hit rate: XX%"
```

---

## Regression Testing

### Workflow Output Validation

Verify workflows produce expected outputs:

```yaml
name: Output Validation

jobs:
  test-maven-outputs:
    name: Validate Maven CI Outputs
    uses: ./.github/workflows/maven_ci.yml
    id: maven

  verify-outputs:
    name: Verify Outputs
    needs: test-maven-outputs
    runs-on: ubuntu-latest

    steps:
      - name: Verify version output
        run: |
          if [ -z "${{ needs.test-maven-outputs.outputs.version }}" ]; then
            echo "❌ Missing version output"
            exit 1
          fi
          echo "✅ Version output present: ${{ needs.test-maven-outputs.outputs.version }}"

      - name: Verify artifacts
        uses: actions/download-artifact@fa0a91b85d4f404e444e00e005971372dc801d16
        with:
          name: build-artifacts-*

      - name: Validate artifacts
        run: |
          if [ ! -f "target/*.jar" ]; then
            echo "❌ JAR artifact not found"
            exit 1
          fi
          echo "✅ Artifacts validated"
```

---

## Test Fixtures

### Minimal Test Projects

Each build system needs minimal test projects:

#### Maven Test Fixture

`test/fixtures/maven/pom.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.artagon.test</groupId>
    <artifactId>maven-test-fixture</artifactId>
    <version>1.0.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.0</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

`test/fixtures/maven/src/main/java/org/artagon/test/Hello.java`:
```java
package org.artagon.test;

public class Hello {
    public String greet() {
        return "Hello, World!";
    }
}
```

`test/fixtures/maven/src/test/java/org/artagon/test/HelloTest.java`:
```java
package org.artagon.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HelloTest {
    @Test
    void testGreet() {
        Hello hello = new Hello();
        assertEquals("Hello, World!", hello.greet());
    }
}
```

---

## Continuous Monitoring

### Workflow Health Dashboard

Create a dashboard to track:
- ✅ Success rate per workflow
- ⏱️ Average execution time
- 💾 Cache hit rate
- 🔄 Version update lag
- 🐛 Open issues per workflow

### Alerting

Set up alerts for:
- Workflow failure rate > 10%
- Execution time > 2x baseline
- Cache hit rate < 70%
- Security vulnerabilities in actions
- Unpinned actions introduced

---

## Testing Checklist

Before merging workflow changes:

- [ ] Syntax validation passes (actionlint, yamllint)
- [ ] Unit tests pass for affected workflows
- [ ] Integration matrix tests pass
- [ ] No hardcoded secrets detected
- [ ] All actions pinned to commit SHAs
- [ ] Permissions follow least-privilege
- [ ] Documentation updated
- [ ] Example workflows updated
- [ ] CHANGELOG.md updated
- [ ] Version bumped (if breaking change)

---

## Test Automation

### Pre-commit Hooks

Create `.pre-commit-config.yaml`:

```yaml
repos:
  - repo: https://github.com/rhysd/actionlint
    rev: v1.6.26
    hooks:
      - id: actionlint

  - repo: https://github.com/adrienverge/yamllint
    rev: v1.33.0
    hooks:
      - id: yamllint
        args: ['.github/workflows/']
```

### CI Pipeline

All tests must pass before merge:

```yaml
name: CI

on:
  pull_request:
  push:
    branches: [main]

jobs:
  lint:
    uses: ./.github/workflows/test_lint.yml

  unit-tests:
    needs: lint
    uses: ./.github/workflows/test_unit.yml

  integration-tests:
    needs: unit-tests
    uses: ./.github/workflows/test_integration.yml

  security-tests:
    needs: lint
    uses: ./.github/workflows/test_security.yml

  all-tests-passed:
    needs: [lint, unit-tests, integration-tests, security-tests]
    runs-on: ubuntu-latest
    if: always()
    steps:
      - name: Verify all tests passed
        run: |
          if [ "${{ needs.lint.result }}" != "success" ] || \
             [ "${{ needs.unit-tests.result }}" != "success" ] || \
             [ "${{ needs.integration-tests.result }}" != "success" ] || \
             [ "${{ needs.security-tests.result }}" != "success" ]; then
            echo "❌ Tests failed"
            exit 1
          fi
          echo "✅ All tests passed"
```

---

## Implementation Roadmap

### Phase 1: Foundation (Week 1-2)
- [ ] Create test fixtures for all build systems
- [ ] Implement syntax/schema validation
- [ ] Set up actionlint and yamllint

### Phase 2: Unit Tests (Week 3-4)
- [ ] Create unit test workflows for each reusable workflow
- [ ] Implement test verification jobs
- [ ] Document expected behaviors

### Phase 3: Integration Tests (Week 5-6)
- [ ] Create matrix test workflows
- [ ] Test all input combinations
- [ ] Set up weekly test schedule

### Phase 4: Security & Performance (Week 7-8)
- [ ] Implement secret scanning tests
- [ ] Add permission validation
- [ ] Set up action pinning validation
- [ ] Create performance benchmarks

### Phase 5: E2E & Monitoring (Week 9-10)
- [ ] Create external test repositories
- [ ] Set up health dashboard
- [ ] Configure alerting
- [ ] Document troubleshooting procedures

---

## Success Metrics

- **Coverage**: 100% of workflows have automated tests
- **Reliability**: <5% failure rate on main branch
- **Speed**: <10 minute average test execution
- **Security**: 0 unpinned actions, 0 hardcoded secrets
- **Adoption**: All consuming projects use pinned versions

---

## References

- [Testing GitHub Actions](https://docs.github.com/en/actions/creating-actions/testing-your-action)
- [actionlint](https://github.com/rhysd/actionlint)
- [act - Run Actions Locally](https://github.com/nektos/act)
- [GitHub Actions Best Practices](https://docs.github.com/en/actions/learn-github-actions/workflow-syntax-for-github-actions)

---

**Next Steps**: Implement Phase 1 (Foundation) immediately after approval.
