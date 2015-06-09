package com.github.lutzblox.databases;

import java.io.IOException;

import com.github.lutzblox.Server;
import com.github.lutzblox.databases.saving.SaveMethod;
import com.github.lutzblox.exceptions.reporters.ErrorReporter;
import com.github.lutzblox.listeners.ServerListener;
import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.sockets.Connection;
import com.github.lutzblox.utils.ExtendedMap;

public class DatabaseServer {

	public static final String DATABASE_NAME_KEY = "database-name";

	private ExtendedMap data = new ExtendedMap();

	private Server server;

	private SaveMethod saveMethod = null;

	public DatabaseServer(int port, String databaseName) {

		this(new Server(port, databaseName));
	}

	public DatabaseServer(int port, String databaseName, int maxConnections) {

		this(new Server(port, databaseName, maxConnections));
	}

	public DatabaseServer(int port, String databaseName, long failCheck) {

		this(new Server(port, databaseName, failCheck));
	}

	public DatabaseServer(int port, String databaseName, int maxConnections,
			long failCheck) {

		this(new Server(port, databaseName, maxConnections, failCheck));
	}

	private DatabaseServer(Server s) {

		this.server = s;

		server.addNetworkListener(new ServerListener() {

			@Override
			public void onReceive(Connection connection, Packet packet) {

				if (packet.hasData(Request.REQUEST_KEY)) {

					String key = (String) packet.getData(Request.REQUEST_KEY);

					Packet response = new Packet();
					response.putData(Response.RESPONSE_KEY, data.get(key));

					connection.sendPacket(response, false);

				} else if (packet.hasData(PutRequest.PUT_REQUEST_KEY_KEY)
						&& packet.hasData(PutRequest.PUT_REQUEST_VALUE_KEY)) {

					String key = (String) packet
							.getData(PutRequest.PUT_REQUEST_KEY_KEY);
					Object value = packet
							.getData(PutRequest.PUT_REQUEST_VALUE_KEY);

					data.put(value.getClass(), key, value);

					if (saveMethod != null) {

						saveMethod.save(data, server);
					}

					connection.setToReceive();

				} else if (packet.hasData(Command.COMMAND_KEY)) {

					String command = (String) packet
							.getData(Command.COMMAND_KEY);

					if (command.equalsIgnoreCase("clear")) {

						data.clear();
					}

					connection.setToReceive();
				}
			}

			@Override
			public Packet onConnect(Connection c, Packet data) {

				data.putData(DATABASE_NAME_KEY, server.getServerName());

				return data;
			}

			@Override
			public void onTimeout(Connection connection) {
			}
		});
	}

	public void setSaveMethod(SaveMethod method) {

		this.saveMethod = method;
	}

	public SaveMethod getSaveMethod() {

		return saveMethod;
	}

	public void loadDatabase() {

		if (saveMethod != null) {

			data.putAll(saveMethod.load(server));
		}
	}

	public void saveDatabase() {

		if (saveMethod != null) {

			saveMethod.save(data, server);
		}
	}

	public int getPort() {

		return server.getPort();
	}

	public String getDatabaseName() {

		return server.getServerName();
	}

	public void putData(String key, Object value) {

		data.put(value.getClass(), key, value);
	}

	public boolean hasData(String key) {

		return data.containsKey(key);
	}

	public Object getData(String key) {

		return data.get(key);
	}

	public void clear() {

		data.clear();
	}

	/**
	 * Attached a {@code ErrorListener} to this {@code DatabaseServer}
	 * 
	 * @param reporter
	 *            The {@code ErrorReporter} to add
	 */
	public void addErrorReporter(ErrorReporter reporter) {

		server.addErrorReporter(reporter);
	}

	/**
	 * Gets all of the {@code ErrorReporters} attached to this
	 * {@code DatabaseServer}
	 * 
	 * @return An {@code ErrorReporter[]} containing all {@code ErrorReporters}
	 *         attached to this {@code DatabaseServer}
	 */
	public ErrorReporter[] getErrorReporters() {

		return server.getErrorReporters();
	}

	/**
	 * Reports an error ({@code Throwable}) through the {@code ErrorReporters}
	 * attached to this {@code DatabaseServer}
	 * 
	 * @param t
	 *            The {@code Throwable} to report
	 */
	public void report(Throwable t) {

		server.report(t);
	}

	public void start() throws IOException {

		server.start();
	}

	public void close() throws IOException {

		server.close();
	}
}
