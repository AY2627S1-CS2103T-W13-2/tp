package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;

/**
 * Stages complete file content before atomic replacement, retaining a recovery copy if restoration fails.
 * This helper assumes a single writer and does not guarantee recovery from power loss or filesystem corruption.
 */
class AtomicFileWriter {
    private static final Logger logger = LogsCenter.getLogger(AtomicFileWriter.class);

    private final FileOperations files;

    AtomicFileWriter() {
        this(new FileOperations());
    }

    /** Creates a writer with injectable filesystem operations for deterministic failure tests. */
    AtomicFileWriter(FileOperations files) {
        this.files = requireNonNull(files);
    }

    /**
     * Writes {@code content} without opening the destination for truncation.
     * Unsupported atomic replacement fails instead of falling back to a direct write.
     *
     * @throws IOException if staging, backing up, or replacing the file fails.
     */
    void write(Path destination, String content) throws IOException {
        requireNonNull(destination);
        requireNonNull(content);
        Path target = destination.toAbsolutePath();
        Path staged = null;
        Path backup = null;
        boolean isBackupComplete = false;
        boolean isReplacementAttempted = false;
        String phase = "prepare the save folder";
        try {
            target = files.resolveTarget(target);
            boolean isExistingFile = files.isExistingFile(target);
            files.createDirectories(target.getParent());
            phase = "create a temporary save file";
            staged = files.createTempFile(target.getParent(), ".tmp");
            phase = "write the complete temporary save file";
            files.write(staged, content);
            phase = "back up the previous participant file";
            if (isExistingFile) {
                backup = files.createTempFile(target.getParent(), ".bak");
                files.copy(target, backup);
                isBackupComplete = true;
            }
            phase = "atomically replace the participant file";
            isReplacementAttempted = true;
            files.replace(staged, target);
        } catch (IOException | UnsupportedOperationException exception) {
            throw handleFailure(target, staged, backup, isBackupComplete, isReplacementAttempted, phase, exception);
        }
        // Replacement has committed. A leftover backup must not turn a successful save into a reported failure.
        cleanup(backup, null);
    }

    private IOException handleFailure(Path target, Path staged, Path backup, boolean isBackupComplete,
            boolean isReplacementAttempted, String phase, Exception cause) {
        logger.warning("Participant save failed while attempting to " + phase + " ("
                + cause.getClass().getSimpleName() + ").");
        String message = "Could not " + phase + ". Check folder permissions, disk space, and atomic-move support.";
        IOException failure = new IOException(message, cause);
        boolean isAtomicMoveUnsupported = cause instanceof AtomicMoveNotSupportedException
                || cause instanceof UnsupportedOperationException;
        if (isReplacementAttempted && isBackupComplete && !isAtomicMoveUnsupported) {
            try {
                files.replace(backup, target);
                backup = null;
                logger.info("Restored the previous participant file after a failed replacement.");
            } catch (IOException | UnsupportedOperationException recoveryException) {
                failure = new IOException(message + " Automatic recovery also failed. The previous file is retained at "
                        + backup + ". Preserve this recovery file before retrying.", cause);
                failure.addSuppressed(recoveryException);
                logger.warning("Previous participant data retained for recovery at " + backup);
                cleanup(staged, failure);
                return failure;
            }
        }
        cleanup(staged, failure);
        cleanup(backup, failure);
        return failure;
    }

    private void cleanup(Path path, IOException failure) {
        if (path == null) {
            return;
        }
        try {
            files.deleteIfExists(path);
        } catch (IOException exception) {
            logger.warning("Could not remove temporary participant save file at " + path);
            if (failure != null) {
                failure.addSuppressed(exception);
            }
        }
    }

    /** Filesystem boundary; tests override only the operations whose failures they exercise. */
    static class FileOperations {
        Path resolveTarget(Path target) throws IOException {
            return Files.isSymbolicLink(target) ? target.toRealPath() : target;
        }

        void createDirectories(Path directory) throws IOException {
            Files.createDirectories(directory);
        }

        Path createTempFile(Path directory, String suffix) throws IOException {
            return Files.createTempFile(directory, ".zoomaddress-save-", suffix);
        }

        boolean isExistingFile(Path target) throws IOException {
            try {
                BasicFileAttributes attributes = Files.readAttributes(target, BasicFileAttributes.class);
                if (!attributes.isRegularFile()) {
                    throw new IOException("The participant data destination is not a regular file.");
                }
                return true;
            } catch (NoSuchFileException exception) {
                return false;
            }
        }

        void write(Path staged, String content) throws IOException {
            try (FileChannel channel = FileChannel.open(staged, StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                ByteBuffer buffer = StandardCharsets.UTF_8.encode(content);
                while (buffer.hasRemaining()) {
                    channel.write(buffer);
                }
                channel.force(true);
            }
        }

        void copy(Path source, Path backup) throws IOException {
            Files.copy(source, backup, StandardCopyOption.REPLACE_EXISTING);
        }

        void replace(Path source, Path target) throws IOException {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        }

        void deleteIfExists(Path path) throws IOException {
            Files.deleteIfExists(path);
        }
    }
}
