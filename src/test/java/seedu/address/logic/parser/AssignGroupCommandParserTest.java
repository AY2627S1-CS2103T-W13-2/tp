package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.GROUP_DESC_LOGISTICS;
import static seedu.address.logic.commands.CommandTestUtil.GROUP_DESC_PROGRAMMES;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_GROUP_DESC;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_LOGISTICS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_PROGRAMMES;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AssignGroupCommand;
import seedu.address.model.group.Group;

public class AssignGroupCommandParserTest {

    private final AssignGroupCommandParser parser = new AssignGroupCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        assertParseSuccess(parser, "1" + GROUP_DESC_LOGISTICS,
                new AssignGroupCommand(INDEX_FIRST_PERSON, new Group(VALID_GROUP_LOGISTICS)));
    }

    @Test
    public void parse_groupWithSpaces_success() {
        String groupName = "Orientation Group 1";
        assertParseSuccess(parser, "1 g/" + groupName,
                new AssignGroupCommand(INDEX_FIRST_PERSON, new Group(groupName)));
    }

    @Test
    public void parse_missingIndex_failure() {
        assertParseFailure(parser, GROUP_DESC_LOGISTICS,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignGroupCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidIndex_failure() {
        assertParseFailure(parser, "0" + GROUP_DESC_LOGISTICS,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignGroupCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingGroupPrefix_failure() {
        assertParseFailure(parser, "1 " + VALID_GROUP_LOGISTICS,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignGroupCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_blankGroup_failure() {
        assertParseFailure(parser, "1" + INVALID_GROUP_DESC, Group.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateGroupPrefix_failure() {
        assertParseFailure(parser, "1" + GROUP_DESC_LOGISTICS + GROUP_DESC_PROGRAMMES,
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_GROUP));
    }

    @Test
    public void parse_secondValidGroup_failure() {
        assertParseFailure(parser, "1 g/" + VALID_GROUP_LOGISTICS + " g/" + VALID_GROUP_PROGRAMMES,
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_GROUP));
    }
}
