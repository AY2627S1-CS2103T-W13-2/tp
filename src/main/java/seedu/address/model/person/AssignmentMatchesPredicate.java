package seedu.address.model.person;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Tests whether a person's selected assignment exactly matches a value,
 * ignoring case and surrounding whitespace.
 */
public class AssignmentMatchesPredicate implements Predicate<Person> {

    /** Assignment fields supported by the filter command. */
    public enum AssignmentType {
        GROUP, ROLE, ACTIVITY
    }

    private final AssignmentType type;
    private final String normalizedValue;

    /** Creates a predicate for the specified assignment type and value. */
    public AssignmentMatchesPredicate(AssignmentType type, String value) {
        this.type = Objects.requireNonNull(type);
        this.normalizedValue = normalize(Objects.requireNonNull(value));
    }

    @Override
    public boolean test(Person person) {
        Objects.requireNonNull(person);
        return switch (type) {
            case GROUP -> person.getGroup().map(Object::toString).map(AssignmentMatchesPredicate::normalize)
                    .filter(normalizedValue::equals).isPresent();
            case ROLE -> person.getRole().map(Object::toString).map(AssignmentMatchesPredicate::normalize)
                    .filter(normalizedValue::equals).isPresent();
            case ACTIVITY -> person.getActivity().map(Object::toString).map(AssignmentMatchesPredicate::normalize)
                    .filter(normalizedValue::equals).isPresent();
        };
    }

    private static String normalize(String value) {
        return value.strip().toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || other instanceof AssignmentMatchesPredicate otherPredicate
                && type == otherPredicate.type
                && normalizedValue.equals(otherPredicate.normalizedValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, normalizedValue);
    }

    @Override
    public String toString() {
        return type + "=" + normalizedValue;
    }
}
