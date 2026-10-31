package seedu.address.ui;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

/**
 * A command text field that preserves separators when text containing tabs is pasted.
 */
public class CommandTextField extends TextField {

    private boolean isPasting;

    /**
     * Creates a command text field that normalizes tabs only during paste operations.
     */
    public CommandTextField() {
        setTextFormatter(new TextFormatter<>(change -> {
            if (isPasting) {
                change.setText(change.getText().replace('\t', ' '));
            }
            return change;
        }));
    }

    /**
     * Pastes clipboard text after converting each tab to one ASCII space, while preserving the inherited undo
     * boundary.
     */
    @Override
    public void paste() {
        isPasting = true;
        try {
            super.paste();
        } finally {
            isPasting = false;
        }
    }
}
