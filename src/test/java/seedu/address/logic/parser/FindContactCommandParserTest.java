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
    public void parse_phoneWithUnicodeWhitespace_returnsFindContactCommand() {
        FindContactCommand expectedCommand = new FindContactCommand(
                new ContactMatchesQueryPredicate("91234567"));

        for (String whitespace : new String[] {"\u2003", "\u2002", "\u3000", " \t\u2003 "}) {
            assertParseSuccess(parser, whitespace + "91234567" + whitespace, expectedCommand);
        }
    }

    @Test
    public void parse_emailWithUnicodeWhitespace_returnsFindContactCommand() {
        FindContactCommand expectedCommand = new FindContactCommand(
                new ContactMatchesQueryPredicate("Alice.Tan@Example.com"));

        for (String whitespace : new String[] {"\u2003", "\u2002", "\u3000", " \t\u2003 "}) {
            assertParseSuccess(parser, whitespace + "Alice.Tan@Example.com" + whitespace, expectedCommand);
        }
    }

    @Test
    public void parse_unicodeWhitespaceOnly_throwsUsageError() {
        assertParseFailure(parser, " \t\u2003\u3000 ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindContactCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_embeddedUnicodeWhitespace_throwsInvalidContactError() {
        for (String query : new String[] {"9123\u20034567", "Alice\u2003Tan@Example.com",
            "alice@example.com\u2003extra"}) {
            assertParseFailure(parser, query, FindContactCommand.MESSAGE_INVALID_CONTACT);
        }
    }

    @Test
    public void parse_nonBreakingOrZeroWidthCharacters_throwsInvalidContactError() {
        for (String character : new String[] {"\u00a0", "\u2007", "\u202f", "\u200b", "\ufeff"}) {
            assertParseFailure(parser, character + "alice@example.com" + character,
                    FindContactCommand.MESSAGE_INVALID_CONTACT);
        }
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
