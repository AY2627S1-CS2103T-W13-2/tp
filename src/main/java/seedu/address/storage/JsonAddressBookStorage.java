package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ReadOnlyAddressBook;

/**
 * A class to access AddressBook data stored as a JSON file on the hard disk.
 */
public class JsonAddressBookStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonAddressBookStorage.class);

    private final Path filePath;
    private final AtomicFileWriter fileWriter;

    public JsonAddressBookStorage(Path filePath) {
        this(filePath, new AtomicFileWriter());
    }

    /** Creates storage with an injectable write boundary; the public read contract is unchanged. */
    JsonAddressBookStorage(Path filePath, AtomicFileWriter fileWriter) {
        this.filePath = requireNonNull(filePath);
        this.fileWriter = requireNonNull(fileWriter);
    }

    public Path getAddressBookFilePath() {
        return filePath;
    }

    /**
     * Returns AddressBook data as a {@link ReadOnlyAddressBook}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
        return readAddressBook(filePath);
    }

    /**
     * Similar to {@link #readAddressBook()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        if (!dataPathExists(filePath)) {
            return Optional.empty();
        }
        Optional<JsonSerializableAddressBook> jsonAddressBook;
        try {
            jsonAddressBook = JsonUtil.readJsonFile(filePath, JsonSerializableAddressBook.class);
        } catch (SecurityException e) {
            logger.warning("Participant data could not be read from " + filePath);
            throw new DataLoadingException(e);
        }
        if (jsonAddressBook.isEmpty()) {
            throw new DataLoadingException(new IOException("Existing participant data could not be read."));
        }

        try {
            return Optional.of(jsonAddressBook.get().toModelType());
        } catch (IllegalValueException e) {
            logger.warning("Invalid participant data found in " + filePath);
            throw new DataLoadingException(e);
        }
    }

    /**
     * Distinguishes an absent first-run file from an existing path that cannot be loaded.
     */
    private boolean dataPathExists(Path filePath) throws DataLoadingException {
        try {
            Files.readAttributes(filePath, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
            return true;
        } catch (NoSuchFileException e) {
            return false;
        } catch (IOException | SecurityException e) {
            logger.warning("Could not inspect participant data path " + filePath);
            throw new DataLoadingException(e);
        }
    }

    /**
     * Saves the given {@link ReadOnlyAddressBook} to the storage.
     * @param addressBook cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        saveAddressBook(addressBook, filePath);
    }

    /**
     * Similar to {@link #saveAddressBook(ReadOnlyAddressBook)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath) throws IOException {
        requireNonNull(addressBook);
        requireNonNull(filePath);

        String content;
        try {
            content = JsonUtil.toJsonString(new JsonSerializableAddressBook(addressBook));
        } catch (IOException | RuntimeException exception) {
            logger.warning("Could not serialize participant data; no file replacement attempted.");
            throw new IOException("Could not serialize participant data. The saved file was not replaced.", exception);
        }
        fileWriter.write(filePath, content);
    }

}
