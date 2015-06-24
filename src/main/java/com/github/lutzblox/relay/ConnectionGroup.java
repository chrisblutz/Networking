package com.github.lutzblox.relay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.sockets.Connection;

public class ConnectionGroup implements Iterable<Connection> {

	private List<Connection> connections = new ArrayList<Connection>();
	private String id;

	public ConnectionGroup(String id) {

		this.id = id;
	}

	public String getId() {
		
		return id;
	}

	public void addConnection(Connection c) {

		connections.add(c);
	}

	public boolean containsConnection(Connection c) {

		return connections.contains(c);
	}

	public Connection[] getConnections() {

		return connections.toArray(new Connection[] {});
	}

	public void removeConnection(Connection c) {

		connections.remove(c);
	}

	public int size() {

		return connections.size();
	}

	public void sendPacket(Connection sender, Packet data) {

		for (Connection c : connections) {

			if (c != sender) {

				c.sendPacket(data, false);
			}
		}
	}

	@Override
	public Iterator<Connection> iterator() {

		return connections.iterator();
	}

	public static void sendPacket(Connection sender, Packet data,
			ConnectionGroup[] groups) {

		for (ConnectionGroup g : groups) {

			if (g != null) {

				if (g.containsConnection(sender)) {

					g.sendPacket(sender, data);
				}
			}
		}
	}
}
