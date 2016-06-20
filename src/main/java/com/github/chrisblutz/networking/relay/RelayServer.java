package com.github.chrisblutz.networking.relay;

import com.github.chrisblutz.networking.Server;
import com.github.chrisblutz.networking.exceptions.reporters.ErrorReporter;
import com.github.chrisblutz.networking.listeners.ServerListener;
import com.github.chrisblutz.networking.packets.Packet;
import com.github.chrisblutz.networking.relay.listeners.RelayListener;
import com.github.chrisblutz.networking.sockets.Connection;
import com.github.chrisblutz.networking.sockets.ConnectionBundle;
import com.github.chrisblutz.networking.states.State;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A class representing the server-side portion of a relay connection
 *
 * @author Christopher Lutz
 */
public class RelayServer {

    private Server server;

    private List<RelayListener> listeners = new ArrayList<RelayListener>();

    private List<ConnectionGroup> groups = new ArrayList<ConnectionGroup>();

    private boolean autoForward = true;

    /**
     * Create a new {@code RelayServer} instance with the specified parameters
     *
     * @param port       The port to open the {@code RelayServer} on
     * @param serverName The name of this {@code RelayServer}
     */
    public RelayServer(int port, String serverName) {

        this(new Server(port, serverName));
    }

    /**
     * Create a new {@code RelayServer} instance with the specified parameters
     *
     * @param port           The port to open the {@code RelayServer} on
     * @param serverName     The name of this {@code RelayServer}
     * @param maxConnections The maximum number of {@code Client} connections to be
     *                       accepted by this {@code RelayServer}
     */
    public RelayServer(int port, String serverName, int maxConnections) {

        this(new Server(port, serverName, maxConnections));
    }

    /**
     * Create a new {@code RelayServer} instance with the specified parameters
     *
     * @param port       The port to open the {@code RelayServer} on
     * @param serverName The name of this {@code RelayServer}
     * @param failCheck  The loop delay in milliseconds to check for {@code Clients}
     *                   that have disconnected or errored
     */
    public RelayServer(int port, String serverName, long failCheck) {

        this(new Server(port, serverName, failCheck));
    }

    /**
     * Create a new {@code RelayServer} instance with the specified parameters
     *
     * @param port           The port to open the {@code RelayServer} on
     * @param serverName     The name of this {@code RelayServer}
     * @param maxConnections The maximum number of {@code Client} connections to be
     *                       accepted by this {@code RelayServer}
     * @param failCheck      The loop delay in milliseconds to check for {@code Clients}
     *                       that have disconnected or errored
     */
    public RelayServer(int port, String serverName, int maxConnections,
                       long failCheck) {

        this(new Server(port, serverName, maxConnections, failCheck));
    }

    private RelayServer(Server server) {

        this.server = server;
        server.setDefaultConnectionState(State.MUTUAL);
        server.addNetworkListener(new ServerListener() {

            @Override
            public void onReceive(Connection connection, Packet packet) {

                for (RelayListener l : listeners) {

                    l.onReceive(RelayServer.this, connection, packet);
                }

                if (autoForward) {

                    ConnectionGroup.sendPacket(connection, packet, groups.toArray(new ConnectionGroup[groups.size()]));
                }
            }

            @Override
            public void onTimeout(Connection connection) {

                for (RelayListener l : listeners) {

                    l.onTimeout(RelayServer.this, connection);
                }
            }

            @Override
            public Packet onConnect(Connection c, Packet data) {

                for (RelayListener l : listeners) {

                    data = l.onConnect(RelayServer.this, c, data);
                }

                return data;
            }

            @Override
            public void onClientFailure(Connection c) {

                for (RelayListener l : listeners) {

                    l.onClientFailure(RelayServer.this, c);
                }

                ungroupAll(c);
            }
        });
    }

    /**
     * Attaches a {@code RelayListener} to this {@code RelayServer}
     *
     * @param listener The {@code RelayListener} to add
     */
    public void addRelayListener(RelayListener listener) {

        listeners.add(listener);
    }

    /**
     * Gets all {@code RelayListeners} attached to this {@code RelayServer}
     *
     * @return A {@code RelayListener[]} containing all {@code RelayListeners}
     * attached to this {@code RelayServer}
     */
    public RelayListener[] getRelayListeners() {

        return listeners.toArray(new RelayListener[]{});
    }

    /**
     * Attaches an {@code ErrorListener} to this {@code RelayServer}
     *
     * @param reporter The {@code ErrorReporter} to add
     */
    public void addErrorReporter(ErrorReporter reporter) {

        server.addErrorReporter(reporter);
    }

    /**
     * Gets all of the {@code ErrorReporters} attached to this
     * {@code RelayServer}
     *
     * @return An {@code ErrorReporter[]} containing all {@code ErrorReporters}
     * attached to this {@code RelayServer}
     */
    public ErrorReporter[] getErrorReporters() {

        return server.getErrorReporters();
    }

    /**
     * Reports an error ({@code Throwable}) through the {@code ErrorReporters}
     * attached to this {@code RelayServer}
     *
     * @param t The {@code Throwable} to report
     */
    public void report(Throwable t) {

        server.report(t);
    }

    public void setAutomaticForwarding(boolean autoForward){

        this.autoForward = autoForward;
    }

    public boolean getAutomaticForwarding(){

        return autoForward;
    }

    /**
     * Groups {@code Connections} into a {@code ConnectionGroup}
     *
     * @param id          The id to be used for the resulting {@code ConnectionGroup}
     * @param connection  The first {@code Connection}
     * @param connections Any more {@code Connections}
     */
    public void group(String id, Connection connection,
                      Connection... connections) {

        ConnectionGroup group = new ConnectionGroup(id);
        group.addConnection(connection);
        for (Connection c : connections) {

            group.addConnection(c);
        }

        groups.add(group);
    }

    /**
     * Adds {@code Connections} to a {@code ConnectionGroup}
     *
     * @param group       The {@code ConnectionGroup} to add to
     * @param connection  The first {@code Connection}
     * @param connections Any more {@code Connections}
     */
    public void group(ConnectionGroup group, Connection connection,
                      Connection... connections) {

        group.addConnection(connection);
        for (Connection c : connections) {

            group.addConnection(c);
        }
    }

    /**
     * Removes {@code Connections} from a {@code ConnectionGroup}
     *
     * @param group       The {@code ConnectionGroup} to remove from
     * @param connection  The first {@code Connection} to remove
     * @param connections Any more {@code Connections} to remove
     */
    public void ungroup(ConnectionGroup group, Connection connection,
                        Connection... connections) {

        group.removeConnection(connection);
        for (Connection c : connections) {

            group.removeConnection(c);
        }

        if (group.size() < 2) {

            groups.remove(group);
        }
    }

    /**
     * Removes {@code Connections} from all {@code ConnectionGroups}
     *
     * @param connection  The first {@code Connection} to remove
     * @param connections Any more {@code Connections} to remove
     */
    public void ungroupAll(Connection connection, Connection... connections) {

        for (ConnectionGroup g : groups.toArray(new ConnectionGroup[]{})) {

            ungroup(g, connection, connections);
        }
    }

    /**
     * Checks if a {@code ConnectionGroup} with the specified id exists
     *
     * @param id The id to check for
     * @return Whether or not there is a {@code ConnectionGroup} with the
     * specified id
     */
    public boolean hasGroup(String id) {

        for (ConnectionGroup group : groups) {

            if (group.getId().equals(id)) {

                return true;
            }
        }

        return false;
    }

    /**
     * Gets a {@code ConnectionGroup} that has the specified id
     *
     * @param id The id to retrieve
     * @return The {@code ConnectionGroup} with the specified id
     */
    public ConnectionGroup getGroupForId(String id) {

        for (ConnectionGroup group : groups) {

            if (group.getId().equals(id)) {

                return group;
            }
        }

        return null;
    }

    /**
     * Gets all {@code ConnectionGroups} associated with this
     * {@code RelayServer}
     *
     * @return A {@code ConnectionGroup[]} containing all
     * {@code ConnectionGroups} associated with this {@code RelayServer}
     */
    public ConnectionGroup[] getGroups() {

        return groups.toArray(new ConnectionGroup[]{});
    }

    /**
     * Removes a {@code ConnectionGroup} from this {@code RelayServer}
     *
     * @param id The id of the {@code ConnectionGroup} to remove
     */
    public void removeGroup(String id) {

        ConnectionGroup g = getGroupForId(id);

        if (g != null) {

            groups.remove(g);
        }
    }

    /**
     * Gets the name attached to this {@code RelayServer}
     *
     * @return The server name of this {@code RelayServer}
     */
    public String getServerName() {

        return server.getServerName();
    }

    /**
     * Gets the port that this {@code RelayServer} will open onto
     *
     * @return This {@code RelayServer}'s port
     */
    public int getPort() {

        return server.getPort();
    }

    /**
     * Checks if this {@code RelayServer} is currently open
     *
     * @return Whether or not this {@code RelayServer} is open
     */
    public boolean isOpen() {

        return server.isOpen();
    }

    /**
     * Checks if this {@code RelayServer} has failed/errored
     *
     * @return Whether or not this {@code RelayServer} has failed/errored
     */
    public boolean hasFailed() {

        return server.hasFailed();
    }

    /**
     * Attempts to open this {@code RelayServer} onto the specified port
     *
     * @throws IOException If an I/O error occurs while starting the {@code RelayServer}
     */
    public void start() throws IOException {

        server.start();
    }

    /**
     * Attempts to close this {@code RelayServer} and disconnect all
     * {@code Clients}
     *
     * @throws IOException If an I/O error occurs while shutting down this
     *                     {@code RelayServer}
     */
    public void close() throws IOException {

        server.close();
    }

    /**
     * Gets all connections to this {@code RelayServer}
     *
     * @return A {@code Connection[]} containing references to all connections
     * on this {@code RelayServer}
     */
    public ConnectionBundle getConnections() {

        return server.getConnections();
    }

    /**
     * Gets a {@code Connection} for a specified IP
     *
     * @param ip The IP to get a {@code Connection} for
     * @return The {@code Connection} for the specified IP
     */
    public Connection getConnectionForIp(String ip) {

        return server.getConnectionForIp(ip);
    }
}
