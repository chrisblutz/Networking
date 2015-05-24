package com.lutz.networking;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.lutz.networking.packets.Packet;
import com.lutz.networking.sockets.Connection;
import com.lutz.networking.states.State;

public class Client extends ClientListenable {

	private int port;
	private String ip;
	private Socket socket;

	private Connection connection = null;

	private boolean open = false;

	public Client(String ip, int port) {

		this.ip = ip;
		this.port = port;
	}

	public String getIp() {

		return ip;
	}

	public int getPort() {

		return port;
	}

	public void connect() throws UnknownHostException, IOException {

		socket = new Socket(ip, port);

		connection = new Connection(this, socket, State.RECEIVING, false);

		open = true;
	}

	public boolean isOpen() {

		return open && !socket.isClosed();
	}

	public void close() throws IOException {

		open = false;
		socket.close();
	}

	public Connection getConnecion() {

		return connection;
	}

	public void sendPacket(Packet p) {

		if (isOpen() && connection != null) {

			connection.sendPacket(p);
		}
	}
}
