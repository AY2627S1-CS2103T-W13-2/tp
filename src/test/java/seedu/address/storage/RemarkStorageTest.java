package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import seedu.address.commons.util.JsonUtil;

public class RemarkStorageTest {

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
}
