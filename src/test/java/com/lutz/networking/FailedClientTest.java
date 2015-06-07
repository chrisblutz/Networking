package com.lutz.networking;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.lutz.networking.listeners.ClientListener;
import com.lutz.networking.listeners.ServerListener;
import com.lutz.networking.packets.Packet;
import com.lutz.networking.sockets.Connection;

public class FailedClientTest extends TestCase {

	private boolean errored = false, closedClient = false;
	private String errorMessage = "";

	public FailedClientTest(String name) {

		super(name);
	}

	public static TestSuite suite() {

		return new TestSuite(FailedClientTest.class);
	}

	public void testFailedClient() {

		final Server server = new Server(12348, "FailedClientTest");
		server.addNetworkListener(new ServerListener() {

			@Override
			public void onReceive(Packet packet) {
			}

			@Override
			public Packet onConnect(Connection c, Packet data) {

				System.out.println("Server: Connection received from IP "
						+ c.getIp());

				return data;
			}
		});

		final Client client = new Client("localhost", 12348);
		client.addNetworkListener(new ClientListener() {

			@Override
			public void onReceive(Packet packet) {
			}

			@Override
			public void onConnect(Packet packet) {
			}
		});

		try {

			System.out.println("Starting server...");

			server.start();

			System.out.println("Starting client...");

			client.connect();

			System.out.println("Waiting...");

			while (true) {

				try {

					Thread.sleep(100);

				} catch (InterruptedException e) {
				}

				if (server.getConnections().length >= 1) {

					break;
				}
			}

			System.out.println("Closing client... (Note: If the check rate for failed clients is low for the server, you may see a bit of a delay here.  Be patient.)");
			
			client.close();
			
			closedClient = true;

		} catch (Exception e) {

			e.printStackTrace();

			errored = true;
			errorMessage = e.getClass().getName();
		}

		while (true) {

			try {

				Thread.sleep(100);

			} catch (InterruptedException e) {
			}

			if (closedClient && server.getConnections().length==0) {

				break;
			}
		}

		try {

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
