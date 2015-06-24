package com.github.lutzblox;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.sockets.Connection;
import com.github.lutzblox.states.State;

/**
 * This class represents the client-side portion of a server <-> client
 * relationship.
 * 
 * @author Christopher Lutz
 */
public class Client extends ClientListenable {

	private int port;
	private String ip;
	private Socket socket;

	private Connection connection = null;

	private boolean open = false;

	/**
	 * Creates a {@code Client} instance that is set up to connect to the
	 * specified port on the specified IP
	 * 
	 * @param ip
	 *            The IP of the server to connect to when {@code connect()} is
	 *            called
	 * @param port
	 *            The port of the server to connect to when {@code connect()} is
	 *            called
	 */
	public Client(String ip, int port) {

		this.ip = ip;
		this.port = port;
	}

	/**
	 * Returns the IP that this {@code Client} will connect to when
	 * {@code connect()} is called
	 * 
	 * @return The server IP to connect to
	 */
	public String getIp() {

		return ip;
	}

	/**
	 * Returns the port that this {@code Client} will connect to when
	 * {@code connect()} is called
	 * 
	 * @return The server port to connect to
	 */
	public int getPort() {

		return port;
	}

	/**
	 * Attempts to connect this {@code Client} to a server on the IP and port
	 * specified
	 * 
	 * @throws UnknownHostException
	 *             If the given host IP could not be determined
	 * @throws IOException
	 *             If an I/O error occurs while connecting the {@code Socket} of
	 *             the {@code Client}
	 */
	public void connect() throws UnknownHostException, IOException {

		socket = new Socket(ip, port);

		connection = new Connection(this, socket,
				this.getDefaultConnectionState() == null ? State.RECEIVING
						: this.getDefaultConnectionState(), false);

		open = true;
	}

	/**
	 * Checks if this {@code Client} is currently open and connected
	 * 
	 * @return Whether or not this {@code Client} is open and connected
	 */
	public boolean isOpen() {

		return open && !socket.isClosed();
	}

	/** Makes the {@code Client} set itself back to the {@code Receiving} state */
	public void setToReceive() {

		this.connection.setToReceive();
	}

	/** Makes the {@code Client} set itself back to the {@code Sending} state */
	public void setToSend() {

		this.connection.setToSend();
	}

	/**
	 * Attempts to close the connection to the server
	 * 
	 * @throws IOException
	 *             If an I/O error occurs while disconnecting this
	 *             {@code Client}
	 */
	public void close() throws IOException {

		open = false;

		connection.close();
	}

	/**
	 * Gets the {@code Connection} object representing the connection between
	 * this {@code Client} and a server
	 * 
	 * @return The {@code Connection} object for this Client
	 */
	public Connection getConnection() {

		return connection;
	}

	/**
	 * Sends a {@code Packet} across the connection to the server on the
	 * receiving end
	 * 
	 * @param p
	 *            The {@code Packet} to send
	 * @param expectResponse
	 *            Whether or not the {@code Connection} should wait for a
	 *            response (decides whether or not to timeout the {@code read()}
	 *            calls
	 */
	public void sendPacket(Packet p, boolean expectResponse) {

		if (isOpen() && connection != null) {

			connection.sendPacket(p, expectResponse);
		}
	}
}
