package com.lutz.networking;

import java.util.ArrayList;
import java.util.List;

import com.lutz.networking.listeners.NetworkListener;
import com.lutz.networking.packets.Packet;

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
	 * @param packet
	 *            The {@code Packet} to pass to the listeners
	 */
	public void fireListenerOnReceive(Packet packet) {

		for (NetworkListener l : getNetworkListeners()) {

			l.onReceive(packet);
		}
	}
}
