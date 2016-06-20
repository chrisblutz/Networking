package com.github.chrisblutz.networking.listeners;

import com.github.chrisblutz.networking.packets.Packet;
import com.github.chrisblutz.networking.sockets.Connection;


/**
 * A listener to be attached to a {@code Server}
 *
 * @author Christopher Lutz
 */
public interface ServerListener extends NetworkListener {

    /**
     * Called when a new {@code Client} connects to this {@code Server}
     *
     * @param c    The {@code Connection} that was connected
     * @param data The {@code Packet} that will be sent across to the {@code Client}
     * @return The {@code Packet} to send (this will be the packet passed to the
     * {@code Client}'s {@code onConnect()} listener method)
     */
    public Packet onConnect(Connection c, Packet data);

    /**
     * Called when the {@code Server} detects that a {@code Client} is no longer
     * connected/open
     *
     * @param c The {@code Connection} that failed
     */
    public void onClientFailure(Connection c);
}
