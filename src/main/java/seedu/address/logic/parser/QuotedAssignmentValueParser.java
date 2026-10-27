package seedu.address.logic.parser;

import seedu.address.logic.parser.exceptions.ParseException;

/** Decodes a complete quoted assignment value shared by the assignment filter parsers. */
final class QuotedAssignmentValueParser {
    static final String MESSAGE_UNFINISHED_QUOTE = "Unfinished quoted value. Close the double quote.";
    static final String MESSAGE_INVALID_ESCAPE = "Unsupported escape. Inside quotes, use \\\" or \\\\.";
    static final String MESSAGE_PARTIAL_QUOTE = "Quote the entire assignment value, not just part of it.";
    static final String MESSAGE_AFTER_QUOTE = "Expected whitespace or end of input after a quoted value.";

    private QuotedAssignmentValueParser() {
    }

    /**
     * Reads a value beginning at its opening double quote.
     *
     * @param args Complete command arguments.
     * @param start Index of the opening double quote.
     * @return The decoded value and the position immediately after its closing quote.
     * @throws ParseException If the quote is unfinished, an escape is invalid, or text follows the closing quote
     *         without a separator.
     */
    static ParsedValue parse(String args, int start) throws ParseException {
        StringBuilder value = new StringBuilder();
        int position = start + 1;
        while (position < args.length()) {
            char next = args.charAt(position++);
            if (next == '"') {
                if (position < args.length() && !Character.isWhitespace(args.charAt(position))) {
                    throw new ParseException(MESSAGE_AFTER_QUOTE);
                }
                return new ParsedValue(value.toString().strip(), position);
            }
            if (next == '\\') {
                if (position == args.length()) {
                    throw new ParseException(MESSAGE_UNFINISHED_QUOTE);
                }
                next = args.charAt(position++);
                if (next != '"' && next != '\\') {
                    throw new ParseException(MESSAGE_INVALID_ESCAPE);
                }
            }
            value.append(next);
        }
        throw new ParseException(MESSAGE_UNFINISHED_QUOTE);
    }

    /** A decoded value and the position immediately after it in the input. */
    record ParsedValue(String text, int end) {
    }
}
