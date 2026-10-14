package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AssignmentType;
import seedu.address.logic.commands.UnassignCommand;

public class UnassignCommandParserTest {

    private final UnassignCommandParser parser = new UnassignCommandParser();

    @Test
    public void parse_eachSupportedAssignment_success() {
        assertParseSuccess(parser, "1 g/", new UnassignCommand(INDEX_FIRST_PERSON, AssignmentType.GROUP));
        assertParseSuccess(parser, "1 r/", new UnassignCommand(INDEX_FIRST_PERSON, AssignmentType.ROLE));
        assertParseSuccess(parser, "1 a/", new UnassignCommand(INDEX_FIRST_PERSON, AssignmentType.ACTIVITY));
    }

    @Test
    public void parse_invalidInputs_failure() {
        assertParseFailure(parser, "g/", "Please provide a participant index before the assignment prefix.");
        assertParseFailure(parser, "0 g/", ParserUtil.MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "1", "Please specify exactly one assignment prefix: g/, r/, or a/.");
        assertParseFailure(parser, "1 x/", "Unsupported assignment prefix: x/ Use g/, r/, or a/.");
        assertParseFailure(parser, "1 g/ r/", "Specify only one assignment prefix: g/, r/, or a/.");
        assertParseFailure(parser, "1 g/ g/", "Assignment prefix must not be repeated: g/");
        assertParseFailure(parser, "1 g/Logistics",
                "The g/ prefix must not contain a value when clearing a group assignment.");
        assertParseFailure(parser, "1 unexpected", "Unexpected extra argument(s): unexpected");
    }
}
