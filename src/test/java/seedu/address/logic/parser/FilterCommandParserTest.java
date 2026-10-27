package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.FilterAllCommandParser.MESSAGE_AFTER_QUOTE;
import static seedu.address.logic.parser.FilterAllCommandParser.MESSAGE_INVALID_ESCAPE;
import static seedu.address.logic.parser.FilterAllCommandParser.MESSAGE_UNFINISHED_QUOTE;
import static seedu.address.logic.parser.FilterCommandParser.MESSAGE_BLANK_VALUE;
import static seedu.address.logic.parser.FilterCommandParser.MESSAGE_MISSING_PREFIX;
import static seedu.address.logic.parser.FilterCommandParser.MESSAGE_MULTIPLE_PREFIXES;
import static seedu.address.logic.parser.FilterCommandParser.MESSAGE_REPEATED_PREFIX;
import static seedu.address.logic.parser.FilterCommandParser.MESSAGE_UNEXPECTED_ARGUMENTS;
import static seedu.address.logic.parser.FilterCommandParser.MESSAGE_UNEXPECTED_ARGUMENTS_AFTER_QUOTE;
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
        assertParseSuccess(parser, " g/Team A/B",
                new FilterCommand(new AssignmentMatchesPredicate(GROUP, "Team A/B")));
        assertParseSuccess(parser, " g/team a/b",
                new FilterCommand(new AssignmentMatchesPredicate(GROUP, "team a/b"), MESSAGE_MULTIPLE_PREFIXES));
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
    public void parse_quotedValues_decodesLiteralsAndProtectsPrefixes() {
        assertParseSuccess(parser, " g/\"Team r/Lead a/Campfire\"",
                new FilterCommand(new AssignmentMatchesPredicate(GROUP, "Team r/Lead a/Campfire")));
        assertParseSuccess(parser, " r/  \"Say \\\"Hi\\\" \\\\ Lead\"  ",
                new FilterCommand(new AssignmentMatchesPredicate(ROLE, "Say \"Hi\" \\ Lead")));
        assertParseSuccess(parser, " a/\"Team A/B\"",
                new FilterCommand(new AssignmentMatchesPredicate(ACTIVITY, "Team A/B")));
        assertParseSuccess(parser, " g/\"\\\"Alpha\\\"\"",
                new FilterCommand(new AssignmentMatchesPredicate(GROUP, "\"Alpha\"")));
    }

    @Test
    public void parse_unquotedLiteralQuotes_preservesExistingSearches() {
        assertParseSuccess(parser, " g/Team \"A\"",
                new FilterCommand(new AssignmentMatchesPredicate(GROUP, "Team \"A\"")));
        assertParseSuccess(parser, " g/Team\"X\"",
                new FilterCommand(new AssignmentMatchesPredicate(GROUP, "Team\"X\"")));
    }

    @Test
    public void parse_invalidQuotedValues_throwsSpecificError() {
        assertParseFailure(parser, " g/\"Team r/Lead", MESSAGE_UNFINISHED_QUOTE);
        assertParseFailure(parser, " g/\"Team\\", MESSAGE_UNFINISHED_QUOTE);
        assertParseFailure(parser, " g/\"Team\\q\"", MESSAGE_INVALID_ESCAPE);
        assertParseFailure(parser, " g/\" \"", MESSAGE_BLANK_VALUE);
        assertParseFailure(parser, " g/\" \" r/Other", MESSAGE_BLANK_VALUE);
        assertParseFailure(parser, " g/\"Team\"extra", MESSAGE_AFTER_QUOTE);
        assertParseFailure(parser, " g/\"Team\"r/Lead", MESSAGE_AFTER_QUOTE);
        assertParseFailure(parser, " g/\"Team\" extra", MESSAGE_UNEXPECTED_ARGUMENTS_AFTER_QUOTE);
        assertParseFailure(parser, " g/\"Team\" p/Other", MESSAGE_UNSUPPORTED_PREFIX);
    }

    @Test
    public void parse_prefixesOutsideQuotedValue_throwsSpecificError() {
        assertParseFailure(parser, " g/\"Team r/Lead\" r/Other", MESSAGE_MULTIPLE_PREFIXES);
        assertParseFailure(parser, " g/\"Team r/Lead\" g/Other", MESSAGE_REPEATED_PREFIX);
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
    public void parse_multiplePrefixes_defersErrorUntilStoredValuesChecked() {
        assertParseSuccess(parser, " g/Logistics r/Facilitator",
                new FilterCommand(new AssignmentMatchesPredicate(GROUP, "Logistics r/Facilitator"),
                        MESSAGE_MULTIPLE_PREFIXES));
    }

    @Test
    public void parse_repeatedPrefix_defersErrorUntilStoredValuesChecked() {
        assertParseSuccess(parser, " a/Campfire a/Workshop",
                new FilterCommand(new AssignmentMatchesPredicate(ACTIVITY, "Campfire a/Workshop"),
                        MESSAGE_REPEATED_PREFIX));
    }

    @Test
    public void parse_unsupportedPrefix_throwsParseException() {
        assertParseFailure(parser, " p/91234567", MESSAGE_UNSUPPORTED_PREFIX);
    }

    @Test
    public void parse_unexpectedArguments_throwsParseException() {
        assertParseFailure(parser, "extra g/Logistics", MESSAGE_UNEXPECTED_ARGUMENTS);
    }
}
