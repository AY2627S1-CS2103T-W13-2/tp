package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROLE;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Role;

/**
 * Assigns a role to an existing participant.
 */
public class AssignRoleCommand extends Command {

    public static final String COMMAND_WORD = "assign-role";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Assigns one role to the participant identified "
            + "by the index number used in the displayed participant list. "
            + "Any existing role will be replaced.\n"
            + "Parameters: INDEX (must be a positive integer) " + PREFIX_ROLE + "ROLE\n"
            + "Example: " + COMMAND_WORD + " 1 " + PREFIX_ROLE + "Logistics Lead";

    public static final String MESSAGE_ASSIGN_ROLE_SUCCESS = "Assigned role '%1$s' to %2$s";

    private final Index index;
    private final Role role;

    /**
     * Creates an {@code AssignRoleCommand} to assign {@code role} to the participant at {@code index}.
     */
    public AssignRoleCommand(Index index, Role role) {
        this.index = requireNonNull(index);
        this.role = requireNonNull(role);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person participant = lastShownList.get(index.getZeroBased());
        Person participantWithRole = new Person(participant.getName(), participant.getPhone(), participant.getEmail(),
                participant.getAddress(), participant.getTags(), role);

        model.setPerson(participant, participantWithRole);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_ASSIGN_ROLE_SUCCESS, role, participant.getName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AssignRoleCommand otherCommand)) {
            return false;
        }
        return index.equals(otherCommand.index) && role.equals(otherCommand.role);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("role", role)
                .toString();
    }
}
