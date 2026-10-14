package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindContactCommand;
import seedu.address.model.person.ContactMatchesQueryPredicate;

public class FindContactCommandParserTest {

    private final FindContactCommandParser parser = new FindContactCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        FindContactCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validPhone_returnsFindContactCommand() {
        FindContactCommand expectedCommand = new FindContactCommand(
                new ContactMatchesQueryPredicate("91234567"));

        assertParseSuccess(parser, "91234567", expectedCommand);
    }

    @Test
    public void parse_validEmailWithWhitespace_returnsFindContactCommand() {
        FindContactCommand expectedCommand = new FindContactCommand(
                new ContactMatchesQueryPredicate("Alice.Tan@Example.com"));

        assertParseSuccess(parser, " \n Alice.Tan@Example.com \t", expectedCommand);
    }

    @Test
    public void parse_malformedContact_throwsParseException() {
        assertParseFailure(parser, "alice-at-example.com",
                FindContactCommand.MESSAGE_INVALID_CONTACT);
    }

    @Test
    public void parse_multipleValues_throwsParseException() {
        assertParseFailure(parser, "alice@example.com extra",
                FindContactCommand.MESSAGE_INVALID_CONTACT);
    }
}
