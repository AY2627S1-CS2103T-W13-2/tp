package seedu.address.ui;

import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;

/**
 * A command text field that preserves separators when text containing tabs is pasted.
 */
public class CommandTextField extends TextField {

    /**
     * Pastes clipboard text after converting each tab to one ASCII space, while preserving the inherited undo
     * boundary.
     */
    @Override
    public void paste() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        String pastedText = clipboard.getString();
        if (pastedText == null || pastedText.indexOf('\t') < 0) {
            super.paste();
            return;
        }

        ClipboardContent originalContent = copyClipboardContent(clipboard);
        ClipboardContent normalizedContent = new ClipboardContent();
        normalizedContent.putAll(originalContent);
        normalizedContent.putString(pastedText.replace('\t', ' '));
        clipboard.setContent(normalizedContent);
        try {
            super.paste();
        } finally {
            clipboard.setContent(originalContent);
        }
    }

    private static ClipboardContent copyClipboardContent(Clipboard clipboard) {
        ClipboardContent content = new ClipboardContent();
        for (DataFormat format : clipboard.getContentTypes()) {
            Object value = clipboard.getContent(format);
            if (value != null) {
                content.put(format, value);
            }
        }
        return content;
    }
}
