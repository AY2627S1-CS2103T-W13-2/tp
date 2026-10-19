package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Locale;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.model.Model;
import seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType;
import seedu.address.model.person.AssignmentMissingPredicate;

/** Shows participants missing one selected assignment without modifying their records. */
public class UnassignedCommand extends Command {

    public static final String COMMAND_WORD = "unassigned";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows participants with no assignment in one category.\n"
            + "Parameters: g/ | r/ | a/ (exactly one empty prefix)\n"
            + "Example: " + COMMAND_WORD + " g/";
    public static final String MESSAGE_SUCCESS = "%1$d participant(s) have no %2$s assignment.";

    private static final Logger logger = LogsCenter.getLogger(UnassignedCommand.class);

    private final AssignmentType type;
    private final AssignmentMissingPredicate predicate;

    /** Creates a query for one non-null assignment category. */
    public UnassignedCommand(AssignmentType type) {
        this.type = requireNonNull(type);
        predicate = new AssignmentMissingPredicate(type);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        int matchCount = model.getFilteredPersonList().size();
        logger.fine("Unassigned query: category=" + type + ", matches=" + matchCount);
        return new CommandResult(String.format(MESSAGE_SUCCESS, matchCount, type.name().toLowerCase(Locale.ROOT)));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || other instanceof UnassignedCommand otherCommand
                && type == otherCommand.type;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + "{category=" + type + "}";
    }
}
