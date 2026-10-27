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
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class ViewCommandTest {

    private static final String ALICE_DETAILS = String.join("\n",
            "Participant details:",
            "Name: Alice Pauline",
            "Phone: 94351253",
            "Email: alice@example.com",
            "Address: 123, Jurong West Ave 6, #08-111",
            "Role: Unassigned",
            "Activity: Unassigned",
            "Group: Unassigned",
            "Tags: friends",
            "Remark: None");

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ViewCommand(null));
    }

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
    public void execute_multipleTags_displaysSortedSeparatedTagNames() throws Exception {
        Person original = model.getFilteredPersonList().get(0);
        Person tagged = new PersonBuilder(original).withTags("zeta", "Alpha").build();
        model.setPerson(original, tagged);

        String feedback = new ViewCommand(INDEX_FIRST_PERSON).execute(model).getFeedbackToUser();

        assertTrue(feedback.contains("Tags: Alpha, zeta"));
    }

    @Test
    public void execute_noTags_displaysNone() throws Exception {
        Person original = model.getFilteredPersonList().get(0);
        Person untagged = new PersonBuilder(original).withTags().build();
        model.setPerson(original, untagged);

        String feedback = new ViewCommand(INDEX_FIRST_PERSON).execute(model).getFeedbackToUser();

        assertTrue(feedback.contains("\nTags: None\n"));
    }

    @Test
    public void execute_unassignedDetails_displaysReadableLabels() throws Exception {
        String feedback = new ViewCommand(INDEX_FIRST_PERSON).execute(model).getFeedbackToUser();

        assertTrue(feedback.contains("\nName: Alice Pauline\n"));
        assertTrue(feedback.contains("\nRole: Unassigned\n"));
        assertTrue(feedback.contains("\nActivity: Unassigned\n"));
        assertTrue(feedback.contains("\nGroup: Unassigned\n"));
    }

    @Test
    public void execute_nonEmptyRemark_displaysExactTextWithoutChangingModel() throws Exception {
        Person original = model.getFilteredPersonList().get(0);
        Person remarked = new PersonBuilder(original).withRemark("Needs wheelchair access").build();
        model.setPerson(original, remarked);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        String feedback = new ViewCommand(INDEX_FIRST_PERSON).execute(model).getFeedbackToUser();

        assertTrue(feedback.endsWith("Remark: Needs wheelchair access"));
        assertEquals(expectedModel, model);
    }

    @Test
    public void execute_emptyRemark_displaysNone() throws Exception {
        String feedback = new ViewCommand(INDEX_FIRST_PERSON).execute(model).getFeedbackToUser();

        assertTrue(feedback.endsWith("Remark: None"));
    }

    @Test
    public void execute_validIndexUnfilteredList_displaysCompleteRecordWithoutChangingModel() {
        ViewCommand viewCommand = new ViewCommand(INDEX_FIRST_PERSON);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(viewCommand, model, new CommandResult(ALICE_DETAILS), expectedModel);
    }

    @Test
    public void execute_validIndexFilteredList_displaysCompleteRecordWithoutChangingModel() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        ViewCommand viewCommand = new ViewCommand(INDEX_FIRST_PERSON);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        assertCommandSuccess(viewCommand, model, ALICE_DETAILS, expectedModel);
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
