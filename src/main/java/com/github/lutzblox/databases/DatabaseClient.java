package com.github.lutzblox.databases;

import java.io.IOException;
import java.net.UnknownHostException;

import com.github.lutzblox.Client;
import com.github.lutzblox.exceptions.NetworkException;
import com.github.lutzblox.exceptions.reporters.ErrorReporter;
import com.github.lutzblox.listeners.ClientListener;
import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.sockets.Connection;

public class DatabaseClient {

	private Client client;

	private String databaseName = "";

	private boolean updated = false;

	private Object recentValue;

	private long respWait = 100;

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

	public DatabaseClient(String ip, int port, long responseLoopWaitTime) {

		this(ip, port);

		this.respWait = responseLoopWaitTime;
	}

	public String getDatabaseName() {

		return databaseName;
	}

	public String getIp() {

		return client.getIp();
	}

	public int getPort() {

		return client.getPort();
	}

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

	public void putValue(String key, Object value) {

		Packet p = new Packet();
		p.putData(PutRequest.PUT_REQUEST_KEY_KEY, key);
		p.putData(PutRequest.PUT_REQUEST_VALUE_KEY, value);

		client.sendPacket(p, false);

		client.setToSend();
	}

	public void putValue(PutRequest putRequest) {

		Packet p = new Packet();
		p.putData(PutRequest.PUT_REQUEST_KEY_KEY, putRequest.getDataKey());
		p.putData(PutRequest.PUT_REQUEST_VALUE_KEY, putRequest.getValue());

		client.sendPacket(p, false);

		client.setToSend();
	}

	public void sendCommand(String command) {

		Packet p = new Packet();
		p.putData(Command.COMMAND_KEY, command);

		client.sendPacket(p, false);

		client.setToSend();
	}

	public void sendCommand(Command command) {

		Packet p = new Packet();
		p.putData(Command.COMMAND_KEY, command.getCommand());

		client.sendPacket(p, false);

		client.setToSend();
	}

	/**
	 * Attached a {@code ErrorListener} to this {@code DatabaseClient}
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

	public void connect() throws UnknownHostException, IOException {

		client.connect();
	}

	public void close() throws IOException {

		client.close();
	}
}
