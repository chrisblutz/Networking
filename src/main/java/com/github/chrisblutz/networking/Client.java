package com.github.chrisblutz.networking;

import com.github.chrisblutz.networking.debugging.Debugger;
import com.github.chrisblutz.networking.packets.Packet;
import com.github.chrisblutz.networking.packets.encryption.EncryptionKey;
import com.github.chrisblutz.networking.query.QueryPolicy;
import com.github.chrisblutz.networking.query.QueryType;
import com.github.chrisblutz.networking.sockets.Connection;
import com.github.chrisblutz.networking.states.State;
import com.github.chrisblutz.networking.utils.PacketKeys;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This class represents the client-side portion of a server &lt;-&gt; client
 * relationship.
 *
 * @author Christopher Lutz
 */
public class Client extends ClientListenable {

    private int port;
    private String ip;
    private String clientName;
    private Socket socket;

    private boolean encrypted = false;
    private EncryptionKey encryptionKey = null;

    private Connection connection = Connection.getUninitializedConnection();

    private boolean open = false;

    private Map<QueryType, QueryPolicy> policies = new ConcurrentHashMap<QueryType, QueryPolicy>();

    /**
     * Creates a {@code Client} instance that is set up to connect to the
     * specified port on the specified IP
     *
     * @param ip         The IP of the server to connect to when {@code connect()} is
     *                   called
     * @param port       The port of the server to connect to when {@code connect()} is
     *                   called
     * @param clientName The name of this {@code Client}
     */
    public Client(String ip, int port, String clientName) {

        this.ip = ip;
        this.port = port;
        this.clientName = clientName;

        if (Debugger.isEnabled()) {

            Debugger.registerListenable(this);
        }
    }

    /**
     * Sets the {@code QueryPolicy} for the specified {@code QueryType} on this {@code Client}
     *
     * @param type   The {@code QueryType} to set the policy for
     * @param policy The {@code QueryPolicy} to set
     */
    public void setQueryPolicy(QueryType type, QueryPolicy policy) {

        policies.put(type, policy);
    }

    /**
     * Gets all of the {@code QueryPolicies} for this {@code Client}
     *
     * @return A {@code Map} containing all of the {@code QueryPolicies} attached to this {@code Client} and their respective {@code QueryTypes}
     */
    public Map<QueryType, QueryPolicy> getQueryPolicies() {

        return policies;
    }

    /**
     * Gets the {@code QueryPolicy} for the specified {@code QueryType}
     *
     * @param type The {@code QueryType} to retrieve the policy for
     * @return The {@code QueryPolicy} associated with the specified {@code QueryType}
     */
    public QueryPolicy getQueryPolicy(QueryType type) {

        return getQueryPolicies().get(type);
    }

    /**
     * Retrieves the name of this {@code Client} object
     *
     * @return The {@code Client}'s name
     */
    public String getClientName() {

        return clientName;
    }

    /**
     * Returns the IP that this {@code Client} will connect to when
     * {@code connect()} is called
     *
     * @return The server IP to connect to
     */
    public String getIp() {

        return ip;
    }

    /**
     * Returns the port that this {@code Client} will connect to when
     * {@code connect()} is called
     *
     * @return The server port to connect to
     */
    public int getPort() {

        return port;
    }

    /**
     * Sets this {@code Client}'s {@code Connection} to be encrypted
     *
     * @param encrypted     Whether or not to encrypt the {@code Client}
     * @param encryptionKey The {@code EncryptionKey} to use for the encryption
     */
    public void setEncrypted(boolean encrypted, EncryptionKey encryptionKey) {

        this.encrypted = encrypted;
        this.encryptionKey = encryptionKey;
    }

    /**
     * Gets whether or not this {@code Client}'s {@code Connection} is encrypted
     *
     * @return Whether or not this {@code Client} is encrypted
     */
    public boolean isEncrypted() {

        return encrypted;
    }

    /**
     * Retrieves the {@code EncryptionKey} used by this {@code Client} for encryption
     *
     * @return This {@code Client}'s {@code EncryptionKey}
     */
    public EncryptionKey getEncryptionKey() {

        return encryptionKey;
    }

    /**
     * Attempts to connect this {@code Client} to a server on the IP and port
     * specified
     *
     * @throws UnknownHostException If the given host IP could not be determined
     * @throws IOException          If an I/O error occurs while connecting the {@code Socket} of
     *                              the {@code Client}
     */
    public void connect() throws UnknownHostException, IOException {

        socket = new Socket(ip, port);

        connection = makeConnection(socket);

        if (Debugger.isEnabled()) {

            Debugger.updateListenable(this);
        }

        open = true;
    }

    protected Connection makeConnection(Socket socket) {

        Connection c = new Connection(this, socket,
                this.getDefaultConnectionState() == null ? State.RECEIVING
                        : this.getDefaultConnectionState(), false, policies);

        if (isEncrypted() || getEncryptionKey() != null) {

            c.setEncrypted(encrypted, encryptionKey);
        }

        return c;
    }

    /**
     * Checks if this {@code Client} is currently open and connected
     *
     * @return Whether or not this {@code Client} is open and connected
     */
    public boolean isOpen() {

        return open && !socket.isClosed();
    }

    /**
     * Makes the {@code Client} set itself back to the {@code Receiving} state
     */
    public void setToReceive() {

        this.connection.setToReceive();
    }

    /**
     * Makes the {@code Client} set itself back to the {@code Sending} state
     */
    public void setToSend() {

        this.connection.setToSend();
    }

    /**
     * Attempts to close the connection to the server
     *
     * @throws IOException If an I/O error occurs while disconnecting this
     *                     {@code Client}
     */
    public void close() throws IOException {

        open = false;

        if (Debugger.isEnabled()) {

            Debugger.updateListenable(this);
        }

        connection.close();
    }

    /**
     * Gets the {@code Connection} object representing the connection between
     * this {@code Client} and a server
     *
     * @return The {@code Connection} object for this Client
     */
    public Connection getConnection() {

        return connection;
    }

    /**
     * Sends a {@code Packet} across the connection to the server on the
     * receiving end
     *
     * @param p              The {@code Packet} to send
     * @param expectResponse Whether or not the {@code Connection} should wait for a
     *                       response (decides whether or not to timeout the {@code read()}
     *                       calls
     */
    public void sendPacket(Packet p, boolean expectResponse) {

        if (isOpen() && connection != null) {

            connection.sendPacket(p, expectResponse);
        }
    }

    public static Packet addBranchCommandPacketData(Packet packet, String branchId){

        packet.putData(PacketKeys.BRANCH_CONNECTION, branchId);

        return packet;
    }
}
