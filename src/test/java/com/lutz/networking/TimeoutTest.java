package com.lutz.networking;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.lutz.networking.listeners.ClientListener;
import com.lutz.networking.listeners.ServerListener;
import com.lutz.networking.packets.Packet;
import com.lutz.networking.sockets.Connection;

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
		});

		final Client client = new Client("localhost", 12351);
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

			System.out.println("Waiting for timeout...");

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
