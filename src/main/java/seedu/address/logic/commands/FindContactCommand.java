package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.ContactMatchesQueryPredicate;

/**
 * Finds participants whose phone number or email address exactly matches a query.
 */
public class FindContactCommand extends Command {

    public static final String COMMAND_WORD = "find-contact";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds participants whose complete phone number or email address matches the given value.\n"
            + "Parameters: PHONE_OR_EMAIL\n"
            + "Example: " + COMMAND_WORD + " alex@example.com";

    public static final String MESSAGE_INVALID_CONTACT =
            "Contact must be one complete valid phone number or email address.";

    private final ContactMatchesQueryPredicate predicate;

    public FindContactCommand(ContactMatchesQueryPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW,
                        model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof FindContactCommand otherCommand)) {
            return false;
        }

        return predicate.equals(otherCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("predicate", predicate).toString();
    }
}
