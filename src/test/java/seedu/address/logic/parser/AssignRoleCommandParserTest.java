package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROLE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AssignRoleCommand;
import seedu.address.model.person.Role;

public class AssignRoleCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignRoleCommand.MESSAGE_USAGE);

    private final AssignRoleCommandParser parser = new AssignRoleCommandParser();

    @Test
    public void parse_missingIndex_failure() {
        assertParseFailure(parser, " " + PREFIX_ROLE + "Facilitator", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_missingRolePrefix_failure() {
        assertParseFailure(parser, "1 Facilitator", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidIndex_failure() {
        assertParseFailure(parser, "0 " + PREFIX_ROLE + "Facilitator", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_blankRole_failure() {
        assertParseFailure(parser, "1 " + PREFIX_ROLE, Role.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateRolePrefix_failure() {
        assertParseFailure(parser, "1 " + PREFIX_ROLE + "Facilitator " + PREFIX_ROLE + "Leader",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ROLE));
    }

    @Test
    public void parse_validArguments_success() {
        Role role = new Role("Logistics Lead");
        assertParseSuccess(parser, "1 " + PREFIX_ROLE + "Logistics Lead",
                new AssignRoleCommand(INDEX_FIRST_PERSON, role));
    }
}
