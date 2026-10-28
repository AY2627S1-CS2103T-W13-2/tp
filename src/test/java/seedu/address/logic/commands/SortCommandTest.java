package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;

public class SortCommandTest {

    private static final Person ALICE_UPPERCASE = new PersonBuilder()
            .withName("Alice").withPhone("11111111").withEmail("alice.upper@example.com").build();
    private static final Person ALICE_LOWERCASE = new PersonBuilder()
            .withName("alice").withPhone("22222222").withEmail("alice.lower@example.com").build();
    private static final Person BOB = new PersonBuilder()
            .withName("Bob").withPhone("33333333").withEmail("bob@example.com").build();
    private static final Person CHARLIE = new PersonBuilder()
            .withName("charlie").withPhone("44444444").withEmail("charlie@example.com").build();
    private static final Person ZOE = new PersonBuilder()
            .withName("Zoe").withPhone("55555555").withEmail("zoe@example.com").build();

    @Test
    public void execute_multipleUnsortedParticipants_sortsAscendingByName() throws Exception {
        Model model = createModel(ZOE, BOB, ALICE_UPPERCASE);

        CommandResult result = new SortCommand().execute(model);

        assertEquals(SortCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertDisplayedOrder(model, "Alice", "Bob", "Zoe");
    }

    @Test
    public void execute_mixedCaseNames_sortsCaseInsensitively() throws Exception {
        Model model = createModel(CHARLIE, BOB, ALICE_UPPERCASE);

        new SortCommand().execute(model);

        assertDisplayedOrder(model, "Alice", "Bob", "charlie");
    }

    @Test
    public void execute_caseInsensitiveNamesEqual_usesExactNameAsSecondaryOrder() throws Exception {
        Model model = createModel(ALICE_LOWERCASE, ALICE_UPPERCASE);

        new SortCommand().execute(model);

        assertDisplayedOrder(model, "Alice", "alice");
    }

    @Test
    public void execute_emptyList_remainsEmpty() throws Exception {
        Model model = createModel();

        new SortCommand().execute(model);

        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_oneParticipant_remainsUnchanged() throws Exception {
        Model model = createModel(BOB);

        new SortCommand().execute(model);

        assertEquals(List.of(BOB), model.getFilteredPersonList());
    }

    @Test
    public void execute_alreadySortedList_remainsInOrder() throws Exception {
        Model model = createModel(ALICE_UPPERCASE, BOB, ZOE);

        new SortCommand().execute(model);

        assertEquals(List.of(ALICE_UPPERCASE, BOB, ZOE), model.getFilteredPersonList());
    }

    @Test
    public void execute_filteredList_sortsOnlyDisplayedParticipants() throws Exception {
        Model model = createModel(ZOE, BOB, ALICE_UPPERCASE);
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Zoe", "Alice")));

        new SortCommand().execute(model);

        assertDisplayedOrder(model, "Alice", "Zoe");
        assertEquals(List.of(ZOE, BOB, ALICE_UPPERCASE), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_thenViewFirstIndex_usesSortedDisplayIndex() throws Exception {
        Model model = createModel(ZOE, BOB, ALICE_UPPERCASE);
        new SortCommand().execute(model);

        CommandResult result = new ViewCommand(INDEX_FIRST_PERSON).execute(model);

        assertTrue(result.getFeedbackToUser().startsWith("Participant details:\nName: Alice\n"));
    }

    @Test
    public void execute_doesNotModifyStoredParticipantData() throws Exception {
        Model model = createModel(ZOE, BOB, ALICE_UPPERCASE);
        AddressBook originalAddressBook = new AddressBook(model.getAddressBook());

        new SortCommand().execute(model);

        assertEquals(originalAddressBook, model.getAddressBook());
        assertEquals(List.of(ZOE, BOB, ALICE_UPPERCASE), model.getAddressBook().getPersonList());
    }

    @Test
    public void equals() {
        SortCommand command = new SortCommand();

        assertEquals(command, command);
        assertEquals(command, new SortCommand());
    }

    private static Model createModel(Person... persons) {
        AddressBookBuilder builder = new AddressBookBuilder();
        Arrays.stream(persons).forEach(builder::withPerson);
        return new ModelManager(builder.build(), new UserPrefs());
    }

    private static void assertDisplayedOrder(Model model, String... expectedNames) {
        List<String> displayedNames = model.getFilteredPersonList().stream()
                .map(person -> person.getName().fullName)
                .collect(Collectors.toList());
        assertEquals(List.of(expectedNames), displayedNames);
    }
}
