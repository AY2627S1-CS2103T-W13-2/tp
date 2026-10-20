package seedu.address;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class MainAppTest {
    @TempDir
    public Path testFolder;

    @Test
    public void initModelManager_missingFile_usesSampleData() throws Exception {
        Path dataFile = testFolder.resolve("missing.json");

        Model model = new MainApp().initModelManager(storage(dataFile), new UserPrefs());

        assertEquals(SampleDataUtil.getSampleAddressBook(), model.getAddressBook());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    public void initModelManager_validFile_loadsWithoutChangingBytes() throws Exception {
        Path dataFile = testFolder.resolve("participants.json");
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(new PersonBuilder().withGroup("Logistics")
                .withRole("Facilitator").withActivity("Campfire").build());
        new JsonAddressBookStorage(dataFile).saveAddressBook(addressBook);
        byte[] originalBytes = Files.readAllBytes(dataFile);

        Model model = new MainApp().initModelManager(storage(dataFile), new UserPrefs());

        assertEquals(addressBook, model.getAddressBook());
        assertArrayEquals(originalBytes, Files.readAllBytes(dataFile));
    }

    @Test
    public void initModelManager_validEmptyFile_keepsDirectoryEmpty() throws Exception {
        Path dataFile = testFolder.resolve("participants.json");
        new JsonAddressBookStorage(dataFile).saveAddressBook(new AddressBook());

        Model model = new MainApp().initModelManager(storage(dataFile), new UserPrefs());

        assertEquals(new AddressBook(), model.getAddressBook());
    }

    @Test
    public void initModelManager_malformedFile_repeatedFailuresPreserveBytes() throws Exception {
        Path dataFile = testFolder.resolve("participants.json");
        Files.writeString(dataFile, "not valid JSON");
        byte[] originalBytes = Files.readAllBytes(dataFile);

        for (int attempt = 0; attempt < 2; attempt++) {
            assertThrows(DataLoadingException.class, () ->
                    new MainApp().initModelManager(storage(dataFile), new UserPrefs()));
            assertArrayEquals(originalBytes, Files.readAllBytes(dataFile));
        }
    }

    @Test
    public void initModelManager_invalidStoredValue_preservesBytes() throws Exception {
        Path dataFile = testFolder.resolve("participants.json");
        Files.writeString(dataFile, """
                {"persons": [{"name":"Alice", "phone":"not-a-phone", "email":"alice@example.com",
                  "address":"Kent Ridge", "tags":[]}]}
                """);
        byte[] originalBytes = Files.readAllBytes(dataFile);

        assertThrows(DataLoadingException.class, () ->
                new MainApp().initModelManager(storage(dataFile), new UserPrefs()));
        assertArrayEquals(originalBytes, Files.readAllBytes(dataFile));
    }

    @Test
    public void initializeFromStorage_nullRoot_blocksStartupAndPreservesBytes() throws Exception {
        Path dataFile = testFolder.resolve("participants.json");
        Files.writeString(dataFile, "null");
        byte[] originalBytes = Files.readAllBytes(dataFile);
        MainApp app = new MainApp();

        app.initializeFromStorage(storage(dataFile), new UserPrefs());

        assertNull(app.model);
        assertNull(app.logic);
        assertNull(app.ui);
        assertNotNull(app.getStartupError());
        assertArrayEquals(originalBytes, Files.readAllBytes(dataFile));
    }

    @Test
    public void initializeFromStorage_nullStoredPerson_blocksStartupAndPreservesBytes() throws Exception {
        Path dataFile = testFolder.resolve("participants.json");
        Files.writeString(dataFile, "{\"persons\":[null]}");
        byte[] originalBytes = Files.readAllBytes(dataFile);
        MainApp app = new MainApp();

        app.initializeFromStorage(storage(dataFile), new UserPrefs());

        assertNull(app.model);
        assertNull(app.logic);
        assertNull(app.ui);
        assertNotNull(app.getStartupError());
        assertArrayEquals(originalBytes, Files.readAllBytes(dataFile));
    }

    @Test
    public void initModelManager_injectedReadFailure_preservesExistingFile() throws Exception {
        Path dataFile = testFolder.resolve("participants.json");
        Files.writeString(dataFile, "existing participant bytes");
        byte[] originalBytes = Files.readAllBytes(dataFile);
        Storage failingStorage = new StorageManager(new JsonAddressBookStorage(dataFile),
                new JsonUserPrefsStorage(testFolder.resolve("prefs.json"))) {
            @Override
            public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
                throw new DataLoadingException(new IOException("Injected read failure"));
            }
        };

        assertThrows(DataLoadingException.class, () ->
                new MainApp().initModelManager(failingStorage, new UserPrefs()));
        assertArrayEquals(originalBytes, Files.readAllBytes(dataFile));
    }

    @Test
    public void initializeFromStorage_repeatedReadFailures_blockEditableModelAndSkipShutdownSave() throws Exception {
        Path dataFile = testFolder.resolve("participants.json");
        Files.writeString(dataFile, "invalid participant data");
        byte[] originalBytes = Files.readAllBytes(dataFile);
        int[] preferenceSaves = {0};
        Storage failingStorage = new StorageManager(new JsonAddressBookStorage(dataFile),
                new JsonUserPrefsStorage(testFolder.resolve("prefs.json"))) {
            @Override
            public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
                throw new DataLoadingException(new IOException("Injected unreadable file"));
            }

            @Override
            public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) {
                preferenceSaves[0]++;
            }
        };

        for (int attempt = 0; attempt < 2; attempt++) {
            MainApp app = new MainApp();
            app.initializeFromStorage(failingStorage, new UserPrefs());

            assertNull(app.model);
            assertNull(app.logic);
            assertNull(app.ui);
            assertNotNull(app.getStartupError());
            assertTrue(app.getStartupError().contains(dataFile.toString()));
            assertTrue(app.getStartupError().contains("Back up the file"));
            app.stop();
            assertArrayEquals(originalBytes, Files.readAllBytes(dataFile));
        }
        assertEquals(0, preferenceSaves[0]);
    }

    @Test
    public void initializeFromStorage_validFile_opensEditableModel() throws Exception {
        Path dataFile = testFolder.resolve("participants.json");
        new JsonAddressBookStorage(dataFile).saveAddressBook(new AddressBook());
        MainApp app = new MainApp();

        app.initializeFromStorage(storage(dataFile), new UserPrefs());

        assertNotNull(app.model);
        assertNotNull(app.logic);
        assertNotNull(app.ui);
        assertNull(app.getStartupError());
    }

    private Storage storage(Path dataFile) {
        return new StorageManager(new JsonAddressBookStorage(dataFile),
                new JsonUserPrefsStorage(testFolder.resolve("prefs.json")));
    }
}
