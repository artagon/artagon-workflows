# Tasks: Review Structure and Workflows

## 1. Action Version Audit

- [ ] 1.1 Catalog all GitHub Actions used across workflows
- [ ] 1.2 Check each action for latest stable version
- [ ] 1.3 Verify all actions are pinned to commit SHAs
- [ ] 1.4 Verify version comments match pinned SHAs
- [ ] 1.5 Create action version update list

**Acceptance**: Complete inventory of actions with current vs latest versions

## 2. Security Compliance Audit

- [ ] 2.1 Verify all jobs have explicit permissions blocks
- [ ] 2.2 Review permissions follow least-privilege principle
- [ ] 2.3 Check all user inputs have validation
- [ ] 2.4 Verify no secrets in command-line arguments
- [ ] 2.5 Check binary downloads have checksum verification
- [ ] 2.6 Document any security gaps found

**Acceptance**: Security audit report with findings and recommendations

## 3. Workflow Structure Review

- [ ] 3.1 Verify all workflows follow naming convention
- [ ] 3.2 Check documentation completeness for each workflow
- [ ] 3.3 Verify examples exist for each workflow
- [ ] 3.4 Review inline comments and descriptions
- [ ] 3.5 Check for deprecated patterns or actions

**Acceptance**: Structure review report with consistency findings

## 4. OpenSpec Accuracy Review

- [ ] 4.1 Compare workflow-security spec to actual implementations
- [ ] 4.2 Compare maven-workflows spec to actual workflows
- [ ] 4.3 Compare cmake-workflows spec to actual workflows
- [ ] 4.4 Compare bazel-workflows spec to actual workflows
- [ ] 4.5 Identify spec gaps or inaccuracies
- [ ] 4.6 Update specs if needed

**Acceptance**: Specs accurately reflect current implementations

## 5. Pattern Consistency Review

- [ ] 5.1 Review error handling patterns across workflows
- [ ] 5.2 Review caching strategies
- [ ] 5.3 Review input/output patterns
- [ ] 5.4 Identify opportunities for consolidation
- [ ] 5.5 Document recommended patterns

**Acceptance**: Pattern guide with best practices

## 6. Documentation Review

- [ ] 6.1 Verify README accuracy
- [ ] 6.2 Verify docs/ folder completeness
- [ ] 6.3 Check all links are valid
- [ ] 6.4 Verify examples are current
- [ ] 6.5 Update any stale documentation

**Acceptance**: All documentation accurate and current

## 7. Final Report

- [ ] 7.1 Compile audit findings
- [ ] 7.2 Prioritize recommendations
- [ ] 7.3 Create follow-up issues for fixes
- [ ] 7.4 Update design.md with findings

**Acceptance**: Comprehensive audit report with actionable recommendations
