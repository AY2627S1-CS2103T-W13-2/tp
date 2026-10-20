package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertSame;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class NoArgumentsCommandParserTest {

    private final HelpCommand expectedCommand = new HelpCommand();
    private final NoArgumentsCommandParser<HelpCommand> parser =
            new NoArgumentsCommandParser<>(() -> expectedCommand, HelpCommand.MESSAGE_USAGE);

    @Test
    public void parse_emptyArguments_returnsCommand() throws Exception {
        assertSame(expectedCommand, parser.parse(""));
    }

    @Test
    public void parse_whitespaceArguments_returnsCommand() throws Exception {
        assertSame(expectedCommand, parser.parse("  \t  "));
    }

    @Test
    public void parse_nonEmptyArguments_throwsParseException() {
        assertThrows(ParseException.class,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), () ->
                    parser.parse(" unexpected"));
    }
}
