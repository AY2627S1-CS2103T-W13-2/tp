package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.LogicManager;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class CommandLoggingTest {
    @TempDir
    public Path temporaryFolder;

    @BeforeAll
    public static void initializeToolkit() throws Exception {
        assumeTrue(!System.getProperty("os.name").startsWith("Linux") || System.getenv("DISPLAY") != null,
                "JavaFX component tests require a display (use Xvfb on Linux)");
        CountDownLatch started = new CountDownLatch(1);
        try {
            Platform.startup(started::countDown);
        } catch (IllegalStateException alreadyStarted) {
            Platform.runLater(started::countDown);
        }
        assertTrue(started.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void execute_uiWorkflow_logsOutcomesWithoutParticipantDataAtAllLevels() throws Exception {
        FutureTask<Void> task = new FutureTask<>(() -> {
            Logger logger = LogsCenter.getLogger(CommandLoggingTest.class).getParent();
            Level previousLevel = logger.getLevel();
            List<LogRecord> records = new ArrayList<>();
            Handler capture = new Handler() {
                @Override
                public void publish(LogRecord record) {
                    records.add(record);
                }

                @Override
                public void flush() {}

                @Override
                public void close() {}
            };
            capture.setLevel(Level.ALL);
            logger.addHandler(capture);
            logger.setLevel(Level.ALL);
            Stage stage = new Stage();
            try {
                Model model = new ModelManager();
                JsonAddressBookStorage data = new JsonAddressBookStorage(temporaryFolder.resolve("participants.json"));
                LogicManager logic = new LogicManager(model, new StorageManager(data,
                        new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));
                MainWindow window = new MainWindow(stage, logic, data.getAddressBookFilePath());
                window.fillInnerParts();
                TextField input = (TextField) stage.getScene().getRoot().lookup("#commandTextField");
                enter(input, "add n/Privacy Sentinel p/87654321 e/private@example.com a/Secret Avenue");
                enter(input, "edit 1 p/88776655");
                enter(input, "remark 1 r/Confidential dietary note");
                enter(input, "view 1");
                assertEquals("Confidential dietary note", model.getFilteredPersonList().getFirst().getRemark().value);
                new ModelManager(data.readAddressBook().orElseThrow(), new UserPrefs());
                enter(input, "remark 99 r/Hidden invalid note");
                enter(input, "add n/Rejected Person p/INVALIDPHONE e/rejected@example.com a/Hidden Road");
                enter(input, "privateUnknownCommand secretArgument");
                enter(input, "delete 1");
                assertTrue(data.readAddressBook().orElseThrow().getPersonList().isEmpty());
                String logged = records.stream().map(record -> new java.util.logging.SimpleFormatter().format(record))
                        .collect(java.util.stream.Collectors.joining());
                for (String secret : List.of("Privacy Sentinel", "87654321", "private@example.com", "Secret Avenue",
                        "88776655", "Confidential dietary note", "Hidden invalid note", "Rejected Person",
                        "INVALIDPHONE", "rejected@example.com", "Hidden Road", "privateUnknownCommand",
                        "secretArgument")) {
                    assertFalse(logged.contains(secret), "Participant input leaked into logs: " + secret);
                }
                assertTrue(logged.contains("AddCommand: succeeded"));
                assertTrue(logged.contains("EditCommand: succeeded"));
                assertTrue(logged.contains("RemarkCommand: succeeded"));
                assertTrue(logged.contains("RemarkCommand: rejected"));
                assertTrue(logged.contains("Command parsing rejected"));
                assertTrue(logged.contains("DeleteCommand: succeeded"));
            } finally {
                stage.close();
                logger.removeHandler(capture);
                logger.setLevel(previousLevel);
            }
            return null;
        });
        Platform.runLater(task);
        task.get(20, TimeUnit.SECONDS);
    }

    private static void enter(TextField field, String command) {
        field.setText(command);
        field.fireEvent(new ActionEvent());
    }
}
