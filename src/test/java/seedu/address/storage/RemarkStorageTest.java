package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import seedu.address.commons.exceptions.DataLoadingException;
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
    public void readAddressBook_numericRemark_rejectsWithoutRewritingFile() throws Exception {
        assertInvalidRemark("42");
        assertInvalidRemark("-7");
        assertInvalidRemark("42.5");
    }

    @Test
    public void readAddressBook_booleanRemark_rejectsWithoutRewritingFile() throws Exception {
        assertInvalidRemark("true");
        assertInvalidRemark("false");
    }

    @Test
    public void readAddressBook_objectRemark_rejectsWithoutRewritingFile() throws Exception {
        assertInvalidRemark("{}");
    }

    @Test
    public void readAddressBook_arrayRemark_rejectsWithoutRewritingFile() throws Exception {
        assertInvalidRemark("[]");
    }

    @Test
    public void readAddressBook_textResemblingScalar_preservesText() throws Exception {
        for (String text : new String[] {"42", "true"}) {
            String json = """
                    {"name":"Alice Tan","phone":"81234567","email":"alice@example.com",
                     "address":"1 Main Street","tags":[],"remark":"%s"}
                    """.formatted(text);
            Person loaded = JsonUtil.fromJsonString(json, JsonAdaptedPerson.class).toModelType();

            assertEquals(new Remark(text), loaded.getRemark());
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
        JsonNode savedPeople = new ObjectMapper().readTree(Files.readString(path)).path("persons");
        JsonNode emptyRemark = savedPeople.get(0).path("remark");
        assertTrue(emptyRemark.isTextual());
        assertEquals("", emptyRemark.textValue());
        JsonNode detailedRemark = savedPeople.get(1).path("remark");
        assertTrue(detailedRemark.isTextual());
        assertEquals(original.getPersonList().get(1).getRemark().value, detailedRemark.textValue());
    }

    private void assertInvalidRemark(String remarkJson) throws Exception {
        String json = """
                {"persons":[{"name":"Alice Tan","phone":"81234567","email":"alice@example.com",
                 "address":"1 Main Street","tags":[],"remark":%s}]}
                """.formatted(remarkJson);
        Path path = temporaryFolder.resolve("invalid-remark.json");
        Files.writeString(path, json);

        JsonAddressBookStorage storage = new JsonAddressBookStorage(path);
        DataLoadingException exception = assertThrows(DataLoadingException.class, storage::readAddressBook);

        assertTrue(exception.getCause().getMessage().contains("Remark must be a JSON string or null."));
        assertEquals(json, Files.readString(path));
    }
}
