# Update Action Versions

Step-by-step guide for updating GitHub Action versions.

## When to Update

- Security vulnerability in action
- New features needed
- Dependabot PR received
- Quarterly maintenance

## Steps

### 1. Identify Current Version

```bash
# Find all uses of an action
grep -rn "actions/checkout@" .github/workflows/

# Check current SHA and version
# Look for comment: # actions/checkout@v4.2.2
```

### 2. Find New Version

```bash
# List releases
gh release list --repo actions/checkout --limit 5

# Get SHA for specific tag
gh api repos/actions/checkout/git/ref/tags/v4.2.2 --jq '.object.sha'

# For annotated tags, get the commit SHA
gh api repos/actions/checkout/git/ref/tags/v4.2.2 --jq '.object.sha' | xargs -I {} gh api repos/actions/checkout/git/tags/{} --jq '.object.sha'
```

### 3. Verify Security

Before updating, check:

- [ ] Release notes for breaking changes
- [ ] Security advisories
- [ ] GitHub Security tab for action

### 4. Update Workflows

```yaml
# Before
# actions/checkout@v4.1.0
- uses: actions/checkout@b4ffde65f46336ab88eb53be808477a3936bae11

# After
# actions/checkout@v4.2.2
- uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683
```

### 5. Update Documentation

If ACTION_VERSIONS.md exists, update it:

```markdown
| Action | Version | SHA |
|--------|---------|-----|
| actions/checkout | v4.2.2 | 11bd71901bbe5b1630ceea73d27597364c9af683 |
```

### 6. Test

```bash
# Lint all workflows
actionlint .github/workflows/*.yml

# Verify SHA format (40 characters)
grep -rn "uses:.*@[a-f0-9]\{40\}" .github/workflows/ | head -5
```

### 7. Commit

```bash
git add .github/workflows/
git commit -m "security: update actions/checkout to v4.2.2

- Update SHA to 11bd71901bbe5b1630ceea73d27597364c9af683
- Includes security fixes from v4.2.0+
- No breaking changes"
```

## Batch Updates

For updating multiple actions:

```bash
# Find all unpinned actions
grep -rn "uses:.*@v[0-9]$" .github/workflows/

# Find all actions using @main or @master
grep -rn "uses:.*@\(main\|master\)$" .github/workflows/
```

## Common Actions Reference

| Action | Latest | SHA (check for updates) |
|--------|--------|-------------------------|
| actions/checkout | v4.2.2 | 11bd71901bbe5b1630ceea73d27597364c9af683 |
| actions/setup-java | v4.5.0 | 7a6d8a8234af8eb26422e24e3006232cccaa061b |
| actions/cache | v4.2.0 | 1bd1e32a3bdc45362d1e726936510720a7c30a57 |
| actions/upload-artifact | v4.4.3 | 6f51ac03b9356f520e9adb1b1b7802705f340c2b |
| actions/download-artifact | v4.1.8 | fa0a91b85d4f404e444e00e005971372dc801d16 |

## Handling Dependabot PRs

When Dependabot creates an action update PR:

1. **Review changes** - Check the diff
2. **Verify SHA format** - Ensure 40-character SHA
3. **Check version comment** - Verify comment matches
4. **Test CI** - Ensure PR passes
5. **Merge** - After verification

## Troubleshooting

### SHA Not 40 Characters

Some actions use lightweight tags. Get the underlying commit:

```bash
gh api repos/OWNER/REPO/git/ref/tags/TAG --jq '.object.sha'
```

### Action Moved or Renamed

If an action has been moved:

1. Find new location
2. Update all references
3. Document the change

### Breaking Changes

If update has breaking changes:

1. Review migration guide
2. Update workflow syntax
3. Test thoroughly
4. Document changes
