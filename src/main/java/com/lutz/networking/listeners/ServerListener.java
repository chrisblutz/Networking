package com.lutz.networking.listeners;

import com.lutz.networking.packets.Packet;
import com.lutz.networking.sockets.Connection;

/**
 * A listener to be attached to a {@code Server}
 * 
 * @author Christopher Lutz
 */
public interface ServerListener extends NetworkListener {

	/**
	 * Called when a new {@code Client} connects to this {@code Server}
	 * 
	 * @return The {@code Packet} to send (this will be the packet passed to the
	 *         {@code Client}'s {@code onConnect()} listener method)
	 */
	public Packet onConnect(Connection c, Packet data);
}
