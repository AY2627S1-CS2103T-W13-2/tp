package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ACTIVITY;

import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Activity;
import seedu.address.model.person.Person;

/**
 * Assigns one activity to a participant identified by their displayed index.
 */
public class AssignActivityCommand extends Command {

    public static final String COMMAND_WORD = "assign-activity";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Assigns an activity to the participant identified by the displayed index. "
            + "Any existing activity will be replaced.\n"
            + "Parameters: INDEX (must be a positive integer) " + PREFIX_ACTIVITY + "ACTIVITY\n"
            + "Example: " + COMMAND_WORD + " 1 " + PREFIX_ACTIVITY + "Campfire";
    public static final String MESSAGE_SUCCESS = "Assigned activity %1$s to %2$s.";

    private final Index index;
    private final Activity activity;

    /**
     * Constructs a command to assign {@code activity} to the participant at {@code index}.
     */
    public AssignActivityCommand(Index index, Activity activity) {
        requireAllNonNull(index, activity);
        this.index = index;
        this.activity = activity;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();
        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person participant = lastShownList.get(index.getZeroBased());
        Person updatedParticipant = new Person(participant.getName(), participant.getPhone(), participant.getEmail(),
                participant.getAddress(), participant.getTags(), participant.getRole(), Optional.of(activity),
                participant.getGroup());
        model.setPerson(participant, updatedParticipant);
        return new CommandResult(String.format(MESSAGE_SUCCESS, activity, participant.getName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof AssignActivityCommand otherCommand
                && index.equals(otherCommand.index)
                && activity.equals(otherCommand.activity);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("index", index).add("activity", activity).toString();
    }
}
