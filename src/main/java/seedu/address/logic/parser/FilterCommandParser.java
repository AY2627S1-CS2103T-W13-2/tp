package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ACTIVITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROLE;

import java.util.List;
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

    private static final List<Prefix> PREFIXES = List.of(PREFIX_GROUP, PREFIX_ROLE, PREFIX_ACTIVITY);
    private static final Pattern PREFIX_LIKE_ARGUMENT = Pattern.compile("(?:^|\\s)\\S+/");

    @Override
    public FilterCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args,
                PREFIX_GROUP, PREFIX_ROLE, PREFIX_ACTIVITY);

        int suppliedPrefixCount = PREFIXES.stream()
                .mapToInt(prefix -> arguments.getAllValues(prefix).size())
                .sum();
        if (suppliedPrefixCount == 0) {
            if (PREFIX_LIKE_ARGUMENT.matcher(args).find()) {
                throw new ParseException(MESSAGE_UNSUPPORTED_PREFIX);
            }
            throw new ParseException(MESSAGE_MISSING_PREFIX);
        }

        if (!arguments.getPreamble().isBlank()) {
            if (PREFIX_LIKE_ARGUMENT.matcher(arguments.getPreamble()).find()) {
                throw new ParseException(MESSAGE_UNSUPPORTED_PREFIX);
            }
            throw new ParseException(MESSAGE_UNEXPECTED_ARGUMENTS);
        }

        boolean hasRepeatedPrefix = PREFIXES.stream()
                .anyMatch(prefix -> arguments.getAllValues(prefix).size() > 1);
        if (hasRepeatedPrefix) {
            throw new ParseException(MESSAGE_REPEATED_PREFIX);
        }
        if (suppliedPrefixCount > 1) {
            throw new ParseException(MESSAGE_MULTIPLE_PREFIXES);
        }

        Prefix suppliedPrefix = PREFIXES.stream()
                .filter(prefix -> arguments.getValue(prefix).isPresent())
                .findFirst().orElseThrow();
        String value = arguments.getValue(suppliedPrefix).orElseThrow().strip();
        if (value.isBlank()) {
            throw new ParseException(MESSAGE_BLANK_VALUE);
        }
        AssignmentType type = suppliedPrefix.equals(PREFIX_GROUP) ? AssignmentType.GROUP
                : suppliedPrefix.equals(PREFIX_ROLE) ? AssignmentType.ROLE
                : AssignmentType.ACTIVITY;
        return new FilterCommand(new AssignmentMatchesPredicate(type, value));
    }
}
