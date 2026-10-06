package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROLE;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AssignRoleCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Role;

/**
 * Parses input arguments and creates a new {@code AssignRoleCommand} object.
 */
public class AssignRoleCommandParser implements Parser<AssignRoleCommand> {

    /**
     * Parses the given {@code args} in the context of the {@code AssignRoleCommand}.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AssignRoleCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_ROLE);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignRoleCommand.MESSAGE_USAGE), pe);
        }

        if (argMultimap.getValue(PREFIX_ROLE).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignRoleCommand.MESSAGE_USAGE));
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ROLE);
        Role role = ParserUtil.parseRole(argMultimap.getValue(PREFIX_ROLE).get());

        return new AssignRoleCommand(index, role);
    }
}
