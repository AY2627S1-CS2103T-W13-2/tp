package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_LOGISTICS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_PROGRAMMES;
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
import seedu.address.model.group.Group;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class AssignGroupCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToAssign = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Group group = new Group(VALID_GROUP_LOGISTICS);
        AssignGroupCommand command = new AssignGroupCommand(INDEX_FIRST_PERSON, group);

        Person assignedPerson = new PersonBuilder(personToAssign).withGroup(VALID_GROUP_LOGISTICS).build();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToAssign, assignedPerson);

        String expectedMessage = String.format(AssignGroupCommand.MESSAGE_ASSIGN_GROUP_SUCCESS,
                assignedPerson.getName(), group);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Person personToAssign = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Group group = new Group(VALID_GROUP_LOGISTICS);
        AssignGroupCommand command = new AssignGroupCommand(INDEX_FIRST_PERSON, group);

        Person assignedPerson = new PersonBuilder(personToAssign).withGroup(VALID_GROUP_LOGISTICS).build();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToAssign, assignedPerson);

        String expectedMessage = String.format(AssignGroupCommand.MESSAGE_ASSIGN_GROUP_SUCCESS,
                assignedPerson.getName(), group);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        AssignGroupCommand command = new AssignGroupCommand(outOfBoundIndex, new Group(VALID_GROUP_LOGISTICS));

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_existingGroup_replacesGroup() {
        Person originalPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personWithGroup = new PersonBuilder(originalPerson).withGroup(VALID_GROUP_PROGRAMMES).build();
        model.setPerson(originalPerson, personWithGroup);

        AssignGroupCommand command = new AssignGroupCommand(INDEX_FIRST_PERSON, new Group(VALID_GROUP_LOGISTICS));
        Person expectedPerson = new PersonBuilder(personWithGroup).withGroup(VALID_GROUP_LOGISTICS).build();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personWithGroup, expectedPerson);

        String expectedMessage = String.format(AssignGroupCommand.MESSAGE_ASSIGN_GROUP_SUCCESS,
                expectedPerson.getName(), VALID_GROUP_LOGISTICS);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_existingRole_rolePreserved() {
        Person originalPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personWithRole = new PersonBuilder(originalPerson).withRole("Facilitator").build();
        model.setPerson(originalPerson, personWithRole);

        Group group = new Group(VALID_GROUP_LOGISTICS);
        AssignGroupCommand command = new AssignGroupCommand(INDEX_FIRST_PERSON, group);
        Person expectedPerson = new PersonBuilder(personWithRole).withGroup(VALID_GROUP_LOGISTICS).build();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personWithRole, expectedPerson);

        String expectedMessage = String.format(AssignGroupCommand.MESSAGE_ASSIGN_GROUP_SUCCESS,
                expectedPerson.getName(), group);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void equals() {
        AssignGroupCommand firstCommand = new AssignGroupCommand(INDEX_FIRST_PERSON,
                new Group(VALID_GROUP_LOGISTICS));
        AssignGroupCommand secondCommand = new AssignGroupCommand(INDEX_SECOND_PERSON,
                new Group(VALID_GROUP_LOGISTICS));
        AssignGroupCommand differentGroupCommand = new AssignGroupCommand(INDEX_FIRST_PERSON,
                new Group(VALID_GROUP_PROGRAMMES));

        assertTrue(firstCommand.equals(firstCommand));
        assertTrue(firstCommand.equals(new AssignGroupCommand(INDEX_FIRST_PERSON,
                new Group(VALID_GROUP_LOGISTICS))));
        assertFalse(firstCommand.equals(secondCommand));
        assertFalse(firstCommand.equals(differentGroupCommand));
        assertFalse(firstCommand.equals(null));
        assertFalse(firstCommand.equals(1));
    }

    @Test
    public void toStringMethod() {
        Group group = new Group(VALID_GROUP_LOGISTICS);
        AssignGroupCommand command = new AssignGroupCommand(INDEX_FIRST_PERSON, group);
        String expected = AssignGroupCommand.class.getCanonicalName() + "{index=" + INDEX_FIRST_PERSON
                + ", group=" + group + "}";
        assertEquals(expected, command.toString());
    }
}
