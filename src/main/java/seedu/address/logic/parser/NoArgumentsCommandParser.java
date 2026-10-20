package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.function.Supplier;

import seedu.address.logic.commands.Command;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses commands that do not accept arguments.
 *
 * @param <T> Type of command created by this parser.
 */
public class NoArgumentsCommandParser<T extends Command> implements Parser<T> {

    private final Supplier<T> commandFactory;
    private final String messageUsage;

    /**
     * Creates a parser that uses {@code commandFactory} to create a command when the input is valid.
     */
    public NoArgumentsCommandParser(Supplier<T> commandFactory, String messageUsage) {
        this.commandFactory = requireNonNull(commandFactory);
        this.messageUsage = requireNonNull(messageUsage);
    }

    /**
     * Returns a new command if {@code args} contains only whitespace.
     *
     * @throws ParseException If any argument is supplied.
     */
    @Override
    public T parse(String args) throws ParseException {
        requireNonNull(args);
        if (!args.isBlank()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, messageUsage));
        }
        return requireNonNull(commandFactory.get());
    }
}
