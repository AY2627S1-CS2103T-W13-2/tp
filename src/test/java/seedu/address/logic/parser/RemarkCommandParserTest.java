package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.model.person.Remark;

public class RemarkCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT = String.format(
            MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);

    private final RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validRemark_success() {
        assertParseSuccess(parser, " 1 r/Needs wheelchair access",
                new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Needs wheelchair access")));
        assertParseSuccess(parser, " 1 r/  Check in at 09:00; bring A/B.  ",
                new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Check in at 09:00; bring A/B.")));
    }

    @Test
    public void parse_emptyRemark_success() {
        for (String padding : new String[] {"", " ", "\t", "\u2003"}) {
            assertParseSuccess(parser, " 1 r/" + padding,
                    new RemarkCommand(INDEX_FIRST_PERSON, new Remark("")));
        }
    }

    @Test
    public void parse_invalidIndex_failure() {
        for (String index : new String[] {"", "0", "-1", "1.5", "one", "2147483648", "1 extra"}) {
            assertParseFailure(parser, " " + index + " r/Valid note", MESSAGE_INVALID_FORMAT);
        }
    }

    @Test
    public void parse_missingRemarkPrefix_failure() {
        assertParseFailure(parser, " 1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1 note", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1 R/Uppercase prefix", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_repeatedRemarkPrefix_failure() {
        assertParseFailure(parser, " 1 r/First note r/Second note",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_REMARK));
    }
}
