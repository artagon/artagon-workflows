# Tasks: Fix Secrets on CLI

## 1. Fix maven_github_release.yml

- [ ] 1.1 Locate secret in CLI argument (line 121-126)
- [ ] 1.2 Refactor to use environment variable
- [ ] 1.3 Verify secret is masked in logs
- [ ] 1.4 Test workflow locally if possible

**Acceptance**: No secrets in command-line arguments

## 2. Fix cmake_cpack_release.yml

- [ ] 2.1 Locate secret in CLI argument (line 120-126)
- [ ] 2.2 Refactor to write GPG key to file with 600 permissions
- [ ] 2.3 Clean up key file after use
- [ ] 2.4 Verify secret is masked in logs

**Acceptance**: GPG key handled via file, not CLI

## 3. Verification

- [ ] 3.1 Run actionlint on modified workflows
- [ ] 3.2 Verify test_security.yml passes
- [ ] 3.3 Manual review of workflow logs for secret exposure

**Acceptance**: All security checks pass
