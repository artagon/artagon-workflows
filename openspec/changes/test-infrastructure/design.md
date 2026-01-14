# Design: Test Repository Infrastructure

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                  artagon-workflows (main)                    │
├─────────────────────────────────────────────────────────────┤
│  PR opened → ci.yml (async) → Triggers test repos           │
│  PR ready  → ci-sync.yml → Blocks until tests pass          │
│  Release   → trigger_test_repos.yml → Full validation       │
└─────────────────────────────────────────────────────────────┘
                              │
           ┌──────────────────┼──────────────────┐
           │                  │                  │
           ▼                  ▼                  ▼
┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
│  test-maven      │ │  test-cmake      │ │  test-bazel      │
│  - maven_ci.yml  │ │  - cmake_c_ci    │ │  - bazel_ci.yml  │
│  - maven_release │ │  - cmake_cpp_ci  │ │  - bazel_release │
└──────────────────┘ └──────────────────┘ └──────────────────┘
```

## Test Repositories

| Repository | Language | Workflows Tested |
|------------|----------|------------------|
| artagon-workflow-test-maven | Java | maven_ci, maven_release, maven_sbom |
| artagon-workflow-test-cmake | C/C++ | cmake_c_ci, cmake_cpp_ci, cmake_release |
| artagon-workflow-test-bazel | Multi | bazel_multi_ci, bazel_multi_release |
| artagon-workflow-test-rust | Rust | Future rust workflows |

## CI Workflow Types

### 1. Async CI (ci.yml)
- Triggers on PR open/update
- Dispatches workflow_dispatch to test repos
- Non-blocking (informational)

```yaml
on:
  pull_request:
    paths:
      - '.github/workflows/*.yml'

jobs:
  trigger-tests:
    steps:
      - uses: actions/github-script
        with:
          script: |
            github.rest.actions.createWorkflowDispatch({
              owner: 'artagon',
              repo: 'artagon-workflow-test-maven',
              workflow_id: 'ci.yml',
              ref: 'main'
            })
```

### 2. Sync CI (ci-sync.yml)
- Runs on PR ready for review
- Executes workflow fixtures inline
- Blocks PR on failure

### 3. Trigger Workflow (trigger_test_repos.yml)
- Manual dispatch support
- Release validation
- Full test suite execution

## Scheduling

| Event | Frequency | Scope |
|-------|-----------|-------|
| PR | On change | Changed workflows |
| Daily | 00:00 UTC | All workflows |
| Release | On tag | Full validation |
| Manual | On demand | Selected workflows |

## Notifications

- GitHub Actions status checks
- PR comments with test results
- Issue creation on persistent failures
