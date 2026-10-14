package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ACTIVITY;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AssignActivityCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Activity;

/**
 * Parses arguments for assigning an activity to a participant.
 */
public class AssignActivityCommandParser implements Parser<AssignActivityCommand> {

    @Override
    public AssignActivityCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, PREFIX_ACTIVITY);
        Index index;
        try {
            index = ParserUtil.parseIndex(arguments.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AssignActivityCommand.MESSAGE_USAGE), pe);
        }
        if (arguments.getValue(PREFIX_ACTIVITY).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AssignActivityCommand.MESSAGE_USAGE));
        }
        arguments.verifyNoDuplicatePrefixesFor(PREFIX_ACTIVITY);
        String activity = arguments.getValue(PREFIX_ACTIVITY).get();
        if (!Activity.isValidActivity(activity)) {
            throw new ParseException(Activity.MESSAGE_CONSTRAINTS);
        }
        return new AssignActivityCommand(index, new Activity(activity));
    }
}
