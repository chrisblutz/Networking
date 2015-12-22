package com.github.lutzblox;

import com.github.lutzblox.exceptions.Errors;
import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.query.QueryPolicy;
import com.github.lutzblox.query.QueryType;
import com.github.lutzblox.sockets.Connection;
import com.github.lutzblox.sockets.ConnectionBundle;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This class represents the server-side portion of a server &lt;-&gt; client
 * relationship.
 *
 * @author Christopher Lutz
 */
public class Server extends ServerListenable {

    public static final class ConnectionKeys {

        public static final String SERVER_NAME = "server:name";
        public static final String MAX_CONNECTIONS = "server:maxcon";
        public static final String CURRENTLY_CONNECTED = "server:curcon";
    }

    private int port, maxConnect;
    private String serverName;
    private ServerSocket socket = null;

    private Thread incoming = null, checkFailed = null;
    private ConnectionBundle connections = new ConnectionBundle();

    private boolean failed = false, open = false;

    private long failCheck;

    private Map<QueryType, QueryPolicy> policies = new ConcurrentHashMap<QueryType, QueryPolicy>();

    /**
     * Create a new {@code Server} instance with the specified parameters
     *
     * @param port       The port to open the {@code Server} on
     * @param serverName The name of this {@code Server}
     */
    public Server(int port, String serverName) {

        this(port, serverName, 20, 5000);
    }

    /**
     * Create a new {@code Server} instance with the specified parameters
     *
     * @param port           The port to open the {@code Server} on
     * @param serverName     The name of this {@code Server}
     * @param maxConnections The maximum number of {@code Client} connections to be
     *                       accepted by this {@code Server}
     */
    public Server(int port, String serverName, int maxConnections) {

        this(port, serverName, maxConnections, 5000);
    }

    /**
     * Create a new {@code Server} instance with the specified parameters
     *
     * @param port       The port to open the {@code Server} on
     * @param serverName The name of this {@code Server}
     * @param failCheck  The loop delay in milliseconds to check for {@code Clients}
     *                   that have disconnected or errored
     */
    public Server(int port, String serverName, long failCheck) {

        this(port, serverName, 20, failCheck);
    }

    /**
     * Create a new {@code Server} instance with the specified parameters
     *
     * @param port           The port to open the {@code Server} on
     * @param serverName     The name of this {@code Server}
     * @param maxConnections The maximum number of {@code Client} connections to be
     *                       accepted by this {@code Server}
     * @param failCheck      The loop delay in milliseconds to check for {@code Clients}
     *                       that have disconnected or errored
     */
    public Server(int port, String serverName, int maxConnections,
                  long failCheck) {

        this.port = port;
        this.serverName = serverName;
        this.maxConnect = maxConnections;
        this.failCheck = failCheck;
    }

    public void setQueryPolicy(QueryType type, QueryPolicy policy) {

        policies.put(type, policy);

        for (Connection c : connections) {

            c.setQueryPolicy(type, policy);
        }
    }

    public Map<QueryType, QueryPolicy> getQueryPolicies() {

        return policies;
    }

    public QueryPolicy getQueryPolicy(QueryType type) {

        return getQueryPolicies().get(type);
    }

    /**
     * Gets the name attached to this {@code Server}
     *
     * @return The server name of this {@code Server}
     */
    public String getServerName() {

        return serverName;
    }

    /**
     * Gets the port that this {@code Server} will open onto
     *
     * @return This {@code Server}'s port
     */
    public int getPort() {

        return port;
    }

    /**
     * Attempts to open this {@code Server} onto the specified port
     *
     * @throws IOException If an I/O error occurs while starting the {@code Server}
     */
    public void start() throws IOException {

        socket = new ServerSocket(port);

        incoming = new Thread() {

            @Override
            public void run() {

                try {

                    while (open) {

                        if (connections.size() < maxConnect) {

                            Socket connect = socket.accept();

                            Connection connection = makeConnection(connect);

                            connections.add(connection);
                        }
                    }

                } catch (Exception e) {

                    Server.this.report(e);

                    failed = true;

                    try {

                        close();

                    } catch (IOException e1) {

                        Server.this.report(e1);
                    }
                }
            }
        };
        incoming.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread arg0, Throwable arg1) {

                Errors.threadErrored(arg0.getName(), Server.this, arg1);
            }
        });
        incoming.setName("Incoming Connection Monitor: Server '"
                + getServerName() + "'");
        open = true;
        incoming.start();
        checkFailed = new Thread() {

            @Override
            public void run() {

                try {

                    while (open) {

                        try {

                            Thread.sleep(failCheck);

                        } catch (Exception e) {
                        }

                        List<Connection> toRem = new ArrayList<Connection>();

                        for (Connection c : connections) {

                            if (c.isRemoteClosed()) {

                                toRem.add(c);
                            }
                        }

                        for (Connection c : toRem) {

                            connections.remove(c);
                            Server.this.fireListenerOnClientFailure(c);
                        }
                    }

                } catch (Exception e) {

                    failed = true;

                    Server.this.report(e);

                    try {

                        close();

                    } catch (IOException e1) {

                        Server.this.report(e1);
                    }
                }
            }
        };
        checkFailed.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread arg0, Throwable arg1) {

                Errors.threadErrored(arg0.getName(), Server.this, arg1);
            }
        });
        checkFailed.setName("Failed Client Monitor: Server '" + getServerName()
                + "'");
        checkFailed.start();

        open = true;
    }

    protected Connection makeConnection(Socket socket) {

        return new Connection(Server.this, socket,
                Server.this.getDefaultConnectionState() == null ? com.github.lutzblox.states.State.SENDING : Server.this.getDefaultConnectionState(),
                true, policies);
    }

    public Packet getInformationPacket() {

        Packet p = new Packet();
        p.putData(ConnectionKeys.SERVER_NAME, serverName);
        p.putData(ConnectionKeys.MAX_CONNECTIONS, maxConnect);
        p.putData(ConnectionKeys.CURRENTLY_CONNECTED, connections.size());

        return p;
    }

    /**
     * Checks if this {@code Server} is currently open
     *
     * @return Whether or not this {@code Server} is open
     */
    public boolean isOpen() {

        return open;
    }

    /**
     * Checks if this {@code Server} has failed/errored
     *
     * @return Whether or not this {@code Server} has failed/errored
     */
    public boolean hasFailed() {

        return failed;
    }

    /**
     * Attempts to close this {@code Server} and disconnect all {@code Clients}
     *
     * @throws IOException If an I/O error occurs while shutting down this
     *                     {@code Server}
     */
    public void close() throws IOException {

        open = false;
        incoming.interrupt();
        checkFailed.interrupt();

        for (Connection c : connections) {

            c.close();
        }
    }

    /**
     * Gets all connections to this {@code Server}
     *
     * @return A {@code ConnectionBundle} containing all the {@code Connection}s currently connected
     * to this {@code Server}
     */
    public ConnectionBundle getConnections() {

        return connections;
    }

    /**
     * Gets a {@code Connection} for a specified IP
     *
     * @param ip The IP to get a {@code Connection} for
     * @return The {@code Connection} for the specified IP
     */
    public Connection getConnectionForIp(String ip) {

        for (Connection c : connections) {

            if (c.getIp().equals(ip)) {

                return c;
            }
        }

        return null;
    }

    /**
     * Sends a {@code Packet} across all connections to the clients on the
     * receiving ends
     *
     * @param p              The {@code Packet} to send
     * @param expectResponse Whether or not the {@code Connection} should wait for a
     *                       response (decides whether or not to timeout the {@code read()}
     *                       calls
     */
    public void sendPacket(Packet p, boolean expectResponse) {

        if (isOpen() && !hasFailed()) {

            for (Connection c : connections) {

                if (c != null) {

                    c.sendPacket(p, expectResponse);
                }
            }
        }
    }
}
