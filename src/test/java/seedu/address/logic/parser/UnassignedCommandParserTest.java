package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

public class UnassignedCommandParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parse_singleEmptyAssignmentPrefixWithWhitespace_acceptsAllCategories() {
        String[] inputs = {"unassigned g/", "unassigned r/", "unassigned a/", "  unassigned \t g/  "};
        for (String input : inputs) {
            assertDoesNotThrow(() -> parser.parseCommand(input), input);
        }
    }

    @Test
    public void parse_missingPrefix_reportsRequiredSyntax() {
        assertInvalid("unassigned", "exactly one empty assignment prefix");
        assertInvalid("unassigned   ", "exactly one empty assignment prefix");
    }

    @Test
    public void parse_repeatedPrefix_rejectsRatherThanIgnoringDuplicates() {
        assertInvalid("unassigned g/ g/", "must not be repeated");
        assertInvalid("unassigned r/ r/", "must not be repeated");
        assertInvalid("unassigned a/ a/", "must not be repeated");
    }

    @Test
    public void parse_multipleAssignmentPrefixes_rejectsAmbiguousCategory() {
        assertInvalid("unassigned g/ r/", "only one assignment prefix");
        assertInvalid("unassigned a/ g/ r/", "only one assignment prefix");
        assertInvalid("unassigned r/ a/", "only one assignment prefix");
    }

    @Test
    public void parse_nonemptyValue_rejectsAssignedSearchAndQuotedEmptyValue() {
        assertInvalid("unassigned g/Logistics", "must not contain a value");
        assertInvalid("unassigned r/Facilitator", "must not contain a value");
        assertInvalid("unassigned a/Campfire", "must not contain a value");
        assertInvalid("unassigned g/Team A/B", "must not contain a value");
        assertInvalid("unassigned g/\"\"", "must not contain a value");
    }

    @Test
    public void parse_unsupportedOrUppercasePrefix_reportsSupportedPrefixes() {
        assertInvalid("unassigned n/", "Unsupported assignment prefix");
        assertInvalid("unassigned G/", "Unsupported assignment prefix");
        assertInvalid("unassigned g/ t/", "Unsupported assignment prefix");
    }

    @Test
    public void parse_extraTextOrMalformedPrefix_rejectsWithoutExecution() {
        assertInvalid("unassigned 1 g/", "Unexpected extra argument");
        assertInvalid("unassigned g/ extra", "Unexpected extra argument");
        assertInvalid("unassigned /", "Unexpected extra argument");
        assertInvalid("unassigned g", "Unexpected extra argument");
        assertInvalid("unassigned g/r/", "must not contain a value");
    }

    private void assertInvalid(String input, String diagnostic) {
        ParseException exception = assertThrows(ParseException.class, () -> parser.parseCommand(input), input);
        assertTrue(exception.getMessage().contains(diagnostic), exception.getMessage());
    }
}
