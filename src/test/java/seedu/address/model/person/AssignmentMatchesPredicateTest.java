package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType.ACTIVITY;
import static seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType.GROUP;
import static seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType.ROLE;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class AssignmentMatchesPredicateTest {

    @Test
    public void test_exactNormalizedGroup_matches() {
        Person person = new PersonBuilder().withGroup("Logistics Team").build();
        assertTrue(new AssignmentMatchesPredicate(GROUP, "  logistics team  ").test(person));
        assertFalse(new AssignmentMatchesPredicate(GROUP, "Logistics").test(person));
    }

    @Test
    public void test_exactNormalizedRole_matches() {
        Person person = new PersonBuilder().withRole("Facilitator").build();
        assertTrue(new AssignmentMatchesPredicate(ROLE, "FACILITATOR").test(person));
        assertFalse(new AssignmentMatchesPredicate(ROLE, "Facilitate").test(person));
    }

    @Test
    public void test_exactNormalizedActivity_matches() {
        Person person = new PersonBuilder().withActivity("Campfire").build();
        assertTrue(new AssignmentMatchesPredicate(ACTIVITY, "campfire").test(person));
        assertFalse(new AssignmentMatchesPredicate(ACTIVITY, "Camp").test(person));
    }

    @Test
    public void test_missingAssignment_doesNotMatch() {
        Person unassignedPerson = new PersonBuilder().build();
        assertFalse(new AssignmentMatchesPredicate(GROUP, "Logistics").test(unassignedPerson));
        assertFalse(new AssignmentMatchesPredicate(ROLE, "Facilitator").test(unassignedPerson));
        assertFalse(new AssignmentMatchesPredicate(ACTIVITY, "Campfire").test(unassignedPerson));
    }
}
