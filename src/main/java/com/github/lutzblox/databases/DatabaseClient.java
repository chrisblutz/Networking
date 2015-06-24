package com.github.lutzblox.databases;

import java.io.IOException;
import java.net.UnknownHostException;

import com.github.lutzblox.Client;
import com.github.lutzblox.exceptions.NetworkException;
import com.github.lutzblox.exceptions.reporters.ErrorReporter;
import com.github.lutzblox.listeners.ClientListener;
import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.sockets.Connection;

/**
 * This class represents the client-side portion of a database server <-> client
 * relationship.
 * 
 * @author Christopher Lutz
 */
public class DatabaseClient {

	private Client client;

	private String databaseName = "";

	private boolean updated = false;

	private Object recentValue;

	private long respWait = 100;

	/**
	 * Creates a {@code DatabaseClient} instance that is set up to connect to
	 * the specified port on the specified IP
	 * 
	 * @param ip
	 *            The IP of the server to connect to when {@code connect()} is
	 *            called
	 * @param port
	 *            The port of the server to connect to when {@code connect()} is
	 *            called
	 */
	public DatabaseClient(String ip, int port) {

		client = new Client(ip, port);

		client.addNetworkListener(new ClientListener() {

			@Override
			public void onReceive(Connection connection, Packet packet) {

				if (packet.hasData(Response.RESPONSE_KEY)) {

					recentValue = packet.getData(Response.RESPONSE_KEY);
					updated = true;
				}
			}

			@Override
			public void onConnect(Packet packet) {

				databaseName = (String) packet
						.getData(DatabaseServer.DATABASE_NAME_KEY);
			}

			@Override
			public void onTimeout(Connection connection) {

				NetworkException ex = new NetworkException(
						"The database server took too long to respond!");

				client.report(ex);

				recentValue = null;
				updated = true;
			}
		});
	}

	/**
	 * Creates a {@code DatabaseClient} instance that is set up to connect to
	 * the specified port on the specified IP
	 * 
	 * @param ip
	 *            The IP of the server to connect to when {@code connect()} is
	 *            called
	 * @param port
	 *            The port of the server to connect to when {@code connect()} is
	 *            called
	 * @param responseLoopWaitTime
	 *            The amount of time to wait for a response from the server
	 */
	public DatabaseClient(String ip, int port, long responseLoopWaitTime) {

		this(ip, port);

		this.respWait = responseLoopWaitTime;
	}

	/**
	 * Gets the name of the database this {@code DatabaseClient} is connected to
	 * 
	 * @return The name of the database
	 */
	public String getDatabaseName() {

		return databaseName;
	}

	/**
	 * Returns the IP that this {@code DatabaseClient} will connect to when
	 * {@code connect()} is called
	 * 
	 * @return The server IP to connect to
	 */
	public String getIp() {

		return client.getIp();
	}

	/**
	 * Returns the port that this {@code DatabaseClient} will connect to when
	 * {@code connect()} is called
	 * 
	 * @return The server port to connect to
	 */
	public int getPort() {

		return client.getPort();
	}

	/**
	 * Checks if this {@code DatabaseClient} is currently open and connected
	 * 
	 * @return Whether or not this {@code DatabaseClient} is open and connected
	 */
	public boolean isOpen() {

		return client.isOpen();
	}

	/**
	 * Requests a value from the {@code DatabaseServer}
	 * 
	 * @param key
	 *            The key of the value to request
	 * @return The value bound to the requested key
	 */
	public Object requestValue(String key) {

		updated = false;

		Packet p = new Packet();
		p.putData(Request.REQUEST_KEY, key);

		client.sendPacket(p, true);

		while (!updated) {

			try {

				Thread.sleep(respWait);

			} catch (Exception e) {
			}
		}

		return recentValue;
	}

	/**
	 * Requests a value from the {@code DatabaseServer}
	 * 
	 * @param request
	 *            The request to send
	 * @return The value bound to the request key
	 */
	public Object requestValue(Request request) {

		updated = false;

		Packet p = new Packet();
		p.putData(Request.REQUEST_KEY, request.getDataKey());

		client.sendPacket(p, true);

		while (!updated) {

			try {

				Thread.sleep(100);

			} catch (Exception e) {
			}
		}

		return recentValue;
	}

	/**
	 * Puts a value into the database
	 * 
	 * @param key
	 *            The key of the data
	 * @param value
	 *            The value of the data
	 */
	public void putValue(String key, Object value) {

		Packet p = new Packet();
		p.putData(PutRequest.PUT_REQUEST_KEY_KEY, key);
		p.putData(PutRequest.PUT_REQUEST_VALUE_KEY, value);

		client.sendPacket(p, false);

		client.setToSend();
	}

	/**
	 * Puts a value into the database
	 * 
	 * @param putRequest
	 *            The {@code PutRequest} to add
	 */
	public void putValue(PutRequest putRequest) {

		Packet p = new Packet();
		p.putData(PutRequest.PUT_REQUEST_KEY_KEY, putRequest.getDataKey());
		p.putData(PutRequest.PUT_REQUEST_VALUE_KEY, putRequest.getValue());

		client.sendPacket(p, false);

		client.setToSend();
	}

	/**
	 * Deletes a value from the database
	 * 
	 * @param key
	 *            The key of the data
	 */
	public void deleteValue(String key) {

		Packet p = new Packet();
		p.putData(DeleteRequest.DELETE_REQUEST_KEY_KEY, key);

		client.sendPacket(p, false);

		client.setToSend();
	}

	/**
	 * Deletes a value from the database
	 * 
	 * @param deleteRequest
	 *            The {@code DeleteRequest} to add
	 */
	public void deleteValue(DeleteRequest deleteRequest) {

		Packet p = new Packet();
		p.putData(DeleteRequest.DELETE_REQUEST_KEY_KEY, deleteRequest.getDataKey());

		client.sendPacket(p, false);

		client.setToSend();
	}

	/**
	 * Sends a command to the {@code DatabaseServer}
	 * 
	 * @param command
	 *            The command to send
	 */
	public void sendCommand(String command) {

		Packet p = new Packet();
		p.putData(Command.COMMAND_KEY, command);

		client.sendPacket(p, false);

		client.setToSend();
	}

	/**
	 * Sends a {@code Command} to the {@code DatabaseServer}
	 * 
	 * @param command
	 *            The {@code Command} to send
	 */
	public void sendCommand(Command command) {

		Packet p = new Packet();
		p.putData(Command.COMMAND_KEY, command.getCommand());

		client.sendPacket(p, false);

		client.setToSend();
	}

	/**
	 * Attaches an {@code ErrorListener} to this {@code DatabaseClient}
	 * 
	 * @param reporter
	 *            The {@code ErrorReporter} to add
	 */
	public void addErrorReporter(ErrorReporter reporter) {

		client.addErrorReporter(reporter);
	}

	/**
	 * Gets all of the {@code ErrorReporters} attached to this
	 * {@code DatabaseClient}
	 * 
	 * @return An {@code ErrorReporter[]} containing all {@code ErrorReporters}
	 *         attached to this {@code DatabaseClient}
	 */
	public ErrorReporter[] getErrorReporters() {

		return client.getErrorReporters();
	}

	/**
	 * Reports an error ({@code Throwable}) through the {@code ErrorReporters}
	 * attached to this {@code DatabaseClient}
	 * 
	 * @param t
	 *            The {@code Throwable} to report
	 */
	public void report(Throwable t) {

		client.report(t);
	}

	/**
	 * Attempts to connect this {@code DatabaseClient} to a server on the IP and
	 * port specified
	 * 
	 * @throws UnknownHostException
	 *             If the given host IP could not be determined
	 * @throws IOException
	 *             If an I/O error occurs while connecting the {@code Socket} of
	 *             the {@code DatabaseClient}
	 */
	public void connect() throws UnknownHostException, IOException {

		client.connect();
	}

	/**
	 * Attempts to close the connection to the server
	 * 
	 * @throws IOException
	 *             If an I/O error occurs while disconnecting this
	 *             {@code DatabaseClient}
	 */
	public void close() throws IOException {

		client.close();
	}
}
