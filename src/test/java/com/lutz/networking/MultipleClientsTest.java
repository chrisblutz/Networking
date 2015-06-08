package com.lutz.networking;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.lutz.networking.listeners.ClientListener;
import com.lutz.networking.listeners.ServerListener;
import com.lutz.networking.packets.Packet;
import com.lutz.networking.sockets.Connection;

public class MultipleClientsTest extends TestCase {

	private boolean finished1 = false, finished2 = false,
			errored = false;
	private String errorMessage = "";

	public MultipleClientsTest(String name){
		
		super(name);
	}
	
	public static TestSuite suite(){
		
		return new TestSuite(MultipleClientsTest.class);
	}
	
	public void testMultipleClients() {
		
		final Server server = new Server(12346, "MultipleClientsTest");
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

		final Client client1 = new Client("localhost", 12346);
		client1.addNetworkListener(new ClientListener() {

			@Override
			public void onReceive(Connection connection, Packet packet) {
			}

			@Override
			public void onConnect(Packet packet) {

				System.out.println("Client 1: Successfully received packet!");

				finished1 = true;
			}

			@Override
			public void onTimeout(Connection connection) {
			}
		});

		final Client client2 = new Client("localhost", 12346);
		client2.addNetworkListener(new ClientListener() {

			@Override
			public void onReceive(Connection connection, Packet packet) {
			}

			@Override
			public void onConnect(Packet packet) {

				System.out.println("Client 2: Successfully received packet!");

				finished2 = true;
			}

			@Override
			public void onTimeout(Connection connection) {
			}
		});

		try {

			System.out.println("Starting server...");

			server.start();

			System.out.println("Starting client 1...");

			client1.connect();

			System.out.println("Starting client 2...");

			client2.connect();

		} catch (Exception e) {

			e.printStackTrace();

			errored = true;
			errorMessage = e.getClass().getName();

			finished1 = true;
			finished2 = true;
		}

		while (true) {

			try {

				Thread.sleep(100);

			} catch (InterruptedException e) {
			}

			if (finished1 && finished2) {

				break;
			}
		}

		try {

			client1.close();
			client2.close();
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
