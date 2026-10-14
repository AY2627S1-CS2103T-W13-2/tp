package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests whether a {@code Person}'s phone number or email address exactly matches a query.
 */
public class ContactMatchesQueryPredicate implements Predicate<Person> {
    private final String query;

    public ContactMatchesQueryPredicate(String query) {
        this.query = requireNonNull(query);
    }

    @Override
    public boolean test(Person person) {
        return person.getPhone().value.equals(query)
                || person.getEmail().value.equalsIgnoreCase(query);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ContactMatchesQueryPredicate otherPredicate)) {
            return false;
        }

        return query.equals(otherPredicate.query);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("query", query).toString();
    }
}
