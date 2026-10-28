package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class UnassignedCommandTest {

    @TempDir
    public Path temporaryFolder;

    private final Person fullyAssigned = new PersonBuilder().withName("Zed Tan")
            .withGroup("Team A/B").withRole("Facilitator").withActivity("Campfire").build();
    private final Person missingGroup = new PersonBuilder().withName("Ada Tan")
            .withRole("Lead").withActivity("Campfire").build();
    private final Person missingRole = new PersonBuilder().withName("Bea Lim")
            .withGroup("Logistics").withActivity("Campfire").build();
    private final Person missingActivity = new PersonBuilder().withName("Cal Ong")
            .withGroup("Logistics").withRole("Lead").build();
    private final Person missingAll = new PersonBuilder().withName("Dan Raj").build();
    private final AddressBookParser parser = new AddressBookParser();
    private Model model;
    private AddressBook original;

    @BeforeEach
    public void setUp() {
        original = new AddressBook();
        original.setPersons(List.of(fullyAssigned, missingRole, missingAll, missingGroup, missingActivity));
        model = new ModelManager(original, new UserPrefs());
    }

    @Test
    public void execute_group_showsOnlyParticipantsMissingGroup() throws Exception {
        CommandResult result = parser.parseCommand("unassigned g/").execute(model);

        assertEquals(List.of(missingAll, missingGroup), model.getFilteredPersonList());
        assertEquals("2 participant(s) have no group assignment.", result.getFeedbackToUser());
        assertEquals(original, model.getAddressBook());
    }

    @Test
    public void execute_role_showsOnlyParticipantsMissingRole() throws Exception {
        CommandResult result = parser.parseCommand("unassigned r/").execute(model);

        assertEquals(List.of(missingRole, missingAll), model.getFilteredPersonList());
        assertEquals("2 participant(s) have no role assignment.", result.getFeedbackToUser());
        assertEquals(original, model.getAddressBook());
    }

    @Test
    public void execute_activity_showsOnlyParticipantsMissingActivity() throws Exception {
        CommandResult result = parser.parseCommand("unassigned a/").execute(model);

        assertEquals(List.of(missingAll, missingActivity), model.getFilteredPersonList());
        assertEquals("2 participant(s) have no activity assignment.", result.getFeedbackToUser());
        assertEquals(original, model.getAddressBook());
    }

    @Test
    public void execute_afterEmptyFilter_searchesAllStoredParticipants() throws Exception {
        parser.parseCommand("filter g/Nonexistent").execute(model);
        assertEquals(List.of(), model.getFilteredPersonList());

        parser.parseCommand("unassigned g/").execute(model);

        assertEquals(List.of(missingAll, missingGroup), model.getFilteredPersonList());
        assertEquals(original, model.getAddressBook());
    }

    @Test
    public void execute_afterSort_preservesComparatorAndDisplayedIndices() throws Exception {
        parser.parseCommand("sort name").execute(model);
        parser.parseCommand("unassigned g/").execute(model);

        assertEquals(List.of(missingGroup, missingAll), model.getFilteredPersonList());
        assertTrue(new ViewCommand(Index.fromOneBased(2)).execute(model).getFeedbackToUser()
                .startsWith("Participant details:\nName: Dan Raj\n"));
        assertEquals(original, model.getAddressBook());

        parser.parseCommand("list").execute(model);
        assertEquals(List.of(missingGroup, missingRole, missingActivity, missingAll, fullyAssigned),
                model.getFilteredPersonList());
    }

    @Test
    public void execute_emptyAddressBook_showsZeroMatches() throws Exception {
        model = new ModelManager();

        CommandResult result = parser.parseCommand("unassigned g/").execute(model);

        assertEquals(List.of(), model.getFilteredPersonList());
        assertEquals("0 participant(s) have no group assignment.", result.getFeedbackToUser());
    }

    @Test
    public void execute_everyoneAssigned_showsZeroMatchesWithoutMutation() throws Exception {
        model = new ModelManager();
        model.addPerson(fullyAssigned);

        CommandResult result = parser.parseCommand("unassigned a/").execute(model);

        assertEquals(List.of(), model.getFilteredPersonList());
        assertEquals("0 participant(s) have no activity assignment.", result.getFeedbackToUser());
        assertEquals(List.of(fullyAssigned), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_everyoneMissingSelectedField_showsAllParticipants() throws Exception {
        model = new ModelManager();
        model.addPerson(missingAll);
        model.addPerson(missingGroup);

        parser.parseCommand("unassigned g/").execute(model);

        assertEquals(List.of(missingAll, missingGroup), model.getFilteredPersonList());
    }

    @Test
    public void execute_repeatedQueriesAndReload_preservesAllStoredFieldsAndOrder() throws Exception {
        Path dataFile = temporaryFolder.resolve("participants.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(dataFile);
        storage.saveAddressBook(model.getAddressBook());
        String originalJson = Files.readString(dataFile);
        Logic logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));

        logic.execute("sort name");
        logic.execute("unassigned g/");
        logic.execute("unassigned r/");
        logic.execute("unassigned a/");

        assertEquals(originalJson, Files.readString(dataFile));
        assertEquals(original, storage.readAddressBook().orElseThrow());
        Model restarted = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(original.getPersonList(), restarted.getFilteredPersonList());
    }
}
