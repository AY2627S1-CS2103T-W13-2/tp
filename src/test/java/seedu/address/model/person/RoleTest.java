package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class RoleTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Role(null));
    }

    @Test
    public void constructor_blankRole_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Role(""));
        assertThrows(IllegalArgumentException.class, () -> new Role("   "));
    }

    @Test
    public void isValidRole() {
        assertFalse(Role.isValidRole(""));
        assertFalse(Role.isValidRole("   "));
        assertTrue(Role.isValidRole("Facilitator"));
        assertTrue(Role.isValidRole("Logistics Lead"));
    }
}
