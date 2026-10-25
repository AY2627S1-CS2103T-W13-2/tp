package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.Group;
import seedu.address.model.person.Activity;
import seedu.address.model.person.Person;
import seedu.address.model.person.Role;

/**
 * Clears one assignment from a participant.
 */
public class UnassignCommand extends Command {

    public static final String COMMAND_WORD = "unassign";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Clears one assignment from the participant identified "
            + "by the index number used in the displayed participant list.\n"
            + "Parameters: INDEX (must be a positive integer) exactly one of g/, r/, or a/\n"
            + "Examples: " + COMMAND_WORD + " 1 g/\n"
            + "          " + COMMAND_WORD + " 1 r/\n"
            + "          " + COMMAND_WORD + " 1 a/";
    public static final String MESSAGE_UNASSIGN_SUCCESS = "Cleared %1$s assignment '%2$s' for %3$s.";
    public static final String MESSAGE_ASSIGNMENT_ABSENT = "%1$s has no %2$s assignment to clear.";

    private final Index index;
    private final AssignmentType assignmentType;

    /**
     * Creates an {@code UnassignCommand} to clear {@code assignmentType} from the person at {@code index}.
     */
    public UnassignCommand(Index index, AssignmentType assignmentType) {
        this.index = requireNonNull(index);
        this.assignmentType = requireNonNull(assignmentType);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToUpdate = lastShownList.get(index.getZeroBased());
        Optional<?> assignmentToClear = getAssignment(personToUpdate);
        if (assignmentToClear.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_ASSIGNMENT_ABSENT, personToUpdate.getName(),
                    assignmentType.getDisplayName()));
        }

        Person updatedPerson = clearAssignment(personToUpdate);
        model.setPerson(personToUpdate, updatedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_UNASSIGN_SUCCESS, assignmentType.getDisplayName(),
                assignmentToClear.orElseThrow(), updatedPerson.getName()));
    }

    private Optional<?> getAssignment(Person person) {
        return switch (assignmentType) {
            case GROUP -> person.getGroup();
            case ROLE -> person.getRole();
            case ACTIVITY -> person.getActivity();
        };
    }

    private Person clearAssignment(Person person) {
        Optional<Role> role = assignmentType == AssignmentType.ROLE ? Optional.empty() : person.getRole();
        Optional<Activity> activity = assignmentType == AssignmentType.ACTIVITY
                ? Optional.empty() : person.getActivity();
        Optional<Group> group = assignmentType == AssignmentType.GROUP ? Optional.empty() : person.getGroup();
        return new Person(person.getName(), person.getPhone(), person.getEmail(), person.getAddress(), person.getTags(),
                role, activity, group, person.getRemark());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UnassignCommand otherCommand)) {
            return false;
        }
        return index.equals(otherCommand.index) && assignmentType.equals(otherCommand.assignmentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, assignmentType);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("index", index).add("assignmentType", assignmentType).toString();
    }
}
