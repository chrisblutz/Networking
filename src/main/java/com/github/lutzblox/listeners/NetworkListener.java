package com.github.lutzblox.listeners;

import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.sockets.Connection;


/**
 * A listener to be attached to a {@code Listenable}
 *
 * @author Christopher Lutz
 */
public interface NetworkListener {

    /**
     * Called when a {@code Packet} is received from the opposite end of the
     * connection
     *
     * @param connection The {@code Connection} responsible for the {@code Packet}
     * @param packet     The {@code Packet} received
     */
    public void onReceive(Connection connection, Packet packet);

    /**
     * Called when a {@code Connection} times out on a {@code read()} call from the {@code Socket}
     *
     * @param connection The {@code Connection} responsible for the timeout
     */
    public void onTimeout(Connection connection);
}
