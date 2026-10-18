## Linked work

- Closes #
- Milestone:
- Assignee(s):

## Behavior summary

Describe the single concern addressed by this PR, including what changed and why.

## Test evidence

List the automated and manual checks performed, including relevant positive, negative, boundary, parser, and persistence cases.

## Documentation impact

Describe the UG, DG, and UML updates, or explain why no documentation change is required.

## Release impact

Describe any JAR, smoke-test, screenshot, release-note, migration, or compatibility impact, or state that there is none.

## Quality checklist

- [ ] This PR addresses one independent concern and is linked to a clearly assigned issue in the current milestone.
- [ ] The branch uses an appropriate conventional prefix and contains no assistant or tool branding.
- [ ] Commit subjects are imperative, capitalized, have no trailing period, target 50 characters, and do not exceed 72 characters; non-trivial commits have explanatory bodies as required by the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).
- [ ] Java changes follow the [basic](https://se-education.org/guides/conventions/java/basic.html) and [intermediate](https://se-education.org/guides/conventions/java/intermediate.html) SE-EDU Java coding standards.
- [ ] I manually checked naming, SLAP, duplication, nesting, documentation, validation, error messages, and defensive programming.
- [ ] I added all applicable positive, negative, boundary, parser, and persistence tests.
- [ ] I ran `./gradlew check coverage` successfully.
- [ ] I ran `.github/run-checks.sh` successfully.
- [ ] I updated the UG for all user-visible behavior, command, example, validation-rule, and error-message changes, or explained why not.
- [ ] I updated the DG and UML for architecture or interaction changes, or explained why not.
- [ ] Issue status, assignees, milestone, PR metadata, and release notes are accurate.
- [ ] A reviewer has inspected the full diff, linked issue, tests, CI results, and affected UG/DG sections.
- [ ] If release behavior is affected, I ran `./gradlew clean shadowJar`, smoke-tested the exact JAR from a clean temporary folder, and checked UG/JAR consistency.
- [ ] If release behavior is affected, I recorded the tested commit and JAR identity in the release evidence.
