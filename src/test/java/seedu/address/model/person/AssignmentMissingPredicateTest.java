package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType.ACTIVITY;
import static seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType.GROUP;
import static seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType.ROLE;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class AssignmentMissingPredicateTest {

    @Test
    public void test_groupPresence_checksOnlySelectedOptionalField() {
        Person person = new PersonBuilder().withGroup("Team A/B").build();
        assertFalse(new AssignmentMissingPredicate(GROUP).test(person));
        assertTrue(new AssignmentMissingPredicate(ROLE).test(person));
        assertTrue(new AssignmentMissingPredicate(ACTIVITY).test(person));
    }

    @Test
    public void test_rolePresence_checksOnlySelectedOptionalField() {
        Person person = new PersonBuilder().withRole("Unassigned").build();
        assertTrue(new AssignmentMissingPredicate(GROUP).test(person));
        assertFalse(new AssignmentMissingPredicate(ROLE).test(person));
        assertTrue(new AssignmentMissingPredicate(ACTIVITY).test(person));
    }

    @Test
    public void test_activityPresence_checksOnlySelectedOptionalField() {
        Person person = new PersonBuilder().withActivity("campfire").build();
        assertTrue(new AssignmentMissingPredicate(GROUP).test(person));
        assertTrue(new AssignmentMissingPredicate(ROLE).test(person));
        assertFalse(new AssignmentMissingPredicate(ACTIVITY).test(person));
    }

    @Test
    public void test_noAssignments_matchesEveryCategory() {
        Person person = new PersonBuilder().build();
        assertTrue(new AssignmentMissingPredicate(GROUP).test(person));
        assertTrue(new AssignmentMissingPredicate(ROLE).test(person));
        assertTrue(new AssignmentMissingPredicate(ACTIVITY).test(person));
    }

    @Test
    public void test_nullPersonOrCategory_rejectsInvalidInternalInput() {
        assertThrows(NullPointerException.class, () -> new AssignmentMissingPredicate(null));
        assertThrows(NullPointerException.class, () -> new AssignmentMissingPredicate(GROUP).test(null));
    }
}
