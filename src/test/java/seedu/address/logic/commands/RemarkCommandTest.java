package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;

public class RemarkCommandTest {

    private static final Remark ACCESSIBILITY_REMARK = new Remark("Needs wheelchair access");

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RemarkCommand(null, ACCESSIBILITY_REMARK));
    }

    @Test
    public void constructor_nullRemark_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RemarkCommand(INDEX_FIRST_PERSON, null));
    }

    @Test
    public void execute_participantWithoutRemark_addsRemarkAndPreservesOtherFields() throws Exception {
        Person originalParticipant = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        CommandResult result = new RemarkCommand(INDEX_FIRST_PERSON, ACCESSIBILITY_REMARK).execute(model);

        Person expectedParticipant = new PersonBuilder(originalParticipant)
                .withRemark(ACCESSIBILITY_REMARK.value).build();
        assertEquals(String.format(RemarkCommand.MESSAGE_SET_REMARK_SUCCESS, originalParticipant.getName()),
                result.getFeedbackToUser());
        assertEquals(expectedParticipant, model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()));
    }

    @Test
    public void execute_participantWithRemark_replacesRemark() throws Exception {
        Person originalParticipant = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person participantWithRemark = new PersonBuilder(originalParticipant).withRemark("Old note").build();
        model.setPerson(originalParticipant, participantWithRemark);

        new RemarkCommand(INDEX_FIRST_PERSON, ACCESSIBILITY_REMARK).execute(model);

        Person expectedParticipant = new PersonBuilder(originalParticipant)
                .withRemark(ACCESSIBILITY_REMARK.value).build();
        assertEquals(expectedParticipant, model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()));
    }

    @Test
    public void execute_emptyRemark_clearsRemarkAndPreservesOtherFields() throws Exception {
        Person originalParticipant = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person participantWithRemark = new PersonBuilder(originalParticipant).withRemark("Old note").build();
        model.setPerson(originalParticipant, participantWithRemark);

        CommandResult result = new RemarkCommand(INDEX_FIRST_PERSON, new Remark("")).execute(model);

        assertEquals(String.format(RemarkCommand.MESSAGE_CLEAR_REMARK_SUCCESS, originalParticipant.getName()),
                result.getFeedbackToUser());
        assertEquals(originalParticipant, model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()));
    }

    @Test
    public void execute_filteredAndSortedView_usesDisplayedIndexAndPreservesView() throws Exception {
        Person zed = new PersonBuilder().withName("Zed Tan").withPhone("81111111")
                .withEmail("zed@example.com").build();
        Person alice = new PersonBuilder().withName("Alice Tan").withPhone("82222222")
                .withEmail("alice@example.com").withGroup("Logistics").withRole("Facilitator")
                .withActivity("Campfire").withTags("friend").build();
        Person bob = new PersonBuilder().withName("Bob Tan").withPhone("83333333")
                .withEmail("bob@example.com").build();
        Model filteredAndSortedModel = createModel(zed, alice, bob);
        filteredAndSortedModel.updateFilteredPersonList(
                new NameContainsKeywordsPredicate(List.of("Alice", "Zed")));
        new SortCommand().execute(filteredAndSortedModel);
        assertDisplayedNames(filteredAndSortedModel, "Alice Tan", "Zed Tan");

        new RemarkCommand(INDEX_FIRST_PERSON, ACCESSIBILITY_REMARK).execute(filteredAndSortedModel);

        Person updatedAlice = new PersonBuilder(alice).withRemark(ACCESSIBILITY_REMARK.value).build();
        assertDisplayedNames(filteredAndSortedModel, "Alice Tan", "Zed Tan");
        assertEquals(updatedAlice, filteredAndSortedModel.getFilteredPersonList().getFirst());
        assertEquals(List.of(zed, updatedAlice, bob), filteredAndSortedModel.getAddressBook().getPersonList());
    }

    @Test
    public void execute_outOfRangeDisplayedIndex_throwsCommandException() {
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Alice")));

        RemarkCommand command = new RemarkCommand(INDEX_SECOND_PERSON, ACCESSIBILITY_REMARK);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        RemarkCommand command = new RemarkCommand(INDEX_FIRST_PERSON, ACCESSIBILITY_REMARK);
        RemarkCommand equivalentCommand = new RemarkCommand(INDEX_FIRST_PERSON,
                new Remark(ACCESSIBILITY_REMARK.value));

        assertEquals(command, command);
        assertEquals(command, equivalentCommand);
        assertEquals(command.hashCode(), equivalentCommand.hashCode());
        assertNotEquals(command, new RemarkCommand(INDEX_SECOND_PERSON, ACCESSIBILITY_REMARK));
        assertNotEquals(command, new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Different note")));
        assertNotEquals(command, new Object());
        assertNotEquals(command, null);
    }

    @Test
    public void toStringMethod() {
        RemarkCommand command = new RemarkCommand(INDEX_FIRST_PERSON, ACCESSIBILITY_REMARK);
        String expected = RemarkCommand.class.getCanonicalName() + "{index=" + INDEX_FIRST_PERSON
                + ", remark=" + ACCESSIBILITY_REMARK + "}";
        assertEquals(expected, command.toString());
    }

    private static Model createModel(Person... participants) {
        AddressBookBuilder builder = new AddressBookBuilder();
        Arrays.stream(participants).forEach(builder::withPerson);
        return new ModelManager(builder.build(), new UserPrefs());
    }

    private static void assertDisplayedNames(Model targetModel, String... expectedNames) {
        List<String> displayedNames = targetModel.getFilteredPersonList().stream()
                .map(person -> person.getName().fullName)
                .toList();
        assertEquals(List.of(expectedNames), displayedNames);
    }
}
