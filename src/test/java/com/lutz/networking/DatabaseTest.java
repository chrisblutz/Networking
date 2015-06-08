package com.lutz.networking;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.lutz.networking.databases.DatabaseClient;
import com.lutz.networking.databases.DatabaseServer;

public class DatabaseTest extends TestCase {

	private boolean finished = false, errored = false;
	private String errorMessage = "";

	public DatabaseTest(String name) {

		super(name);
	}

	public static TestSuite suite() {

		return new TestSuite(DatabaseTest.class);
	}

	public void testDatabase() {

		final DatabaseServer server = new DatabaseServer(12350, "DatabaseTest");

		final DatabaseClient client = new DatabaseClient("localhost", 12350);

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

		} else {

			System.out.println("Success!");
		}
	}
}