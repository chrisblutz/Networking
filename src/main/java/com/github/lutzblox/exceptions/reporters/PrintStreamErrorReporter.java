package com.github.lutzblox.exceptions.reporters;

import java.io.PrintStream;

public class PrintStreamErrorReporter extends StreamErrorReporter {

	private PrintStream pStr;

	public PrintStreamErrorReporter(PrintStream stream) {

		super(stream);
		this.pStr = stream;
	}

	public PrintStream getPrintStream() {

		return pStr;
	}
}
