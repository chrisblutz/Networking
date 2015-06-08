package com.github.lutzblox;

import java.util.ArrayList;
import java.util.List;

import com.github.lutzblox.listeners.NetworkListener;
import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.sockets.Connection;

/**
 * A class that holds listeners for sides of a connection
 * 
 * @author Christopher Lutz
 */
public class Listenable {

	protected List<NetworkListener> lists = new ArrayList<NetworkListener>();

	/**
	 * Attaches a {@code NetworkListener} to this {@code Listenable}
	 * 
	 * @param listener
	 *            The listener to add
	 */
	public void addNetworkListener(NetworkListener listener) {

		lists.add(listener);
	}

	/**
	 * Gets all of the {@code NetworkListener} objects attached to this
	 * {@code Listenable}
	 * 
	 * @return A {@code NetworkListener[]} containing all listeners attached to
	 *         this {@code Listenable}
	 */
	public NetworkListener[] getNetworkListeners() {

		return lists.toArray(new NetworkListener[] {});
	}

	/**
	 * Fires the {@code onRecieve()} method in all of the attached
	 * {@code NetworkListener} objects
	 * 
	 * @param connection
	 *            The {@code Connection} responsible for the {@code Packet}
	 * @param packet
	 *            The {@code Packet} to pass to the listeners
	 */
	public void fireListenerOnReceive(Connection connection, Packet packet) {

		for (NetworkListener l : getNetworkListeners()) {

			l.onReceive(connection, packet);
		}
	}

	/**
	 * Fires the {@code onTimeout()} method in all of the attached
	 * {@code NetworkListener} objects
	 * 
	 * @param connection
	 *            The {@code Connection} responsible for the timeout
	 */
	public void fireListenerOnTimeout(Connection connection) {

		for (NetworkListener l : getNetworkListeners()) {

			l.onTimeout(connection);
		}
	}
}
