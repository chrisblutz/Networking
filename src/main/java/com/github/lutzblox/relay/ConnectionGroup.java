package com.github.lutzblox.relay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.sockets.Connection;


/**
 * A class representing a group of {@code Connections} for use in
 * {@code RelayServers}
 *
 * @author Christopher Lutz
 */
public class ConnectionGroup implements Iterable<Connection> {

    private List<Connection> connections = new ArrayList<Connection>();
    private String id;

    /**
     * Creates a {@code ConnectionGroup} with the specified id
     *
     * @param id The id of the {@code ConnectionGroup}
     */
    public ConnectionGroup(String id) {

        this.id = id;
    }

    /**
     * Gets the id used by this {@code ConnectionGroup}
     *
     * @return The id of this {@code ConnectionGroup}
     */
    public String getId() {

        return id;
    }

    /**
     * Adds a {@code Connection} to this {@code ConnectionGroup}
     *
     * @param c The {@code Connection} to add
     */
    public void addConnection(Connection c) {

        connections.add(c);
    }

    /**
     * Checks if this {@code ConnectionGroup} contains a specified
     * {@code Connection}
     *
     * @param c The {@code Connection} to check for
     * @return Whether or not this {@code ConnectionGroup} contains the
     * {@code Connection}
     */
    public boolean containsConnection(Connection c) {

        return connections.contains(c);
    }

    /**
     * Gets all {@code Connections} attached to this {@code ConnectionGroup}
     *
     * @return A {@code Connection[]} containing all {@code Connections}
     * associated with this {@code ConnectionGroup}
     */
    public Connection[] getConnections() {

        return connections.toArray(new Connection[]{});
    }

    /**
     * Removes a {@code Connection} from this {@code ConnectionGroup}
     *
     * @param c The {@code Connection} to remove
     */
    public void removeConnection(Connection c) {

        connections.remove(c);
    }

    /**
     * Gets the number of {@code Connections} attached to this
     * {@code ConnectionGroup}
     *
     * @return The size of this {@code ConnectionGroup}
     */
    public int size() {

        return connections.size();
    }

    /**
     * Sends a {@code Packet} to all {@code Connections} in the group except the
     * sender
     *
     * @param sender The {@code Connection} that sent this {@code Packet}
     * @param data   The {@code Packet} to send
     */
    public void sendPacket(Connection sender, Packet data) {

        for (Connection c : connections) {

            if (c != sender) {

                c.sendPacket(data, false);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Connection> iterator() {

        return connections.iterator();
    }

    /**
     * Sends a {@code Packet} to all {@code Connections} in all groups
     * containing the sender {@code Connection}
     *
     * @param sender The {@code Connection} that sent this {@code Packet}
     * @param data   The {@code Packet} to send
     * @param groups All {@code ConnectionGroups} to check through. If the group
     *               contains the sender {@code Connection}, the {@code Packet}
     *               will be sent to all other {@code Connections} in that group.
     */
    public static void sendPacket(Connection sender, Packet data,
                                  ConnectionGroup[] groups) {

        for (ConnectionGroup g : groups) {

            if (g != null) {

                if (g.containsConnection(sender)) {

                    g.sendPacket(sender, data);
                }
            }
        }
    }
}
