package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class UnassignCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_groupAssignment_successAndOtherFieldsPreserved() {
        Person original = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person assigned = new PersonBuilder(original).withGroup("Logistics").withRole("Leader")
                .withActivity("Campfire").build();
        model.setPerson(original, assigned);

        UnassignCommand command = new UnassignCommand(INDEX_FIRST_PERSON, AssignmentType.GROUP);
        Person expected = clear(assigned, AssignmentType.GROUP);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(assigned, expected);

        assertCommandSuccess(command, model, String.format(UnassignCommand.MESSAGE_UNASSIGN_SUCCESS,
                "group", expected.getName()), expectedModel);
    }

    @Test
    public void execute_roleAndActivityAssignments_success() {
        Person original = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person assigned = new PersonBuilder(original).withRole("Leader").withActivity("Campfire").build();
        model.setPerson(original, assigned);

        Person withoutRole = clear(assigned, AssignmentType.ROLE);
        Model expectedAfterRole = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedAfterRole.setPerson(assigned, withoutRole);
        assertCommandSuccess(new UnassignCommand(INDEX_FIRST_PERSON, AssignmentType.ROLE), model,
                String.format(UnassignCommand.MESSAGE_UNASSIGN_SUCCESS, "role", assigned.getName()), expectedAfterRole);
        assertFalse(model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()).getRole().isPresent());

        Person withoutActivity = clear(withoutRole, AssignmentType.ACTIVITY);
        Model expectedAfterActivity = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedAfterActivity.setPerson(withoutRole, withoutActivity);
        assertCommandSuccess(new UnassignCommand(INDEX_FIRST_PERSON, AssignmentType.ACTIVITY), model,
                String.format(UnassignCommand.MESSAGE_UNASSIGN_SUCCESS, "activity", assigned.getName()),
                expectedAfterActivity);
        assertFalse(model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()).getActivity().isPresent());
    }

    @Test
    public void execute_filteredList_usesDisplayedIndex() {
        Person original = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person assigned = new PersonBuilder(original).withGroup("Logistics").build();
        model.setPerson(original, assigned);
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        UnassignCommand command = new UnassignCommand(INDEX_FIRST_PERSON, AssignmentType.GROUP);
        Person expected = clear(assigned, AssignmentType.GROUP);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(assigned, expected);
        assertCommandSuccess(command, model, String.format(UnassignCommand.MESSAGE_UNASSIGN_SUCCESS,
                "group", assigned.getName()), expectedModel);
    }

    @Test
    public void execute_missingAssignment_throwsCommandException() {
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertCommandFailure(new UnassignCommand(INDEX_FIRST_PERSON, AssignmentType.GROUP), model,
                String.format(UnassignCommand.MESSAGE_ASSIGNMENT_ABSENT, person.getName(), "group"));
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        assertCommandFailure(new UnassignCommand(seedu.address.commons.core.index.Index.fromOneBased(99),
                AssignmentType.GROUP), model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    private Person clear(Person person, AssignmentType type) {
        Optional<seedu.address.model.person.Role> role = type == AssignmentType.ROLE
                ? Optional.empty() : person.getRole();
        Optional<seedu.address.model.person.Activity> activity = type == AssignmentType.ACTIVITY
                ? Optional.empty() : person.getActivity();
        Optional<seedu.address.model.group.Group> group = type == AssignmentType.GROUP
                ? Optional.empty() : person.getGroup();
        return new Person(person.getName(), person.getPhone(), person.getEmail(), person.getAddress(), person.getTags(),
                role, activity, group);
    }
}
