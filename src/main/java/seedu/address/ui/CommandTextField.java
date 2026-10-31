package seedu.address.ui;

import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;

/**
 * A command text field that preserves separators when text containing tabs is pasted.
 */
public class CommandTextField extends TextField {

    @Override
    public void paste() {
        String pastedText = Clipboard.getSystemClipboard().getString();
        if (pastedText == null || pastedText.indexOf('\t') < 0) {
            super.paste();
            return;
        }

        replaceSelection(pastedText.replace('\t', ' '));
    }
}
