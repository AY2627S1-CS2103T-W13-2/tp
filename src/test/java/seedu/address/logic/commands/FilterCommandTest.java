package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType.ACTIVITY;
import static seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType.GROUP;
import static seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType.ROLE;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
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

    private Model model;
    private AddressBook originalAddressBook;

    @BeforeEach
    public void setUp() {
        AddressBook addressBook = new AddressBook();
        addressBook.setPersons(List.of(logisticsLead, programmesLead, logisticsFacilitator, unassigned));
        model = new ModelManager(addressBook, new UserPrefs());
        originalAddressBook = new AddressBook(addressBook);
    }

    @Test
    public void execute_group_multipleMatchesPreserveIndicesAndStoredOrder() throws Exception {
        CommandResult result = filter(GROUP, "  lOgIsTiCs  ").execute(model);

        assertEquals(String.format(FilterCommand.MESSAGE_SUCCESS, 2), result.getFeedbackToUser());
        assertEquals(List.of(logisticsLead, logisticsFacilitator), model.getFilteredPersonList());
        assertEquals(String.format(ViewCommand.MESSAGE_VIEW_PARTICIPANT_SUCCESS,
                Messages.format(logisticsFacilitator)),
                new ViewCommand(Index.fromOneBased(2)).execute(model).getFeedbackToUser());
        assertEquals(List.of(logisticsLead, programmesLead, logisticsFacilitator, unassigned),
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

    private FilterCommand filter(AssignmentMatchesPredicate.AssignmentType type, String value) {
        return new FilterCommand(new AssignmentMatchesPredicate(type, value));
    }
}
