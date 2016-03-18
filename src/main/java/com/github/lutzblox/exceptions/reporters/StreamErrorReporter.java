package com.github.lutzblox.exceptions.reporters;

import java.io.*;


/**
 * An {@code ErrorReporter} that writes errors to an {@code OutputStream}
 *
 * @author Christopher Lutz
 */
public class StreamErrorReporter extends ErrorReporter implements Closeable,
        Flushable {

    private OutputStream stream;
    private PrintStream printStream;

    /**
     * Creates a new {@code StreamErrorReporter} that writes to the specified
     * {@code OutputStream}
     *
     * @param stream The stream to write to
     */
    public StreamErrorReporter(OutputStream stream) {

        this.stream = stream;
        this.printStream = new PrintStream(stream);
    }

    /**
     * Gets the output stream used to write errors
     *
     * @return The reporting stream
     */
    public OutputStream getOutputStream() {

        return stream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void report(String toReport) {

        printStream.println(toReport);
    }

    /**
     * Flushes the reporter and writes any buffered bytes to the underlying {@code OutputStream}
     */
    @Override
    public void flush() throws IOException {

        printStream.flush();
    }

    /**
     * Flushes the underlying {@code OutputStream} and closes this reporter.  This method will not close the {@code OutputStream}, it only closes the reporter.  The stream itself will need to be closed by calling its {@code close()} method.
     */
    @Override
    public void close() throws IOException {

        printStream.flush();
    }
}
