# Team Engineering Standards

These instructions apply to the entire repository. They are the minimum quality bar for every issue, branch, commit, pull request (PR), review, and release.

## Git and project tracking

- Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).
- Write commit subjects in the imperative mood, capitalize the first word, and omit a trailing period. Target 50 characters and never exceed 72 characters.
- Add a wrapped explanatory body for non-trivial commits. Explain what changed and why; do not merely restate the diff.
- Name branches with a suitable conventional prefix such as `feature/`, `fix/`, `docs/`, `chore/`, or `refactor/`.
- Do not include assistant or tool branding in branch names or any other repository identifier.
- Keep one independent concern per PR. Link it to a clearly assigned issue in the current milestone.
- Keep issue status, assignees, milestones, PR reviews, release notes, and release state accurate throughout the work.

## Java and code quality

- Follow the [basic](https://se-education.org/guides/conventions/java/basic.html) and [intermediate](https://se-education.org/guides/conventions/java/intermediate.html) SE-EDU Java coding standards.
- Run Checkstyle and manually inspect naming, documentation, abstraction level, duplication, and nesting. Automated checks do not replace a manual review.
- Keep methods at a consistent level of abstraction (SLAP), remove avoidable duplication, and prefer small, focused methods over deeply nested logic.
- Use assertions, exceptions, logging, defensive checks, and input validation where they make failures explicit and diagnosable.
- Make validation rules and error messages specific, consistent, and actionable.

## Tests and verification

- Add all applicable positive, negative, boundary, parser, and persistence tests for changed behavior.
- Add a regression test before fixing a defect, and confirm that the test fails for the expected reason before changing production code.
- Run both verification commands before requesting review:
  - `./gradlew check coverage`
  - `.github/run-checks.sh`
- Record relevant test evidence in the PR. Do not claim completion while required checks are failing.

## Documentation and UML

- Update the User Guide (UG) for every user-visible behavior, command, example, validation rule, and error-message change.
- Update the Developer Guide (DG) and UML diagrams when architecture, design, components, or interactions change.
- Ensure UML notation, multiplicities, activation bars, messages, and diagram explanations accurately match the implementation.
- State explicitly in the PR when no UG or DG update is required and explain why.

### Documentation quality gate for every PR

- Write for the intended reader: the UG must let an event director complete realistic tasks; the DG must let a maintainer understand the implemented design. Prefer short, task-oriented explanations over inherited template text.
- Check every documented command, argument, example, output, error, persistence claim, and platform instruction against the current code. For a release, repeat this against the **exact packaged JAR** and record its commit and checksum. Do not describe proposed behavior as implemented.
- Explain displayed-list indices, destructive effects, limitations, and recovery steps where they affect a user's decision. Avoid claiming confirmation, rollback, backups, or portability that has not been demonstrated.
- Keep requirements traceable: distinguish implemented scope from future user stories; make use-case main and extension flows match real behavior; make non-functional requirements measurable and state whether they are targets or verified results.
- Choose a UML diagram type that suits the point being explained. Check class names, relationships, multiplicities, messages, lifelines, guards, and control flow against source code. Keep diagrams small enough to read, and update both the `.puml` source and rendered image together.
- Use genuine, current screenshots for user-visible workflows. Place each image next to the relevant explanation, add useful alternative text, and remove obsolete mockups or inherited examples that could be mistaken for the product.
- Check Markdown rendering, table of contents, anchors, relative links, image paths, captions, spelling, and consistent terminology. Run a local link/image check and inspect the rendered pages when practical.
- In the PR, identify the exact guide sections and diagram files changed, provide the behavior or source evidence used to verify them, and state any platform or test coverage that remains unverified.

## Release readiness

- Build the exact candidate JAR with `./gradlew clean shadowJar` when release behavior is affected.
- Smoke-test that exact JAR from a clean temporary folder, using the documented Java version and commands.
- Audit the UG against the exact candidate JAR, including commands, examples, screenshots, validation rules, and error messages.
- Record the tested commit and JAR identity in the release evidence. Keep release notes, issues, milestones, assignments, and approvals complete and accurate before publication.

## Reviews

- Review the complete diff, linked issue, tests, CI results, and affected UG/DG sections.
- Leave line-specific comments only for genuine correctness, edge-case, testing, error-handling, duplication, defensive-programming, or maintainability concerns.
- Do not invent feedback or focus on formatting trivia. Approve a sound PR once all substantive concerns are resolved.
