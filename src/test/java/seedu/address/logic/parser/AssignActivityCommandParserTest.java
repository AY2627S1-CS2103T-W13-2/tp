package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ACTIVITY;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AssignActivityCommand;
import seedu.address.model.person.Activity;

public class AssignActivityCommandParserTest {

    private final AssignActivityCommandParser parser = new AssignActivityCommandParser();

    @Test
    public void parse_validArguments_success() {
        assertParseSuccess(parser, " 1 a/Campfire",
                new AssignActivityCommand(INDEX_FIRST_PERSON, new Activity("Campfire")));
        assertParseSuccess(parser, " 1 a/  Arts & Crafts  ",
                new AssignActivityCommand(INDEX_FIRST_PERSON, new Activity("Arts & Crafts")));
    }

    @Test
    public void parse_invalidIndex_failure() {
        for (String index : new String[] {"", "0", "-1", "1.5", "one", "2147483648", "1 extra"}) {
            assertParseFailure(parser, " " + index + " a/Campfire",
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignActivityCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_missingActivityPrefix_failure() {
        assertParseFailure(parser, " 1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignActivityCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_blankActivity_failure() {
        for (String blank : new String[] {"", " ", "\t", "\u2003"}) {
            assertParseFailure(parser, " 1 a/" + blank, Activity.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_repeatedActivityPrefix_failure() {
        assertParseFailure(parser, " 1 a/Campfire a/Hiking",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ACTIVITY));
    }
}
