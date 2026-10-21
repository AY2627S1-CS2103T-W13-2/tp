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

* **`shadowJar`**: Uses the Shadow plugin to create `build/libs/addressbook-mac-aarch64.jar`
  when building with native arm64 Java on macOS; other hosts produce `build/libs/addressbook.jar`.<br>
  Use the explicit classifier and verification commands in
  [Packaging JavaFX for macOS architectures](DeveloperGuide.md#packaging-javafx-for-macos-architectures)
  to build both release variants. `./gradlew shadowJar` alone does not verify the native payloads.

* **`run`**: Builds and runs the application.<br>
  **`runShadow`**: Builds the application as a fat JAR, then runs it.

* **`checkstyleMain`**: Runs the code style check for the main code base.<br>
  **`checkstyleTest`**: Runs the code style check for the test code base.

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

Here are the steps to create a new release.

1. Agree the release version and new tag in release tracking. Update `MainApp.VERSION`,
   guide filenames and release asset names before merging the release source.
1. From that exact merged commit, build and verify **both** variants using the
   [architecture-specific recipe](DeveloperGuide.md#packaging-javafx-for-macos-architectures).
   Preserve each output before the next `clean`; record the full commit and each SHA-256.
1. Test those exact artifacts on the supported runtimes (including native M1 startup
   with a fresh JavaFX cache), audit the guide and screenshots, and obtain renewed
   approval for those hashes. Rebuilding invalidates the previous artifact approval.
1. Create the agreed tag at the tested commit. Never silently move an existing published
   tag or attach a different commit's binaries to it. The tag and generated source archives
   must match the approved build commit.
1. Create the release with detailed evidence and limitations, upload only the approved
   JARs/checksums/screenshots, and verify public downloads, hashes, tag/source identity
   and documentation links. Complete remaining issue tracking before closing the release
   issue and milestone.
