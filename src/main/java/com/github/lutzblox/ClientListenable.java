package com.github.lutzblox;

import java.util.ArrayList;
import java.util.List;

import com.github.lutzblox.listeners.ClientListener;
import com.github.lutzblox.listeners.NetworkListener;
import com.github.lutzblox.packets.Packet;

/**
 * This class extends the {@code Listenable} class to make it specifically for
 * the {@code Client} side of a connection
 * 
 * @author Christopher Lutz
 */
public class ClientListenable extends Listenable {

	/**
	 * Gets all of the {@code ClientListener} objects attached to the
	 * {@code Client}
	 * 
	 * @return A {@code ClientListener[]} containing all listeners attached to
	 *         the {@code Client}
	 */
	public ClientListener[] getClientListeners() {

		List<ClientListener> l = new ArrayList<ClientListener>();

		for (NetworkListener n : lists) {

			if (n instanceof ClientListener) {

				l.add((ClientListener) n);
			}
		}

		return l.toArray(new ClientListener[] {});
	}

	/**
	 * Fires the {@code onConnect()} method in all of the {@code ClientListener}
	 * objects attached to the {@code Client}
	 * 
	 * @param packet
	 *            The {@code Packet} to pass to the listener
	 */
	public void fireListenerOnConnect(Packet packet) {

		for (ClientListener l : getClientListeners()) {

			l.onConnect(packet);
		}
	}
}
