package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.FilterCommandParser.MESSAGE_BLANK_VALUE;
import static seedu.address.logic.parser.FilterCommandParser.MESSAGE_MISSING_PREFIX;
import static seedu.address.logic.parser.FilterCommandParser.MESSAGE_MULTIPLE_PREFIXES;
import static seedu.address.logic.parser.FilterCommandParser.MESSAGE_REPEATED_PREFIX;
import static seedu.address.logic.parser.FilterCommandParser.MESSAGE_UNEXPECTED_ARGUMENTS;
import static seedu.address.logic.parser.FilterCommandParser.MESSAGE_UNSUPPORTED_PREFIX;
import static seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType.ACTIVITY;
import static seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType.GROUP;
import static seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType.ROLE;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.model.person.AssignmentMatchesPredicate;

public class FilterCommandParserTest {

    private final FilterCommandParser parser = new FilterCommandParser();

    @Test
    public void parse_validGroup_returnsFilterCommand() {
        assertParseSuccess(parser, " g/  Logistics Team  ",
                new FilterCommand(new AssignmentMatchesPredicate(GROUP, "Logistics Team")));
    }

    @Test
    public void parse_validRole_returnsFilterCommand() {
        assertParseSuccess(parser, " r/Facilitator",
                new FilterCommand(new AssignmentMatchesPredicate(ROLE, "Facilitator")));
    }

    @Test
    public void parse_validActivity_returnsFilterCommand() {
        assertParseSuccess(parser, " a/Campfire",
                new FilterCommand(new AssignmentMatchesPredicate(ACTIVITY, "Campfire")));
    }

    @Test
    public void parse_missingPrefix_throwsParseException() {
        assertParseFailure(parser, "Logistics", MESSAGE_MISSING_PREFIX);
        assertParseFailure(parser, "   ", MESSAGE_MISSING_PREFIX);
    }

    @Test
    public void parse_blankValue_throwsParseException() {
        assertParseFailure(parser, " g/   ", MESSAGE_BLANK_VALUE);
    }

    @Test
    public void parse_multiplePrefixes_throwsParseException() {
        assertParseFailure(parser, " g/Logistics r/Facilitator", MESSAGE_MULTIPLE_PREFIXES);
    }

    @Test
    public void parse_repeatedPrefix_throwsParseException() {
        assertParseFailure(parser, " a/Campfire a/Workshop", MESSAGE_REPEATED_PREFIX);
    }

    @Test
    public void parse_unsupportedPrefix_throwsParseException() {
        assertParseFailure(parser, " p/91234567", MESSAGE_UNSUPPORTED_PREFIX);
        assertParseFailure(parser, " g/Logistics p/91234567", MESSAGE_UNSUPPORTED_PREFIX);
    }

    @Test
    public void parse_unexpectedArguments_throwsParseException() {
        assertParseFailure(parser, "extra g/Logistics", MESSAGE_UNEXPECTED_ARGUMENTS);
    }
}
