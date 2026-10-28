package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class RemarkCommandIntegrationTest {

    private static final String INITIAL_REMARK = "Needs vegetarian food; contact Sam at A/B.";
    private static final String REPLACEMENT_REMARK = "Wheelchair access confirmed.";

    @TempDir
    public Path temporaryFolder;

    private final Person zed = new PersonBuilder().withName("Zed Tan").withPhone("81111111")
            .withEmail("zed@example.com").withRemark("Zed's note").build();
    private final Person alice = new PersonBuilder().withName("Alice Tan").withPhone("82222222")
            .withEmail("alice@example.com").withGroup("Logistics").withRole("Facilitator")
            .withActivity("Campfire").withTags("friend").build();

    private Path participantData;
    private Model model;
    private Logic logic;
    private JsonAddressBookStorage storage;

    @BeforeEach
    public void setUp() {
        participantData = temporaryFolder.resolve("participants.json");
        model = new ModelManager();
        model.addPerson(zed);
        model.addPerson(alice);
        storage = new JsonAddressBookStorage(participantData);
        logic = createLogic(model);
    }

    @Test
    public void execute_addReplaceAndClearRemark_persistsEachChangeAcrossReload() throws Exception {
        logic.execute("sort name");
        logic.execute("remark 1 r/" + INITIAL_REMARK);
        assertDisplayedNames("Alice Tan", "Zed Tan");
        assertPersistedAlice(new PersonBuilder(alice).withRemark(INITIAL_REMARK).build());

        restartLogic();
        logic.execute("find Alice");
        logic.execute("remark 1 r/" + REPLACEMENT_REMARK);
        assertDisplayedNames("Alice Tan");
        assertPersistedAlice(new PersonBuilder(alice).withRemark(REPLACEMENT_REMARK).build());

        restartLogic();
        logic.execute("find Alice");
        logic.execute("remark 1 r/");
        assertDisplayedNames("Alice Tan");
        assertPersistedAlice(alice);
    }

    private Logic createLogic(Model targetModel) {
        JsonUserPrefsStorage preferences = new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"));
        return new LogicManager(targetModel, new StorageManager(storage, preferences));
    }

    private void restartLogic() throws Exception {
        model = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        logic = createLogic(model);
    }

    private void assertDisplayedNames(String... expectedNames) {
        List<String> displayedNames = logic.getFilteredPersonList().stream()
                .map(person -> person.getName().fullName)
                .toList();
        assertEquals(List.of(expectedNames), displayedNames);
    }

    private void assertPersistedAlice(Person expectedAlice) throws Exception {
        ReadOnlyAddressBook savedAddressBook = storage.readAddressBook().orElseThrow();
        Person persistedAlice = savedAddressBook.getPersonList().stream()
                .filter(person -> person.getName().fullName.equals("Alice Tan"))
                .findFirst()
                .orElseThrow();
        assertEquals(expectedAlice, persistedAlice);
        assertEquals(zed, savedAddressBook.getPersonList().getFirst());
    }
}
