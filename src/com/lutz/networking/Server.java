package com.lutz.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import com.lutz.networking.packets.Packet;
import com.lutz.networking.sockets.Connection;

public class Server extends Listenable {

	private int port, maxConnect;
	private String serverName;
	private ServerSocket socket = null;

	private Thread incoming = null, checkFailed = null;
	private ArrayList<Connection> connections = new ArrayList<Connection>();

	private boolean failed = false, open = false;

	public Server(int port, String serverName) {

		this(port, serverName, 20);
	}

	public Server(int port, String serverName, int maxConnections) {

		this.port = port;
		this.serverName = serverName;
		this.maxConnect = maxConnections;
	}

	public String getServerName() {

		return serverName;
	}

	public int getPort() {

		return port;
	}

	public void start() throws IOException {

		socket = new ServerSocket(port);

		incoming = new Thread() {

			@Override
			public void run() {

				try {

					while (true) {

						if (connections.size() < maxConnect) {

							Connection connection = new Connection(Server.this,
									socket.accept(),
									com.lutz.networking.states.State.SENDING,
									true);

							System.out.println("Connected to client from "
									+ connection.getIp());

							connections.add(connection);
						}
					}

				} catch (Exception e) {

					failed = true;

					try {

						close();

					} catch (IOException e1) {

						e1.printStackTrace();
					}
				}
			}
		};
		incoming.start();
		checkFailed = new Thread() {

			@Override
			public void run() {

				try {

					while (true) {

						for (Connection c : connections) {

							if (c.isClosed()) {

								connections.remove(c);
							}
						}
					}

				} catch (Exception e) {

					failed = true;

					try {

						close();

					} catch (IOException e1) {

						e1.printStackTrace();
					}
				}
			}
		};
		checkFailed.start();

		open = true;
	}

	public boolean isOpen() {

		return open;
	}

	public boolean hasFailed() {

		return failed;
	}

	public void close() throws IOException {

		open = false;
		incoming.interrupt();
		checkFailed.interrupt();
		for (Connection c : connections) {

			c.close();
		}
	}

	public Connection getConnectionForIp(String ip) {

		for (Connection c : connections) {

			if (c.getIp().equals(ip)) {

				return c;
			}
		}

		return null;
	}

	public void sendPacket(Packet p) {

		if (isOpen() && !hasFailed()) {

			for (Connection c : connections) {

				if (c != null) {

					c.sendPacket(p);
				}
			}
		}
	}
}
