---
layout: page
title: Testing guide
---

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Running tests

You can run tests in two ways.

* **Method 1: Using IntelliJ JUnit test runner**
  * To run all tests, right-click on the `src/test/java` folder and choose `Run 'All Tests'`
  * To run a subset of tests, you can right-click on a test package,
    test class, or a test and choose `Run 'ABC'`
* **Method 2: Using Gradle**
  * Open a console and run the command `gradlew clean test` (Mac/Linux: `./gradlew clean test`)

<div markdown="span" class="alert alert-secondary">:link: **Link**: Read [this Gradle Tutorial from the se-edu/guides](https://se-education.org/guides/tutorials/gradle.html) to learn more about using Gradle.
</div>

--------------------------------------------------------------------------------------------------------------------

## Types of tests

This project has three types of tests:

1. *Unit tests* target the lowest-level methods and classes.<br>
   For example: `seedu.address.commons.StringUtilTest`
1. *Integration tests* check how multiple code units work together; the individual units are assumed to work.<br>
   For example: `seedu.address.storage.StorageManagerTest`
1. *Hybrid tests* combine unit and integration testing. These tests check both the individual units and how they work together.<br>
   For example: `seedu.address.logic.LogicManagerTest`

--------------------------------------------------------------------------------------------------------------------

## Command validation and workflow tests

`NoArgumentsCommandParserTest` checks the shared parser used by `list`, `clear`, `help`, and `exit` at the unit level.
`AddressBookParserTest` checks that each command is dispatched correctly and rejects extraneous arguments with its
own usage message. `NoArgumentsCommandIntegrationTest` then exercises the complete parser-command-model-storage path,
including persistence after `clear` and recovery with a valid command after invalid input.

--------------------------------------------------------------------------------------------------------------------

## Performance regression test

`LogicPerformanceTest` provides an automated regression check for the response-time target in the Developer Guide.
It starts with 1,000 participant records and times 100 valid commands spanning add, list, find, edit, delete, view,
and group, role, and activity assignment. At least 95 commands must complete within two seconds each. Each measurement
includes parsing, model updates, and JSON persistence.

The test deliberately excludes JavaFX rendering and therefore complements, rather than replaces, a manual release-JAR
trial on the target operating systems. Run it alone with:

`./gradlew test --tests seedu.address.logic.LogicPerformanceTest`
