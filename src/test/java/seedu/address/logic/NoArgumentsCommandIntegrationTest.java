package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class NoArgumentsCommandIntegrationTest {

    @TempDir
    public Path temporaryFolder;

    private Path participantData;
    private Logic logic;

    @BeforeEach
    public void setUp() {
        participantData = temporaryFolder.resolve("participants.json");
        Model model = new ModelManager();
        StorageManager storage = new StorageManager(
                new JsonAddressBookStorage(participantData),
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_noArgumentCommands_runsCompleteWorkflowAndPersistsClear() throws Exception {
        logic.execute("add n/Alice Tan p/81234567 e/alice@example.com a/1 Main Street");
        logic.execute("find Nobody");
        assertTrue(logic.getFilteredPersonList().isEmpty());

        CommandResult listResult = logic.execute(ListCommand.COMMAND_WORD);
        assertEquals(ListCommand.MESSAGE_SUCCESS, listResult.getFeedbackToUser());
        assertEquals(1, logic.getFilteredPersonList().size());

        CommandResult helpResult = logic.execute(HelpCommand.COMMAND_WORD);
        assertTrue(helpResult.isShowHelp());
        assertFalse(helpResult.isExit());

        CommandResult exitResult = logic.execute(ExitCommand.COMMAND_WORD);
        assertFalse(exitResult.isShowHelp());
        assertTrue(exitResult.isExit());

        assertEquals(ClearCommand.MESSAGE_SUCCESS,
                logic.execute(ClearCommand.COMMAND_WORD).getFeedbackToUser());
        assertTrue(logic.getFilteredPersonList().isEmpty());

        Model reloaded = new ModelManager(
                new JsonAddressBookStorage(participantData).readAddressBook().orElseThrow(), new UserPrefs());
        assertTrue(reloaded.getAddressBook().getPersonList().isEmpty());
    }

    @Test
    public void execute_noArgumentCommandsWithArguments_rejectsInputWithoutChangingData() throws Exception {
        logic.execute("add n/Alice Tan p/81234567 e/alice@example.com a/1 Main Street");
        String savedData = Files.readString(participantData);

        assertInvalidCommand("list participants", ListCommand.MESSAGE_USAGE);
        assertInvalidCommand("clear all", ClearCommand.MESSAGE_USAGE);
        assertInvalidCommand("help commands", HelpCommand.MESSAGE_USAGE);
        assertInvalidCommand("exit now", ExitCommand.MESSAGE_USAGE);

        assertEquals(1, logic.getFilteredPersonList().size());
        assertEquals(savedData, Files.readString(participantData));
        assertEquals(ListCommand.MESSAGE_SUCCESS,
                logic.execute(ListCommand.COMMAND_WORD).getFeedbackToUser());
    }

    private void assertInvalidCommand(String commandText, String usageMessage) {
        assertThrows(ParseException.class,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, usageMessage), () ->
                    logic.execute(commandText));
    }
}
