package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType;

/** Tests whether a participant has no assignment in the selected category. */
public class AssignmentMissingPredicate implements Predicate<Person> {

    private final AssignmentType type;

    /** Creates a predicate for the selected assignment category. */
    public AssignmentMissingPredicate(AssignmentType type) {
        this.type = requireNonNull(type);
    }

    @Override
    public boolean test(Person person) {
        requireNonNull(person);
        return switch (type) {
            case GROUP -> person.getGroup().isEmpty();
            case ROLE -> person.getRole().isEmpty();
            case ACTIVITY -> person.getActivity().isEmpty();
        };
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || other instanceof AssignmentMissingPredicate otherPredicate
                && type == otherPredicate.type;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public String toString() {
        return "missing=" + type;
    }
}
