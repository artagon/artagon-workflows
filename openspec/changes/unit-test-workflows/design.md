# Design: Unit Test Workflows

## Test Structure

```
test/
├── workflows/
│   ├── test_maven_ci.yml
│   ├── test_cmake_c_ci.yml
│   ├── test_cmake_cpp_ci.yml
│   └── test_bazel_ci.yml
└── fixtures/
    ├── maven/
    │   └── simple-project/
    ├── cmake-c/
    │   └── simple-project/
    ├── cmake-cpp/
    │   └── simple-project/
    └── bazel/
        └── simple-project/
```

## Test Matrix

### Maven CI Tests

| Test | Java | Args | Skip Tests | Expected |
|------|------|------|------------|----------|
| minimal | 21 | - | false | JAR artifact |
| java-17 | 17 | - | false | JAR artifact |
| skip-tests | 21 | - | true | JAR (no test) |
| custom-args | 21 | -Dfoo=bar | false | JAR artifact |

### CMake C CI Tests

| Test | Standard | Compiler | Sanitizer | Expected |
|------|----------|----------|-----------|----------|
| minimal | 17 | gcc | none | Binary |
| c11 | 11 | gcc | none | Binary |
| c23 | 23 | clang | none | Binary |
| asan | 17 | gcc | address | Binary |

### CMake C++ CI Tests

| Test | Standard | Compiler | Sanitizer | Expected |
|------|----------|----------|-----------|----------|
| minimal | 20 | g++ | none | Binary |
| cpp17 | 17 | g++ | none | Binary |
| cpp23 | 23 | clang++ | none | Binary |
| ubsan | 20 | g++ | undefined | Binary |

### Bazel CI Tests

| Test | Config | Coverage | Expected |
|------|--------|----------|----------|
| minimal | release | false | Binaries |
| debug | debug | false | Binaries |
| coverage | release | true | Coverage report |
| asan | asan | false | Binaries |

## Test Workflow Template

```yaml
name: Test Maven CI

on:
  pull_request:
    paths:
      - '.github/workflows/maven_ci.yml'
      - 'test/workflows/test_maven_ci.yml'
  push:
    branches: [main]

jobs:
  test-minimal:
    uses: ./.github/workflows/maven_ci.yml
    with:
      java-version: '21'

  test-java-17:
    uses: ./.github/workflows/maven_ci.yml
    with:
      java-version: '17'

  verify-outputs:
    needs: [test-minimal, test-java-17]
    runs-on: ubuntu-latest
    steps:
      - name: Verify artifacts exist
        run: |
          # Check artifacts were produced
```

## Verification Steps

1. **Artifact Check**: Verify expected files exist
2. **Output Check**: Verify workflow outputs are set
3. **Exit Code**: Verify workflow succeeded
4. **Log Check**: Verify no unexpected errors
