package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Role;
import seedu.address.testutil.PersonBuilder;

public class AssignRoleCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndex_assignsRole() {
        Person participant = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Role role = new Role("Facilitator");
        AssignRoleCommand command = new AssignRoleCommand(INDEX_FIRST_PERSON, role);

        Person participantWithRole = new PersonBuilder(participant).withRole(role.value).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(participant, participantWithRole);

        String expectedMessage = String.format(AssignRoleCommand.MESSAGE_ASSIGN_ROLE_SUCCESS,
                role, participant.getName());
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_existingRole_replacesRole() {
        Person participant = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person participantWithExistingRole = new PersonBuilder(participant).withRole("Facilitator").build();
        model.setPerson(participant, participantWithExistingRole);

        Role replacementRole = new Role("Logistics Lead");
        AssignRoleCommand command = new AssignRoleCommand(INDEX_FIRST_PERSON, replacementRole);
        Person participantWithReplacementRole = new PersonBuilder(participantWithExistingRole)
                .withRole(replacementRole.value).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(participantWithExistingRole, participantWithReplacementRole);

        String expectedMessage = String.format(AssignRoleCommand.MESSAGE_ASSIGN_ROLE_SUCCESS,
                replacementRole, participant.getName());
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBounds = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        AssignRoleCommand command = new AssignRoleCommand(outOfBounds, new Role("Facilitator"));
        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        AssignRoleCommand command = new AssignRoleCommand(INDEX_FIRST_PERSON, new Role("Facilitator"));

        assertTrue(command.equals(command));
        assertTrue(command.equals(new AssignRoleCommand(INDEX_FIRST_PERSON, new Role("Facilitator"))));
        assertFalse(command.equals(new AssignRoleCommand(INDEX_SECOND_PERSON, new Role("Facilitator"))));
        assertFalse(command.equals(new AssignRoleCommand(INDEX_FIRST_PERSON, new Role("Leader"))));
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
    }

    @Test
    public void toStringMethod() {
        Role role = new Role("Facilitator");
        AssignRoleCommand command = new AssignRoleCommand(INDEX_FIRST_PERSON, role);
        String expected = AssignRoleCommand.class.getCanonicalName() + "{index=" + INDEX_FIRST_PERSON
                + ", role=" + role + "}";
        assertEquals(expected, command.toString());
    }
}
