package com.github.lutzblox.exceptions.reporters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;

public final class ErrorReporterFactory {

	public static final StreamErrorReporter newInstance(OutputStream stream) {

		return new StreamErrorReporter(stream);
	}

	public static final PrintStreamErrorReporter newInstance(PrintStream stream) {

		return new PrintStreamErrorReporter(stream);
	}

	public static final FileErrorReporter newInstance(File file)
			throws FileNotFoundException {

		return new FileErrorReporter(file);
	}

	public static final PrintStreamErrorReporter newInstance() {

		return new PrintStreamErrorReporter(System.err);
	}
}
