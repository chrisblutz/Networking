package com.github.lutzblox;

import com.github.lutzblox.databases.DatabaseClient;
import com.github.lutzblox.databases.DatabaseServer;
import com.github.lutzblox.exceptions.reporters.ErrorReporterFactory;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DatabaseTest extends TestCase {

	private boolean errored = false;
	private String errorMessage = "";

	public DatabaseTest(String name) {

		super(name);
	}

	public static TestSuite suite() {

		return new TestSuite(DatabaseTest.class);
	}

	public void testDatabase() {

		final DatabaseServer server = new DatabaseServer(12350, "DatabaseTest");
		server.addErrorReporter(ErrorReporterFactory.newInstance());

		final DatabaseClient client = new DatabaseClient("0.0.0.0", 12350);
		client.addErrorReporter(ErrorReporterFactory.newInstance());

		boolean finished = false;

		try {

			System.out.println("Starting database server...");

			server.start();

			System.out.println("Starting database client...");

			client.connect();

			System.out.println("Putting data...");

			client.putValue("test-value", "TestValue");

			System.out.println("Waiting...");

			try {

				Thread.sleep(1000);

			} catch (Exception e) {
			}

			System.out.println("Retrieving data...");

			String data = (String) client.requestValue("test-value");

			if (!data.equals("TestValue")) {

				errored = true;
				errorMessage = "Test did not return the correct value!";
			}

			finished = true;

		} catch (Exception e) {

			e.printStackTrace();

			errored = true;
			errorMessage = e.getClass().getName();

			finished = true;
		}

		while (true) {

			try {

				Thread.sleep(100);

			} catch (InterruptedException e) {
			}

			if (finished) {

				break;
			}
		}

		try {

			client.close();
			server.close();

		} catch (Exception e) {

			e.printStackTrace();

			errored = true;
			errorMessage = e.getClass().getName();
		}

		if (errored) {

			System.out.println("Errored - " + errorMessage);
			fail(errorMessage);

		} else {

			System.out.println("Success!");
		}
	}
}
