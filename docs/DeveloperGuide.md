---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the originals.}_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams are in `docs/diagrams`. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Logic.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.

</div>

Similarly, how an undo operation goes through the `Model` component is shown below:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Model.png)

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command is correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* is a director coordinating a large-scale computing event, such as an orientation camp
* repeatedly records, retrieves, and corrects participant information
* needs to track how participants are assigned to groups, roles, and activities
* prefers fast, keyboard-driven workflows
* needs participant data to remain available locally without relying on a remote service

**Value proposition**: ZoomAddress helps computing event directors keep track of participants and their relevant
information, making it easier to coordinate people across different groups, roles, and activities during large-scale
computing events.

ZoomAddress covers a local participant directory and participant-to-group, participant-to-role, and
participant-to-activity assignments. It does not cover registration collection, messaging, payments, a full event
timetable, simultaneous multi-user editing, or remote hosting.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| ID | Priority | As a …​ | I want to …​ | So that I can …​ |
| -- | -------- | ------- | ------------ | ---------------- |
| ZA-01 | `* * *` | event director | add a participant | record someone involved in the event |
| ZA-02 | `* * *` | event director | view all participants | see whom I am coordinating |
| ZA-03 | `* * *` | event director | view one participant's full record | understand that participant's relevant details |
| ZA-04 | `* * *` | busy event director | find participants by name | retrieve a record quickly |
| ZA-05 | `* *` | event director | find a participant by contact information | identify a person when the name is uncertain |
| ZA-06 | `* *` | event director | edit a participant's details | correct or update information |
| ZA-07 | `* * *` | event director | delete a participant | remove withdrawn or mistakenly entered people |
| ZA-08 | `*` | new user | see sample participant data | understand the information ZoomAddress manages |
| ZA-09 | `*` | user ready to enter real data | clear all sample data | begin with a clean directory |
| ZA-10 | `* *` | event director | be warned about a possible duplicate | avoid conflicting participant records |
| ZA-11 | `* *` | event director | record the groups used by the event | use meaningful assignment groupings |
| ZA-12 | `* * *` | event director | assign a participant to a group | know which group is responsible for them |
| ZA-13 | `* *` | event director | move a participant to another group | keep late allocation changes accurate |
| ZA-14 | `* *` | event director | list the participants in a group | coordinate that group as a unit |
| ZA-15 | `* *` | event director | list participants without a group | finish incomplete allocations |
| ZA-16 | `* *` | event director | record the roles used by the event | use consistent responsibility labels |
| ZA-17 | `* * *` | event director | assign a role to a participant | know what the participant is responsible for |
| ZA-18 | `* *` | event director | assign more than one role to a participant | represent overlapping responsibilities |
| ZA-19 | `* *` | event director | list participants with a specified role | find the people responsible for a task |
| ZA-20 | `* *` | event director | record the activities in an event | use consistent activity names |
| ZA-21 | `* * *` | event director | assign a participant to an activity | know who is expected to take part |
| ZA-22 | `* *` | event director | remove a participant from an activity | keep withdrawals and changes accurate |
| ZA-23 | `* *` | event director | list participants assigned to an activity | prepare and coordinate that activity |
| ZA-24 | `* *` | event director | list participants without any activity | identify incomplete allocations |
| ZA-25 | `* *` | busy event director | filter by group, role, and activity together | answer operational questions quickly |
| ZA-26 | `* *` | event director | sort participant records by name | scan a large directory predictably |
| ZA-27 | `*` | event director | add a short note to a participant | retain context not covered by standard fields |
| ZA-28 | `*` | event director | mark a participant's attendance | see who has arrived |
| ZA-29 | `*` | event director | list participants whose attendance is unconfirmed | follow up on missing people |
| ZA-30 | `*` | event director | record authorised dietary or accessibility needs | support participants appropriately |
| ZA-31 | `*` | event director | record an authorised emergency contact | respond appropriately during an incident |
| ZA-32 | `*` | event director migrating from a spreadsheet | import participant records in bulk | avoid re-entering every participant |
| ZA-33 | `*` | event director | export an authorised participant list | use it in permitted offline workflows |
| ZA-34 | `*` | event director who made a mistake | undo my latest data-changing action | recover quickly |
| ZA-35 | `* * *` | event director | retain data between sessions | avoid losing records when the app closes |
| ZA-36 | `* * *` | privacy-conscious event director | avoid sending data to a remote server | keep participant records local |
| ZA-37 | `*` | event director | create a local backup | recover from device or file failure |
| ZA-38 | `* *` | first-time user | access concise usage help | perform essential operations independently |
| ZA-39 | `* * *` | experienced user | perform common operations by typing | update records quickly during a busy event |
| ZA-40 | `* *` | event director | see a clear confirmation after a change | know whether records were updated |

### Use cases

(For all use cases below, the **System** is `ZoomAddress` and the **Actor** is the event director.)

**Use case UC01: Add a participant**

**MSS**

1.  Event director enters the participant's name, phone, email, and address.
2.  ZoomAddress validates the supplied information.
3.  ZoomAddress checks the phone number and email address for a definite duplicate.
4.  ZoomAddress adds and saves the participant.
5.  ZoomAddress refreshes the list, selects the new participant, and confirms the addition.

    Use case ends.

**Extensions**

* 2a. Any supplied field is invalid.

  * 2a1. ZoomAddress shows the relevant validation error.

    Use case ends.

* 3a. A participant has the same normalized phone number or email address.

  * 3a1. ZoomAddress reports that the participant already exists.

    Use case ends.

* 4a. ZoomAddress cannot save the change.

  * 4a1. ZoomAddress rolls back the addition and reports that no participant was added.

    Use case ends.

**Use case UC02: Assign a participant to event structures**

**MSS**

1.  Event director lists or finds participants.
2.  ZoomAddress displays the matching participants with visible indexes.
3.  Event director assigns the selected participant to a group.
4.  Event director assigns a role to the participant.
5.  Event director assigns an activity to the participant.
6.  ZoomAddress saves each change and displays the updated record.

    Use case ends.

**Extensions**

* 2a. No participant matches the search.

  * 2a1. ZoomAddress reports that no participants matched.

    Use case ends.

* 3a. The visible index or assignment label is invalid.

  * 3a1. ZoomAddress shows the relevant error without changing the record.

    Use case resumes at step 2.

* 4a. The participant already has the normalized role.

  * 4a1. ZoomAddress reports the duplicate role.

    Use case resumes at step 4.

* 5a. The participant is already assigned to the normalized activity.

  * 5a1. ZoomAddress reports the duplicate activity.

    Use case resumes at step 5.

**Use case UC03: Find and inspect a participant**

**MSS**

1.  Event director enters one or more name keywords.
2.  ZoomAddress displays participants whose names contain at least one complete matching word.
3.  Event director requests to view one participant using the visible index.
4.  ZoomAddress highlights the participant and displays the full record.

    Use case ends.

**Extensions**

* 1a. No keyword is supplied.

  * 1a1. ZoomAddress requests at least one name keyword.

    Use case ends.

* 2a. No participant matches the keywords.

  * 2a1. ZoomAddress displays an empty result list and a no-matches message.

    Use case ends.

* 3a. The given index is invalid.

  * 3a1. ZoomAddress shows an error message.

    Use case resumes at step 2.

### Non-Functional Requirements

1.  ZoomAddress should work on any _mainstream OS_ with Java `25` or above installed.
2.  ZoomAddress should support at least 1000 participant records, with typical commands completing within 2 seconds.
3.  A user with above-average typing speed should be able to complete common record operations faster using commands
    than using a mouse-driven interface.
4.  Participant data must remain on the user's device; ZoomAddress must not require a remote server or internet
    connection for core operations.
5.  Every successful data-changing command must be saved automatically before success is reported to the user.
6.  A failed or interrupted save must not silently overwrite the last valid data file.
7.  Commands and validation errors should use consistent terminology and state how the user can correct the input.
8.  The application should start and display the participant directory within 5 seconds on a typical course-approved
    computer.

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, or macOS
* **Participant**: A person involved in the computing event and recorded in ZoomAddress
* **Group**: The single operational unit currently responsible for a participant
* **Role**: A responsibility held by a participant; a participant can have multiple roles
* **Activity**: An event programme or task in which a participant is involved
* **Visible index**: A positive integer referring to a participant's position in the list currently shown
* **Normalized value**: A value transformed for comparison by applying its field-specific spacing and case rules
* **Definite duplicate**: A participant record with a normalized phone number or email address matching an existing
  record

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command, with multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: The first contact is deleted from the list. The status message shows the deleted contact's details.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases …​ }_
