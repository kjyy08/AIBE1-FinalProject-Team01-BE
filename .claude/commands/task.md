---
argument-hint: [issue-number] [additional-instructions]
description: Execute development workflow from issue
---

# Development Task Workflow

Follow this development workflow for all feature implementations. All work and responses must be in Korean.

## Development Workflow

1. **Issue Analysis**
    - Review GitHub issue #$1 requirements and acceptance criteria
    - Understand the technical scope and dependencies

2. **Branch Management**
    - Create branches from `main` using @github-manager agent
    - Use descriptive branch names that reflect the feature being implemented

3. **Development Process**
    - Start development by updating GitHub project status to "In Progress"
    - Proceed with development using @agents that are appropriate for the given situation
    - Use Context7 tools. If Context7 is not available, provide guidance to the developer before starting development

4. **Developer Review (Pre-Commit)**
    - **MANDATORY**: Present completed work to the developer for review before any commits
    - Provide comprehensive summary of changes made:
        - Files modified and new files created
        - Key implementation decisions and rationale
        - Test coverage and validation performed
        - Architecture compliance verification
    - Wait for developer approval before proceeding to commit stage
    - Address any feedback or concerns raised during review

5. **Commit Standards** (After Developer Approval)
    - Use github-project-manager agent
    - Only proceed after receiving developer approval from step 4

6. **Code Push**
    - Push branches to remote repository
    - Ensure all tests pass before pushing: `./gradlew test`

7. **Pull Request Creation**
    - Create PR with descriptive title matching commit convention
    - Use team PR template(`.github/pull_request_template.md`): Follow the standardized PR template for consistent documentation
    - Update GitHub project status to "In Review"

$2