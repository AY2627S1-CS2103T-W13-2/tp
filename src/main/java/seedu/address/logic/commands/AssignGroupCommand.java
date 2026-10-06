package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
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
import seedu.address.model.person.Person;

/**
 * Assigns a person to a group.
 */
public class AssignGroupCommand extends Command {

    public static final String COMMAND_WORD = "assign-group";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Assigns a group to the person identified "
            + "by the index number used in the displayed person list.\n"
            + "Parameters: INDEX (must be a positive integer) " + PREFIX_GROUP + "GROUP\n"
            + "Example: " + COMMAND_WORD + " 1 " + PREFIX_GROUP + "Logistics";

    public static final String MESSAGE_ASSIGN_GROUP_SUCCESS = "Assigned %1$s to group %2$s.";

    private final Index index;
    private final Group group;

    /**
     * Creates an {@code AssignGroupCommand} to assign the person at {@code index} to {@code group}.
     */
    public AssignGroupCommand(Index index, Group group) {
        this.index = requireNonNull(index);
        this.group = requireNonNull(group);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToAssign = lastShownList.get(index.getZeroBased());
        Person assignedPerson = new Person(personToAssign.getName(), personToAssign.getPhone(),
                personToAssign.getEmail(), personToAssign.getAddress(), personToAssign.getTags(),
                personToAssign.getRole(), personToAssign.getActivity(), Optional.of(group));

        model.setPerson(personToAssign, assignedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_ASSIGN_GROUP_SUCCESS, assignedPerson.getName(), group));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AssignGroupCommand otherCommand)) {
            return false;
        }

        return index.equals(otherCommand.index) && group.equals(otherCommand.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, group);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("group", group)
                .toString();
    }
}
