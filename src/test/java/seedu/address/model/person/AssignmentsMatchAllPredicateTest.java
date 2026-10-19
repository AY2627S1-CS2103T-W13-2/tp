package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType.ACTIVITY;
import static seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType.GROUP;
import static seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType.ROLE;

import java.util.EnumMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType;
import seedu.address.testutil.PersonBuilder;

public class AssignmentsMatchAllPredicateTest {
    @Test
    public void test_allCriteriaMatch_ignoresCaseAndSurroundingWhitespace() {
        AssignmentsMatchAllPredicate predicate = new AssignmentsMatchAllPredicate(
                Map.of(GROUP, " logistics ", ROLE, "LEAD", ACTIVITY, "campfire"));
        assertTrue(predicate.test(new PersonBuilder().withGroup("Logistics").withRole("Lead")
                .withActivity("Campfire").build()));
        assertFalse(predicate.test(new PersonBuilder().withGroup("Logistics").withRole("Lead")
                .withActivity("Workshop").build()));
    }

    @Test
    public void test_missingSelectedAssignment_doesNotMatch() {
        AssignmentsMatchAllPredicate predicate = new AssignmentsMatchAllPredicate(
                Map.of(GROUP, "Unassigned", ROLE, "Lead", ACTIVITY, "Campfire"));
        assertFalse(predicate.test(new PersonBuilder().withRole("Lead").withActivity("Campfire").build()));
        assertFalse(predicate.test(new PersonBuilder().withGroup("Unassigned").withActivity("Campfire").build()));
        assertFalse(predicate.test(new PersonBuilder().withGroup("Unassigned").withRole("Lead").build()));
        assertTrue(predicate.test(new PersonBuilder().withGroup("Unassigned").withRole("Lead")
                .withActivity("Campfire").build()));
    }

    @Test
    public void test_unspecifiedCategory_doesNotRestrictMatch() {
        AssignmentsMatchAllPredicate predicate = new AssignmentsMatchAllPredicate(
                Map.of(GROUP, "Team A/B", ROLE, "Lead"));
        assertTrue(predicate.test(new PersonBuilder().withGroup("Team A/B").withRole("Lead").build()));
        assertFalse(predicate.test(new PersonBuilder().withGroup("Team A").withRole("Lead").build()));
    }

    @Test
    public void constructor_inputMapChanges_doNotChangeQuery() {
        Map<AssignmentType, String> values = new EnumMap<>(AssignmentType.class);
        values.put(GROUP, "Logistics");
        values.put(ROLE, "Lead");
        AssignmentsMatchAllPredicate predicate = new AssignmentsMatchAllPredicate(values);
        values.clear();
        assertTrue(predicate.test(new PersonBuilder().withGroup("Logistics").withRole("Lead").build()));
        assertFalse(predicate.test(new PersonBuilder().build()));
    }

    @Test
    public void constructor_invalidCriteria_rejects() {
        assertThrows(NullPointerException.class, () -> new AssignmentsMatchAllPredicate(null));
        assertThrows(IllegalArgumentException.class, () -> new AssignmentsMatchAllPredicate(Map.of()));
        assertThrows(IllegalArgumentException.class, () -> new AssignmentsMatchAllPredicate(Map.of(GROUP, "X")));
        assertThrows(IllegalArgumentException.class, () -> {
            new AssignmentsMatchAllPredicate(Map.of(GROUP, " ", ROLE, "Y"));
        });
        Map<AssignmentType, String> values = new EnumMap<>(AssignmentType.class);
        values.put(GROUP, null);
        values.put(ROLE, "Lead");
        assertThrows(NullPointerException.class, () -> new AssignmentsMatchAllPredicate(values));
        AssignmentsMatchAllPredicate predicate = new AssignmentsMatchAllPredicate(Map.of(GROUP, "X", ROLE, "Y"));
        assertThrows(NullPointerException.class, () -> predicate.test(null));
    }

    @Test
    public void equals_normalizedCriteria_comparesByCategoryAndValue() {
        AssignmentsMatchAllPredicate predicate = new AssignmentsMatchAllPredicate(Map.of(GROUP, "X", ROLE, "Y"));
        AssignmentsMatchAllPredicate equivalent = new AssignmentsMatchAllPredicate(Map.of(ROLE, " y ", GROUP, "x"));
        assertEquals(predicate, equivalent);
        assertEquals(predicate.hashCode(), equivalent.hashCode());
        assertNotEquals(predicate, new AssignmentsMatchAllPredicate(Map.of(GROUP, "X", ACTIVITY, "Y")));
        assertNotEquals(predicate, new AssignmentsMatchAllPredicate(Map.of(GROUP, "Z", ROLE, "Y")));
        assertNotEquals(predicate, null);
        assertNotEquals(predicate, "other");
    }
}
