#!/bin/bash
# OpenSpec Utility Script
# Provides functions for gathering and processing OpenSpec documents

set -euo pipefail

# Default openspec path
OPENSPEC_PATH="${OPENSPEC_PATH:-openspec/}"

# Gather openspec content from all spec directories
# Usage: gather_openspec [path] [max_lines_per_doc]
gather_openspec() {
    local path="${1:-$OPENSPEC_PATH}"
    local max_lines="${2:-100}"
    local output=""

    if [ ! -d "$path" ]; then
        echo ""
        return 0
    fi

    for spec_dir in "$path"*/; do
        if [ -d "$spec_dir" ]; then
            local spec_name
            spec_name=$(basename "$spec_dir")
            output="${output}\n=== OpenSpec: ${spec_name} ===\n"

            for doc in proposal.md design.md tasks.md; do
                if [ -f "${spec_dir}${doc}" ]; then
                    output="${output}\n--- ${doc} ---\n"
                    output="${output}$(head -n "$max_lines" "${spec_dir}${doc}")\n"
                fi
            done
        fi
    done

    echo -e "$output"
}

# Check if openspec exists
# Usage: has_openspec [path]
has_openspec() {
    local path="${1:-$OPENSPEC_PATH}"

    if [ -d "$path" ]; then
        # Check if any spec directories exist
        for spec_dir in "$path"*/; do
            if [ -d "$spec_dir" ]; then
                echo "true"
                return 0
            fi
        done
    fi
    echo "false"
}

# List all openspec directories
# Usage: list_openspecs [path]
list_openspecs() {
    local path="${1:-$OPENSPEC_PATH}"

    if [ ! -d "$path" ]; then
        echo ""
        return 0
    fi

    for spec_dir in "$path"*/; do
        if [ -d "$spec_dir" ]; then
            basename "$spec_dir"
        fi
    done
}

# Check openspec completeness for a specific spec
# Usage: check_openspec_completeness [spec_path]
check_openspec_completeness() {
    local spec_path="$1"
    local missing=""

    for doc in proposal.md design.md tasks.md; do
        if [ ! -f "${spec_path}/${doc}" ]; then
            missing="${missing}${doc} "
        fi
    done

    if [ -n "$missing" ]; then
        echo "missing: $missing"
    else
        echo "complete"
    fi
}

# Get relevant openspec for changed files
# Usage: get_relevant_openspec [changed_files] [openspec_path]
get_relevant_openspec() {
    local changed_files="$1"
    local path="${2:-$OPENSPEC_PATH}"
    local relevant=""

    # Check if any changed files are in openspec directory
    for file in $changed_files; do
        if [[ "$file" == "$path"* ]]; then
            # Extract spec name from path
            local spec_name
            spec_name=$(echo "$file" | sed "s|^$path||" | cut -d'/' -f1)
            if [ -n "$spec_name" ] && [[ ! "$relevant" == *"$spec_name"* ]]; then
                relevant="${relevant}${spec_name} "
            fi
        fi
    done

    echo "$relevant"
}

# Generate openspec summary for AI prompt
# Usage: generate_openspec_prompt [path] [max_lines]
generate_openspec_prompt() {
    local path="${1:-$OPENSPEC_PATH}"
    local max_lines="${2:-50}"

    if [ "$(has_openspec "$path")" = "false" ]; then
        echo ""
        return 0
    fi

    local prompt="## Project Design Context (OpenSpec)

The repository contains OpenSpec design documents that describe the intended architecture and design decisions.

### Available Specifications:
"

    for spec_dir in "$path"*/; do
        if [ -d "$spec_dir" ]; then
            local spec_name
            spec_name=$(basename "$spec_dir")
            prompt="${prompt}
#### ${spec_name}
"
            for doc in proposal.md design.md tasks.md; do
                if [ -f "${spec_dir}${doc}" ]; then
                    prompt="${prompt}- ${doc}: Present
"
                    # Add first few lines as summary
                    local summary
                    summary=$(head -n 5 "${spec_dir}${doc}" | grep -v "^#" | head -n 2)
                    if [ -n "$summary" ]; then
                        prompt="${prompt}  > ${summary}
"
                    fi
                else
                    prompt="${prompt}- ${doc}: Missing
"
                fi
            done
        fi
    done

    prompt="${prompt}
Use the Read tool to examine specific openspec files for detailed design context.
"

    echo "$prompt"
}

# Load AI agent skill from .agents/skills
# Usage: load_skill [skill_name]
load_skill() {
    local skill_name="$1"
    local skill_path=".agents/skills/${skill_name}.md"

    if [ -f "$skill_path" ]; then
        cat "$skill_path"
    else
        echo "# Skill not found: ${skill_name}"
        echo "Available skills:"
        ls -1 .agents/skills/*.md 2>/dev/null | xargs -n1 basename | sed 's/.md$//' || echo "None"
    fi
}

# Main function for CLI usage
main() {
    local command="${1:-help}"
    shift || true

    case "$command" in
        gather)
            gather_openspec "$@"
            ;;
        has)
            has_openspec "$@"
            ;;
        list)
            list_openspecs "$@"
            ;;
        check)
            check_openspec_completeness "$@"
            ;;
        relevant)
            get_relevant_openspec "$@"
            ;;
        prompt)
            generate_openspec_prompt "$@"
            ;;
        skill)
            load_skill "$@"
            ;;
        help|*)
            echo "OpenSpec Utility"
            echo ""
            echo "Usage: $0 <command> [args]"
            echo ""
            echo "Commands:"
            echo "  gather [path] [max_lines]  - Gather openspec content"
            echo "  has [path]                 - Check if openspec exists"
            echo "  list [path]                - List openspec directories"
            echo "  check <spec_path>          - Check spec completeness"
            echo "  relevant <files> [path]    - Get relevant specs for files"
            echo "  prompt [path] [max_lines]  - Generate AI prompt"
            echo "  skill <name>               - Load agent skill"
            echo "  help                       - Show this help"
            ;;
    esac
}

# Run main if script is executed directly
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
