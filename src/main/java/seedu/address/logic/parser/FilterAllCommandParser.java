package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.FilterAllCommand;
import seedu.address.logic.parser.QuotedAssignmentValueParser.ParsedValue;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType;
import seedu.address.model.person.AssignmentsMatchAllPredicate;

/**
 * Parses two or three assignment criteria, with quoted values protecting embedded prefix-like text.
 */
public class FilterAllCommandParser implements Parser<FilterAllCommand> {
    public static final String MESSAGE_CRITERIA_COUNT =
            "Specify two or three distinct assignment prefixes: g/, r/, a/.";
    public static final String MESSAGE_EXPECTED_PREFIX = "Expected an assignment prefix: g/, r/, or a/.";
    public static final String MESSAGE_BLANK_VALUE = "Assignment values must not be blank.";
    public static final String MESSAGE_UNFINISHED_QUOTE = QuotedAssignmentValueParser.MESSAGE_UNFINISHED_QUOTE;
    public static final String MESSAGE_INVALID_ESCAPE = QuotedAssignmentValueParser.MESSAGE_INVALID_ESCAPE;
    public static final String MESSAGE_PARTIAL_QUOTE = QuotedAssignmentValueParser.MESSAGE_PARTIAL_QUOTE;
    public static final String MESSAGE_AFTER_QUOTE = QuotedAssignmentValueParser.MESSAGE_AFTER_QUOTE;

    private static final Pattern PREFIX = Pattern.compile("[A-Za-z]+/");

    @Override
    public FilterAllCommand parse(String args) throws ParseException {
        requireNonNull(args);
        Map<AssignmentType, String> criteria = new EnumMap<>(AssignmentType.class);
        int position = skipWhitespace(args, 0);
        while (position < args.length()) {
            Matcher prefix = PREFIX.matcher(args).region(position, args.length());
            if (!prefix.lookingAt()) {
                throw new ParseException(MESSAGE_EXPECTED_PREFIX);
            }
            AssignmentType type = parseType(prefix.group());
            if (criteria.containsKey(type)) {
                throw new ParseException("Assignment prefix must not be repeated: " + prefix.group());
            }
            int valueStart = skipWhitespace(args, prefix.end());
            ParsedValue value = valueStart < args.length() && args.charAt(valueStart) == '"'
                    ? QuotedAssignmentValueParser.parse(args, valueStart) : readPlainValue(args, prefix.end());
            if (value.text().isBlank()) {
                throw new ParseException(MESSAGE_BLANK_VALUE);
            }
            criteria.put(type, value.text());
            position = skipWhitespace(args, value.end());
        }
        if (criteria.size() < 2) {
            throw new ParseException(MESSAGE_CRITERIA_COUNT);
        }
        return new FilterAllCommand(new AssignmentsMatchAllPredicate(criteria));
    }

    private AssignmentType parseType(String prefix) throws ParseException {
        return switch (prefix) {
            case "g/" -> AssignmentType.GROUP;
            case "r/" -> AssignmentType.ROLE;
            case "a/" -> AssignmentType.ACTIVITY;
            default -> throw new ParseException("Unsupported assignment prefix: " + prefix + " Use g/, r/, or a/.");
        };
    }

    private ParsedValue readPlainValue(String args, int start) throws ParseException {
        int end = start;
        while (end < args.length()) {
            if ((end > start && Character.isWhitespace(args.charAt(end - 1)))
                    && PREFIX.matcher(args).region(end, args.length()).lookingAt()) {
                break;
            }
            if (args.charAt(end) == '"') {
                throw new ParseException(MESSAGE_PARTIAL_QUOTE);
            }
            end++;
        }
        return new ParsedValue(args.substring(start, end).strip(), end);
    }

    private int skipWhitespace(String args, int position) {
        while (position < args.length() && Character.isWhitespace(args.charAt(position))) {
            position++;
        }
        return position;
    }
}
