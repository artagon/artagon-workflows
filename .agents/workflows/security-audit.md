# Security Audit Workflow

Step-by-step guide for conducting security audits of workflows.

## Audit Frequency

- **Weekly**: Review Dependabot PRs
- **Monthly**: Check for unpinned actions
- **Quarterly**: Full security audit

## Quick Security Check

```bash
# Check for unpinned actions (mutable tags)
grep -rn "uses:.*@v[0-9]$" .github/workflows/

# Check for @main or @master references
grep -rn "uses:.*@\(main\|master\)$" .github/workflows/

# Check for missing permissions
for f in .github/workflows/*.yml; do
  grep -q "permissions:" "$f" || echo "Missing permissions: $f"
done

# Run actionlint
actionlint .github/workflows/*.yml
```

## Full Audit Checklist

### 1. Action Pinning Audit

- [ ] All actions use 40-character SHA
- [ ] Version comment above each SHA
- [ ] No mutable tag references
- [ ] No @main/@master references

```bash
# Find all action references
grep -rn "uses:" .github/workflows/ | grep -v "^#"

# Verify SHA format
grep -rn "uses:.*@[a-f0-9]\{40\}" .github/workflows/ | wc -l
# Should match total action count
```

### 2. Permissions Audit

- [ ] All jobs have permissions block
- [ ] Permissions follow least privilege
- [ ] Write permissions justified

```bash
# Check each workflow
for f in .github/workflows/*.yml; do
  echo "=== $f ==="
  grep -A 5 "permissions:" "$f" || echo "NO PERMISSIONS BLOCK"
done
```

### 3. Input Validation Audit

- [ ] All user inputs identified
- [ ] Validation before shell use
- [ ] Allowlist patterns used

```bash
# Find all inputs
grep -rn "inputs\." .github/workflows/

# Check for validation steps
grep -rn "Validate inputs" .github/workflows/
```

### 4. Secret Handling Audit

- [ ] No secrets in CLI arguments
- [ ] Secrets passed via env block
- [ ] No echo of secrets

```bash
# Check for CLI secret usage
grep -rn "secrets\.\w*\}" .github/workflows/ | grep "run:"

# Check for env block usage
grep -rn "secrets\.\w*\}" .github/workflows/ | grep "env:"
```

### 5. Binary Download Audit

- [ ] All downloads have checksums
- [ ] HTTPS URLs only
- [ ] Versions pinned

```bash
# Find download commands
grep -rn "wget\|curl" .github/workflows/

# Check for checksum verification
grep -rn "sha256sum\|shasum" .github/workflows/
```

## Vulnerability Assessment

### Critical (Fix Immediately)

- Unpinned actions to @master/@main
- Secrets in command-line arguments
- No input validation with shell execution
- Missing permissions (defaults too broad)

### High (Fix Within 1 Week)

- Actions pinned to mutable tags (@v4)
- Binary downloads without checksums
- Overly permissive permissions

### Medium (Fix Within 1 Month)

- Missing version comments on SHAs
- Inconsistent permission patterns
- Missing validation on low-risk inputs

### Low (Track for Future)

- Deprecated action versions
- Minor documentation gaps
- Formatting inconsistencies

## Audit Report Template

```markdown
# Security Audit Report

**Date**: YYYY-MM-DD
**Auditor**: [Name/AI Agent]
**Scope**: All workflows in .github/workflows/

## Summary

- Total workflows: X
- Critical issues: X
- High issues: X
- Medium issues: X
- Low issues: X

## Findings

### Critical

1. **[Issue Title]**
   - File: workflow.yml:line
   - Description: ...
   - Recommendation: ...

### High

...

## Recommendations

1. ...
2. ...

## Next Steps

- [ ] Fix critical issues immediately
- [ ] Schedule high issue fixes
- [ ] Add to backlog: medium/low issues
```

## Remediation Workflow

1. **Triage** - Prioritize by severity
2. **Fix** - Address issues in order
3. **Verify** - Run audit checks again
4. **Document** - Update security docs
5. **Monitor** - Set up recurring checks

## References

- [OSSF Scorecard](https://github.com/ossf/scorecard)
- [GitHub Actions Security](https://docs.github.com/en/actions/security-guides/security-hardening-for-github-actions)
- [Supply Chain Security](https://slsa.dev/)
