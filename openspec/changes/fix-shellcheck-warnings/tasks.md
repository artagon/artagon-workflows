# Tasks: Fix Shellcheck Warnings

## 1. Fix bazel_multi_ci.yml

- [ ] 1.1 Fix SC2086 at line 92 (quote variables)
- [ ] 1.2 Fix SC2086 at line 207 (quote variables)

**Acceptance**: No shellcheck warnings

## 2. Fix bazel_multi_release.yml

- [ ] 2.1 Fix SC2086 at line 60 (quote variables)
- [ ] 2.2 Fix SC2086 at line 66 (quote variables)
- [ ] 2.3 Fix SC2086 at line 99 (quote $GITHUB_OUTPUT)
- [ ] 2.4 Fix SC2035 at line 129 (glob pattern)
- [ ] 2.5 Fix SC2086 at line 198 (quote $GITHUB_OUTPUT)
- [ ] 2.6 Fix SC2035 at line 228 (glob pattern)

**Acceptance**: No shellcheck warnings

## 3. Fix cmake_c_ci.yml

- [ ] 3.1 Fix SC2086 at lines 97, 244 (quote variables)
- [ ] 3.2 Fix SC2046 at lines 151, 155, 240, 292, 350, 389 (quote $(nproc))

**Acceptance**: No shellcheck warnings

## 4. Fix cmake_c_release.yml

- [ ] 4.1 Fix SC2086 at lines 82, 88, 124 (quote variables)
- [ ] 4.2 Fix SC2046 at lines 155, 210 (quote command substitutions)

**Acceptance**: No shellcheck warnings

## 5. Fix Remaining Workflows

- [ ] 5.1 Fix cmake_cpp_ci.yml (similar to cmake_c_ci.yml)
- [ ] 5.2 Fix cmake_cpp_release.yml (similar to cmake_c_release.yml)
- [ ] 5.3 Fix any other workflows with shellcheck warnings

**Acceptance**: All workflows pass shellcheck

## 6. Verification

- [ ] 6.1 Run actionlint locally on all workflows
- [ ] 6.2 Verify CI passes

**Acceptance**: CI green
