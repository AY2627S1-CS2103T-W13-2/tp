---
layout: page
title: User Guide
---

ZoomAddress is a **desktop participant-management application for directors of large-scale computing events**. It is optimized for keyboard-driven commands while retaining the benefits of a graphical interface, allowing event directors to manage participant records quickly and locally.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from the [ZoomAddress releases page](https://github.com/AY2627S1-CS2103T-W13-2/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for ZoomAddress.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   The ZoomAddress window should appear in a few seconds with some sample data.

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all participants.

   * `sort name` : Sorts the currently displayed participants by name.

   * `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01` : Adds a participant named `John Doe` to ZoomAddress.

   * `delete 3` : Deletes the 3rd participant shown in the current list.

   * `view 3` : Displays all stored details of the 3rd participant shown in the current list.

   * `find-contact 98765432` : Finds participants with the exact phone number `98765432`.

   * `clear` : Deletes all participant records.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items followed by `…`​ can appear zero or more times.<br>
  For example, `[t/TAG]…​` may be omitted, or written as `t/friend` or `t/friend t/family`.

* For `add` and `edit`, prefixed fields can be in any order.<br>
  For example, `p/PHONE_NUMBER n/NAME` is also acceptable when adding a participant. Commands such as `unassign`
  require the displayed index before the assignment prefix.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

ZoomAddress adds `view`, `assign-group`, `assign-role`, `assign-activity`, `unassign`, `sort`, `filter`, and
`find-contact` to the original AddressBook-Level3 commands. The other commands below retain their familiar
AddressBook workflow, with participant-focused wording.

### Viewing help: `help`

Opens a window with a link to this ZoomAddress User Guide.

Format: `help`


### Adding a participant: `add`

Adds a participant to ZoomAddress.

Format: `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​`

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A participant can have any number of tags, including zero.
</div>

Examples:
* `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01`
* `add n/Betsy Crowe t/friend e/betsycrowe@example.com a/Newgate Prison p/1234567 t/criminal`

### Listing all participants: `list`

Shows a list of all participants in ZoomAddress.

Format: `list`

### Sorting participants by name: `sort`

Sorts the currently displayed participants by name without changing any participant data.

Format: `sort name`

* Names are sorted in ascending alphabetical order using a case-insensitive comparison.
* If two names are equal under the case-insensitive comparison, their exact capitalization is
  used as a deterministic secondary comparison.
* Sorting applies to the current view. For example, after `find Alex`, `sort name` sorts only the
  matching participants.
* The displayed index numbers are updated to match the sorted order. Commands such as `view 1`,
  `edit 1`, and `delete 1` therefore act on the first participant shown after sorting.
* Sorting is a view operation: it does not rewrite participant records or modify their fields.
* The sorted view lasts for the current application session but is not saved. After restarting
  ZoomAddress, enter `sort name` again to restore the sorted view.

Valid example:
* `sort name` sorts `alice`, `Bob`, and `charlie` into that displayed order and shows
  `Sorted participants by name in ascending order.`

Invalid examples:
* `sort` is rejected because the criterion is missing.
* `sort role` is rejected because only the `name` criterion is supported.
* `sort name descending` is rejected because extra arguments are not accepted.

### Viewing a participant: `view`

Displays all stored details of a participant without changing the participant's data.

Format: `view INDEX`

* Displays the participant at the specified `INDEX`.
* The index refers to the index number shown in the displayed participant list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list` followed by `view 2` displays all stored details of the 2nd participant in ZoomAddress.
* `find Betsy` followed by `view 1` displays all stored details of the 1st participant in the results of the `find` command.

### Editing a participant: `edit`

Edits an existing participant in ZoomAddress.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`

* Edits the participant at the specified `INDEX`. The index refers to the index number shown in the displayed participant list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, all of the participant's existing tags are removed; adding tags is not cumulative.
* To remove all of a participant's tags, enter `t/` without a tag after it.

Examples:
*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st participant to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd participant to be `Betsy Crower` and clears all existing tags.

### Assigning an activity: `assign-activity`

Assigns one activity to an existing participant and displays it on their card.

Format: `assign-activity INDEX a/ACTIVITY`

* `INDEX` is the positive integer shown beside the participant in the current list. After `find`, use the index in the search results.
* Each participant can have at most one activity. Assigning another activity replaces the previous assignment.
* `ACTIVITY` must not be blank. Names can contain spaces and punctuation; surrounding whitespace is removed.
* Supply `a/` exactly once. In this command it means activity; in `add` and `edit`, it still means address.
* The assignment is saved automatically and retained after restarting or editing the participant's other details. Assigning or replacing an activity preserves their role; assigning or replacing a role preserves their activity.
* Records created before activity assignments were introduced start without an activity. Multiple activities and
  attendance are not supported yet.

Examples:
* `assign-activity 1 a/Campfire` assigns Campfire to the first participant in the displayed list.
* `assign-activity 1 a/Arts & Crafts` replaces that participant's activity with Arts & Crafts.

### Assigning a role to a participant: `assign-role`

Assigns one event role to an existing participant.

Format: `assign-role INDEX r/ROLE`

* Assigns the role to the participant at the specified `INDEX` in the displayed participant list.
* The index **must be a positive integer** 1, 2, 3, …​ and refer to a displayed participant.
* The role must contain at least one non-whitespace character.
* Assigning another role to the same participant replaces the existing role.

Example:
* `assign-role 2 r/Logistics Lead` assigns the role `Logistics Lead` to the second displayed participant.

### Assigning a participant to a group: `assign-group`

Assigns one group to an existing participant.

Format: `assign-group INDEX g/GROUP`

* Assigns the participant at the specified `INDEX` to `GROUP`.
* The index refers to the index number shown in the displayed participant list and must be a positive integer.
* `GROUP` must not be blank and may contain spaces.
* A participant can belong to at most one group. Assigning another group replaces the current group and preserves their role and activity.
* Managing a separate list of groups is not supported yet.

Example:
* `assign-group 2 g/Logistics` assigns the second displayed participant to the `Logistics` group.

### Clearing an assignment: `unassign`

Clears one group, role, or activity assignment from an existing participant.

Format: `unassign INDEX g/` or `unassign INDEX r/` or `unassign INDEX a/`

* `INDEX` must be a positive integer from the displayed participant list. After `find` or `sort name`, use the index
  shown in that filtered or sorted view.
* Supply exactly one empty prefix: `g/` for group, `r/` for role, or `a/` for activity. Do not add a value after the prefix.
* Clearing one assignment preserves the participant's name, contact details, tags, and other assignments. The change is saved automatically.
* You cannot clear an assignment that is already absent.
* On success, ZoomAddress names the removed value, for example,
  `Cleared group assignment 'Logistics' for Beth Lee.`

Examples:
* `unassign 2 g/` clears the group assigned to the second displayed participant.
* `unassign 2 r/` clears their role.
* `unassign 2 a/` clears their activity.

Invalid examples and responses:

| Command | Response |
|---|---|
| `unassign` | `Please provide a participant index before the assignment prefix.` |
| `unassign abc g/` | `Index must be a positive integer.` |
| `unassign 99 g/` | `The participant index provided is invalid.` if fewer than 99 participants are displayed. |
| `unassign 2` | `Please specify exactly one assignment prefix: g/, r/, or a/.` |
| `unassign 2 g/ r/` | `Specify only one assignment prefix: g/, r/, or a/.` |
| `unassign 2 g/ g/` | `Assignment prefix must not be repeated: g/` |
| `unassign 2 x/` | `Unsupported assignment prefix: x/ Use g/, r/, or a/.` |
| `unassign 2 g/Logistics` | `The g/ prefix must not contain a value when clearing the group assignment.` |
| `unassign 2 unexpected g/` | `Unexpected extra argument(s): unexpected g/` |

If Beth Lee has no group assignment, `unassign 2 g/` reports `Beth Lee has no group assignment to clear.`

### Filtering participants by assignment: `filter`

Filters the displayed participant list to one exact group, role, or activity. This helps event directors quickly find
everyone sharing an assignment during event operations.

Formats:
* `filter g/GROUP`
* `filter r/ROLE`
* `filter a/ACTIVITY`

* Begin with exactly one of `g/`, `r/`, or `a/`. Missing, repeated, or multiple supported prefixes are rejected with a
  specific error. Text before the assignment prefix is also rejected. An embedded slash such as in `Tech/Media` is
  ordinary value text, but a separate `g/`, `r/`, or `a/` token is treated as another prefix.
* Matching is case-insensitive and ignores leading and trailing whitespace. It remains exact after that normalisation:
  `filter g/Logistics` does not match `Logistics Team`.
* Participants without the selected assignment are excluded.
* A successful filter displays matching participants with list indices starting from 1. If nothing matches, the list
  is empty and ZoomAddress reports that 0 participants matched.
* Filtering only changes the displayed list. It does not modify, remove, or reorder stored participant data.

Examples:
* `filter g/Logistics` displays all participants assigned to the Logistics group.
* `filter r/facilitator` displays participants whose role is Facilitator, regardless of letter case.
* `filter a/  Campfire  ` displays participants assigned to Campfire after trimming the value.

### Locating participants by name: `find`

Finds participants whose names contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive; for example, `hans` matches `Hans`.
* Keyword order does not matter; for example, `Hans Bo` matches `Bo Hans`.
* The search considers only names.
* Only full words match; for example, `Han` does not match `Hans`.
* Participants matching at least one keyword are returned (an `OR` search); for example, `Hans Bo` returns `Hans Gruber` and `Bo Yang`.

Examples:
* `find John` returns `john` and `John Doe`
* `find alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)

### Locating participants by contact information: `find-contact`

Finds participants whose complete phone number or email address matches the given value.

Format: `find-contact PHONE_OR_EMAIL`

* Provide exactly one valid phone number or email address. Surrounding whitespace is ignored.
* Phone numbers are matched exactly. Partial phone numbers do not match.
* Email addresses are matched exactly and case-insensitively. For example, `ALEX@EXAMPLE.COM`
  matches `alex@example.com`.
* The search always considers every stored participant, even if the displayed list was previously filtered.
* The command only filters the displayed list. It does not change or delete participant data.

Examples:
* `find-contact 98765432` finds participants whose phone number is exactly `98765432`.
* `find-contact alex@example.com` finds participants whose email address is `alex@example.com`.

### Deleting a participant: `delete`

Deletes the specified participant from ZoomAddress.

Format: `delete INDEX`

* Deletes the participant at the specified `INDEX`.
* The index refers to the index number shown in the displayed participant list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list` followed by `delete 2` deletes the 2nd participant in ZoomAddress.
* `find Betsy` followed by `delete 1` deletes the 1st participant in the results of the `find` command.

### Clearing all entries: `clear`

Clears all participant records from ZoomAddress.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

ZoomAddress automatically saves data after every successful command. You do not need to save manually.

### Editing the data file

ZoomAddress data is saved automatically as a JSON file `data/addressbook.json` relative to the folder where you run
the `java -jar` command. The Quick Start steps make that folder the JAR file's location. Advanced users can update
data directly by editing the JSON file.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, ZoomAddress starts with an empty participant directory at the next run. The invalid file remains on disk until you run a command (ZoomAddress saves after every command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause ZoomAddress to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous ZoomAddress home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add** | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 t/friend t/colleague`
**Assign role** | `assign-role INDEX r/ROLE`<br> e.g., `assign-role 2 r/Logistics Lead`
**Assign group** | `assign-group INDEX g/GROUP`<br> e.g., `assign-group 2 g/Logistics`
**Clear** | `clear`
**Assign activity** | `assign-activity INDEX a/ACTIVITY`<br> e.g., `assign-activity 1 a/Campfire`
**Clear assignment** | `unassign INDEX g/` or `unassign INDEX r/` or `unassign INDEX a/`<br> e.g., `unassign 2 g/`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Edit** | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`<br> e.g., `edit 2 n/James Lee e/jameslee@example.com`
**Find** | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find James Jake`
**Find by contact** | `find-contact PHONE_OR_EMAIL`<br> e.g., `find-contact alex@example.com`
**Filter by assignment** | `filter g/GROUP` or `filter r/ROLE` or `filter a/ACTIVITY`<br> e.g., `filter g/Logistics`
**List** | `list`
**Sort** | `sort name`
**View** | `view INDEX`<br> e.g., `view 3`
**Help** | `help`
