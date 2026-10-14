package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;

import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Sorts the currently displayed participants by name.
 */
public class SortCommand extends Command {

    public static final String COMMAND_WORD = "sort";
    public static final String CRITERION_NAME = "name";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Sorts the currently displayed participants by name in ascending order.\n"
            + "Parameters: " + CRITERION_NAME + "\n"
            + "Example: " + COMMAND_WORD + " " + CRITERION_NAME;

    public static final String MESSAGE_SUCCESS = "Sorted participants by name in ascending order.";

    static final Comparator<Person> NAME_COMPARATOR = Comparator
            .comparing((Person person) -> person.getName().fullName, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(person -> person.getName().fullName);

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateSortedPersonList(NAME_COMPARATOR);
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof SortCommand;
    }

    @Override
    public int hashCode() {
        return SortCommand.class.hashCode();
    }
}
