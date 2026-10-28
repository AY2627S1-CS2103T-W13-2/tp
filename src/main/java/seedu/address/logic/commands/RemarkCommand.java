package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;

import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;

/**
 * Sets or clears the remark of a participant identified by their displayed index.
 */
public class RemarkCommand extends Command {

    public static final String COMMAND_WORD = "remark";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Sets or clears the remark of the participant identified by the index number used in the "
            + "displayed participant list. An empty remark clears the existing remark.\n"
            + "Parameters: INDEX (must be a positive integer) " + PREFIX_REMARK + "REMARK\n"
            + "Examples: " + COMMAND_WORD + " 1 " + PREFIX_REMARK + "Needs wheelchair access\n"
            + "          " + COMMAND_WORD + " 1 " + PREFIX_REMARK;

    public static final String MESSAGE_SET_REMARK_SUCCESS = "Updated remark for %1$s.";
    public static final String MESSAGE_CLEAR_REMARK_SUCCESS = "Cleared remark for %1$s.";

    private final Index index;
    private final Remark remark;

    /**
     * Creates a {@code RemarkCommand} to update the participant at {@code index} with {@code remark}.
     */
    public RemarkCommand(Index index, Remark remark) {
        this.index = requireNonNull(index);
        this.remark = requireNonNull(remark);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> displayedParticipants = model.getFilteredPersonList();

        if (index.getZeroBased() >= displayedParticipants.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person participantToUpdate = displayedParticipants.get(index.getZeroBased());
        Person updatedParticipant = createParticipantWithRemark(participantToUpdate);
        model.setPerson(participantToUpdate, updatedParticipant);

        String successMessage = remark.value.isEmpty()
                ? MESSAGE_CLEAR_REMARK_SUCCESS
                : MESSAGE_SET_REMARK_SUCCESS;
        return new CommandResult(String.format(successMessage, updatedParticipant.getName()));
    }

    private Person createParticipantWithRemark(Person participant) {
        return new Person(participant.getName(), participant.getPhone(), participant.getEmail(),
                participant.getAddress(), participant.getTags(), participant.getRole(), participant.getActivity(),
                participant.getGroup(), remark);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof RemarkCommand otherCommand)) {
            return false;
        }
        return index.equals(otherCommand.index) && remark.equals(otherCommand.remark);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, remark);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("remark", remark)
                .toString();
    }
}
