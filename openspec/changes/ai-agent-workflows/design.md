# Design: Reusable AI Agent Workflows

## Overview

Implement reusable GitHub Actions workflows that integrate with multiple AI providers (Claude, GPT-4, Gemini) for automated code review, security analysis, issue triage, code fixes, and documentation generation.

**Key Design Principle**: Use official actions and SDKs from each AI provider.

## Architecture

### Workflow Structure

```
.github/workflows/
├── ai_pr_review.yml       # PR review with AI
├── ai_security_review.yml # Security vulnerability detection (Claude only)
├── ai_issue_triage.yml    # Issue analysis and labeling
├── ai_code_fix.yml        # Automated code fixes
├── ai_doc_gen.yml         # Documentation generation
└── ai_docs_update.yml     # Documentation updates
```

### Official Actions and SDKs

| Provider | Integration Method | Package/Action |
|----------|-------------------|----------------|
| **Anthropic (Claude)** | Official GitHub Action | `anthropics/claude-code-action@v1` |
| **Anthropic Security** | Official GitHub Action | `anthropics/claude-code-security-review@main` |
| **OpenAI (GPT)** | Official SDK | `npm install openai` |
| **Google (Gemini)** | Official SDK | `npm install @google/generative-ai` |

### Why Official Actions for Claude?

Anthropic provides two official, GitHub-verified actions:

1. **`anthropics/claude-code-action@v1`** - General purpose Claude integration
   - PR review, code implementation, Q&A, refactoring
   - Supports: Anthropic API, AWS Bedrock, Google Vertex AI, Microsoft Foundry
   - Intelligent mode detection for PRs and issues

2. **`anthropics/claude-code-security-review@main`** - Security-focused analysis
   - Detects: SQL injection, XSS, hardcoded secrets, auth flaws, crypto weaknesses
   - Deep semantic security analysis
   - Supports all programming languages

### Why SDKs for OpenAI and Gemini?

Neither OpenAI nor Google provide official GitHub Actions for their AI APIs. The recommended approach is:
- Use official Node.js SDKs (`openai`, `@google/generative-ai`)
- Set up Node.js 20+ with `actions/setup-node`
- Install SDK and run analysis scripts

### Default Models

| Provider | Default Model | Purpose |
|----------|---------------|---------|
| claude | claude-opus-4-5-20251101 | Most capable Claude model |
| openai | gpt-4.1-2025-04-14 | Latest GPT-4.1 commercial model |
| gemini | gemini-2.5-pro-preview-05-06 | Latest Gemini Pro model |

## Component Design

### 1. AI PR Review (`ai_pr_review.yml`)

**Trigger**: `workflow_call` (reusable)

**Implementation by Provider**:
- **Claude**: Uses `anthropics/claude-code-action@v1`
- **OpenAI/Gemini**: Uses official SDKs

```yaml
# Claude (Official Action)
- uses: anthropics/claude-code-action@v1
  with:
    anthropic_api_key: ${{ secrets.ANTHROPIC_API_KEY }}
    prompt: "Review this pull request..."
    claude_args: "--model claude-opus-4-5-20251101"

# OpenAI (SDK)
- run: npm install openai
- run: node review_openai.mjs
```

### 2. AI Security Review (`ai_security_review.yml`)

**Trigger**: `workflow_call` on pull requests

**Implementation**: Uses official `anthropics/claude-code-security-review@main`

```yaml
- uses: anthropics/claude-code-security-review@main
  with:
    claude-api-key: ${{ secrets.ANTHROPIC_API_KEY }}
    claude-model: claude-opus-4-1-20250805
    comment-pr: true
    upload-results: true
```

**Detects**:
- SQL/command injection
- Authentication flaws
- Hardcoded secrets
- Cryptographic weaknesses
- XSS vulnerabilities
- Deserialization exploits
- Business logic issues

### 3. AI Issue Triage (`ai_issue_triage.yml`)

**Trigger**: `workflow_call` on issues

**Implementation by Provider**:
- **Claude**: Uses `anthropics/claude-code-action@v1`
- **OpenAI/Gemini**: Uses official SDKs with JSON response format

### 4. AI Code Fix (`ai_code_fix.yml`)

**Trigger**: `workflow_call` or `workflow_dispatch`

**Implementation by Provider**:
- **Claude**: Uses `anthropics/claude-code-action@v1` with implementation prompts
- **OpenAI/Gemini**: Uses official SDKs

### 5. AI Documentation Generator (`ai_doc_gen.yml`)

**Trigger**: `workflow_call` or `workflow_dispatch`

Uses SDKs for all providers (documentation generation is less suited for the action-based approach).

### 6. AI Documentation Update (`ai_docs_update.yml`)

**Trigger**: `workflow_call` on code changes

Detects outdated documentation and suggests updates using SDKs.

## Security Considerations

### API Key Handling

```yaml
# Keys passed as secrets to official actions
- uses: anthropics/claude-code-action@v1
  with:
    anthropic_api_key: ${{ secrets.ANTHROPIC_API_KEY }}

# Or via environment for SDKs
- env:
    OPENAI_API_KEY: ${{ secrets.OPENAI_API_KEY }}
  run: node analyze.mjs
```

### Input Validation

```yaml
- name: Validate inputs
  run: |
    PROVIDER="${{ inputs.ai-provider }}"
    if ! echo "$PROVIDER" | grep -qE '^(claude|openai|gemini)$'; then
      echo "Invalid provider"
      exit 1
    fi
```

### Security Review Considerations

From Anthropic's documentation:
> The tool should only be used to review trusted PRs. Repositories should enforce "Require approval for all external contributors" to prevent prompt injection attacks.

## Prompt Templates

### PR Review Prompt (Claude Action)

```yaml
prompt: |
  You are a senior code reviewer performing a pull request review.

  Review depth: ${{ inputs.review-depth }}
  Focus areas: ${{ inputs.focus-areas }}

  Please review this pull request and provide:
  1. A brief summary of the changes
  2. Code quality score (0-100)
  3. List of issues found with severity
  4. Positive aspects of the code
  5. Suggestions for improvement
```

### Issue Triage Prompt

```yaml
prompt: |
  You are a technical project manager triaging a GitHub issue.

  Available labels: ${{ inputs.available-labels }}
  Project context: ${{ inputs.project-context }}

  Please analyze and provide:
  1. Category (bug/feature/question/documentation)
  2. Priority (critical/high/medium/low)
  3. Suggested labels
  4. Technical summary
  5. Action items
```

## Testing Strategy

1. **Unit Tests**: Test input validation
2. **Integration Tests**: Test with mock API responses
3. **E2E Tests**: Test with real AI providers (rate-limited)

## Rollback Plan

- Workflows are opt-in via `workflow_call`
- No automatic execution without explicit configuration
- Easy to disable by removing workflow reference
- Can pin to specific action versions for stability
