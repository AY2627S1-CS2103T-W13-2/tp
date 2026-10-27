package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.FindContactCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class FindContactWorkflowIntegrationTest {

    @TempDir
    public Path temporaryFolder;

    private final Person alice = new PersonBuilder().withName("Alice Tan").withPhone("81234567")
            .withEmail("alice@example.com").withTags("friend").withGroup("Logistics").withRole("Helper")
            .withActivity("Campfire").withRemark("Vegetarian meal").build();
    private final Person beth = new PersonBuilder().withName("Beth Lee").withPhone("82345678")
            .withEmail("beth@example.com").build();
    private AddressBook original;
    private Model model;
    private Logic logic;
    private JsonAddressBookStorage storage;

    @BeforeEach
    public void setUp() throws Exception {
        original = new AddressBook();
        original.addPerson(beth);
        original.addPerson(alice);
        model = new ModelManager(original, new UserPrefs());
        storage = new JsonAddressBookStorage(temporaryFolder.resolve("participants.json"));
        storage.saveAddressBook(original);
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
    }

    @Test
    public void execute_unicodePaddedContacts_searchesAllRecordsWithoutChangingData() throws Exception {
        for (String query : List.of("\u2003ALICE@EXAMPLE.COM\u2003", " \t\u300081234567\u2002 ")) {
            logic.execute("sort name");
            logic.execute("find Beth");
            String before = Files.readString(storage.getAddressBookFilePath());

            assertEquals("1 person(s) listed!", logic.execute("find-contact " + query).getFeedbackToUser());
            assertEquals(List.of(alice), logic.getFilteredPersonList());
            String details = logic.execute("view 1").getFeedbackToUser();
            assertTrue(details.contains("Name: Alice Tan"));
            assertTrue(details.contains("Phone: 81234567"));
            assertTrue(details.contains("Remark: Vegetarian meal"));
            assertEquals(original, model.getAddressBook());
            assertEquals(original, new AddressBook(storage.readAddressBook().orElseThrow()));
            assertEquals(before, Files.readString(storage.getAddressBookFilePath()));
        }
    }

    @Test
    public void execute_unicodePaddedNoMatch_returnsEmptyViewWithoutChangingData() throws Exception {
        String before = Files.readString(storage.getAddressBookFilePath());

        assertEquals("0 person(s) listed!",
                logic.execute("find-contact \u2003nobody@example.com\u2003").getFeedbackToUser());
        assertEquals(List.of(), logic.getFilteredPersonList());
        assertEquals(original, model.getAddressBook());
        assertEquals(before, Files.readString(storage.getAddressBookFilePath()));
    }

    @Test
    public void execute_invalidContacts_preservesViewAndSavedData() throws Exception {
        logic.execute("find Beth");
        String before = Files.readString(storage.getAddressBookFilePath());

        for (String query : List.of("alice\u2003@example.com", "8123\u20034567",
                "alice@example.com\u2003extra", "\u00a0alice@example.com\u00a0")) {
            ParseException exception = assertThrows(ParseException.class, () -> logic.execute("find-contact " + query));
            assertEquals(FindContactCommand.MESSAGE_INVALID_CONTACT, exception.getMessage());
            assertEquals(List.of(beth), logic.getFilteredPersonList());
            assertEquals(original, model.getAddressBook());
            assertEquals(before, Files.readString(storage.getAddressBookFilePath()));
        }
    }
}
