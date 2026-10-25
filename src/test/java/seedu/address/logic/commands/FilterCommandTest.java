package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.FilterCommandParser.MESSAGE_MULTIPLE_PREFIXES;
import static seedu.address.logic.parser.FilterCommandParser.MESSAGE_REPEATED_PREFIX;
import static seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType.ACTIVITY;
import static seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType.GROUP;
import static seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType.ROLE;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.AssignmentMatchesPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class FilterCommandTest {

    private final Person logisticsLead = new PersonBuilder().withName("Alicia Tan")
            .withGroup("Logistics").withRole("Lead").withActivity("Campfire").build();
    private final Person programmesLead = new PersonBuilder().withName("Ben Lim")
            .withGroup("Programmes").withRole("Lead").withActivity("Workshop").build();
    private final Person logisticsFacilitator = new PersonBuilder().withName("Cara Ong")
            .withGroup("Logistics").withRole("Facilitator").withActivity("Workshop").build();
    private final Person unassigned = new PersonBuilder().withName("Dev Raj").build();
    private final Person slashGroupMember = new PersonBuilder().withName("Evan Koh")
            .withGroup("Team A/B").build();

    private Model model;
    private AddressBook originalAddressBook;

    @BeforeEach
    public void setUp() {
        AddressBook addressBook = new AddressBook();
        addressBook.setPersons(List.of(logisticsLead, programmesLead, logisticsFacilitator, unassigned,
                slashGroupMember));
        model = new ModelManager(addressBook, new UserPrefs());
        originalAddressBook = new AddressBook(addressBook);
    }

    @Test
    public void execute_group_multipleMatchesPreserveIndicesAndStoredOrder() throws Exception {
        CommandResult result = filter(GROUP, "  lOgIsTiCs  ").execute(model);

        assertEquals(String.format(FilterCommand.MESSAGE_SUCCESS, 2), result.getFeedbackToUser());
        assertEquals(List.of(logisticsLead, logisticsFacilitator), model.getFilteredPersonList());
        assertTrue(new ViewCommand(Index.fromOneBased(2)).execute(model).getFeedbackToUser()
                .startsWith("Participant details:\nName: Cara Ong\n"));
        assertEquals(List.of(logisticsLead, programmesLead, logisticsFacilitator, unassigned, slashGroupMember),
                model.getAddressBook().getPersonList());
        assertEquals(originalAddressBook, model.getAddressBook());
    }

    @Test
    public void execute_role_returnsExactMatches() throws Exception {
        filter(ROLE, "lead").execute(model);
        assertEquals(List.of(logisticsLead, programmesLead), model.getFilteredPersonList());
    }

    @Test
    public void execute_activity_returnsExactMatches() throws Exception {
        filter(ACTIVITY, "WORKSHOP").execute(model);
        assertEquals(List.of(programmesLead, logisticsFacilitator), model.getFilteredPersonList());
    }

    @Test
    public void execute_noMatchesReturnsEmptyListAndClearMessage() throws Exception {
        CommandResult result = filter(GROUP, "Safety").execute(model);
        assertEquals(String.format(FilterCommand.MESSAGE_SUCCESS, 0), result.getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredPersonList());
        assertEquals(originalAddressBook, model.getAddressBook());
    }

    @Test
    public void execute_caseInsensitiveSlashValue_returnsExactMatch() throws Exception {
        FilterCommand command = new FilterCommand(new AssignmentMatchesPredicate(GROUP, "team a/b"),
                MESSAGE_MULTIPLE_PREFIXES);

        command.execute(model);

        assertEquals(List.of(slashGroupMember), model.getFilteredPersonList());
        assertEquals(originalAddressBook, model.getAddressBook());
    }

    @Test
    public void execute_quotedPrefixLikeValue_matchesLiteralAndPreservesIndices() throws Exception {
        Person quoted = new PersonBuilder().withName("Quoted Person")
                .withGroup("Team r/Lead a/Campfire").build();
        model.addPerson(quoted);

        new AddressBookParser().parseCommand("filter g/\"team r/lead a/campfire\"").execute(model);

        assertEquals(List.of(quoted), model.getFilteredPersonList());
        assertTrue(new ViewCommand(Index.fromOneBased(1)).execute(model).getFeedbackToUser()
                .contains(quoted.getName().toString()));
        assertEquals(6, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_leadingLiteralQuotes_requiresEscapedQuotedSearch() throws Exception {
        Person plainGroup = new PersonBuilder().withName("Plain Alpha")
                .withGroup("Alpha").build();
        Person quotedGroup = new PersonBuilder().withName("Quoted Alpha")
                .withGroup("\"Alpha\"").build();
        model.addPerson(plainGroup);
        model.addPerson(quotedGroup);
        AddressBookParser parser = new AddressBookParser();

        parser.parseCommand("filter g/\"Alpha\"").execute(model);
        assertEquals(List.of(plainGroup), model.getFilteredPersonList());

        parser.parseCommand("filter g/\"\\\"Alpha\\\"\"").execute(model);
        assertEquals(List.of(quotedGroup), model.getFilteredPersonList());
    }

    @Test
    public void execute_unquotedLiteralQuote_matchesStoredGroup() throws Exception {
        Person quoted = new PersonBuilder().withName("Quote Person").withGroup("Team \"A\"").build();
        model.addPerson(quoted);

        new AddressBookParser().parseCommand("filter g/Team \"A\"").execute(model);

        assertEquals(List.of(quoted), model.getFilteredPersonList());
    }

    @Test
    public void execute_invalidQuotedValue_preservesCurrentViewAndData() throws Exception {
        AddressBookParser parser = new AddressBookParser();
        parser.parseCommand("filter g/Logistics").execute(model);

        assertThrows(ParseException.class, () -> parser.parseCommand("filter g/\"unfinished"));

        assertEquals(List.of(logisticsLead, logisticsFacilitator), model.getFilteredPersonList());
        assertEquals(originalAddressBook, model.getAddressBook());
    }

    @Test
    public void execute_ambiguousMultiplePrefixWithoutStoredMatch_throwsSpecificError() {
        FilterCommand command = new FilterCommand(
                new AssignmentMatchesPredicate(GROUP, "Logistics r/Facilitator"), MESSAGE_MULTIPLE_PREFIXES);

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(MESSAGE_MULTIPLE_PREFIXES, exception.getMessage());
        assertEquals(originalAddressBook, model.getAddressBook());
    }

    @Test
    public void execute_ambiguousRepeatedPrefixWithoutStoredMatch_throwsSpecificError() {
        FilterCommand command = new FilterCommand(
                new AssignmentMatchesPredicate(ACTIVITY, "Campfire a/Workshop"), MESSAGE_REPEATED_PREFIX);

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(MESSAGE_REPEATED_PREFIX, exception.getMessage());
        assertEquals(originalAddressBook, model.getAddressBook());
    }

    private FilterCommand filter(AssignmentMatchesPredicate.AssignmentType type, String value) {
        return new FilterCommand(new AssignmentMatchesPredicate(type, value));
    }
}
