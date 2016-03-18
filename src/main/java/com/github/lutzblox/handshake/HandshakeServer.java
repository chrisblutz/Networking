package com.github.lutzblox.handshake;

import com.github.lutzblox.Server;
import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.sockets.Connection;
import com.github.lutzblox.states.State;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A class representing the server side of a connection.  Handshake connections are in constant communication to ensure the connection always exists.
 *
 * @author Christopher Lutz
 */
public class HandshakeServer extends Server {

    private Map<Connection, List<Packet>> waiting = new HashMap<Connection, List<Packet>>();

    /**
     * Create a new {@code HandshakeServer} instance with the specified parameters
     *
     * @param port       The port to open the {@code HandshakeServer} on
     * @param serverName The name of this {@code HandshakeServer}
     */
    public HandshakeServer(int port, String serverName) {

        super(port, serverName);
    }

    /**
     * Create a new {@code HandshakeServer} instance with the specified parameters
     *
     * @param port           The port to open the {@code HandshakeServer} on
     * @param serverName     The name of this {@code HandshakeServer}
     * @param maxConnections The maximum number of {@code HandshakeClient} connections to be
     *                       accepted by this {@code HandshakeServer}
     */
    public HandshakeServer(int port, String serverName, int maxConnections) {

        super(port, serverName, maxConnections);
    }

    /**
     * Create a new {@code HandshakeServer} instance with the specified parameters
     *
     * @param port       The port to open the {@code HandshakeServer} on
     * @param serverName The name of this {@code HandshakeServer}
     * @param failCheck  The loop delay in milliseconds to check for {@code HandshakeClients}
     *                   that have disconnected or errored
     */
    public HandshakeServer(int port, String serverName, long failCheck) {

        super(port, serverName, failCheck);
    }

    /**
     * Create a new {@code HandshakeServer} instance with the specified parameters
     *
     * @param port           The port to open the {@code HandshakeServer} on
     * @param serverName     The name of this {@code HandshakeServer}
     * @param maxConnections The maximum number of {@code HandshakeClient} connections to be
     *                       accepted by this {@code HandshakeServer}
     * @param failCheck      The loop delay in milliseconds to check for {@code HandshakeClients}
     *                       that have disconnected or errored
     */
    public HandshakeServer(int port, String serverName, int maxConnections, long failCheck) {

        super(port, serverName, maxConnections, failCheck);
    }

    @Override
    protected Connection makeConnection(Socket socket) {

        return new Connection(this, socket, State.SENDING, true, false, getQueryPolicies());
    }

    /**
     * Adds a {@code Packet} to the list of {@code Packets} that will be combined the next time a {@code Connection} on this server sets itself to the {@code SENDING} state
     *
     * @param p              The {@code Packet} to add
     * @param expectResponse This parameter has no effect on this {@code HandshakeServer}
     */
    @Override
    public void sendPacket(Packet p, boolean expectResponse) {

        for (Connection c : getConnections()) {

            sendPacket(c, p);
        }
    }

    /**
     * Adds a {@code Packet} to the list of {@code Packets} that will be combined the next time the specified {@code Connection} on this server sets itself to the {@code SENDING} state
     *
     * @param c The {@code Connection} to add the {@code Packet} to
     * @param p The {@code Packet} to add
     */
    public void sendPacket(Connection c, Packet p) {

        if (!waiting.containsKey(c)) {

            waiting.put(c, new ArrayList<Packet>());
        }

        waiting.get(c).add(p);
    }

    /**
     * Fires the {@code onConnect()} method in all of the {@code ServerListener}
     * objects attached to the {@code HandshakeServer}
     *
     * @param connection The {@code Connection} that was connected
     * @param packet     The {@code Packet} to pass to the listener
     * @return The packet after passing through the listener methods
     */
    @Override
    public Packet fireListenerOnConnect(Connection connection, Packet packet) {

        packet = super.fireListenerOnConnect(connection, packet);

        return packet;
    }

    /**
     * Fires the {@code onRecieve()} method in all of the attached
     * {@code NetworkListener} objects
     *
     * @param connection The {@code Connection} responsible for the {@code Packet}
     * @param packet     The {@code Packet} to pass to the listeners
     */
    @Override
    public void fireListenerOnReceive(Connection connection, Packet packet) {

        if (!packet.isEmpty()) {

            super.fireListenerOnReceive(connection, packet);
        }

        Packet toSend = new Packet();

        if (waiting.containsKey(connection)) {

            List<Packet> waitingPackets = waiting.get(connection);

            if (waitingPackets != null && waitingPackets.size() > 0) {

                for (Packet p : waitingPackets) {

                    toSend.putData(p.getData());

                    if (p.isVital()) {

                        toSend.setVital(true);
                    }
                }
            }
        }

        connection.sendPacket(toSend, true);
    }

    /**
     * Fires the {@code onClientFailure()} method in all of the
     * {@code ServerListener} objects attached to the {@code HandshakeServer}
     *
     * @param c The {@code Connection} that failed
     */
    @Override
    public void fireListenerOnClientFailure(Connection c) {

        super.fireListenerOnClientFailure(c);

        waiting.remove(c);
    }
}
