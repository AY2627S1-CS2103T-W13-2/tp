package seedu.address.model.group;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class GroupTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Group(null));
    }

    @Test
    public void constructor_invalidGroup_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Group(""));
    }

    @Test
    public void isValidGroupName() {
        assertThrows(NullPointerException.class, () -> Group.isValidGroupName(null));

        assertFalse(Group.isValidGroupName(""));
        assertFalse(Group.isValidGroupName(" "));

        assertTrue(Group.isValidGroupName("Logistics"));
        assertTrue(Group.isValidGroupName("Orientation Group 1"));
        assertTrue(Group.isValidGroupName("1"));
    }

    @Test
    public void equals() {
        Group group = new Group("Logistics");

        assertTrue(group.equals(new Group("Logistics")));
        assertTrue(group.equals(group));
        assertFalse(group.equals(null));
        assertFalse(group.equals(5.0f));
        assertFalse(group.equals(new Group("Programmes")));
    }
}
