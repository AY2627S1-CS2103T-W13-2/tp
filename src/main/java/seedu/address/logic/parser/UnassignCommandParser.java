package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
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
    private static final Pattern PREFIX_TOKEN = Pattern.compile("(?<prefix>[A-Za-z]+/)(?<value>.*)");

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
        String[] arguments = remainder.split("\\s+");
        Matcher firstArgument = PREFIX_TOKEN.matcher(arguments[0]);
        if (!firstArgument.matches()) {
            throw new ParseException("Unexpected extra argument(s): " + remainder);
        }

        String firstPrefix = firstArgument.group("prefix");
        AssignmentType firstType = AssignmentType.fromPrefix(firstPrefix);
        if (firstType == null) {
            throw new ParseException("Unsupported assignment prefix: " + firstPrefix + " Use g/, r/, or a/.");
        }

        for (int i = 1; i < arguments.length; i++) {
            Matcher nextArgument = PREFIX_TOKEN.matcher(arguments[i]);
            if (!nextArgument.matches()) {
                continue;
            }
            String nextPrefix = nextArgument.group("prefix");
            AssignmentType nextType = AssignmentType.fromPrefix(nextPrefix);
            if (nextType == null) {
                throw new ParseException("Unsupported assignment prefix: " + nextPrefix + " Use g/, r/, or a/.");
            }
            if (nextType == firstType) {
                throw new ParseException("Assignment prefix must not be repeated: " + firstPrefix);
            }
            throw new ParseException("Specify only one assignment prefix: g/, r/, or a/.");
        }
        if (!firstArgument.group("value").isEmpty()) {
            throw new ParseException("The " + firstPrefix + " prefix must not contain a value when clearing the "
                    + firstType.getDisplayName() + " assignment.");
        }
        if (arguments.length > 1) {
            throw new ParseException("Unexpected extra argument(s): "
                    + String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length)));
        }
        return firstType;
    }

    private boolean isPrefix(String argument) {
        return PREFIX_TOKEN.matcher(argument).matches();
    }
}
