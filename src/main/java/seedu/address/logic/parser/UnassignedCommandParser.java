package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.UnassignedCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType;

/** Parses a single empty assignment prefix for an unassigned-participant query. */
public class UnassignedCommandParser implements Parser<UnassignedCommand> {

    public static final String MESSAGE_MISSING_PREFIX = "Please specify exactly one empty assignment prefix: "
            + "g/, r/, or a/.";
    public static final String MESSAGE_MULTIPLE_PREFIXES = "Specify only one assignment prefix: g/, r/, or a/.";
    public static final String MESSAGE_EXTRA_ARGUMENTS = "Unexpected extra argument(s). Use unassigned g/, "
            + "unassigned r/, or unassigned a/.";

    private static final Pattern PREFIX_TOKEN = Pattern.compile("(?<prefix>[A-Za-z]+/)(?<value>.*)");

    @Override
    public UnassignedCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmed = args.trim();
        if (trimmed.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_PREFIX);
        }

        String[] tokens = trimmed.split("\\s+");
        Matcher first = PREFIX_TOKEN.matcher(tokens[0]);
        if (!first.matches()) {
            throw new ParseException(MESSAGE_EXTRA_ARGUMENTS);
        }
        String prefix = first.group("prefix");
        AssignmentType type = parseType(prefix);
        if (!first.group("value").isEmpty()) {
            throw new ParseException("The " + prefix + " prefix must not contain a value for an unassigned query.");
        }
        rejectAdditionalPrefixes(tokens, type);
        if (tokens.length > 1) {
            throw new ParseException(MESSAGE_EXTRA_ARGUMENTS);
        }
        return new UnassignedCommand(type);
    }

    private void rejectAdditionalPrefixes(String[] tokens, AssignmentType firstType) throws ParseException {
        for (int i = 1; i < tokens.length; i++) {
            Matcher next = PREFIX_TOKEN.matcher(tokens[i]);
            if (!next.matches()) {
                continue;
            }
            String prefix = next.group("prefix");
            if (parseType(prefix) == firstType) {
                throw new ParseException("Assignment prefix must not be repeated: " + prefix);
            }
            throw new ParseException(MESSAGE_MULTIPLE_PREFIXES);
        }
    }

    private AssignmentType parseType(String prefix) throws ParseException {
        return switch (prefix) {
            case "g/" -> AssignmentType.GROUP;
            case "r/" -> AssignmentType.ROLE;
            case "a/" -> AssignmentType.ACTIVITY;
            default -> throw new ParseException("Unsupported assignment prefix: " + prefix + " Use g/, r/, or a/.");
        };
    }
}
