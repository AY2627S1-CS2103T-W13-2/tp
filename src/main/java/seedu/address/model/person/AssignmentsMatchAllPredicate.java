package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;

import seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType;

/**
 * Tests whether a participant matches every selected assignment, ignoring case and surrounding whitespace.
 */
public class AssignmentsMatchAllPredicate implements Predicate<Person> {
    private final Map<AssignmentType, AssignmentMatchesPredicate> criteria;

    /**
     * Creates an immutable query with two or three distinct, non-blank assignment criteria.
     */
    public AssignmentsMatchAllPredicate(Map<AssignmentType, String> values) {
        requireNonNull(values);
        checkArgument(values.size() >= 2 && values.size() <= 3, "Expected two or three assignment criteria.");
        Map<AssignmentType, AssignmentMatchesPredicate> predicates = new EnumMap<>(AssignmentType.class);
        values.forEach((type, value) -> {
            requireNonNull(type);
            requireNonNull(value);
            checkArgument(!value.isBlank(), "Assignment values must not be blank.");
            predicates.put(type, new AssignmentMatchesPredicate(type, value));
        });
        criteria = Map.copyOf(predicates);
    }

    @Override
    public boolean test(Person person) {
        requireNonNull(person);
        return criteria.values().stream().allMatch(predicate -> predicate.test(person));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || other instanceof AssignmentsMatchAllPredicate otherPredicate
                && criteria.equals(otherPredicate.criteria);
    }

    @Override
    public int hashCode() {
        return criteria.hashCode();
    }

    @Override
    public String toString() {
        return "allAssignments=" + criteria.keySet();
    }
}
