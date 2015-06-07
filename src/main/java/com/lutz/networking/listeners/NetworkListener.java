package com.lutz.networking.listeners;

import com.lutz.networking.packets.Packet;

/**
 * A listener to be attached to a {@code Listenable}
 * 
 * @author Christopher Lutz
 */
public interface NetworkListener {

	/**
	 * Called when a {@code Packet} is received from the opposite end of the connection
	 * 
	 * @param packet
	 *            The {@code Packet} received
	 */
	public void onReceive(Packet packet);
}
