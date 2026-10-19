package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.LogsCenter;

public class AtomicFileWriterTest {
    private static final byte[] ORIGINAL =
            "previous participant data\n".getBytes(java.nio.charset.StandardCharsets.UTF_8);
    private static final String UPDATED = "complete new participant data — 新";

    @TempDir
    public Path testFolder;

    @Test
    public void write_existingFile_replacesCompleteContentAndCleansTemporaryFiles() throws Exception {
        Path target = existingFile();
        new AtomicFileWriter().write(target, UPDATED);
        assertEquals(UPDATED, Files.readString(target));
        assertEquals(List.of(target), filesIn(testFolder));
    }

    @Test
    public void write_firstSave_createsParentsAndCompleteFile() throws Exception {
        Path target = testFolder.resolve("new/directory/participants.json");
        new AtomicFileWriter().write(target, UPDATED);
        assertEquals(UPDATED, Files.readString(target));
        assertEquals(List.of(target), filesIn(target.getParent()));
    }

    @Test
    public void write_preparationFailures_preserveOriginal() throws Exception {
        Path target = existingFile();
        for (Failure failure : List.of(Failure.PREPARE, Failure.CREATE_STAGE, Failure.CREATE_BACKUP, Failure.COPY)) {
            IOException exception = assertThrows(IOException.class, () -> writer(failure).write(target, UPDATED));
            assertTrue(exception.getMessage().startsWith("Could not "));
            assertArrayEquals(ORIGINAL, Files.readAllBytes(target));
            assertEquals(List.of(target), filesIn(testFolder));
        }
    }

    @Test
    public void write_partialStageFailure_preservesOriginalAndCleansPartialFile() throws Exception {
        Path target = existingFile();
        IOException exception = assertThrows(IOException.class, () -> writer(Failure.WRITE).write(target, UPDATED));
        assertTrue(exception.getMessage().contains("write the complete temporary save file"));
        assertArrayEquals(ORIGINAL, Files.readAllBytes(target));
        assertEquals(List.of(target), filesIn(testFolder));
    }

    @Test
    public void write_failedFirstSave_leavesDestinationAbsent() throws Exception {
        Path target = testFolder.resolve("participants.json");
        for (Failure failure : List.of(Failure.WRITE, Failure.REPLACE, Failure.UNSUPPORTED)) {
            assertThrows(IOException.class, () -> writer(failure).write(target, UPDATED));
            assertFalse(Files.exists(target));
            assertTrue(filesIn(testFolder).isEmpty());
        }
    }

    @Test
    public void write_replacementFailure_restoresOriginalAndAllowsRetry() throws Exception {
        Path target = existingFile();
        FaultyFileOperations operations = new FaultyFileOperations(Failure.REPLACE);
        assertThrows(IOException.class, () -> new AtomicFileWriter(operations).write(target, UPDATED));
        assertEquals(2, operations.replacementAttempts);
        assertArrayEquals(ORIGINAL, Files.readAllBytes(target));
        assertEquals(List.of(target), filesIn(testFolder));

        new AtomicFileWriter().write(target, UPDATED);
        assertEquals(UPDATED, Files.readString(target));
        assertEquals(List.of(target), filesIn(testFolder));
    }

    @Test
    public void write_replacementReportsFailureAfterMove_restoresPreviousBytes() throws Exception {
        Path target = existingFile();
        assertThrows(IOException.class, () -> writer(Failure.AFTER_MOVE).write(target, UPDATED));
        assertArrayEquals(ORIGINAL, Files.readAllBytes(target));
        assertEquals(List.of(target), filesIn(testFolder));
    }

    @Test
    public void write_unsupportedAtomicReplacement_doesNotAttemptFallback() throws Exception {
        Path target = existingFile();
        FaultyFileOperations operations = new FaultyFileOperations(Failure.UNSUPPORTED);
        IOException exception = assertThrows(IOException.class, () ->
                new AtomicFileWriter(operations).write(target, UPDATED));
        assertTrue(exception.getCause() instanceof AtomicMoveNotSupportedException);
        assertTrue(exception.getMessage().contains("atomic-move support"));
        assertEquals(1, operations.replacementAttempts);
        assertArrayEquals(ORIGINAL, Files.readAllBytes(target));
        assertEquals(List.of(target), filesIn(testFolder));
    }

    @Test
    public void write_recoveryFailure_retainsOriginalBackupAndReportsItsLocation() throws Exception {
        Path target = existingFile();
        IOException exception = assertThrows(IOException.class, () ->
                writer(Failure.AFTER_MOVE, Failure.RECOVERY).write(target, UPDATED));
        Path backup = filesIn(testFolder).stream().filter(path -> path.toString().endsWith(".bak"))
                .findFirst().orElseThrow();
        assertArrayEquals(ORIGINAL, Files.readAllBytes(backup));
        assertEquals(UPDATED, Files.readString(target));
        assertTrue(exception.getMessage().contains(backup.toString()));
        assertTrue(exception.getMessage().contains("Automatic recovery also failed"));
        assertEquals(1, exception.getSuppressed().length);
        assertEquals(2, filesIn(testFolder).size());

        new AtomicFileWriter().write(target, "retried save");
        assertArrayEquals(ORIGINAL, Files.readAllBytes(backup));
        assertEquals("retried save", Files.readString(target));
    }

    @Test
    public void write_partialStageCleanupFailure_preservesOriginalAndPrimaryFailure() throws Exception {
        Path target = existingFile();
        IOException exception = assertThrows(IOException.class, () ->
                writer(Failure.WRITE, Failure.CLEANUP).write(target, UPDATED));
        assertTrue(exception.getMessage().contains("write the complete temporary save file"));
        assertEquals(1, exception.getSuppressed().length);
        assertArrayEquals(ORIGINAL, Files.readAllBytes(target));
        assertEquals(2, filesIn(testFolder).size());
    }

    @Test
    public void write_backupCleanupFailure_doesNotReportCommittedSaveAsFailed() throws Exception {
        Path target = existingFile();
        writer(Failure.CLEANUP).write(target, UPDATED);
        assertEquals(UPDATED, Files.readString(target));
        Path backup = filesIn(testFolder).stream().filter(path -> path.toString().endsWith(".bak"))
                .findFirst().orElseThrow();
        assertArrayEquals(ORIGINAL, Files.readAllBytes(backup));
    }

    @Test
    public void write_directoryDestination_rejectsReplacement() throws Exception {
        Path directory = Files.createDirectory(testFolder.resolve("participants.json"));
        assertThrows(IOException.class, () -> new AtomicFileWriter().write(directory, UPDATED));
        assertTrue(Files.isDirectory(directory));
        assertEquals(List.of(directory), filesIn(testFolder));
    }

    @Test
    public void write_rootDestination_throwsIoException() {
        assertThrows(IOException.class, () -> new AtomicFileWriter().write(testFolder.getRoot(), UPDATED));
    }

    @Test
    public void write_failedSave_logsPhaseWithoutParticipantContent() throws Exception {
        Path target = existingFile();
        List<LogRecord> records = new ArrayList<>();
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                records.add(record);
            }

            @Override
            public void flush() {}

            @Override
            public void close() {}
        };
        AtomicFileWriter.FileOperations operations = new AtomicFileWriter.FileOperations() {
            @Override
            void write(Path staged, String content) throws IOException {
                throw new IOException("Private participant details");
            }
        };
        Logger logger = LogsCenter.getLogger(AtomicFileWriter.class);
        logger.addHandler(handler);
        try {
            assertThrows(IOException.class, () -> new AtomicFileWriter(operations).write(target, UPDATED));
        } finally {
            logger.removeHandler(handler);
        }
        assertTrue(records.stream().anyMatch(record -> record.getMessage().contains("temporary save file")));
        assertTrue(records.stream().noneMatch(record -> record.getMessage().contains("Private participant details")
                || record.getMessage().contains(UPDATED) || record.getThrown() != null));
    }

    @Test
    public void write_nullArguments_throwsNullPointerException() {
        AtomicFileWriter writer = new AtomicFileWriter();
        assertThrows(NullPointerException.class, () -> writer.write(null, UPDATED));
        assertThrows(NullPointerException.class, () -> writer.write(testFolder.resolve("data.json"), null));
        assertThrows(NullPointerException.class, () -> new AtomicFileWriter(null));
    }

    private Path existingFile() throws IOException {
        Path target = testFolder.resolve("participants.json");
        Files.write(target, ORIGINAL);
        return target;
    }

    private List<Path> filesIn(Path directory) throws IOException {
        try (var paths = Files.list(directory)) {
            return paths.sorted().toList();
        }
    }

    private AtomicFileWriter writer(Failure... failures) {
        return new AtomicFileWriter(new FaultyFileOperations(failures));
    }

    private enum Failure {
        PREPARE, CREATE_STAGE, WRITE, CREATE_BACKUP, COPY, REPLACE, UNSUPPORTED, AFTER_MOVE, RECOVERY, CLEANUP
    }

    private static class FaultyFileOperations extends AtomicFileWriter.FileOperations {
        private final EnumSet<Failure> failures = EnumSet.noneOf(Failure.class);
        private int replacementAttempts;

        FaultyFileOperations(Failure... failures) {
            this.failures.addAll(List.of(failures));
        }

        @Override
        void createDirectories(Path directory) throws IOException {
            failIf(Failure.PREPARE);
            super.createDirectories(directory);
        }

        @Override
        Path createTempFile(Path directory, String suffix) throws IOException {
            failIf(suffix.equals(".tmp") ? Failure.CREATE_STAGE : Failure.CREATE_BACKUP);
            return super.createTempFile(directory, suffix);
        }

        @Override
        void write(Path staged, String content) throws IOException {
            if (failures.contains(Failure.WRITE)) {
                Files.writeString(staged, "partial data");
                throw new IOException("Injected incomplete write");
            }
            super.write(staged, content);
        }

        @Override
        void copy(Path source, Path backup) throws IOException {
            if (failures.contains(Failure.COPY)) {
                Files.writeString(backup, "partial backup");
            }
            failIf(Failure.COPY);
            super.copy(source, backup);
        }

        @Override
        void replace(Path source, Path target) throws IOException {
            replacementAttempts++;
            if (replacementAttempts == 1) {
                if (failures.contains(Failure.UNSUPPORTED)) {
                    throw new AtomicMoveNotSupportedException(source.toString(), target.toString(), "Injected failure");
                }
                failIf(Failure.REPLACE);
            } else {
                failIf(Failure.RECOVERY);
            }
            super.replace(source, target);
            if (replacementAttempts == 1) {
                failIf(Failure.AFTER_MOVE);
            }
        }

        @Override
        void deleteIfExists(Path path) throws IOException {
            failIf(Failure.CLEANUP);
            super.deleteIfExists(path);
        }

        private void failIf(Failure failure) throws IOException {
            if (failures.contains(failure)) {
                throw new IOException("Injected " + failure + " failure");
            }
        }
    }
}
