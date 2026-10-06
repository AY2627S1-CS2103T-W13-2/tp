package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a participant's assigned activity. Instances are immutable and non-blank.
 */
public class Activity {

    public static final String MESSAGE_CONSTRAINTS = "Activity names should not be blank.";

    public final String value;

    /**
     * Constructs an {@code Activity} with surrounding whitespace removed.
     */
    public Activity(String activity) {
        requireNonNull(activity);
        checkArgument(isValidActivity(activity), MESSAGE_CONSTRAINTS);
        value = activity.strip();
    }

    /**
     * Returns true if the given activity name is not blank.
     */
    public static boolean isValidActivity(String test) {
        return !test.isBlank();
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof Activity otherActivity && value.equals(otherActivity.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
