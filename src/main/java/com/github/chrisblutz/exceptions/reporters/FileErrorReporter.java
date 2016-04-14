package com.github.chrisblutz.exceptions.reporters;

import java.io.*;


/**
 * An {@code ErrorReporter} that writes errors to files
 *
 * @author Christopher Lutz
 */
public class FileErrorReporter extends ErrorReporter implements Closeable,
        Flushable {

    private File file;
    private PrintStream printStream;

    /**
     * Creates a new {@code FileErrorReporter} that writes to the specified file
     *
     * @param file The file to write to
     * @throws FileNotFoundException If the given file object does not denote an existing,
     *                               writable regular file and a new regular file of that name
     *                               cannot be created, or if some other error occurs while
     *                               opening or creating the file
     */
    public FileErrorReporter(File file) throws FileNotFoundException {

        this.file = file;
        this.printStream = new PrintStream(file);
    }

    /**
     * Gets the file used to write errors
     *
     * @return The reporting file
     */
    public File getFile() {

        return file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void report(String toReport) {

        printStream.println(toReport);
    }

    /**
     * Flushes the reporter and writes any buffered bytes to the underlying {@code File}
     */
    @Override
    public void flush() throws IOException {

        printStream.flush();
    }

    /**
     * Closes the stream to the underlying {@code File} and shuts down the reporter.
     */
    @Override
    public void close() {

        printStream.close();
    }
}
