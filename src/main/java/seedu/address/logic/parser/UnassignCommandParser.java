package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AssignmentType;
import seedu.address.logic.commands.UnassignCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates an {@code UnassignCommand} object.
 */
public class UnassignCommandParser implements Parser<UnassignCommand> {

    private static final Pattern INDEX_AND_REMAINDER = Pattern.compile("(?<index>\\S+)(?:\\s+(?<remainder>.*))?");
    private static final Pattern PREFIX_TOKEN = Pattern.compile("(^|\\s)(?<prefix>\\S+/)");

    /** Parses arguments in the context of an {@code UnassignCommand}. */
    @Override
    public UnassignCommand parse(String args) throws ParseException {
        requireNonNull(args);
        Matcher matcher = INDEX_AND_REMAINDER.matcher(args.trim());
        if (!matcher.matches() || isPrefix(matcher.group("index"))) {
            throw new ParseException("Please provide a participant index before the assignment prefix.");
        }

        Index index = ParserUtil.parseIndex(matcher.group("index"));
        String remainder = matcher.group("remainder");
        if (remainder == null || remainder.trim().isEmpty()) {
            throw new ParseException("Please specify exactly one assignment prefix: g/, r/, or a/.");
        }

        return new UnassignCommand(index, parseAssignmentType(remainder.trim()));
    }

    private AssignmentType parseAssignmentType(String remainder) throws ParseException {
        Matcher prefixMatcher = PREFIX_TOKEN.matcher(remainder);
        if (!prefixMatcher.find()) {
            throw new ParseException("Unexpected extra argument(s): " + remainder);
        }

        String firstPrefix = prefixMatcher.group("prefix");
        AssignmentType firstType = AssignmentType.fromPrefix(firstPrefix);
        if (firstType == null) {
            throw new ParseException("Unsupported assignment prefix: " + firstPrefix + " Use g/, r/, or a/.");
        }

        String firstValue = remainder.substring(prefixMatcher.end()).trim();
        if (prefixMatcher.find()) {
            String nextPrefix = prefixMatcher.group("prefix");
            AssignmentType nextType = AssignmentType.fromPrefix(nextPrefix);
            if (nextType == null) {
                throw new ParseException("Unsupported assignment prefix: " + nextPrefix + " Use g/, r/, or a/.");
            }
            if (nextType == firstType) {
                throw new ParseException("Assignment prefix must not be repeated: " + firstPrefix);
            }
            throw new ParseException("Specify only one assignment prefix: g/, r/, or a/.");
        }
        if (!firstValue.isEmpty()) {
            throw new ParseException("The " + firstPrefix + " prefix must not contain a value when clearing a "
                    + firstType.getDisplayName() + " assignment.");
        }
        return firstType;
    }

    private boolean isPrefix(String argument) {
        return PREFIX_TOKEN.matcher(argument).matches();
    }
}
