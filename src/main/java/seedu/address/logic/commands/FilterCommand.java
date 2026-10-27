package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.AssignmentMatchesPredicate;

/** Filters participants by one exact assignment. */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Filters participants by exactly one assignment (case-insensitive).\n"
            + "Parameters: g/GROUP | r/ROLE | a/ACTIVITY (quote the entire value if it contains prefix-like text)\n"
            + "Examples: filter g/Logistics, filter r/Facilitator, filter g/\"Team r/Lead\"";

    public static final String MESSAGE_SUCCESS = "%1$d participant(s) matched the assignment.";

    private final AssignmentMatchesPredicate predicate;
    private final String noMatchErrorMessage;

    public FilterCommand(AssignmentMatchesPredicate predicate) {
        this(predicate, null);
    }

    /**
     * Creates a filter command, deferring an ambiguous extra-prefix error until
     * the complete value has been checked against stored assignments.
     */
    public FilterCommand(AssignmentMatchesPredicate predicate, String noMatchErrorMessage) {
        this.predicate = requireNonNull(predicate);
        this.noMatchErrorMessage = noMatchErrorMessage;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        boolean hasStoredMatch = model.getAddressBook().getPersonList().stream().anyMatch(predicate);
        if (!hasStoredMatch && noMatchErrorMessage != null) {
            throw new CommandException(noMatchErrorMessage);
        }
        model.updateFilteredPersonList(predicate);
        return new CommandResult(String.format(MESSAGE_SUCCESS, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || other instanceof FilterCommand otherCommand
                && predicate.equals(otherCommand.predicate)
                && Objects.equals(noMatchErrorMessage, otherCommand.noMatchErrorMessage);
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + "{predicate=" + predicate
                + ", noMatchErrorMessage=" + noMatchErrorMessage + "}";
    }
}
