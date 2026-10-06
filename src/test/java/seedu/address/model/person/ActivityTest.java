package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class ActivityTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Activity(null));
    }

    @Test
    public void constructor_blank_throwsIllegalArgumentException() {
        for (String blank : new String[] {"", " ", "\t\n", "\u2003"}) {
            assertFalse(Activity.isValidActivity(blank));
            assertThrows(IllegalArgumentException.class, Activity.MESSAGE_CONSTRAINTS, () -> new Activity(blank));
        }
    }

    @Test
    public void constructor_validName_preservesWordsAndPunctuation() {
        Activity activity = new Activity("  Arts & Crafts (Session 1)  ");
        assertEquals("Arts & Crafts (Session 1)", activity.value);
        assertEquals(activity.value, activity.toString());
        assertTrue(Activity.isValidActivity("Arts & Crafts (Session 1)"));
    }

    @Test
    public void equals() {
        Activity activity = new Activity("Campfire");
        assertTrue(activity.equals(activity));
        assertTrue(activity.equals(new Activity("Campfire")));
        assertEquals(activity.hashCode(), new Activity("Campfire").hashCode());
        assertFalse(activity.equals(new Activity("Hiking")));
        assertFalse(activity.equals(null));
        assertFalse(activity.equals("Campfire"));
    }
}
