package com.github.lutzblox.databases;

import com.github.lutzblox.Server;
import com.github.lutzblox.databases.saving.SaveMethod;
import com.github.lutzblox.exceptions.reporters.ErrorReporter;
import com.github.lutzblox.listeners.ServerListener;
import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.sockets.Connection;
import com.github.lutzblox.utils.ExtendedMap;

import java.io.IOException;


/**
 * This class represents the client-side portion of a database server &lt;-&gt; client
 * relationship.
 *
 * @deprecated This class will be removed in 1.2 in favor of more reliable SQL database support
 *
 * @author Christopher Lutz
 */
public class DatabaseServer {

    /**
     * Key representing the name of a database in a {@code Packet}
     */
    public static final String DATABASE_NAME_KEY = "database-name";

    private ExtendedMap data = new ExtendedMap();

    private Server server;

    private SaveMethod saveMethod = null;

    /**
     * Create a new {@code DatabaseServer} instance with the specified
     * parameters
     *
     * @param port         The port to open the {@code DatabaseServer} on
     * @param databaseName The name of this {@code DatabaseServer}
     */
    public DatabaseServer(int port, String databaseName) {

        this(new Server(port, databaseName));
    }

    /**
     * Create a new {@code DatabaseServer} instance with the specified
     * parameters
     *
     * @param port           The port to open the {@code DatabaseServer} on
     * @param databaseName   The name of this {@code DatabaseServer}
     * @param maxConnections The maximum number of {@code Client} connections to be
     *                       accepted by this {@code DatabaseServer}
     */
    public DatabaseServer(int port, String databaseName, int maxConnections) {

        this(new Server(port, databaseName, maxConnections));
    }

    /**
     * Create a new {@code DatabaseServer} instance with the specified
     * parameters
     *
     * @param port         The port to open the {@code DatabaseServer} on
     * @param databaseName The name of this {@code DatabaseServer}
     * @param failCheck    The loop delay in milliseconds to check for {@code Clients}
     *                     that have disconnected or errored
     */
    public DatabaseServer(int port, String databaseName, long failCheck) {

        this(new Server(port, databaseName, failCheck));
    }

    /**
     * Create a new {@code DatabaseServer} instance with the specified
     * parameters
     *
     * @param port           The port to open the {@code DatabaseServer} on
     * @param databaseName   The name of this {@code DatabaseServer}
     * @param maxConnections The maximum number of {@code Client} connections to be
     *                       accepted by this {@code DatabaseServer}
     * @param failCheck      The loop delay in milliseconds to check for {@code Clients}
     *                       that have disconnected or errored
     */
    public DatabaseServer(int port, String databaseName, int maxConnections,
                          long failCheck) {

        this(new Server(port, databaseName, maxConnections, failCheck));
    }

    private DatabaseServer(Server s) {

        this.server = s;

        server.addNetworkListener(new ServerListener() {

            @Override
            public void onReceive(Connection connection, Packet packet) {

                if (packet.hasData(Request.REQUEST_KEY)) {

                    String key = (String) packet.getData(Request.REQUEST_KEY);

                    Packet response = new Packet();
                    response.putData(Response.RESPONSE_KEY, data.get(key));

                    connection.sendPacket(response, false);

                } else if (packet.hasData(PutRequest.PUT_REQUEST_KEY_KEY)
                        && packet.hasData(PutRequest.PUT_REQUEST_VALUE_KEY)) {

                    String key = (String) packet
                            .getData(PutRequest.PUT_REQUEST_KEY_KEY);
                    Object value = packet
                            .getData(PutRequest.PUT_REQUEST_VALUE_KEY);

                    data.put(value.getClass(), key, value);

                    if (saveMethod != null) {

                        saveMethod.save(data, server);
                    }

                    connection.setToReceive();

                } else if (packet.hasData(DeleteRequest.DELETE_REQUEST_KEY_KEY)) {

                    String key = (String) packet
                            .getData(DeleteRequest.DELETE_REQUEST_KEY_KEY);

                    data.removeKey(key);

                    if (saveMethod != null) {

                        saveMethod.save(data, server);
                    }

                    connection.setToReceive();

                } else if (packet.hasData(Command.COMMAND_KEY)) {

                    String command = (String) packet
                            .getData(Command.COMMAND_KEY);

                    if (command.equalsIgnoreCase("clear")) {

                        data.clear();
                    }

                    connection.setToReceive();
                }
            }

            @Override
            public Packet onConnect(Connection c, Packet data) {

                data.putData(DATABASE_NAME_KEY, server.getServerName());

                return data;
            }

            @Override
            public void onTimeout(Connection connection) {

            }

            @Override
            public void onClientFailure(Connection c) {

            }
        });
    }

    /**
     * Sets the {@code SaveMethod} used by this {@code DatabaseServer} to save
     * data
     *
     * @param method The {@code SaveMethod} to use
     */
    public void setSaveMethod(SaveMethod method) {

        this.saveMethod = method;
    }

    /**
     * Gets the {@code SaveMethod} used by this {@code DatabaseServer} to save
     * data
     *
     * @return The {@code SaveMethod} used
     */
    public SaveMethod getSaveMethod() {

        return saveMethod;
    }

    /**
     * Loads database data through the {@code SaveMethod}
     */
    public void loadDatabase() {

        if (saveMethod != null) {

            data.putAll(saveMethod.load(server));
        }
    }

    /**
     * Saves database data through the {@code SaveMethod}
     */
    public void saveDatabase() {

        if (saveMethod != null) {

            saveMethod.save(data, server);
        }
    }

    /**
     * Gets the port that this {@code DatabaseServer} will open onto
     *
     * @return This {@code DatabaseServer}'s port
     */
    public int getPort() {

        return server.getPort();
    }

    /**
     * Gets the name attached to this {@code DatabaseServer}
     *
     * @return The database name of this {@code DatabaseServer}
     */
    public String getDatabaseName() {

        return server.getServerName();
    }

    /**
     * Puts a value into the database
     *
     * @param key   The key of the data
     * @param value The value of the data
     */
    public void putData(String key, Object value) {

        data.put(value.getClass(), key, value);
    }

    /**
     * Checks if the database contains a value with the specified key
     *
     * @param key The key to check for
     * @return Whether or not the database contains a value for the specified
     * key
     */
    public boolean hasData(String key) {

        return data.containsKey(key);
    }

    /**
     * Gets the value associated with the specified key
     *
     * @param key The key to find the value for
     * @return The value for the key
     */
    public Object getData(String key) {

        return data.get(key);
    }

    /**
     * Clears the data from the database
     */
    public void clear() {

        data.clear();
    }

    /**
     * Checks if this {@code DatabaseServer} is currently open
     *
     * @return Whether or not this {@code DatabaseServer} is open
     */
    public boolean isOpen() {

        return server.isOpen();
    }

    /**
     * Checks if this {@code DatabaseServer} has failed/errored
     *
     * @return Whether or not this {@code DatabaseServer} has failed/errored
     */
    public boolean hasFailed() {

        return server.hasFailed();
    }

    /**
     * Attaches an {@code ErrorListener} to this {@code DatabaseServer}
     *
     * @param reporter The {@code ErrorReporter} to add
     */
    public void addErrorReporter(ErrorReporter reporter) {

        server.addErrorReporter(reporter);
    }

    /**
     * Gets all of the {@code ErrorReporters} attached to this
     * {@code DatabaseServer}
     *
     * @return An {@code ErrorReporter[]} containing all {@code ErrorReporters}
     * attached to this {@code DatabaseServer}
     */
    public ErrorReporter[] getErrorReporters() {

        return server.getErrorReporters();
    }

    /**
     * Reports an error ({@code Throwable}) through the {@code ErrorReporters}
     * attached to this {@code DatabaseServer}
     *
     * @param t The {@code Throwable} to report
     */
    public void report(Throwable t) {

        server.report(t);
    }

    /**
     * Attempts to open this {@code DatabaseServer} onto the specified port
     *
     * @throws IOException If an I/O error occurs while starting the
     *                     {@code DatabaseServer}
     */
    public void start() throws IOException {

        server.start();
    }

    /**
     * Attempts to close this {@code DatabaseServer} and disconnect all
     * {@code Clients}
     *
     * @throws IOException If an I/O error occurs while shutting down this
     *                     {@code DatabaseServer}
     */
    public void close() throws IOException {

        server.close();
    }
}
