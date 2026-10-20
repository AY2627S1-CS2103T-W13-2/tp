package seedu.address;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.ui.Ui;
import seedu.address.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final String VERSION = "V1.3";

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);
    private static final Path USER_PREFS_FILE_PATH = Paths.get("preferences.json");
    private static final Path ADDRESS_BOOK_FILE_PATH = Paths.get("data", "addressbook.json");

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    private String startupError;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing AddressBook ]===========================");
        super.init();

        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(USER_PREFS_FILE_PATH);
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(ADDRESS_BOOK_FILE_PATH);
        initializeFromStorage(new StorageManager(addressBookStorage, userPrefsStorage), userPrefs);
    }

    /**
     * Creates an editable model and UI only after participant data loads successfully.
     */
    void initializeFromStorage(Storage storage, ReadOnlyUserPrefs userPrefs) {
        this.storage = storage;
        model = null;
        logic = null;
        ui = null;
        startupError = null;

        try {
            model = initModelManager(storage, userPrefs);
        } catch (DataLoadingException e) {
            Path dataFile = storage.getAddressBookFilePath().toAbsolutePath().normalize();
            startupError = "Could not load participant data from " + dataFile + ". The file was not changed, and "
                    + "no editable participant directory was opened. Back up the file, then correct its JSON or "
                    + "read permissions, or restore a known-good copy before restarting ZoomAddress.";
            logger.severe("Participant data could not be loaded from " + dataFile + ". Startup was blocked.");
            return;
        }

        logic = new LogicManager(model, storage);
        ui = new UiManager(logic, storage.getAddressBookFilePath());
    }

    /**
     * Returns a model using stored participant data, or sample data only when the file is genuinely absent.
     * A load failure must reach the startup blocker rather than becoming an editable empty directory.
     */
    Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) throws DataLoadingException {
        logger.info("Using data file : " + storage.getAddressBookFilePath());

        Optional<ReadOnlyAddressBook> addressBookOptional = storage.readAddressBook();
        if (addressBookOptional.isEmpty()) {
            logger.info("Creating a new data file " + storage.getAddressBookFilePath()
                    + " populated with a sample AddressBook.");
        }
        ReadOnlyAddressBook initialData = addressBookOptional.orElseGet(SampleDataUtil::getSampleAddressBook);

        return new ModelManager(initialData, userPrefs);
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(JsonUserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (prefsOptional.isEmpty()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save preference file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting AddressBook " + MainApp.VERSION);
        if (startupError != null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ZoomAddress could not start");
            alert.setHeaderText("Participant data could not be loaded");
            alert.setContentText(startupError);
            alert.showAndWait();
            Platform.exit();
            return;
        }
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping AddressBook ] =============================");
        if (storage == null || model == null) {
            return;
        }
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }

    String getStartupError() {
        return startupError;
    }
}
