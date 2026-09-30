package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class RemarkTest {

    @Test
    public void constructor_validRemark_setsValue() {
        Remark remark = new Remark("Likes baseball");

        assertEquals("Likes baseball", remark.value);
    }

    @Test
    public void constructor_emptyRemark_setsEmptyValue() {
        Remark remark = new Remark("");

        assertEquals("", remark.value);
    }
}
