package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class LogicPerformanceTest {

    private static final int INITIAL_PARTICIPANT_COUNT = 1_000;
    private static final int COMMAND_COUNT = 100;
    private static final int REQUIRED_COMMANDS_WITHIN_TARGET = 95;
    private static final long RESPONSE_TIME_TARGET_MILLIS = 2_000;

    @TempDir
    public Path temporaryFolder;

    @Test
    public void execute_representativeCommandsWithOneThousandParticipants_meetsResponseTimeTarget()
            throws Exception {
        Logic logic = createLogicWithOneThousandParticipants();
        List<String> commands = createRepresentativeCommands();
        List<Long> responseTimesMillis = new ArrayList<>();

        for (String command : commands) {
            long startTime = System.nanoTime();
            logic.execute(command);
            long elapsedNanos = System.nanoTime() - startTime;
            responseTimesMillis.add(TimeUnit.NANOSECONDS.toMillis(elapsedNanos));
        }

        long commandsWithinTarget = responseTimesMillis.stream()
                .filter(time -> time <= RESPONSE_TIME_TARGET_MILLIS)
                .count();
        long slowestCommandMillis = responseTimesMillis.stream().mapToLong(Long::longValue).max().orElseThrow();

        assertEquals(COMMAND_COUNT, commands.size());
        assertTrue(commandsWithinTarget >= REQUIRED_COMMANDS_WITHIN_TARGET, () ->
                commandsWithinTarget + " of " + COMMAND_COUNT + " commands completed within "
                        + RESPONSE_TIME_TARGET_MILLIS + " ms; slowest command took "
                        + slowestCommandMillis + " ms.");
    }

    private Logic createLogicWithOneThousandParticipants() {
        List<Person> participants = new ArrayList<>();
        for (int i = 0; i < INITIAL_PARTICIPANT_COUNT; i++) {
            participants.add(new PersonBuilder()
                    .withName("Participant " + i)
                    .withPhone(String.format("8%07d", i))
                    .withEmail("participant" + i + "@example.com")
                    .withAddress(i + " Benchmark Road")
                    .build());
        }

        AddressBook addressBook = new AddressBook();
        addressBook.setPersons(participants);
        Model model = new ModelManager(addressBook, new UserPrefs());
        StorageManager storage = new StorageManager(
                new JsonAddressBookStorage(temporaryFolder.resolve("participants.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
        return new LogicManager(model, storage);
    }

    private List<String> createRepresentativeCommands() {
        List<String> commands = new ArrayList<>();
        for (int round = 0; round < 10; round++) {
            commands.add("add n/Added Person " + round + " p/9" + String.format("%07d", round)
                    + " e/added" + round + "@example.com a/Added Address " + round);
            commands.add("list");
            commands.add("find Participant");
            commands.add("edit 1 a/Updated Address " + round);
            commands.add("assign-role 1 r/Coordinator " + round);
            commands.add("assign-group 1 g/Group " + round);
            commands.add("assign-activity 1 a/Activity " + round);
            commands.add("view 1");
            commands.add("find Participant");
            commands.add("delete 1");
        }
        return commands;
    }
}
