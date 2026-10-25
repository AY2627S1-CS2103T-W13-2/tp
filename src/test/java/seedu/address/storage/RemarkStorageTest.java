package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.ObjectMapper;

import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.testutil.PersonBuilder;

public class RemarkStorageTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    public void toModelType_storedRemark_preservesTextWhenSavedAgain() throws Exception {
        String json = """
                {"name":"Alice Tan","phone":"81234567","email":"alice@example.com",
                 "address":"1 Main Street","tags":[],"remark":"Needs a vegetarian meal"}
                """;
        JsonAdaptedPerson stored = JsonUtil.fromJsonString(json, JsonAdaptedPerson.class);
        String saved = JsonUtil.toJsonString(new JsonAdaptedPerson(stored.toModelType()));

        assertEquals("Needs a vegetarian meal", new ObjectMapper().readTree(saved).path("remark").asText());
    }

    @Test
    public void readAddressBook_legacyAndNullAndEmptyRemark_loadsWithoutRewritingFile() throws Exception {
        for (String field : new String[] {"", ",\"remark\":null", ",\"remark\":\"\""}) {
            String json = """
                    {"persons":[{"name":"Alice Tan","phone":"81234567","email":"alice@example.com",
                     "address":"1 Main Street","tags":["friend"],"group":"Logistics",
                     "role":"Helper","activity":"Campfire"%s}]}
                    """.formatted(field);
            Path path = temporaryFolder.resolve("legacy.json");
            Files.writeString(path, json);

            Person loaded = new JsonAddressBookStorage(path).readAddressBook().orElseThrow()
                    .getPersonList().getFirst();

            Person expected = new PersonBuilder().withName("Alice Tan").withPhone("81234567")
                    .withEmail("alice@example.com").withAddress("1 Main Street").withTags("friend")
                    .withGroup("Logistics").withRole("Helper").withActivity("Campfire").build();
            assertEquals(expected, loaded);
            assertEquals(new Remark(""), loaded.getRemark());
            assertEquals(json, Files.readString(path));
        }
    }

    @Test
    public void saveAndReadAddressBook_emptyAndFreeTextRemarks_preservesAllFields() throws Exception {
        AddressBook original = new AddressBook();
        original.addPerson(new PersonBuilder().withName("Empty Note").build());
        original.addPerson(new PersonBuilder().withName("Detailed Note").withTags("friend", "helper")
                .withGroup("Logistics").withRole("Helper").withActivity("Campfire")
                .withRemark("  Meal: 素食 🌱\nAsk \"Sam\" about A/B \\ C.  ").build());
        Path path = temporaryFolder.resolve("participants.json");

        new JsonAddressBookStorage(path).saveAddressBook(original);
        AddressBook reloaded = new AddressBook(new JsonAddressBookStorage(path).readAddressBook().orElseThrow());

        assertEquals(original, reloaded);
        assertTrue(Files.readString(path).contains("\"remark\""));
    }
}
