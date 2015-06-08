package com.lutz.networking;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.lutz.networking.packets.Packet;
import com.lutz.networking.sockets.Connection;

/**
 * This class represents the server-side portion of a server <-> client
 * relationship.
 * 
 * @author Christopher Lutz
 */
public class Server extends ServerListenable {

	private int port, maxConnect;
	private String serverName;
	private ServerSocket socket = null;

	private Thread incoming = null, checkFailed = null;
	private CopyOnWriteArrayList<Connection> connections = new CopyOnWriteArrayList<Connection>();

	private boolean failed = false, open = false;

	private long failCheck;

	/**
	 * Create a new {@code Server} instance with the specified parameters
	 * 
	 * @param port
	 *            The port to open the {@code Server} on
	 * @param serverName
	 *            The name of this {@code Server}
	 */
	public Server(int port, String serverName) {

		this(port, serverName, 20, 5000);
	}

	/**
	 * Create a new {@code Server} instance with the specified parameters
	 * 
	 * @param port
	 *            The port to open the {@code Server} on
	 * @param serverName
	 *            The name of this {@code Server}
	 * @param maxConnections
	 *            The maximum number of {@code Client} connections to be
	 *            accepted by this Server
	 */
	public Server(int port, String serverName, int maxConnections) {

		this(port, serverName, maxConnections, 5000);
	}

	/**
	 * Create a new {@code Server} instance with the specified parameters
	 * 
	 * @param port
	 *            The port to open the {@code Server} on
	 * @param serverName
	 *            The name of this {@code Server}
	 * @param failCheck
	 *            The loop delay in milliseconds to check for {@code Clients}
	 *            that have disconnected or errored
	 */
	public Server(int port, String serverName, long failCheck) {

		this(port, serverName, 20, failCheck);
	}

	/**
	 * Create a new {@code Server} instance with the specified parameters
	 * 
	 * @param port
	 *            The port to open the {@code Server} on
	 * @param serverName
	 *            The name of this {@code Server}
	 * @param maxConnections
	 *            The maximum number of {@code Client} connections to be
	 *            accepted by this Server
	 * @param failCheck
	 *            The loop delay in milliseconds to check for {@code Clients}
	 *            that have disconnected or errored
	 */
	public Server(int port, String serverName, int maxConnections,
			long failCheck) {

		this.port = port;
		this.serverName = serverName;
		this.maxConnect = maxConnections;
		this.failCheck = failCheck;
	}

	/**
	 * Gets the name attached to this {@code Server}
	 * 
	 * @return The server name of this {@code Server}
	 */
	public String getServerName() {

		return serverName;
	}

	/**
	 * Gets the port that this {@code Server} will open onto
	 * 
	 * @return This {@code Server}'s port
	 */
	public int getPort() {

		return port;
	}

	/**
	 * Attempts to open this {@code Server} onto the specified port
	 * 
	 * @throws IOException
	 *             If an I/O error occurs while starting the {@code Server}
	 */
	public void start() throws IOException {

		socket = new ServerSocket(port);

		incoming = new Thread() {

			@Override
			public void run() {

				try {

					while (open) {

						if (connections.size() < maxConnect) {

							Connection connection = new Connection(Server.this,
									socket.accept(),
									com.lutz.networking.states.State.SENDING,
									true);

							connections.add(connection);
						}
					}

				} catch (Exception e) {

					e.printStackTrace();

					failed = true;

					try {

						close();

					} catch (IOException e1) {

						e1.printStackTrace();
					}
				}
			}
		};
		incoming.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread arg0, Throwable arg1) {

				System.err.println(arg0.getName() + " has errored: "
						+ arg1.getClass().getName());
				arg1.printStackTrace();
			}
		});
		incoming.setName("Incoming Connection Monitor: Server '"
				+ getServerName() + "'");
		open = true;
		incoming.start();
		checkFailed = new Thread() {

			@Override
			public void run() {

				try {

					while (open) {

						try {

							Thread.sleep(failCheck);

						} catch (Exception e) {
						}

						List<Connection> toRem = new ArrayList<Connection>();

						for (Connection c : connections) {

							if (c.isRemoteClosed()) {

								toRem.add(c);
							}
						}

						for (Connection c : toRem) {

							connections.remove(c);
						}
					}

				} catch (Exception e) {

					failed = true;

					e.printStackTrace();

					try {

						close();

					} catch (IOException e1) {

						e1.printStackTrace();
					}
				}
			}
		};
		checkFailed.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread arg0, Throwable arg1) {

				System.err.println(arg0.getName() + " has errored: "
						+ arg1.getClass().getName());
				arg1.printStackTrace();
			}
		});
		checkFailed.setName("Failed Client Monitor: Server '" + getServerName()
				+ "'");
		checkFailed.start();

		open = true;
	}

	/**
	 * Checks if this {@code Server} is currently open
	 * 
	 * @return Whether or not this {@code Server} is open
	 */
	public boolean isOpen() {

		return open;
	}

	/**
	 * Checks if this {@code Server} has failed/errored
	 * 
	 * @return Whether or not this {@code Server} has failed/errored
	 */
	public boolean hasFailed() {

		return failed;
	}

	/**
	 * Makes the {@code Server}'s {@code Connections} set themselves back to the
	 * {@code Receiving} state
	 */
	public void setToReceive() {

		for (Connection c : connections) {

			c.setToReceive();
		}
	}

	/**
	 * Makes the {@code Server}'s {@code Connections} set themselves back to the
	 * {@code Sending} state
	 */
	public void setToSend() {

		for (Connection c : connections) {

			c.setToSend();
		}
	}

	/**
	 * Attempts to close this {@code Server} and disconnect all {@code Clients}
	 * 
	 * @throws IOException
	 *             If an I/O error occurs while shutting down this
	 *             {@code Server}
	 */
	public void close() throws IOException {

		open = false;
		incoming.interrupt();
		checkFailed.interrupt();

		for (Connection c : connections) {

			c.close();
		}
	}

	/**
	 * Gets all connections to this {@code Server}
	 * 
	 * @return A {@code Connection[]} containing references to all connections
	 *         on this {@code Server}
	 */
	public Connection[] getConnections() {

		return connections.toArray(new Connection[] {});
	}

	/**
	 * Gets a {@code Connection} for a specified IP
	 * 
	 * @param ip
	 *            The IP to get a {@code Connection} for
	 * @return The {@code Connection} for the specified IP
	 */
	public Connection getConnectionForIp(String ip) {

		for (Connection c : connections) {

			if (c.getIp().equals(ip)) {

				return c;
			}
		}

		return null;
	}

	/**
	 * Sends a {@code Packet} across all connections to the clients on the
	 * receiving ends
	 * 
	 * @param p
	 *            The {@code Packet} to send
	 * @param expectResponse
	 *            Whether or not the {@code Connection} should wait for a
	 *            response (decides whether or not to timeout the {@code read()}
	 *            calls
	 */
	public void sendPacket(Packet p, boolean expectResponse) {

		if (isOpen() && !hasFailed()) {

			for (Connection c : connections) {

				if (c != null) {

					c.sendPacket(p, expectResponse);
				}
			}
		}
	}
}
