package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class ContactMatchesQueryPredicateTest {

    private static final Person ALICE = new PersonBuilder()
            .withName("Alice Tan")
            .withPhone("91234567")
            .withEmail("alice.tan@example.com")
            .withAddress("1 Computing Drive")
            .build();

    @Test
    public void test_exactPhone_returnsTrue() {
        ContactMatchesQueryPredicate predicate = new ContactMatchesQueryPredicate("91234567");

        assertTrue(predicate.test(ALICE));
    }

    @Test
    public void test_phoneSubstring_returnsFalse() {
        ContactMatchesQueryPredicate predicate = new ContactMatchesQueryPredicate("1234567");

        assertFalse(predicate.test(ALICE));
    }

    @Test
    public void test_exactEmailIgnoringCase_returnsTrue() {
        ContactMatchesQueryPredicate predicate =
                new ContactMatchesQueryPredicate("ALICE.TAN@EXAMPLE.COM");

        assertTrue(predicate.test(ALICE));
    }

    @Test
    public void test_emailSubstring_returnsFalse() {
        ContactMatchesQueryPredicate predicate =
                new ContactMatchesQueryPredicate("alice.tan@example");

        assertFalse(predicate.test(ALICE));
    }

    @Test
    public void test_nonContactField_returnsFalse() {
        ContactMatchesQueryPredicate namePredicate = new ContactMatchesQueryPredicate("Alice Tan");
        ContactMatchesQueryPredicate addressPredicate =
                new ContactMatchesQueryPredicate("1 Computing Drive");

        assertFalse(namePredicate.test(ALICE));
        assertFalse(addressPredicate.test(ALICE));
    }

    @Test
    public void equals() {
        ContactMatchesQueryPredicate firstPredicate =
                new ContactMatchesQueryPredicate("alice.tan@example.com");
        ContactMatchesQueryPredicate firstPredicateCopy =
                new ContactMatchesQueryPredicate("alice.tan@example.com");
        ContactMatchesQueryPredicate secondPredicate =
                new ContactMatchesQueryPredicate("91234567");

        assertTrue(firstPredicate.equals(firstPredicate));
        assertTrue(firstPredicate.equals(firstPredicateCopy));
        assertFalse(firstPredicate.equals(secondPredicate));
        assertFalse(firstPredicate.equals(1));
        assertFalse(firstPredicate.equals(null));
    }

    @Test
    public void toStringMethod() {
        ContactMatchesQueryPredicate predicate =
                new ContactMatchesQueryPredicate("alice.tan@example.com");

        String expected = ContactMatchesQueryPredicate.class.getCanonicalName()
                + "{query=alice.tan@example.com}";
        assertEquals(expected, predicate.toString());
    }
}
