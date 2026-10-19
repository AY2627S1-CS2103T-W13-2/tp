package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

public class FilterAllCommandParserTest {
    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parse_validPairsTriplesAndWhitespace_accepts() {
        String[] inputs = {"g/Logistics r/Facilitator", "r/Lead a/Campfire", "a/Campfire g/Logistics",
            "a/Campfire r/Lead g/Logistics", "\t g/  Logistics Team \t r/ Lead  ",
            "g/\"Team A/B\" r/Lead", "g/Team/B r/Lead", "g/\"r/Lead\" r/Lead",
            "g/\"Say \\\"Hi\\\"\" r/\"Path \\\\ Lead\""};
        for (String args : inputs) {
            assertDoesNotThrow(() -> parser.parseCommand("filter-all " + args), args);
        }
    }

    @Test
    public void parse_missingCriteria_reportsRequiredCount() {
        assertInvalid("", "two or three");
        assertInvalid("g/Logistics", "two or three");
    }

    @Test
    public void parse_repeatedPrefixes_rejects() {
        assertInvalid("g/Logistics g/Other", "repeated");
        assertInvalid("r/Lead a/Campfire r/Lead", "repeated");
        assertInvalid("g/X r/Y a/Z g/X", "repeated");
    }

    @Test
    public void parse_blankValues_rejects() {
        assertInvalid("g/ r/Lead", "blank");
        assertInvalid("g/X r/", "blank");
        assertInvalid("g/\" \" r/Lead", "blank");
        assertInvalid("g/X r/\"\"", "blank");
    }

    @Test
    public void parse_unsupportedPrefixesAndPreamble_rejects() {
        assertInvalid("p/123 r/Lead", "Unsupported");
        assertInvalid("g/X R/Lead", "Unsupported");
        assertInvalid("g/X r/Lead p/123", "Unsupported");
        assertInvalid("extra g/X r/Lead", "Expected an assignment prefix");
    }

    @Test
    public void parse_unfinishedQuotesAndInvalidEscapes_rejects() {
        assertInvalid("g/\"Team r/Lead", "Unfinished quoted value");
        assertInvalid("g/\"Team\\", "Unfinished quoted value");
        assertInvalid("g/\"Team\\q\" r/Lead", "Unsupported escape");
    }

    @Test
    public void parse_partialQuotesAndTextAfterQuotes_rejects() {
        assertInvalid("g/Team\"X\" r/Lead", "Quote the entire");
        assertInvalid("g/\"Team\"extra r/Lead", "after a quoted value");
        assertInvalid("g/\"Team\" extra r/Lead", "Expected an assignment prefix");
        assertInvalid("g/\"Team\"r/Lead", "after a quoted value");
    }

    private void assertInvalid(String args, String diagnostic) {
        String input = "filter-all " + args;
        ParseException exception = assertThrows(ParseException.class, () -> parser.parseCommand(input), args);
        assertTrue(exception.getMessage().contains(diagnostic), exception.getMessage());
    }
}
