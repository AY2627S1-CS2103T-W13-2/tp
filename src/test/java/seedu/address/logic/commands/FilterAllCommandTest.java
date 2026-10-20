package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.logic.Messages;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class FilterAllCommandTest {
    @TempDir
    public Path temporaryFolder;

    private final Person zed = new PersonBuilder().withName("Zed Tan").withGroup("Logistics")
            .withRole("Facilitator").withActivity("Campfire").build();
    private final Person ada = new PersonBuilder().withName("Ada Tan").withGroup("Logistics")
            .withRole("Facilitator").withActivity("Workshop").build();
    private final Person bea = new PersonBuilder().withName("Bea Lim").withGroup("Logistics")
            .withRole("Lead").withActivity("Campfire").build();
    private final Person cal = new PersonBuilder().withName("Cal Ong").withGroup("Other")
            .withRole("Facilitator").withActivity("Campfire").build();
    private final Person dan = new PersonBuilder().withName("Dan Raj").withGroup("Logistics").build();
    private final AddressBookParser parser = new AddressBookParser();
    private AddressBook original;
    private Model model;

    @BeforeEach
    public void setUp() {
        original = new AddressBook();
        original.setPersons(List.of(zed, ada, bea, cal, dan));
        model = new ModelManager(original, new UserPrefs());
    }

    @Test
    public void execute_twoCriteria_usesAndAndExcludesMissingAssignments() throws Exception {
        CommandResult result = parser.parseCommand("filter-all g/Logistics r/Facilitator").execute(model);
        assertEquals(List.of(zed, ada), model.getFilteredPersonList());
        assertEquals("2 participant(s) matched all assignments.", result.getFeedbackToUser());
        assertEquals(original, model.getAddressBook());
    }

    @Test
    public void execute_otherPairs_matchOnlyBothCriteria() throws Exception {
        parser.parseCommand("filter-all g/Logistics a/Campfire").execute(model);
        assertEquals(List.of(zed, bea), model.getFilteredPersonList());
        parser.parseCommand("filter-all r/Facilitator a/Campfire").execute(model);
        assertEquals(List.of(zed, cal), model.getFilteredPersonList());
    }

    @Test
    public void execute_threeCriteriaAnyOrderAndCase_matchesAllThree() throws Exception {
        parser.parseCommand("filter-all a/cAMPfire r/facilitator g/LOGISTICS").execute(model);
        assertEquals(List.of(zed), model.getFilteredPersonList());
    }

    @Test
    public void execute_afterEmptyFilterAndSort_replacesFilterAndPreservesIndices() throws Exception {
        parser.parseCommand("sort name").execute(model);
        parser.parseCommand("unassigned a/").execute(model);
        parser.parseCommand("filter g/Nonexistent").execute(model);
        assertEquals(List.of(), model.getFilteredPersonList());
        parser.parseCommand("filter-all r/Facilitator g/Logistics").execute(model);
        assertEquals(List.of(ada, zed), model.getFilteredPersonList());
        assertEquals(String.format(ViewCommand.MESSAGE_VIEW_PARTICIPANT_SUCCESS, Messages.format(zed)),
                new ViewCommand(Index.fromOneBased(2)).execute(model).getFeedbackToUser());
        parser.parseCommand("list").execute(model);
        assertEquals(List.of(ada, bea, cal, dan, zed), model.getFilteredPersonList());
    }

    @Test
    public void execute_quotedPrefixLikeLabelsAndEscapes_matchesLiteralValues() throws Exception {
        Person quoted = new PersonBuilder().withName("Quoted Person").withGroup("Team A/B r/Lead")
                .withRole("Say \"Hi\" \\ Lead").build();
        model.addPerson(quoted);
        parser.parseCommand("filter-all g/\" team a/b r/lead \" r/\"Say \\\"Hi\\\" \\\\ Lead\"").execute(model);
        assertEquals(List.of(quoted), model.getFilteredPersonList());
    }

    @Test
    public void execute_noMatchAndEmptyDirectory_reportsZero() throws Exception {
        String input = "filter-all g/Absent r/Facilitator";
        assertEquals("0 participant(s) matched all assignments.",
                parser.parseCommand(input).execute(model).getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredPersonList());
        model = new ModelManager();
        assertEquals("0 participant(s) matched all assignments.",
                parser.parseCommand(input).execute(model).getFeedbackToUser());
    }

    @Test
    public void execute_everyoneMatches_showsEveryone() throws Exception {
        model = new ModelManager();
        model.addPerson(zed);
        model.addPerson(ada);
        parser.parseCommand("filter-all g/Logistics r/Facilitator").execute(model);
        assertEquals(List.of(zed, ada), model.getFilteredPersonList());
    }

    @Test
    public void execute_repeatedQueriesAndReload_preservesStoredFieldsAndOrder() throws Exception {
        Path dataFile = temporaryFolder.resolve("participants.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(dataFile);
        storage.saveAddressBook(model.getAddressBook());
        String before = Files.readString(dataFile);
        Logic logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
        logic.execute("sort name");
        logic.execute("filter-all g/Logistics r/Facilitator");
        logic.execute("filter-all a/Campfire r/Facilitator g/Logistics");
        assertEquals(before, Files.readString(dataFile));
        assertEquals(original, storage.readAddressBook().orElseThrow());
        Model restarted = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(original.getPersonList(), restarted.getFilteredPersonList());
    }

    @Test
    public void execute_invalidQuery_preservesViewAndData() throws Exception {
        parser.parseCommand("sort name").execute(model);
        parser.parseCommand("filter-all g/Logistics r/Facilitator").execute(model);
        assertThrows(ParseException.class, () -> parser.parseCommand("filter-all g/Logistics r/\"unfinished"));
        assertEquals(List.of(ada, zed), model.getFilteredPersonList());
        assertEquals(original, model.getAddressBook());
    }

    @Test
    public void execute_existingFilter_keepsCaseInsensitiveSlashBehavior() throws Exception {
        Person slash = new PersonBuilder().withName("Slash Person").withGroup("Team A/B").build();
        model.addPerson(slash);
        parser.parseCommand("filter g/team a/b").execute(model);
        assertEquals(List.of(slash), model.getFilteredPersonList());
        parser.parseCommand("unassigned g/").execute(model);
        assertEquals(List.of(), model.getFilteredPersonList());
    }
}
