package com.lutz.networking;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.lutz.networking.listeners.ClientListener;
import com.lutz.networking.listeners.ServerListener;
import com.lutz.networking.packets.Packet;
import com.lutz.networking.sockets.Connection;

public class EmptyPacketTest extends TestCase {

	private boolean finished = false, errored = false;
	private String errorMessage = "";

	public EmptyPacketTest(String name) {

		super(name);
	}

	public static TestSuite suite() {

		return new TestSuite(EmptyPacketTest.class);
	}

	public void testEmptyPacket() {

		final Server server = new Server(12345, "EmptyPacketTest");
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

		final Client client = new Client("localhost", 12345);
		client.addNetworkListener(new ClientListener() {

			@Override
			public void onReceive(Connection connection, Packet packet) {
			}

			@Override
			public void onConnect(Packet packet) {

				System.out
						.println("Client: Successfully received empty packet!");

				finished = true;
			}

			@Override
			public void onTimeout(Connection connection) {
			}
		});

		try {

			System.out.println("Starting server...");

			server.start();

			System.out.println("Starting client...");

			client.connect();

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
