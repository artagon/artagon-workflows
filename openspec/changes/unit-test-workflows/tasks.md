# Tasks: Unit Test Workflows

## 1. Maven CI Tests

- [ ] 1.1 Create `test/workflows/test_maven_ci.yml`
- [ ] 1.2 Create `test/fixtures/maven/simple-project/`
- [ ] 1.3 Test with Java 17, 21
- [ ] 1.4 Test with/without tests
- [ ] 1.5 Test with custom maven-args
- [ ] 1.6 Verify JAR artifact produced

**Acceptance**: Maven CI validated with various configs
**Status**: PENDING

## 2. CMake C CI Tests

- [ ] 2.1 Create `test/workflows/test_cmake_c_ci.yml`
- [ ] 2.2 Create `test/fixtures/cmake-c/simple-project/`
- [ ] 2.3 Test with C standards: 11, 17, 23
- [ ] 2.4 Test with custom cmake-options
- [ ] 2.5 Test sanitizers (asan, ubsan)
- [ ] 2.6 Verify build artifacts

**Acceptance**: CMake C CI validated
**Status**: PENDING

## 3. CMake C++ CI Tests

- [ ] 3.1 Create `test/workflows/test_cmake_cpp_ci.yml`
- [ ] 3.2 Create `test/fixtures/cmake-cpp/simple-project/`
- [ ] 3.3 Test with C++ standards: 17, 20, 23
- [ ] 3.4 Test with custom cmake-options
- [ ] 3.5 Test sanitizers
- [ ] 3.6 Verify build artifacts

**Acceptance**: CMake C++ CI validated
**Status**: PENDING

## 4. Bazel CI Tests

- [ ] 4.1 Create `test/workflows/test_bazel_ci.yml`
- [ ] 4.2 Create `test/fixtures/bazel/simple-project/`
- [ ] 4.3 Test with configs: release, debug
- [ ] 4.4 Test with custom bazel-configs
- [ ] 4.5 Test coverage reporting
- [ ] 4.6 Verify build and test outputs

**Acceptance**: Bazel CI validated
**Status**: PENDING

## 5. CI Integration

- [ ] 5.1 Configure tests to run on PR
- [ ] 5.2 Configure tests to run on push to main
- [ ] 5.3 Add path filters for relevant workflow changes
- [ ] 5.4 Configure as required checks

**Acceptance**: Tests integrated into CI
**Status**: PENDING

## 6. Documentation

- [ ] 6.1 Document test structure in test/README.md
- [ ] 6.2 Document how to add new tests
- [ ] 6.3 Document fixture requirements
- [ ] 6.4 Update TESTING_STRATEGY.md

**Acceptance**: Complete test documentation
**Status**: PENDING
