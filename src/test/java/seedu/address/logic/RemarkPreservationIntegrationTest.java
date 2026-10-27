package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class RemarkPreservationIntegrationTest {

    private static final String NOTE = "Needs vegetarian food; contact \"Sam\" at A/B.";

    @TempDir
    public Path temporaryFolder;

    private final Person otherParticipant = new PersonBuilder().withName("Zed Tan").withRemark("Zed's note").build();
    private Model model;
    private Logic logic;
    private JsonAddressBookStorage storage;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        model.addPerson(otherParticipant);
        model.addPerson(new PersonBuilder().withName("Alice Tan").withTags("friend")
                .withGroup("Logistics").withRole("Helper").withActivity("Campfire").withRemark(NOTE).build());
        storage = new JsonAddressBookStorage(temporaryFolder.resolve("participants.json"));
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
    }

    @Test
    public void execute_editAndAssignmentCommands_preservesRemarkAcrossEachSaveAndReload() throws Exception {
        for (String command : List.of("edit 1 p/83456789", "assign-group 1 g/Team A",
                "assign-role 1 r/Facilitator", "assign-activity 1 a/Registration",
                "unassign 1 g/", "unassign 1 r/", "unassign 1 a/")) {
            logic.execute("sort name");
            logic.execute("find Alice");
            logic.execute(command);
            assertRemarkAndReload();
        }
    }

    @Test
    public void execute_invalidIndex_keepsRemarkAndSavedBytesUnchanged() throws Exception {
        logic.execute("find Alice");
        String original = Files.readString(storage.getAddressBookFilePath());
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, () ->
                logic.execute("assign-activity 2 a/Registration"));
        assertEquals(original, Files.readString(storage.getAddressBookFilePath()));
        assertRemarkAndReload();
    }

    private void assertRemarkAndReload() throws Exception {
        Person alice = model.getAddressBook().getPersonList().get(1);
        assertEquals(new Remark(NOTE), alice.getRemark());
        assertEquals(otherParticipant, model.getAddressBook().getPersonList().getFirst());
        assertEquals(model.getAddressBook(), new AddressBook(storage.readAddressBook().orElseThrow()));
    }
}
