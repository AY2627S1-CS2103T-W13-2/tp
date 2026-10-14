package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.ContactMatchesQueryPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests for {@code FindContactCommand}.
 */
public class FindContactCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_exactPhone_personFound() {
        ContactMatchesQueryPredicate predicate =
                new ContactMatchesQueryPredicate(CARL.getPhone().value);
        FindContactCommand command = new FindContactCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(CARL), model.getFilteredPersonList());
    }

    @Test
    public void execute_emailWithDifferentCase_personFound() {
        ContactMatchesQueryPredicate predicate =
                new ContactMatchesQueryPredicate(ALICE.getEmail().value.toUpperCase());
        FindContactCommand command = new FindContactCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_noMatch_noPersonFound() {
        ContactMatchesQueryPredicate predicate =
                new ContactMatchesQueryPredicate("nobody@example.com");
        FindContactCommand command = new FindContactCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_afterOtherFilter_searchesFullAddressBook() {
        model.updateFilteredPersonList(person -> false);
        ContactMatchesQueryPredicate predicate =
                new ContactMatchesQueryPredicate(ALICE.getEmail().value);
        FindContactCommand command = new FindContactCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_sharedContact_multiplePersonsFound() {
        Person firstPerson = new PersonBuilder()
                .withName("Alex Tan")
                .withPhone("91234567")
                .withEmail("alex@example.com")
                .build();
        Person secondPerson = new PersonBuilder()
                .withName("Jamie Lim")
                .withPhone("91234567")
                .withEmail("jamie@example.com")
                .build();
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(firstPerson);
        addressBook.addPerson(secondPerson);
        model = new ModelManager(addressBook, new UserPrefs());
        expectedModel = new ModelManager(addressBook, new UserPrefs());
        ContactMatchesQueryPredicate predicate =
                new ContactMatchesQueryPredicate("91234567");
        FindContactCommand command = new FindContactCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(firstPerson, secondPerson), model.getFilteredPersonList());
    }

    @Test
    public void equals() {
        ContactMatchesQueryPredicate firstPredicate =
                new ContactMatchesQueryPredicate("alice@example.com");
        ContactMatchesQueryPredicate secondPredicate =
                new ContactMatchesQueryPredicate("91234567");
        FindContactCommand firstCommand = new FindContactCommand(firstPredicate);
        FindContactCommand secondCommand = new FindContactCommand(secondPredicate);

        assertTrue(firstCommand.equals(firstCommand));
        assertTrue(firstCommand.equals(new FindContactCommand(firstPredicate)));
        assertFalse(firstCommand.equals(secondCommand));
        assertFalse(firstCommand.equals(1));
        assertFalse(firstCommand.equals(null));
    }

    @Test
    public void toStringMethod() {
        ContactMatchesQueryPredicate predicate =
                new ContactMatchesQueryPredicate("alice@example.com");
        FindContactCommand command = new FindContactCommand(predicate);

        String expected = FindContactCommand.class.getCanonicalName()
                + "{predicate=" + predicate + "}";
        assertEquals(expected, command.toString());
    }
}
