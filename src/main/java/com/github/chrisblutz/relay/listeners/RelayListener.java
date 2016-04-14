package com.github.chrisblutz.relay.listeners;

import com.github.chrisblutz.packets.Packet;
import com.github.chrisblutz.relay.RelayServer;
import com.github.chrisblutz.sockets.Connection;


/**
 * A listener interface for {@code RelayServers} for events such as receiving
 * {@code Packets}, timing out a {@code Connection}, or connecting a
 * {@code Client}
 *
 * @author Christopher Lutz
 */
public interface RelayListener {

    /**
     * Fires when the {@code RelayServer} receives a {@code Packet} from a
     * {@code Connection}
     *
     * @param server The {@code RelayServer} that received the {@code Packet}
     * @param c      The {@code Connection} that received the {@code Packet}
     * @param data   The {@code Packet} that was received
     */
    public void onReceive(RelayServer server, Connection c, Packet data);

    /**
     * Fires when a {@code Connection} times out
     *
     * @param server The {@code RelayServer} containing the timed-out
     *               {@code Connection}
     * @param c      The {@code Connection} that timed out
     */
    public void onTimeout(RelayServer server, Connection c);

    /**
     * Fires when a {@code Client} connects to the {@code RelayServer}
     *
     * @param server The {@code RelayServer} that had a {@code Client} connect to
     *               it
     * @param c      The {@code Connection} that connected to the
     *               {@code RelayServer}
     * @param data   The {@code Packet} to be sent to the connected {@code Client}
     * @return The {@code Packet} to be sent to the connected {@code Client}
     */
    public Packet onConnect(RelayServer server, Connection c, Packet data);
}
