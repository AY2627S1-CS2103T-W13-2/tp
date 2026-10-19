package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;
import seedu.address.model.person.AssignmentsMatchAllPredicate;

/** Shows participants matching all selected assignments without modifying their records or sort order. */
public class FilterAllCommand extends Command {
    public static final String COMMAND_WORD = "filter-all";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Filters participants by two or three assignments using AND (case-insensitive).\n"
            + "Parameters: two or three distinct g/GROUP, r/ROLE, a/ACTIVITY criteria\n"
            + "Quote values containing prefix-like text. Example: filter-all g/\"Team A/B\" r/Facilitator";
    public static final String MESSAGE_SUCCESS = "%1$d participant(s) matched all assignments.";

    private final AssignmentsMatchAllPredicate predicate;

    /** Creates a command for the supplied non-null combined assignment predicate. */
    public FilterAllCommand(AssignmentsMatchAllPredicate predicate) {
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
                || other instanceof FilterAllCommand otherCommand
                && predicate.equals(otherCommand.predicate);
    }

    @Override
    public int hashCode() {
        return predicate.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + "{predicate=" + predicate + "}";
    }
}
