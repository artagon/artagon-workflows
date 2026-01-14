# Copilot Review Instructions (OpenSpec Compliance)

Start with `openspec/AGENTS.md`. Reviews must be grounded in the active OpenSpec change.

## Review Steps

1. Verify the PR references a spec issue (label `spec`).
2. Identify the active change in `openspec/changes/<change-id>/`.
3. Read `proposal.md`, `tasks.md`, and the spec deltas in `openspec/changes/<change-id>/specs/`.
4. Check that every acceptance criteria item is implemented or explicitly deferred.
5. Flag scope creep: any work not described by the spec or proposal.
6. Confirm tests/validation cover the spec requirements and note missing coverage.
7. Call out breaking changes not described in the spec.

## Security Checklist (CRITICAL for Workflow PRs)

For any PR modifying `.github/workflows/*.yml`:

- [ ] All actions pinned to 40-character commit SHAs
- [ ] Version comment above each pinned action
- [ ] Permissions block on every job
- [ ] Permissions follow least-privilege principle
- [ ] User inputs validated before shell execution
- [ ] No secrets in command-line arguments
- [ ] Binary downloads have checksum verification

## Spec Compliance Ratings

- **Full**: All acceptance criteria are met with appropriate validation.
- **Partial**: Most criteria are met, gaps clearly listed.
- **Minimal**: Only a small subset is met.
- **None**: No meaningful alignment with the spec.

## Structured Review Format

Use this format in review comments:

```
Spec Reference: <issue link or "missing">
OpenSpec Change: <change-id or "unknown">
Compliance Rating: <full|partial|minimal|none>

Acceptance Criteria Coverage:
- [ ] Item 1 ... (status)
- [ ] Item 2 ... (status)

Security Compliance:
- [ ] Actions pinned to SHAs
- [ ] Permissions declared
- [ ] Inputs validated
- [ ] Secrets handled securely
- [ ] Binary checksums verified

Scope Creep:
- <list any out-of-scope changes>

Missing Validation:
- <list missing tests/checks tied to criteria>

Breaking Changes:
- <list and confirm spec coverage>

Notes:
- <additional context or suggestions>
```

## Common Security Issues to Flag

1. **Unpinned Actions**
   ```yaml
   # REJECT: Mutable tag
   uses: actions/checkout@v4

   # ACCEPT: Pinned SHA with comment
   # actions/checkout@v4.2.2
   uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683
   ```

2. **Missing Permissions**
   ```yaml
   # REJECT: No permissions block
   jobs:
     build:
       runs-on: ubuntu-latest

   # ACCEPT: Explicit permissions
   jobs:
     build:
       runs-on: ubuntu-latest
       permissions:
         contents: read
   ```

3. **Unvalidated Inputs**
   ```yaml
   # REJECT: Direct use of user input
   run: mvn ${{ inputs.maven-args }}

   # ACCEPT: Validated input
   - name: Validate inputs
     run: |
       if ! echo "${{ inputs.maven-args }}" | grep -qE '^[SAFE]*$'; then
         exit 1
       fi
   ```

4. **Secrets in CLI**
   ```yaml
   # REJECT: Secret in command line
   run: mvn -Dpassword="${{ secrets.PASS }}"

   # ACCEPT: Secret in env/config file
   env:
     PASSWORD: ${{ secrets.PASS }}
   run: echo "$PASSWORD" > config && mvn
   ```
