package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class RemarkTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Remark(null));
    }

    @Test
    public void constructor_emptyOrWhitespace_preservesText() {
        for (String text : new String[] {"", " ", "\t\n", "\u2003"}) {
            assertEquals(text, new Remark(text).value);
        }
    }

    @Test
    public void constructor_freeText_preservesUnicodeQuotesAndLineBreaks() {
        String text = "  Meal: 素食 🌱\nAsk \"Sam\" about A/B \\ C.  ";
        assertEquals(text, new Remark(text).value);
        assertEquals(text, new Remark(text).toString());
    }

    @Test
    public void equals_comparesExactText() {
        Remark remark = new Remark("Needs transport");
        assertTrue(remark.equals(remark));
        assertTrue(remark.equals(new Remark("Needs transport")));
        assertEquals(remark.hashCode(), new Remark("Needs transport").hashCode());
        assertFalse(remark.equals(new Remark("needs transport")));
        assertFalse(remark.equals(new Remark("Needs transport ")));
        assertFalse(remark.equals("Needs transport"));
        assertFalse(remark.equals(null));
    }
}
