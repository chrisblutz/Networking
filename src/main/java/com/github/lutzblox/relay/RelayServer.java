package com.github.lutzblox.relay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.lutzblox.Server;
import com.github.lutzblox.exceptions.reporters.ErrorReporter;
import com.github.lutzblox.listeners.ServerListener;
import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.relay.listeners.RelayListener;
import com.github.lutzblox.sockets.Connection;
import com.github.lutzblox.states.State;

public class RelayServer {

	private Server server;

	private List<RelayListener> listeners = new ArrayList<RelayListener>();

	private List<ConnectionGroup> groups = new ArrayList<ConnectionGroup>();

	public RelayServer(int port, String serverName) {

		this(new Server(port, serverName));
	}

	public RelayServer(int port, String serverName, int maxConnections) {

		this(new Server(port, serverName, maxConnections));
	}

	public RelayServer(int port, String serverName, long failCheck) {

		this(new Server(port, serverName, failCheck));
	}

	public RelayServer(int port, String serverName, int maxConnections,
			long failCheck) {

		this(new Server(port, serverName, maxConnections, failCheck));
	}

	private RelayServer(Server server) {

		this.server = server;
		server.setDefaultConnectionState(State.MUTUAL);
		server.addNetworkListener(new ServerListener() {

			@Override
			public void onReceive(Connection connection, Packet packet) {

				for (RelayListener l : listeners) {

					l.onReceive(RelayServer.this, connection, packet);
				}

				ConnectionGroup.sendPacket(connection, packet,
						groups.toArray(new ConnectionGroup[] {}));
			}

			@Override
			public void onTimeout(Connection connection) {

				for (RelayListener l : listeners) {

					l.onTimeout(RelayServer.this, connection);
				}
			}

			@Override
			public Packet onConnect(Connection c, Packet data) {

				for (RelayListener l : listeners) {

					data = l.onConnect(RelayServer.this, c, data);
				}

				return data;
			}

			@Override
			public void onClientFailure(Connection c) {

				ungroupAll(c);
			}
		});
	}

	public void addRelayListener(RelayListener listener) {

		listeners.add(listener);
	}

	public RelayListener[] getRelayListeners() {

		return listeners.toArray(new RelayListener[] {});
	}

	/**
	 * Attaches an {@code ErrorListener} to this {@code RelayServer}
	 * 
	 * @param reporter
	 *            The {@code ErrorReporter} to add
	 */
	public void addErrorReporter(ErrorReporter reporter) {

		server.addErrorReporter(reporter);
	}

	/**
	 * Gets all of the {@code ErrorReporters} attached to this
	 * {@code RelayServer}
	 * 
	 * @return An {@code ErrorReporter[]} containing all {@code ErrorReporters}
	 *         attached to this {@code RelayServer}
	 */
	public ErrorReporter[] getErrorReporters() {

		return server.getErrorReporters();
	}

	/**
	 * Reports an error ({@code Throwable}) through the {@code ErrorReporters}
	 * attached to this {@code RelayServer}
	 * 
	 * @param t
	 *            The {@code Throwable} to report
	 */
	public void report(Throwable t) {

		server.report(t);
	}

	public void group(String id, Connection connection,
			Connection... connections) {

		ConnectionGroup group = new ConnectionGroup(id);
		group.addConnection(connection);
		for (Connection c : connections) {

			group.addConnection(c);
		}

		groups.add(group);
	}

	public void group(ConnectionGroup group, Connection connection,
			Connection... connections) {

		group.addConnection(connection);
		for (Connection c : connections) {

			group.addConnection(c);
		}
	}

	public void ungroup(ConnectionGroup group, Connection connection,
			Connection... connections) {

		group.removeConnection(connection);
		for (Connection c : connections) {

			group.removeConnection(c);
		}

		if (group.size() < 2) {

			groups.remove(group);
		}
	}

	public void ungroupAll(Connection connection, Connection... connections) {

		for (ConnectionGroup g : groups.toArray(new ConnectionGroup[] {})) {

			ungroup(g, connection, connections);
		}
	}

	public boolean hasGroup(String id) {

		for (ConnectionGroup group : groups) {

			if (group.getId().equals(id)) {

				return true;
			}
		}

		return false;
	}

	public ConnectionGroup getGroupForId(String id) {

		for (ConnectionGroup group : groups) {

			if (group.getId().equals(id)) {

				return group;
			}
		}

		return null;
	}
	
	public ConnectionGroup[] getGroups(){
		
		return groups.toArray(new ConnectionGroup[]{});
	}

	public void removeGroup(String id) {

		ConnectionGroup g = getGroupForId(id);

		if (g != null) {

			groups.remove(g);
		}
	}

	public String getServerName() {

		return server.getServerName();
	}

	public int getPort() {

		return server.getPort();
	}

	public boolean isOpen() {

		return server.isOpen();
	}

	public boolean hasFailed() {

		return server.hasFailed();
	}

	public void start() throws IOException {

		server.start();
	}

	public void close() throws IOException {

		server.close();
	}

	public Connection[] getConnections() {

		return server.getConnections();
	}

	public Connection getConnectionForIp(String ip) {

		return server.getConnectionForIp(ip);
	}
}
