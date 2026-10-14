package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code SortCommand} object.
 */
public class SortCommandParser implements Parser<SortCommand> {

    public static final String MESSAGE_MISSING_CRITERION =
            "A sort criterion is required. Use: sort name";
    public static final String MESSAGE_UNSUPPORTED_CRITERION =
            "Unsupported sort criterion: %s. Only 'name' is supported.";
    public static final String MESSAGE_EXTRA_ARGUMENTS =
            "Unexpected extra arguments after 'name': %s";

    /**
     * Parses the given {@code args} in the context of the {@code SortCommand}.
     *
     * @throws ParseException if the input does not contain exactly the supported
     *     {@code name} criterion.
     */
    public SortCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_CRITERION);
        }

        String[] arguments = trimmedArgs.split("\\s+");
        String criterion = arguments[0];
        if (!SortCommand.CRITERION_NAME.equals(criterion)) {
            throw new ParseException(String.format(MESSAGE_UNSUPPORTED_CRITERION, criterion));
        }
        if (arguments.length > 1) {
            String extraArguments = trimmedArgs.substring(criterion.length()).trim();
            throw new ParseException(String.format(MESSAGE_EXTRA_ARGUMENTS, extraArguments));
        }

        return new SortCommand();
    }
}
