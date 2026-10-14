package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SortCommand;

public class SortCommandParserTest {

    private final SortCommandParser parser = new SortCommandParser();

    @Test
    public void parse_nameCriterion_success() {
        assertParseSuccess(parser, " name ", new SortCommand());
    }

    @Test
    public void parse_missingCriterion_failure() {
        assertParseFailure(parser, "   ", SortCommandParser.MESSAGE_MISSING_CRITERION);
    }

    @Test
    public void parse_unsupportedCriterion_failure() {
        assertParseFailure(parser, "role",
                String.format(SortCommandParser.MESSAGE_UNSUPPORTED_CRITERION, "role"));
    }

    @Test
    public void parse_extraArguments_failure() {
        assertParseFailure(parser, "name descending",
                String.format(SortCommandParser.MESSAGE_EXTRA_ARGUMENTS, "descending"));
    }
}
