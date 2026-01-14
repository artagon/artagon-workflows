# Release Checklist

Pre-release verification steps for workflow releases.

## Pre-Release Verification

### 1. Security Validation

- [ ] All actions pinned to commit SHAs
- [ ] No mutable tag references (`@v4`, `@main`, `@master`)
- [ ] All jobs have explicit permissions blocks
- [ ] All user inputs validated
- [ ] No secrets in command-line arguments
- [ ] All binary downloads verified with checksums

### 2. Code Quality

- [ ] actionlint passes on all workflow files
- [ ] yamllint passes on all workflow files
- [ ] No syntax errors
- [ ] No deprecated action versions
- [ ] Consistent naming conventions

### 3. Documentation

- [ ] README.md updated
- [ ] CHANGELOG.md updated
- [ ] New workflows documented
- [ ] Examples provided for new features
- [ ] Breaking changes documented

### 4. Testing

- [ ] CI passes on all branches
- [ ] Manual testing completed for new workflows
- [ ] Test fixtures updated if needed

### 5. Backwards Compatibility

- [ ] No breaking changes without major version bump
- [ ] Migration guide provided for breaking changes
- [ ] Deprecation warnings added for removed features

## Release Process

### 1. Version Bump

```bash
# Determine version based on changes
# MAJOR: Breaking changes
# MINOR: New features
# PATCH: Bug fixes, security updates
```

### 2. Create Release Branch

```bash
git checkout main
git pull origin main
git checkout -b release-X.Y.Z
```

### 3. Update Documentation

- Update version references
- Update CHANGELOG.md
- Verify all links work

### 4. Create Tag

```bash
git tag -a vX.Y.Z -m "Release vX.Y.Z"
git push origin vX.Y.Z
```

### 5. GitHub Release

- Create release from tag
- Include changelog entries
- Attach relevant artifacts
- Mark as latest

## Post-Release

### 1. Verification

- [ ] Release visible in GitHub Releases
- [ ] Tag created correctly
- [ ] Documentation accessible
- [ ] Example workflows reference new version

### 2. Communication

- [ ] Announce release if significant
- [ ] Update downstream consumers if needed
- [ ] Close related issues

### 3. Archive OpenSpec Changes

```bash
git checkout main
git pull origin main
git checkout -b archive/[change-id]

openspec archive [change-id] --yes
openspec validate --strict

git add openspec/
git commit -m "Archive [change-id] after vX.Y.Z release"
git push origin archive/[change-id]
```

## Emergency Rollback

If a critical issue is found after release:

1. **Assess severity** - Determine impact
2. **Communicate** - Notify affected users
3. **Hotfix or rollback** - Create fix or revert
4. **Post-mortem** - Document lessons learned

## References

- [Semantic Versioning](https://semver.org/)
- [Keep a Changelog](https://keepachangelog.com/)
- [GitHub Releases](https://docs.github.com/en/repositories/releasing-projects-on-github/managing-releases-in-a-repository)
