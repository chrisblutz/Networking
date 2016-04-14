package com.github.chrisblutz.handshake;

import com.github.chrisblutz.Client;
import com.github.chrisblutz.packets.Packet;
import com.github.chrisblutz.sockets.Connection;
import com.github.chrisblutz.states.State;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
 * A class representing the client side of a connection.  Handshake connections are in constant communication to ensure the connection always exists.
 *
 * @author Christopher Lutz
 */
public class HandshakeClient extends Client {

    private List<Packet> waiting = new ArrayList<Packet>();

    /**
     * Creates a new {@code HandshakeClient} with the specified IP and port
     *
     * @param ip         The IP to use
     * @param port       The port to use
     * @param clientName The name of this {@code HandshakeClient}
     */
    public HandshakeClient(String ip, int port, String clientName) {

        super(ip, port, clientName);
    }

    @Override
    protected Connection makeConnection(Socket socket) {

        Connection c = new Connection(this, socket, State.RECEIVING, false, false, getQueryPolicies());

        if (isEncrypted()) {

            c.setEncrypted(isEncrypted(), getEncryptionKey());
        }

        return c;
    }

    /**
     * Adds a {@code Packet} to the list of {@code Packets} that will be combined the next time this client {@code Connection} sets itself to the {@code SENDING} state
     *
     * @param p              The {@code Packet} to add
     * @param expectResponse This parameter has no effect on this {@code HandshakeClient}
     */
    @Override
    public void sendPacket(Packet p, boolean expectResponse) {

        waiting.add(p);
    }

    /**
     * Fires the {@code onConnect()} method in all of the {@code ClientListener}
     * objects attached to the {@code HandshakeClient}
     *
     * @param packet The {@code Packet} to pass to the listener
     */
    @Override
    public void fireListenerOnConnect(Packet packet) {

        super.fireListenerOnConnect(packet);

        Packet toSend = new Packet();

        if (waiting.size() > 0) {

            for (Packet p : waiting) {

                toSend.putData(p.getData());

                if (p.isVital()) {

                    toSend.setVital(true);
                }
            }
        }

        getConnection().sendPacket(toSend, true);
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

        if (waiting.size() > 0) {

            for (Packet p : waiting) {

                toSend.putData(p.getData());

                if (p.isVital()) {

                    toSend.setVital(true);
                }
            }
        }

        connection.sendPacket(toSend, true);
    }
}
