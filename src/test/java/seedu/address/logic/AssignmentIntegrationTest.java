package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
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
}
