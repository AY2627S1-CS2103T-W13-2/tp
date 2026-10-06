package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class ViewCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_bothAssignments_displaysRoleAndActivity() throws Exception {
        Person original = model.getFilteredPersonList().get(0);
        Person assigned = new PersonBuilder(original).withRole("Facilitator").withActivity("Campfire").build();
        model.setPerson(original, assigned);

        String feedback = new ViewCommand(INDEX_FIRST_PERSON).execute(model).getFeedbackToUser();
        assertTrue(feedback.contains("Role: Facilitator"));
        assertTrue(feedback.contains("Activity: Campfire"));
        assertEquals(assigned, model.getFilteredPersonList().get(0));
    }

    @Test
    public void execute_validIndexUnfilteredList_displaysCompleteRecordWithoutChangingModel() {
        Person participantToView = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        ViewCommand viewCommand = new ViewCommand(INDEX_FIRST_PERSON);
        String expectedMessage = String.format(ViewCommand.MESSAGE_VIEW_PARTICIPANT_SUCCESS,
                Messages.format(participantToView));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(viewCommand, model, new CommandResult(expectedMessage), expectedModel);
    }

    @Test
    public void execute_validIndexFilteredList_displaysCompleteRecordWithoutChangingModel() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Person participantToView = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        ViewCommand viewCommand = new ViewCommand(INDEX_FIRST_PERSON);
        String expectedMessage = String.format(ViewCommand.MESSAGE_VIEW_PARTICIPANT_SUCCESS,
                Messages.format(participantToView));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        assertCommandSuccess(viewCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_outOfRangeIndexUnfilteredList_throwsCommandException() {
        Index outOfRangeIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        ViewCommand viewCommand = new ViewCommand(outOfRangeIndex);

        assertCommandFailure(viewCommand, model, ViewCommand.MESSAGE_INVALID_PARTICIPANT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_outOfRangeIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        ViewCommand viewCommand = new ViewCommand(INDEX_SECOND_PERSON);

        assertCommandFailure(viewCommand, model, ViewCommand.MESSAGE_INVALID_PARTICIPANT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        ViewCommand viewFirstCommand = new ViewCommand(INDEX_FIRST_PERSON);
        ViewCommand viewSecondCommand = new ViewCommand(INDEX_SECOND_PERSON);

        assertTrue(viewFirstCommand.equals(viewFirstCommand));
        assertTrue(viewFirstCommand.equals(new ViewCommand(INDEX_FIRST_PERSON)));
        assertFalse(viewFirstCommand.equals(viewSecondCommand));
        assertFalse(viewFirstCommand.equals(1));
        assertFalse(viewFirstCommand.equals(null));
    }

    @Test
    public void toStringMethod() {
        ViewCommand viewCommand = new ViewCommand(INDEX_FIRST_PERSON);
        String expected = ViewCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON + "}";

        assertEquals(expected, viewCommand.toString());
    }
}
