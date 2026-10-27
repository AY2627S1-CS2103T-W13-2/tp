package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * An immutable free-text participant note. Empty text represents no remark.
 * Text is retained exactly, including whitespace and punctuation.
 */
public final class Remark {

    public final String value;

    /**
     * Creates a remark from non-null text, which may be empty.
     *
     * @throws NullPointerException If {@code remark} is null.
     */
    public Remark(String remark) {
        value = requireNonNull(remark);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof Remark otherRemark && value.equals(otherRemark.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
