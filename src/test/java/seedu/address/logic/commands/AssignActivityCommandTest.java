package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Activity;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class AssignActivityCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AssignActivityCommand(null, new Activity("Campfire")));
        assertThrows(NullPointerException.class, () -> new AssignActivityCommand(INDEX_FIRST_PERSON, null));
    }

    @Test
    public void execute_unassignedParticipant_success() {
        assertAssignmentSuccess("Campfire");
    }

    @Test
    public void execute_assignedParticipant_replacesActivity() {
        assertAssignmentSuccess("Campfire");
        assertAssignmentSuccess("Hiking");
    }

    @Test
    public void execute_sameActivity_success() {
        assertAssignmentSuccess("Campfire");
        assertAssignmentSuccess("Campfire");
    }

    @Test
    public void execute_filteredList_usesDisplayedIndex() throws Exception {
        Person participant = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Person firstParticipant = model.getFilteredPersonList().get(0);
        showPersonAtIndex(model, INDEX_SECOND_PERSON);

        new AssignActivityCommand(INDEX_FIRST_PERSON, new Activity("Campfire")).execute(model);

        Person expectedParticipant = new PersonBuilder(participant).withActivity("Campfire").build();
        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals(expectedParticipant, model.getFilteredPersonList().get(0));
        assertEquals(expectedParticipant,
                model.getAddressBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased()));
        assertEquals(firstParticipant, model.getAddressBook().getPersonList().get(0));
    }

    @Test
    public void execute_invalidIndex_failure() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        assertCommandFailure(new AssignActivityCommand(invalidIndex, new Activity("Campfire")), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidFilteredIndex_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertCommandFailure(new AssignActivityCommand(INDEX_SECOND_PERSON, new Activity("Campfire")), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_emptyList_failure() {
        assertCommandFailure(new AssignActivityCommand(INDEX_FIRST_PERSON, new Activity("Campfire")),
                new ModelManager(), Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        AssignActivityCommand command = new AssignActivityCommand(INDEX_FIRST_PERSON, new Activity("Campfire"));
        assertTrue(command.equals(command));
        assertTrue(command.equals(new AssignActivityCommand(INDEX_FIRST_PERSON, new Activity("Campfire"))));
        assertFalse(command.equals(new AssignActivityCommand(INDEX_SECOND_PERSON, new Activity("Campfire"))));
        assertFalse(command.equals(new AssignActivityCommand(INDEX_FIRST_PERSON, new Activity("Hiking"))));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ClearCommand()));
    }

    private void assertAssignmentSuccess(String activity) {
        Person participant = model.getFilteredPersonList().get(0);
        Person updatedParticipant = new PersonBuilder(participant).withActivity(activity).build();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(participant, updatedParticipant);
        assertCommandSuccess(new AssignActivityCommand(INDEX_FIRST_PERSON, new Activity(activity)), model,
                String.format(AssignActivityCommand.MESSAGE_SUCCESS, activity, participant.getName()), expectedModel);
    }
}
