package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.testutil.PersonBuilder;

public class SafeSavingIntegrationTest {
    @TempDir
    public Path testFolder;

    @Test
    public void execute_failedSave_preservesDiskWithoutClaimingModelRollback() throws Exception {
        Path target = testFolder.resolve("participants.json");
        AddressBook original = new AddressBook();
        original.addPerson(new PersonBuilder().withName("Alice Tan").withGroup("Team A/B")
                .withRole("Facilitator").withActivity("Arts & Crafts").withTags("volunteer").build());
        new JsonAddressBookStorage(target).saveAddressBook(original);
        byte[] originalBytes = Files.readAllBytes(target);
        AtomicFileWriter.FileOperations failingFiles = new AtomicFileWriter.FileOperations() {
            @Override
            void write(Path staged, String content) throws IOException {
                Files.writeString(staged, "partial JSON");
                throw new IOException("Injected write failure");
            }
        };
        JsonAddressBookStorage storage = new JsonAddressBookStorage(target, new AtomicFileWriter(failingFiles));
        Model model = new ModelManager(original, new UserPrefs());
        LogicManager logic = new LogicManager(model,
                new StorageManager(storage, new JsonUserPrefsStorage(testFolder.resolve("prefs.json"))));

        CommandException exception = assertThrows(CommandException.class, () -> logic.execute("edit 1 p/91234567"));
        assertTrue(exception.getMessage().contains("Could not write the complete temporary save file"));
        assertArrayEquals(originalBytes, Files.readAllBytes(target));
        assertEquals(original, new AddressBook(storage.readAddressBook().orElseThrow()));
        assertEquals("91234567", model.getAddressBook().getPersonList().getFirst().getPhone().value);

        JsonAddressBookStorage retry = new JsonAddressBookStorage(target);
        retry.saveAddressBook(model.getAddressBook());
        assertEquals(model.getAddressBook(), new AddressBook(retry.readAddressBook().orElseThrow()));
        assertEquals(new PersonBuilder(original.getPersonList().getFirst()).withPhone("91234567").build(),
                retry.readAddressBook().orElseThrow().getPersonList().getFirst());
    }
}
