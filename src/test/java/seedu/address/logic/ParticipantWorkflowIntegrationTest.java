package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class ParticipantWorkflowIntegrationTest {

    @TempDir
    public Path temporaryFolder;

    private Path participantData;
    private Model model;
    private Logic logic;

    @BeforeEach
    public void setUp() {
        participantData = temporaryFolder.resolve("participants.json");
        model = new ModelManager();
        logic = createLogic(model);
    }

    @Test
    public void execute_fullParticipantWorkflow_usesFilteredIndicesAndPersistsChanges() throws Exception {
        logic.execute("add n/Alice Tan p/81234567 e/alice@example.com a/1 Main Street");
        logic.execute("add n/Beth Lee p/82345678 e/beth@example.com a/2 Main Street");
        logic.execute("list");
        assertEquals(2, logic.getFilteredPersonList().size());

        logic.execute("find bEtH");
        assertEquals("Beth Lee", logic.getFilteredPersonList().getFirst().getName().fullName);
        assertTrue(logic.execute("view 1").getFeedbackToUser().contains("Beth Lee"));

        logic.execute("assign-role 1 r/Facilitator");
        logic.execute("find Beth");
        logic.execute("assign-group 1 g/Logistics");
        logic.execute("find Beth");
        logic.execute("assign-activity 1 a/Campfire");
        logic.execute("find Beth");
        logic.execute("edit 1 p/83456789");

        Model reloaded = new ModelManager(
                new JsonAddressBookStorage(participantData).readAddressBook().orElseThrow(), new UserPrefs());
        Person alice = reloaded.getAddressBook().getPersonList().stream()
                .filter(person -> person.getName().fullName.equals("Alice Tan"))
                .findFirst().orElseThrow();
        Person beth = reloaded.getAddressBook().getPersonList().stream()
                .filter(person -> person.getName().fullName.equals("Beth Lee"))
                .findFirst().orElseThrow();

        assertTrue(alice.getRole().isEmpty());
        assertTrue(alice.getGroup().isEmpty());
        assertTrue(alice.getActivity().isEmpty());
        assertEquals("83456789", beth.getPhone().value);
        assertEquals("Facilitator", beth.getRole().orElseThrow().value);
        assertEquals("Logistics", beth.getGroup().orElseThrow().groupName);
        assertEquals("Campfire", beth.getActivity().orElseThrow().value);

        Logic restartedLogic = createLogic(reloaded);
        restartedLogic.execute("find Beth");
        restartedLogic.execute("delete 1");
        restartedLogic.execute("find Beth");
        assertTrue(restartedLogic.getFilteredPersonList().isEmpty());
        restartedLogic.execute("list");
        assertEquals(1, restartedLogic.getFilteredPersonList().size());
        assertEquals("Alice Tan", restartedLogic.getFilteredPersonList().getFirst().getName().fullName);
    }

    @Test
    public void execute_noMatchAndEmptyDisplayedList_indexedCommandsFailWithoutDataLoss() throws Exception {
        logic.execute("add n/Alice Tan p/81234567 e/alice@example.com a/1 Main Street");
        logic.execute("find Nobody");
        assertTrue(logic.getFilteredPersonList().isEmpty());

        for (String command : new String[] {
            "view 1",
            "edit 1 p/82345678",
            "delete 1",
            "assign-role 1 r/Facilitator",
            "assign-group 1 g/Logistics",
            "assign-activity 1 a/Campfire"
        }) {
            assertThrows(CommandException.class, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, () ->
                    logic.execute(command));
            assertEquals(1, model.getAddressBook().getPersonList().size());
            assertTrue(logic.getFilteredPersonList().isEmpty());
        }
    }

    private Logic createLogic(Model targetModel) {
        JsonAddressBookStorage storage = new JsonAddressBookStorage(participantData);
        JsonUserPrefsStorage preferences = new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"));
        return new LogicManager(targetModel, new StorageManager(storage, preferences));
    }
}
