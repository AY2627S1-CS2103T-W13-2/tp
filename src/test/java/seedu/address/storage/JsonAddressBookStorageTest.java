package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.HOON;
import static seedu.address.testutil.TypicalPersons.IDA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class JsonAddressBookStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonAddressBookStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readAddressBook(null));
    }

    private java.util.Optional<ReadOnlyAddressBook> readAddressBook(String filePath) throws Exception {
        return new JsonAddressBookStorage(Paths.get(filePath)).readAddressBook(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void saveAndReadAddressBook_optionalAssignments_preservesEachCombination() throws Exception {
        Path filePath = testFolder.resolve("Assignments.json");
        AddressBook original = new AddressBook();
        original.addPerson(new PersonBuilder().withName("Unassigned Participant").build());
        original.addPerson(new PersonBuilder().withName("Role Participant").withRole("Facilitator").build());
        original.addPerson(new PersonBuilder().withName("Activity Participant").withActivity("Campfire").build());
        original.addPerson(new PersonBuilder().withName("Group Participant").withGroup("Logistics").build());
        original.addPerson(new PersonBuilder().withName("Role Group Participant")
                .withRole("Facilitator").withGroup("Logistics").build());
        original.addPerson(new PersonBuilder().withName("Activity Group Participant")
                .withActivity("Campfire").withGroup("Logistics").build());
        original.addPerson(new PersonBuilder().withName("All Assignments Participant")
                .withRole("Facilitator").withActivity("Campfire").withGroup("Logistics").build());
        original.addPerson(new PersonBuilder().withName("Both Participant")
                .withRole("Facilitator").withActivity("Campfire").build());
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);

        storage.saveAddressBook(original);
        JsonAddressBookStorage restartedStorage = new JsonAddressBookStorage(filePath);
        assertEquals(original, new AddressBook(restartedStorage.readAddressBook().orElseThrow()));
    }

    @Test
    public void readAddressBook_missingOptionalAssignments_preservesExistingAssignment() throws Exception {
        Path filePath = testFolder.resolve("OlderAssignments.json");
        Files.writeString(filePath, """
                {"persons": [
                  {"name": "Role Participant", "phone": "91234567", "email": "role@example.com",
                   "address": "Kent Ridge", "tags": [], "role": "Facilitator"},
                  {"name": "Activity Participant", "phone": "92345678", "email": "activity@example.com",
                   "address": "Kent Ridge", "tags": [], "activity": "Campfire"}
                ]}
                """);
        AddressBook expected = new AddressBook();
        expected.addPerson(new PersonBuilder().withName("Role Participant").withPhone("91234567")
                .withEmail("role@example.com").withAddress("Kent Ridge").withRole("Facilitator").build());
        expected.addPerson(new PersonBuilder().withName("Activity Participant").withPhone("92345678")
                .withEmail("activity@example.com").withAddress("Kent Ridge").withActivity("Campfire").build());

        ReadOnlyAddressBook loaded = new JsonAddressBookStorage(filePath).readAddressBook().orElseThrow();
        assertEquals(expected, new AddressBook(loaded));
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readAddressBook("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_danglingSymbolicLink_throwsDataLoadingException() throws Exception {
        Path link = testFolder.resolve("participants.json");
        try {
            Files.createSymbolicLink(link, testFolder.resolve("missing.json"));
        } catch (UnsupportedOperationException | FileSystemException | SecurityException e) {
            assumeTrue(false, "Symbolic links are unavailable on this test platform");
        }

        assertThrows(DataLoadingException.class, () -> new JsonAddressBookStorage(link).readAddressBook());
        assertFalse(Files.exists(testFolder.resolve("missing.json")));
    }

    @Test
    public void read_directoryAtDataPath_throwsDataLoadingException() throws Exception {
        Path directory = Files.createDirectory(testFolder.resolve("participants.json"));

        assertThrows(DataLoadingException.class, () -> new JsonAddressBookStorage(directory).readAddressBook());
        try (var files = Files.list(directory)) {
            assertEquals(0, files.count());
        }
    }

    @Test
    public void readAddressBook_legacyFileWithoutActivities_success() throws Exception {
        Path legacyFile = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest",
                "typicalPersonsAddressBook.json");
        ReadOnlyAddressBook legacy = new JsonAddressBookStorage(legacyFile).readAddressBook().orElseThrow();
        assertEquals(getTypicalAddressBook(), new AddressBook(legacy));
        legacy.getPersonList().forEach(person -> assertFalse(person.getActivity().isPresent()));
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("notJsonFormatAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidPersonAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("invalidPersonAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidAndValidPersonAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("invalidAndValidPersonAddressBook.json"));
    }

    @Test
    public void readAddressBook_nullTag_throwsDataLoadingException() throws Exception {
        Path filePath = testFolder.resolve("NullTag.json");
        String originalContent = """
                {"persons": [{"name":"Alice", "phone":"91234567", "email":"alice@example.com",
                  "address":"Kent Ridge", "tags":[null]}]}
                """;
        Files.writeString(filePath, originalContent);

        assertThrows(DataLoadingException.class, () -> new JsonAddressBookStorage(filePath).readAddressBook());
        assertEquals(originalContent, Files.readString(filePath));
    }

    @Test
    public void readAddressBook_nullPersons_throwsDataLoadingException() throws Exception {
        Path filePath = testFolder.resolve("NullPersons.json");
        Files.writeString(filePath, "{\"persons\":null}");

        assertThrows(DataLoadingException.class, () -> new JsonAddressBookStorage(filePath).readAddressBook());
        assertEquals("{\"persons\":null}", Files.readString(filePath));
    }

    @Test
    public void readAddressBook_duplicatePersonsKeys_preservesBytes() throws Exception {
        assertDuplicateKeysRejected("""
                {"persons": [
                  {"name":"Alice", "phone":"91234567", "email":"alice@example.com",
                   "address":"Kent Ridge", "tags":[]}
                ], "persons": []}
                """);
    }

    @Test
    public void readAddressBook_duplicateEmptyOrEscapedPersonsKeys_preservesBytes() throws Exception {
        assertDuplicateKeysRejected("{\"persons\":[],\"persons\":[]}");
        assertDuplicateKeysRejected("{\"persons\":[],\"per\\u0073ons\":[]}");
    }

    @Test
    public void readAddressBook_duplicateParticipantFields_preservesBytes() throws Exception {
        assertDuplicateKeysRejected("""
                {"persons": [{"name":"Alice", "phone":"91234567", "email":"alice@example.com",
                  "address":"Kent Ridge", "tags":[], "remark":"Original note", "remark":""}]}
                """);
    }

    @Test
    public void readAddressBook_distinctUnknownFields_successWithoutChangingBytes() throws Exception {
        Path filePath = testFolder.resolve("FutureFields.json");
        Files.writeString(filePath, "{\"persons\":[],\"future\":{\"first\":1,\"second\":2}}");
        byte[] originalBytes = Files.readAllBytes(filePath);

        ReadOnlyAddressBook loaded = new JsonAddressBookStorage(filePath).readAddressBook().orElseThrow();

        assertEquals(new AddressBook(), new AddressBook(loaded));
        assertArrayEquals(originalBytes, Files.readAllBytes(filePath));
    }

    private void assertDuplicateKeysRejected(String content) throws Exception {
        Path filePath = testFolder.resolve("DuplicateKeys.json");
        Files.writeString(filePath, content);
        byte[] originalBytes = Files.readAllBytes(filePath);

        assertThrows(DataLoadingException.class, () -> new JsonAddressBookStorage(filePath).readAddressBook());
        assertArrayEquals(originalBytes, Files.readAllBytes(filePath));
    }

    @Test
    public void readAndSaveAddressBook_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempAddressBook.json");
        AddressBook original = getTypicalAddressBook();
        JsonAddressBookStorage jsonAddressBookStorage = new JsonAddressBookStorage(filePath);

        // Save in new file and read back
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        ReadOnlyAddressBook readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Modify data, overwrite existing file, and read back
        original.addPerson(HOON);
        original.removePerson(ALICE);
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Save and read without specifying file path
        original.addPerson(IDA);
        jsonAddressBookStorage.saveAddressBook(original); // file path not specified
        readBack = jsonAddressBookStorage.readAddressBook().get(); // file path not specified
        assertEquals(original, new AddressBook(readBack));

    }

    @Test
    public void saveAndReadAddressBook_personWithRole_rolePreserved() throws Exception {
        Path filePath = testFolder.resolve("AddressBookWithRole.json");
        Person participantWithRole = new PersonBuilder().withRole("Logistics Lead").build();
        AddressBook original = new AddressBook();
        original.addPerson(participantWithRole);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);

        storage.saveAddressBook(original);
        ReadOnlyAddressBook readBack = storage.readAddressBook().get();

        assertEquals(original, new AddressBook(readBack));
        assertEquals("Logistics Lead", readBack.getPersonList().get(0).getRole().get().value);
    }

    @Test
    public void readAndSaveAddressBook_groupAssignmentPreserved() throws Exception {
        Path filePath = testFolder.resolve("AddressBookWithGroup.json");
        Person assignedPerson = new PersonBuilder().withGroup("Logistics").build();
        AddressBook original = new AddressBook();
        original.addPerson(assignedPerson);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);

        storage.saveAddressBook(original);
        ReadOnlyAddressBook readBack = storage.readAddressBook().orElseThrow();

        assertEquals(original, new AddressBook(readBack));
        assertEquals(assignedPerson.getGroup(), readBack.getPersonList().getFirst().getGroup());
    }

    @Test
    public void saveAddressBook_nullAddressBook_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(null, "SomeFile.json"));
    }

    @Test
    public void saveAddressBook_snapshotFailure_doesNotCreateDestination() {
        Path filePath = testFolder.resolve("FailedFirstSave.json");
        ReadOnlyAddressBook brokenSnapshot = () -> {
            throw new IllegalStateException("Injected snapshot failure");
        };

        assertThrows(IOException.class, () ->
                new JsonAddressBookStorage(filePath).saveAddressBook(brokenSnapshot));

        assertFalse(Files.exists(filePath));
    }

    @Test
    public void saveAddressBook_snapshotFailure_preservesExistingFile() throws Exception {
        Path filePath = testFolder.resolve("FailedExistingSave.json");
        String originalContent = "original participant file";
        Files.writeString(filePath, originalContent);
        ReadOnlyAddressBook brokenSnapshot = () -> {
            throw new IllegalStateException("Injected snapshot failure");
        };

        assertThrows(IOException.class, () ->
                new JsonAddressBookStorage(filePath).saveAddressBook(brokenSnapshot));

        assertEquals(originalContent, Files.readString(filePath));
    }

    /**
     * Saves {@code addressBook} at the specified {@code filePath}.
     */
    private void saveAddressBook(ReadOnlyAddressBook addressBook, String filePath) {
        try {
            new JsonAddressBookStorage(Paths.get(filePath))
                    .saveAddressBook(addressBook, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(new AddressBook(), null));
    }
}
