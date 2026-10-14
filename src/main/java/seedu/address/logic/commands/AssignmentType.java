package seedu.address.logic.commands;

import java.util.Arrays;

/**
 * The supported participant assignment types.
 */
public enum AssignmentType {
    GROUP("g/", "group"),
    ROLE("r/", "role"),
    ACTIVITY("a/", "activity");

    private final String prefix;
    private final String displayName;

    AssignmentType(String prefix, String displayName) {
        this.prefix = prefix;
        this.displayName = displayName;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the assignment type represented by {@code prefix}, if supported.
     */
    public static AssignmentType fromPrefix(String prefix) {
        return Arrays.stream(values())
                .filter(type -> type.prefix.equals(prefix))
                .findFirst()
                .orElse(null);
    }
}
