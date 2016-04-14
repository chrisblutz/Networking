package com.github.chrisblutz.exceptions.reporters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;


/**
 * A factory class to create instances of {@code ErrorReporter} for error
 * reporting in {@code Listenables}
 *
 * @author Christopher Lutz
 */
public final class ErrorReporterFactory {

    /**
     * Creates a new {@code StreamErrorReporter} that writes to the specified
     * {@code OutputStream}
     *
     * @param stream The stream to be used
     * @return The finalized {@code StreamErrorReporter}
     */
    public static final StreamErrorReporter newInstance(OutputStream stream) {

        return new StreamErrorReporter(stream);
    }

    /**
     * Creates a new {@code PrintStreamErrorReporter} that writes to the
     * specified {@code PrintStream}
     *
     * @param stream The stream to be used
     * @return The finalized {@code PrintStreamErrorReporter}
     */
    public static final PrintStreamErrorReporter newInstance(PrintStream stream) {

        return new PrintStreamErrorReporter(stream);
    }

    /**
     * Creates a new {@code FileErrorReporter} that writes to the specified
     * {@code File}
     *
     * @param file The file to be used
     * @return The finalized {@code FileErrorReporter}
     * @throws FileNotFoundException A {@code FileNotFoundException} is thrown if the {@code File} passed to this method is {@code null}
     */
    public static final FileErrorReporter newInstance(File file)
            throws FileNotFoundException {

        return new FileErrorReporter(file);
    }

    /**
     * Creates a new {@code PrintStreamErrorReporter} that writes to the default
     * {@code System.err} stream
     *
     * @return The finalized {@code PrintStreamErrorReporter}
     */
    public static final PrintStreamErrorReporter newInstance() {

        return new PrintStreamErrorReporter(System.err);
    }
}
