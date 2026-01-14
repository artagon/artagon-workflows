# Proposal: Review Structure and Workflows

## Why

The artagon-workflows repository has evolved organically with 24+ reusable workflows across multiple build systems. A comprehensive review is needed to:

1. Ensure all workflows follow current security best practices
2. Verify action versions are up-to-date
3. Identify inconsistencies in workflow patterns
4. Validate the OpenSpec structure is complete and accurate
5. Document any gaps or improvements needed

## What Changes

This is a **review and audit** change, not an implementation change. The deliverables are:

- Audit report of current workflow state
- List of actions needing version updates
- Identified security gaps or inconsistencies
- Recommendations for improvements
- Updated specs if current behavior differs from documented

## Impact

- Affected specs: All workflow specs (workflow-security, maven-workflows, cmake-workflows, bazel-workflows)
- Affected code: Potentially all `.github/workflows/*.yml` files
- No breaking changes - this is an audit

## Scope

### In Scope

1. **Action Version Audit**
   - Catalog all GitHub Actions used
   - Check for latest stable versions
   - Verify SHA pinning is correct

2. **Security Compliance Audit**
   - Permissions blocks on all jobs
   - Input validation patterns
   - Secret handling practices
   - Binary download verification

3. **Structure Review**
   - Workflow naming conventions
   - Documentation completeness
   - Example coverage
   - OpenSpec accuracy

4. **Pattern Consistency**
   - Common patterns across workflows
   - Error handling approaches
   - Caching strategies

### Out of Scope

- Adding new workflows
- Major refactoring
- Breaking changes to existing workflows
