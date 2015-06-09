package com.github.lutzblox.exceptions.reporters;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Flushable;
import java.io.IOException;
import java.io.PrintStream;

public class FileErrorReporter extends ErrorReporter implements Closeable,
		Flushable {

	private File file;
	private PrintStream printStream;

	public FileErrorReporter(File file) throws FileNotFoundException {

		this.file = file;
		this.printStream = new PrintStream(file);
	}

	public File getFile() {

		return file;
	}

	@Override
	protected void report(String toReport) {

		printStream.println(toReport);
	}

	@Override
	public void flush() throws IOException {

		printStream.flush();
	}

	@Override
	public void close() {

		printStream.close();
	}
}
