# Branch Protection Configuration

This document describes the branch protection rules configured for the `main` branch.

## Main Branch Protection Rules

### Required Status Checks

The following checks must pass before merging:

- ✅ **Validate YAML Syntax** - Ensures all workflow files are valid YAML
- ✅ **Security Validation** - Verifies all actions are pinned to commit SHAs
- ✅ **Lint GitHub Actions Workflows** - Runs actionlint with shellcheck validation

**Status checks are strict**: PRs must be up-to-date with the base branch before merging.

### Pull Request Reviews

- **Required approving reviews**: 1
- **Dismiss stale reviews**: Yes (new commits dismiss previous approvals)
- **Require code owner reviews**: No
- **Require approval of most recent push**: No

### Protection Rules

| Rule | Status | Description |
|------|--------|-------------|
| Enforce admins | ✅ Enabled | Rules apply to administrators |
| Allow force pushes | ❌ Disabled | No force pushes to main |
| Allow deletions | ❌ Disabled | Cannot delete main branch |
| Require conversation resolution | ✅ Enabled | All review comments must be resolved |
| Require linear history | ❌ Disabled | Merge commits allowed |
| Lock branch | ❌ Disabled | Branch is not locked |

### Repository Settings

| Setting | Status | Description |
|---------|--------|-------------|
| Delete branch on merge | ✅ Enabled | PR branches auto-deleted after merge |
| Allow auto-merge | ❌ Disabled | Manual merge required |
| Allow squash merge | ✅ Enabled | Squash and merge available |
| Allow merge commit | ✅ Enabled | Create merge commit available |
| Allow rebase merge | ✅ Enabled | Rebase and merge available |

## Workflow

### Creating a Pull Request

1. Create a feature branch from `main`
2. Make your changes and commit
3. Push branch and open PR
4. Wait for status checks to pass:
   - YAML syntax validation
   - Security validation (action pinning)
   - Actionlint with shellcheck
5. Request review from maintainer
6. Address review comments
7. Once approved and checks pass, merge
8. Branch is automatically deleted after merge

### Merging Options

All three merge strategies are available:

**Squash and Merge** (Recommended for most PRs)
- Combines all commits into single commit
- Cleaner history
- Preserves PR description as commit message

**Create a Merge Commit**
- Preserves all commits
- Shows branch history
- Use for feature branches with meaningful commits

**Rebase and Merge**
- Replays commits on top of main
- Linear history
- Each commit must pass checks individually

## Security Benefits

### Action Pinning Validation

All GitHub Actions must be pinned to immutable commit SHAs:

```yaml
# ✅ Correct - Pinned to commit SHA
uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683

# ❌ Wrong - Mutable tag reference
uses: actions/checkout@v4
```

### Shellcheck Validation

All shell scripts in workflows are validated with shellcheck to catch:
- Syntax errors
- Quoting issues
- Path traversal vulnerabilities
- Variable expansion problems
- Best practice violations

### Required Reviews

At least one approval prevents:
- Accidental direct pushes
- Unreviewed code changes
- Bypassing CI checks

### Conversation Resolution

All review comments must be addressed, ensuring:
- Feedback is not ignored
- Issues are documented as resolved
- Changes are properly justified

## Bypassing Protection Rules

⚠️ **Not recommended** but possible for emergencies:

Administrators can bypass protection rules by:
1. Temporarily disabling "Enforce admins"
2. Making the change
3. Re-enabling protection

This should only be done for critical hotfixes and should be:
- Documented in commit message
- Followed by immediate PR for review
- Reported to team

## Modifying Protection Rules

To update branch protection rules:

```bash
# View current protection
gh api repos/artagon/artagon-workflows/branches/main/protection

# Update protection rules
gh api repos/artagon/artagon-workflows/branches/main/protection \
  --method PUT --input protection.json

# Update repository settings
gh api repos/artagon/artagon-workflows \
  --method PATCH --input settings.json
```

## References

- [GitHub Branch Protection Documentation](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches)
- [Required Status Checks](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches#require-status-checks-before-merging)
- [Pull Request Reviews](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/reviewing-changes-in-pull-requests/about-pull-request-reviews)

## Applied On

**Date**: 2025-10-25
**Applied By**: Automated via GitHub CLI
**Configuration Tool**: `gh api`
