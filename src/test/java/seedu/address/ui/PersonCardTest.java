package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {

    @BeforeAll
    public static void initializeToolkit() throws Exception {
        assumeTrue(!System.getProperty("os.name").startsWith("Linux") || System.getenv("DISPLAY") != null,
                "JavaFX component tests require a display (use Xvfb on Linux)");
        CountDownLatch started = new CountDownLatch(1);
        try {
            Platform.startup(started::countDown);
        } catch (IllegalStateException alreadyStarted) {
            Platform.runLater(started::countDown);
        }
        assertTrue(started.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void constructor_emptyRemark_hidesLabelAndRemovesLayoutSpace() throws Exception {
        onFxThread(() -> {
            Label remark = remarkLabel(new PersonBuilder().withRemark("").build());
            assertFalse(remark.isVisible());
            assertFalse(remark.isManaged());
        });
    }

    @Test
    public void constructor_presentRemark_preservesTextAndParticipant() throws Exception {
        onFxThread(() -> {
            for (String text : new String[]{"Needs wheelchair access", "None", "Use \"quiet\" room\nBring form"}) {
                Person person = new PersonBuilder().withRemark(text).build();
                Label remark = remarkLabel(person);
                assertEquals("Remark: " + text, remark.getText());
                assertTrue(remark.isVisible());
                assertTrue(remark.isManaged());
                assertEquals(text, person.getRemark().value);
            }
        });
    }

    @Test
    public void constructor_longRemark_wrapsWithinNarrowCard() throws Exception {
        onFxThread(() -> {
            Person person = new PersonBuilder().withRemark("Long participant note ".repeat(30)).build();
            Region card = new PersonCard(person, 1).getRoot();
            new Scene(card, 450, 900);
            card.resize(450, 900);
            card.applyCss();
            card.layout();
            Label remark = (Label) card.lookup("#remark");
            assertNotNull(remark);
            assertTrue(remark.isWrapText());
            assertTrue(remark.getWidth() <= 430);
            assertTrue(remark.getHeight() > 40);
            assertEquals("Remark: " + person.getRemark().value, remark.getText());
        });
    }

    private static Label remarkLabel(Person person) {
        Label label = (Label) new PersonCard(person, 1).getRoot().lookup("#remark");
        assertNotNull(label);
        return label;
    }

    private static void onFxThread(Runnable assertions) throws Exception {
        FutureTask<Void> task = new FutureTask<>(assertions, null);
        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }
}
