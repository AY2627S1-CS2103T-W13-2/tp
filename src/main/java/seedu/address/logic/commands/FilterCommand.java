package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;
import seedu.address.model.person.AssignmentMatchesPredicate;

/** Filters participants by one exact assignment. */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Filters participants by exactly one assignment (case-insensitive).\n"
            + "Parameters: g/GROUP | r/ROLE | a/ACTIVITY\n"
            + "Examples: filter g/Logistics, filter r/Facilitator, filter a/Campfire";

    public static final String MESSAGE_SUCCESS = "%1$d participant(s) matched the assignment.";

    private final AssignmentMatchesPredicate predicate;

    public FilterCommand(AssignmentMatchesPredicate predicate) {
        this.predicate = requireNonNull(predicate);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(String.format(MESSAGE_SUCCESS, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || other instanceof FilterCommand otherCommand
                && predicate.equals(otherCommand.predicate);
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + "{predicate=" + predicate + "}";
    }
}
