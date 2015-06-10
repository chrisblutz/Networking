package com.github.lutzblox.exceptions.reporters;

import java.io.PrintStream;

/**
 * An {@code ErrorReporter} that writes errors to a {@code PrintStream}
 * 
 * @author Christopher Lutz
 */
public class PrintStreamErrorReporter extends StreamErrorReporter {

	private PrintStream pStr;

	/**
	 * Creates a new {@code PrintStreamErrorReporter} that writes to the
	 * specified {@code PrintStream}
	 * 
	 * @param stream
	 *            The stream to write to
	 */
	public PrintStreamErrorReporter(PrintStream stream) {

		super(stream);
		this.pStr = stream;
	}

	/**
	 * Gets the stream used to write errors
	 * 
	 * @return The reporting stream
	 */
	public PrintStream getPrintStream() {

		return pStr;
	}
}
