---
layout: page
title: DevOps guide
---

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Build automation

This project uses Gradle for **build automation and dependency management**. **We recommend reading [this Gradle tutorial from se-edu/guides](https://se-education.org/guides/tutorials/gradle.html).**


The following commands perform common Gradle tasks.


* **`clean`**: Deletes the files created during the previous build tasks (e.g. files in the `build` folder).<br>
  For example: `./gradlew clean`

* **`shadowJar`**: Uses the Shadow plugin to create the fat JAR file `build/libs/addressbook.jar`.<br>
  For example: `./gradlew shadowJar`

* **`run`**: Builds and runs the application.<br>
  **`runShadow`**: Builds the application as a fat JAR, then runs it.

* **`checkstyleMain`**: Runs the code style check for the main code base.<br>
  **`checkstyleTest`**: Runs the code style check for the test code base.

* **`check`**: Runs tests and Checkstyle; use `./gradlew check coverage` to generate coverage evidence too.

* **`test`**
  * `./gradlew test`: Runs all tests.
  * `./gradlew clean test`: Cleans the project before running all tests

--------------------------------------------------------------------------------------------------------------------

## Continuous integration (CI)

This project uses GitHub Actions for CI. The necessary workflow configuration files are in `.github/workflows`. No further setup is required.

### Code coverage

As part of CI, Gradle generates JaCoCo coverage reports from the tests, and CI uploads the coverage data to Codecov. Codecov then provides information about test coverage.

However, because Codecov is known to run into intermittent problems (e.g., report upload fails) due to issues on the Codecov service side, the CI is configured to pass even if the Codecov task failed. Therefore, developers are advised to check the code coverage levels periodically and take corrective actions if the coverage level falls below desired levels.

To enable Codecov for forks of this project, follow the steps given in [this se-edu guide](https://se-education.org/guides/tutorials/codecov.html).

### Repository-wide checks

In addition to Gradle checks, CI runs repository-wide checks. Unlike Gradle checks, which cover files used in the build, these checks cover every repository file and enforce rules that are hard to apply on development machines, such as line-ending requirements.

These checks are implemented as POSIX shell scripts, and thus can only be run on POSIX-compliant operating systems such as macOS and Linux. To run all checks locally on these operating systems, execute the following in the repository root directory:

`./.github/run-checks.sh`

Any warnings or errors will be printed out to the console.

**If adding new checks:**

* Checks are implemented as executable `check-*` scripts within the `.github` directory. The `run-checks.sh` script will automatically pick up and run files named as such. That is, you can add more such files if you need and the CI will do the rest.

* Check scripts should print out errors in the format `SEVERITY:FILENAME:LINE: MESSAGE`
  * SEVERITY is either ERROR or WARN.
  * FILENAME is the path to the file relative to the current directory.
  * LINE is the line of the file where the error occurred and MESSAGE is the message explaining the error.

* Check scripts must exit with a non-zero exit code if any errors occur.

--------------------------------------------------------------------------------------------------------------------

## Making a release

Release an **audited artifact**, not just a passing source tree:

1. Merge all intended code, documentation, and version changes. Confirm the release issue, milestone, and approvals
   refer to the same final commit. Update the version in [`MainApp.java`](https://github.com/AY2627S1-CS2103T-W13-2/tp/blob/master/src/main/java/seedu/address/MainApp.java).
1. Run `./gradlew check coverage` and `./.github/run-checks.sh` from that commit; inspect hosted CI. Record failures and
   the exact commands used to resolve them.
1. Build each required platform package from that commit using the commands documented in the release issue and
   [Developer Guide](DeveloperGuide.md), after the corresponding build changes have merged. Preserve each output
   before a subsequent `clean`.
1. Record the SHA-256 of **each** packaged JAR. In a clean temporary folder, launch and smoke-test the exact candidate
   on the operating systems and Java architectures available. Test core commands, save/reopen, and the User Guide's
   examples; explicitly record any platform that was **not** tested. Review screenshots against that same candidate.
1. Have another team member audit the User Guide, Developer Guide, release notes, and assets. Fix discrepancies
   before publication. Any rebuild or code change produces a new artifact identity and requires renewed relevant
   tests, documentation audit, and approval.
1. After explicit approval of the exact candidates, create or update the [GitHub release](https://github.com/AY2627S1-CS2103T-W13-2/tp/releases).
   Upload the correctly named JARs and current screenshots, list the tested commit and checksums, and state actual
   platform coverage and known limitations. Verify the public downloads before closing the release issue and milestone.

The standard `shadowJar` build currently produces `build/libs/addressbook.jar`. A platform-specific packaging change
must be merged and tested before its additional artifact is described as a published download. Never reuse a checksum
or approval from an earlier candidate for a rebuilt JAR.
