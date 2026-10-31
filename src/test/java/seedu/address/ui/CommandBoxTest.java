package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class CommandBoxTest {

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
    public void paste_tabs_normalizesEachTabBeforeExecution() throws Exception {
        onFxThread(() -> {
            assertPastedCommand("edit 1 a/Room 1\tt/helper", "edit 1 a/Room 1 t/helper");
            assertPastedCommand("edit 1 a/Room 1\t\tt/helper", "edit 1 a/Room 1  t/helper");
            assertPastedCommand("edit 1 a/Room 1 t/helper", "edit 1 a/Room 1 t/helper");
        });
    }

    @Test
    public void paste_invalidCommand_normalizesTabAndShowsError() throws Exception {
        onFxThread(() -> {
            CommandBox commandBox = new CommandBox(command -> {
                throw new ParseException("Invalid command");
            });
            TextField input = commandField(commandBox);

            paste(input, "list\tunexpected");
            input.fireEvent(new ActionEvent());

            assertEquals("list unexpected", input.getText());
            assertTrue(input.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
        });
    }

    @Test
    public void paste_editCommand_separatesAddressAndTagAfterReload() throws Exception {
        onFxThread(() -> {
            JsonAddressBookStorage data = new JsonAddressBookStorage(temporaryFolder.resolve("participants.json"));
            StorageManager storage = new StorageManager(data,
                    new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json")));
            LogicManager logic = new LogicManager(new ModelManager(), storage);
            TextField input = commandField(new CommandBox(logic::execute));

            enter(input, "add n/Alice Tan p/81234567 e/alice@example.com a/1 Main Street");
            enter(input, "find Alice");
            paste(input, "edit 1 a/Room 1\tt/helper");
            input.fireEvent(new ActionEvent());

            Person persisted = data.readAddressBook().orElseThrow().getPersonList().getFirst();
            assertEquals("Room 1", persisted.getAddress().value);
            assertEquals(Set.of(new Tag("helper")), persisted.getTags());

            ModelManager reloaded = new ModelManager(data.readAddressBook().orElseThrow(), new UserPrefs());
            Person reloadedPerson = reloaded.getAddressBook().getPersonList().getFirst();
            assertEquals(persisted, reloadedPerson);
        });
    }

    private static void assertPastedCommand(String pastedText, String expectedCommand) {
        AtomicReference<String> executedCommand = new AtomicReference<>();
        CommandBox commandBox = new CommandBox(command -> {
            executedCommand.set(command);
            return new CommandResult("Executed");
        });
        TextField input = commandField(commandBox);

        paste(input, pastedText);
        input.fireEvent(new ActionEvent());

        assertEquals(expectedCommand, executedCommand.get());
    }

    private static TextField commandField(CommandBox commandBox) {
        return (TextField) commandBox.getRoot().lookup("#commandTextField");
    }

    private static void enter(TextField input, String command) {
        input.setText(command);
        input.fireEvent(new ActionEvent());
    }

    private static void paste(TextField input, String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent previousContent = copyClipboardContent(clipboard);
        ClipboardContent pastedContent = new ClipboardContent();
        pastedContent.putString(text);
        clipboard.setContent(pastedContent);
        try {
            input.paste();
        } finally {
            clipboard.setContent(previousContent);
        }
    }

    private static ClipboardContent copyClipboardContent(Clipboard clipboard) {
        ClipboardContent content = new ClipboardContent();
        for (DataFormat format : clipboard.getContentTypes()) {
            Object value = clipboard.getContent(format);
            if (value != null) {
                content.put(format, value);
            }
        }
        return content;
    }

    private static void onFxThread(FxAssertions assertions) throws Exception {
        FutureTask<Void> task = new FutureTask<>(() -> {
            assertions.run();
            return null;
        });
        Platform.runLater(task);
        task.get(20, TimeUnit.SECONDS);
    }

    @FunctionalInterface
    private interface FxAssertions {
        void run() throws Exception;
    }
}
