package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class AssignmentIntegrationTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    public void execute_assignReplaceAndEdit_preservesAllAssignmentsAcrossRestarts() throws Exception {
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("participants.json"));
        JsonUserPrefsStorage prefs = new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"));
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().build());
        Logic logic = new LogicManager(model, new StorageManager(storage, prefs));

        logic.execute("assign-role 1 r/Facilitator");
        logic.execute("assign-activity 1 a/Campfire");
        logic.execute("assign-group 1 g/Logistics");
        logic.execute("edit 1 p/91234567");
        Model reloaded = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(new PersonBuilder().withPhone("91234567").withRole("Facilitator")
                .withActivity("Campfire").withGroup("Logistics").build(), reloaded.getFilteredPersonList().get(0));

        Logic restartedLogic = new LogicManager(reloaded, new StorageManager(storage, prefs));
        restartedLogic.execute("assign-group 1 g/Programmes");
        restartedLogic.execute("assign-role 1 r/Logistics Lead");
        restartedLogic.execute("assign-activity 1 a/Arts & Crafts");
        restartedLogic.execute("edit 1 n/Amy Updated");
        Model reloadedAgain = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(new PersonBuilder().withName("Amy Updated").withPhone("91234567")
                .withRole("Logistics Lead").withActivity("Arts & Crafts").withGroup("Programmes").build(),
                reloadedAgain.getFilteredPersonList().get(0));
    }

    @Test
    public void execute_unassign_persistsClearedAssignmentsAcrossRestart() throws Exception {
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("participants.json"));
        JsonUserPrefsStorage prefs = new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"));
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().withRole("Facilitator").withActivity("Campfire")
                .withGroup("Logistics").build());
        Logic logic = new LogicManager(model, new StorageManager(storage, prefs));

        logic.execute("unassign 1 r/");
        logic.execute("unassign 1 a/");
        logic.execute("unassign 1 g/");

        Model reloaded = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(new PersonBuilder().build(), reloaded.getFilteredPersonList().get(0));

        Logic restartedLogic = new LogicManager(reloaded, new StorageManager(storage, prefs));
        restartedLogic.execute("assign-role 1 r/Lead");
        restartedLogic.execute("assign-activity 1 a/Workshop");
        restartedLogic.execute("assign-group 1 g/Programmes");

        Model reloadedAfterReassignment = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(new PersonBuilder().withRole("Lead").withActivity("Workshop")
                .withGroup("Programmes").build(), reloadedAfterReassignment.getFilteredPersonList().get(0));
    }

    @Test
    public void execute_assignEditThenUnassignOneField_preservesOtherDataAcrossRestart() throws Exception {
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("participants.json"));
        JsonUserPrefsStorage prefs = new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"));
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().build());
        model.addPerson(new PersonBuilder().withName("Zoe Lee").build());
        Logic logic = new LogicManager(model, new StorageManager(storage, prefs));

        logic.execute("assign-group 1 g/Logistics");
        logic.execute("assign-role 1 r/Facilitator");
        logic.execute("assign-activity 1 a/Campfire");
        logic.execute("edit 1 p/91234567");
        logic.execute("unassign 1 r/");

        Model reloaded = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        Person amy = reloaded.getAddressBook().getPersonList().stream()
                .filter(person -> person.getName().fullName.equals("Amy Bee")).findFirst().orElseThrow();
        Person zoe = reloaded.getAddressBook().getPersonList().stream()
                .filter(person -> person.getName().fullName.equals("Zoe Lee")).findFirst().orElseThrow();
        assertEquals(new PersonBuilder().withPhone("91234567").withGroup("Logistics")
                .withActivity("Campfire").build(), amy);
        assertEquals(new PersonBuilder().withName("Zoe Lee").build(), zoe);
    }

    @Test
    public void execute_unassignAfterSort_usesSortedDisplayedIndex() throws Exception {
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("participants.json"));
        JsonUserPrefsStorage prefs = new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"));
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("Zoe Lee").withGroup("Events").build());
        model.addPerson(new PersonBuilder().withName("Alice Tan").withGroup("Logistics").build());
        Logic logic = new LogicManager(model, new StorageManager(storage, prefs));

        logic.execute("sort name");
        assertEquals("Alice Tan", logic.getFilteredPersonList().getFirst().getName().fullName);
        logic.execute("unassign 1 g/");

        Model reloaded = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        Person alice = reloaded.getAddressBook().getPersonList().stream()
                .filter(person -> person.getName().fullName.equals("Alice Tan")).findFirst().orElseThrow();
        Person zoe = reloaded.getAddressBook().getPersonList().stream()
                .filter(person -> person.getName().fullName.equals("Zoe Lee")).findFirst().orElseThrow();
        assertTrue(alice.getGroup().isEmpty());
        assertEquals("Events", zoe.getGroup().orElseThrow().groupName);
    }
}
