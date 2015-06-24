package com.github.lutzblox;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.github.lutzblox.exceptions.reporters.ErrorReporterFactory;
import com.github.lutzblox.listeners.ClientListener;
import com.github.lutzblox.listeners.ServerListener;
import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.sockets.Connection;

public class TimeoutTest extends TestCase {

	private boolean finished = false, errored = false;
	private String errorMessage = "";

	public TimeoutTest(String name) {

		super(name);
	}

	public static TestSuite suite() {

		return new TestSuite(TimeoutTest.class);
	}

	public void testTimeout() {

		final Server server = new Server(12351, "TimeoutTest");
		server.addErrorReporter(ErrorReporterFactory.newInstance());
		server.addNetworkListener(new ServerListener() {

			@Override
			public void onReceive(Connection connection, Packet packet) {
			}

			@Override
			public Packet onConnect(Connection c, Packet data) {

				System.out.println("Server: Connection received from IP "
						+ c.getIp());

				return data;
			}

			@Override
			public void onTimeout(Connection connection) {
			}

			@Override
			public void onClientFailure(Connection c) {
			}
		});

		final Client client = new Client("localhost", 12351);
		client.addErrorReporter(ErrorReporterFactory.newInstance());
		client.addNetworkListener(new ClientListener() {

			@Override
			public void onReceive(Connection connection, Packet packet) {
			}

			@Override
			public void onConnect(Packet packet) {

				client.sendPacket(new Packet(), true);
			}

			@Override
			public void onTimeout(Connection connection) {

				System.out.println("Connection to server timed out!");

				finished = true;
			}
		});

		try {

			System.out.println("Starting server...");

			server.start();

			System.out.println("Starting client...");

			client.connect();

			System.out.println("Waiting for timeout... (This should take "+((double) client.getConnection().getReadTimeout()/1000)+" seconds)");

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
			fail();

		} else {

			System.out.println("Success!");
		}
	}
}
