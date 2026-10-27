package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.FindContactCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.ContactMatchesQueryPredicate;
import seedu.address.model.person.Email;
import seedu.address.model.person.Phone;

/**
 * Parses input arguments and creates a new {@code FindContactCommand} object.
 */
public class FindContactCommandParser implements Parser<FindContactCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the {@code FindContactCommand}
     * and returns a {@code FindContactCommand} object for execution.
     * Strips surrounding whitespace recognized by {@link Character#isWhitespace(int)} before validation.
     * Internal whitespace, non-breaking spaces and zero-width characters are not removed.
     *
     * @throws ParseException if the stripped input is empty or is not a valid phone number or email address.
     */
    @Override
    public FindContactCommand parse(String args) throws ParseException {
        String query = args.strip();

        if (query.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    FindContactCommand.MESSAGE_USAGE));
        }

        if (!Phone.isValidPhone(query) && !Email.isValidEmail(query)) {
            throw new ParseException(FindContactCommand.MESSAGE_INVALID_CONTACT);
        }

        return new FindContactCommand(new ContactMatchesQueryPredicate(query));
    }
}
