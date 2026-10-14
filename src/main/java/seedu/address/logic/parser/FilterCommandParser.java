package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ACTIVITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROLE;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.AssignmentMatchesPredicate;
import seedu.address.model.person.AssignmentMatchesPredicate.AssignmentType;

/** Parses arguments for {@link FilterCommand}. */
public class FilterCommandParser implements Parser<FilterCommand> {

    public static final String MESSAGE_MISSING_PREFIX = "Specify one assignment prefix: g/, r/, or a/.";
    public static final String MESSAGE_BLANK_VALUE = "The assignment value must not be blank.";
    public static final String MESSAGE_MULTIPLE_PREFIXES = "Specify only one assignment prefix.";
    public static final String MESSAGE_REPEATED_PREFIX = "Do not repeat an assignment prefix.";
    public static final String MESSAGE_UNSUPPORTED_PREFIX = "Unsupported prefix. Use only g/, r/, or a/.";
    public static final String MESSAGE_UNEXPECTED_ARGUMENTS =
            "Unexpected extra arguments before the assignment prefix.";

    private static final Pattern SUPPORTED_PREFIX = Pattern.compile("(?:^|\\s)(g/|r/|a/)");
    private static final Pattern PREFIX_LIKE_ARGUMENT = Pattern.compile("(?:^|\\s)\\S+/");

    @Override
    public FilterCommand parse(String args) throws ParseException {
        requireNonNull(args);
        Matcher matcher = SUPPORTED_PREFIX.matcher(args);
        if (!matcher.find()) {
            if (PREFIX_LIKE_ARGUMENT.matcher(args).find()) {
                throw new ParseException(MESSAGE_UNSUPPORTED_PREFIX);
            }
            throw new ParseException(MESSAGE_MISSING_PREFIX);
        }

        String preamble = args.substring(0, matcher.start()).strip();
        if (!preamble.isBlank()) {
            if (PREFIX_LIKE_ARGUMENT.matcher(preamble).find()) {
                throw new ParseException(MESSAGE_UNSUPPORTED_PREFIX);
            }
            throw new ParseException(MESSAGE_UNEXPECTED_ARGUMENTS);
        }

        Prefix suppliedPrefix = toPrefix(matcher.group(1));
        int valueStart = matcher.end();
        String value = args.substring(valueStart).strip();
        if (value.isBlank()) {
            throw new ParseException(MESSAGE_BLANK_VALUE);
        }

        List<Prefix> laterPrefixes = new ArrayList<>();
        matcher.region(valueStart, args.length());
        while (matcher.find()) {
            laterPrefixes.add(toPrefix(matcher.group(1)));
        }

        String noMatchErrorMessage = null;
        if (laterPrefixes.contains(suppliedPrefix)) {
            noMatchErrorMessage = MESSAGE_REPEATED_PREFIX;
        } else if (!laterPrefixes.isEmpty()) {
            noMatchErrorMessage = MESSAGE_MULTIPLE_PREFIXES;
        }

        AssignmentType type = suppliedPrefix.equals(PREFIX_GROUP) ? AssignmentType.GROUP
                : suppliedPrefix.equals(PREFIX_ROLE) ? AssignmentType.ROLE
                : AssignmentType.ACTIVITY;
        return new FilterCommand(new AssignmentMatchesPredicate(type, value), noMatchErrorMessage);
    }

    private Prefix toPrefix(String prefix) {
        return prefix.equals(PREFIX_GROUP.toString()) ? PREFIX_GROUP
                : prefix.equals(PREFIX_ROLE.toString()) ? PREFIX_ROLE
                : PREFIX_ACTIVITY;
    }
}
