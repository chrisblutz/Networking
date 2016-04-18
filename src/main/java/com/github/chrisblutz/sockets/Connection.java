package com.github.chrisblutz.sockets;

import com.github.chrisblutz.ClientListenable;
import com.github.chrisblutz.Listenable;
import com.github.chrisblutz.Server;
import com.github.chrisblutz.ServerListenable;
import com.github.chrisblutz.exceptions.Errors;
import com.github.chrisblutz.exceptions.NetworkException;
import com.github.chrisblutz.listeners.branching.BranchRegistry;
import com.github.chrisblutz.listeners.branching.BranchingServerListener;
import com.github.chrisblutz.packets.Packet;
import com.github.chrisblutz.packets.PacketReader;
import com.github.chrisblutz.packets.PacketWriter;
import com.github.chrisblutz.packets.encryption.EncryptedPacketReader;
import com.github.chrisblutz.packets.encryption.EncryptedPacketWriter;
import com.github.chrisblutz.packets.encryption.EncryptionKey;
import com.github.chrisblutz.query.Query;
import com.github.chrisblutz.query.QueryPolicy;
import com.github.chrisblutz.query.QueryStatus;
import com.github.chrisblutz.query.QueryType;
import com.github.chrisblutz.states.State;
import com.github.chrisblutz.utils.PacketKeys;

import java.io.*;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * A wrapper for a {@code Socket}, used to send/receive {@code Packets}
 *
 * @author Christopher Lutz
 */
public class Connection {

    private Listenable listenable;

    private Socket socket;

    private Thread listener, connCheck;

    private State mainState, state, nextState = null;

    private List<Packet> dropped = new ArrayList<Packet>();

    private List<Packet> vitalDropped = new ArrayList<Packet>();

    private Packet waiting = null;

    private long ping = -1, pingStart = 0, pingTotal = 0, pingTimes = 0;

    private boolean encrypted = false, allowSettingState = true, running = false, serverSide = false, firstReceive = true,
            firstSend = true, shouldRespond = false, pingShouldRespond = false, remoteClosed = false, initialized = false, qrySent = false;

    private int readTimeout = 8000;

    private PacketReader packetReader;
    private PacketWriter packetWriter;
    private EncryptedPacketReader encryptedReader;
    private EncryptedPacketWriter encryptedWriter;

    private Map<QueryType, QueryPolicy> policies = new ConcurrentHashMap<QueryType, QueryPolicy>();

    private Map<String, Query> toQuery = new ConcurrentHashMap<String, Query>();
    private Map<String, Query> queries = new ConcurrentHashMap<String, Query>();
    private Map<String, Object> completedQueries = new ConcurrentHashMap<String, Object>();

    private EncryptionKey encryptionKey = null;

    private boolean branched = false;
    private BranchingServerListener branchingListener = null;

    private static final String EMPTY_PLACEHOLDER = "::REMCL";

    /**
     * Creates a new {@code Connection} with the specified parameters
     *
     * @param listenable The {@code Listenable} object that created this
     *                   {@code Connection}
     * @param socket     The {@code Socket} to wrap in this {@code Connection}
     * @param state      The beginning {@code State} of this {@code Connection}
     * @param serverSide Whether or not this {@code Connection} represents a
     *                   server-side connection
     */
    public Connection(Listenable listenable, Socket socket, State state,
                      boolean serverSide) {

        this(listenable, socket, state, serverSide, true);
    }

    /**
     * Creates a new {@code Connection} with the specified parameters
     *
     * @param listenable        The {@code Listenable} object that created this
     *                          {@code Connection}
     * @param socket            The {@code Socket} to wrap in this {@code Connection}
     * @param state             The beginning {@code State} of this {@code Connection}
     * @param serverSide        Whether or not this {@code Connection} represents a
     *                          server-side connection
     * @param allowSettingState Whether or not {@code setToSend()} or {@code setToReceive()} have any effect on this {@code Connection}'s {@code State}
     * @param policies          The {@code QueryPolicies} to use when this server/client is queried
     */
    public Connection(Listenable listenable, Socket socket, State state, boolean serverSide, boolean allowSettingState, Map<QueryType, QueryPolicy> policies) {

        this(listenable, socket, state, serverSide, allowSettingState);
        this.policies = policies;
    }

    /**
     * Creates a new {@code Connection} with the specified parameters
     *
     * @param listenable The {@code Listenable} object that created this
     *                   {@code Connection}
     * @param socket     The {@code Socket} to wrap in this {@code Connection}
     * @param state      The beginning {@code State} of this {@code Connection}
     * @param serverSide Whether or not this {@code Connection} represents a
     *                   server-side connection
     * @param policies   The {@code QueryPolicies} to use when this server/client is queried
     */
    public Connection(Listenable listenable, Socket socket, State state,
                      boolean serverSide, Map<QueryType, QueryPolicy> policies) {

        this(listenable, socket, state, serverSide, true);
        this.policies = policies;
    }

    /**
     * Creates a new {@code Connection} with the specified parameters
     *
     * @param listenable        The {@code Listenable} object that created this
     *                          {@code Connection}
     * @param socket            The {@code Socket} to wrap in this {@code Connection}
     * @param state             The beginning {@code State} of this {@code Connection}
     * @param serverSide        Whether or not this {@code Connection} represents a
     *                          server-side connection
     * @param allowSettingState Whether or not {@code setToSend()} or {@code setToReceive()} have any effect on this {@code Connection}'s {@code State}
     */
    public Connection(final Listenable listenable, Socket socket, State state, boolean serverSide, boolean allowSettingState) {

        this.initialized = true;
        this.listenable = listenable;
        this.socket = socket;
        this.mainState = state;
        this.state = state;
        this.serverSide = serverSide;
        this.allowSettingState = allowSettingState;

        packetReader = new PacketReader();
        packetWriter = new PacketWriter();
        encryptedReader = new EncryptedPacketReader();
        encryptedReader.setListenable(listenable);
        encryptedWriter = new EncryptedPacketWriter();
        encryptedWriter.setListenable(listenable);

        listener = new Thread() {

            @Override
            public void run() {

                listenerRun();
            }
        };
        listener.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {

                Errors.threadErrored(t.getName(), Connection.this.listenable, e);
            }
        });
        listener.setName("Packet Listener: "
                + (serverSide ? "Server" : "Client") + " on IP " + getIp());
        connCheck = new Thread() {

            @Override
            public void run() {

                Socket socket = Connection.this.socket;

                while (running) {

                    if (socket != null) {

                        if (socket.isClosed() || !socket.isConnected()) {

                            try {

                                listener.interrupt();
                                close();

                            } catch (Exception e) {

                                Errors.genericFatalConnection(listenable, getIp(), getPort(), e);
                            }
                        }
                    }

                    try {

                        Thread.sleep(1000);

                    } catch (Exception e) {

                        // Ignore sleep interruptions
                    }
                }
            }
        };
        connCheck.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {

                Errors.threadErrored(t.getName(), Connection.this.listenable, e);
            }
        });
        connCheck.setName("Connection Check: " + (serverSide ? "Server" : "Client") + " on IP " + getIp());
        running = true;
        shouldRespond = !serverSide;
        listener.start();
        connCheck.start();
    }

    /**
     * Creates an uninitialized Connection with default values<br><br>
     * <p>
     * Default values:<br>
     * - Listenable: {@code null}<br>
     * - Socket: {@code null}<br>
     * - State: {@code MUTUAL}<br>
     * - Side: Client ({@code false})<br>
     */
    private Connection() {

        this.listenable = null;
        this.socket = null;
        this.mainState = State.MUTUAL;
        this.state = State.MUTUAL;
        this.serverSide = false;
    }

    /**
     * Sets the {@code QueryPolicy} to use when this server receives a query of the specified {@code QueryType}
     *
     * @param type   The {@code QueryType} to assign the policy to
     * @param policy The {@code QueryPolicy} to assign to the type
     */
    public void setQueryPolicy(QueryType type, QueryPolicy policy) {

        policies.put(type, policy);
    }

    /**
     * Gets the {@code QueryPolicies} attached to this {@code Connection}
     *
     * @return A {@code Map} of the {@code QueryTypes} and their respective {@code QueryPolicies}
     */
    public Map<QueryType, QueryPolicy> getQueryPolicies() {

        return policies;
    }

    /**
     * Gets the {@code QueryPolicy} attached to this {@code Connection} for the specified {@code QueryType}
     *
     * @param type The {@code QueryType} to retrieve the policy from
     * @return The {@code QueryPolicy} for the specified {@code QueryType}
     */
    public QueryPolicy getQueryPolicy(QueryType type) {

        return getQueryPolicies().get(type);
    }

    /**
     * Sends a {@code Packet} across the connection
     *
     * @param p              The {@code Packet} to send
     * @param expectResponse Whether or not the {@code Connection} should wait for a
     *                       response (decides whether or not to timeout the {@code read()}
     *                       calls
     */
    public void sendPacket(Packet p, boolean expectResponse) {

        if (waiting != null) {

            dropped.add(waiting);

            if (waiting.isVital()) {

                vitalDropped.add(waiting);
            }
        }

        if (p.isEmpty()) {

            p.putData(Packet.EMPTY_PACKET);
        }

        waiting = p;
        this.shouldRespond = expectResponse;
        qrySent = false;
    }

    /**
     * Gets all {@code Packets} dropped by this {@code Connection}
     *
     * @return A {@code Packet[]} containing all dropped {@code Packets}
     */
    public Packet[] getDroppedPackets() {

        return dropped.toArray(new Packet[]{});
    }

    /**
     * Gets the IP of this {@code Connection}
     *
     * @return The IP of this {@code Connection}
     */
    public String getIp() {

        if (socket != null) {

            InetAddress address = socket.getInetAddress();

            if (address != null) {

                return address.getHostAddress();

            } else {

                return "null";
            }

        } else {

            return "null";
        }
    }

    /**
     * Gets the port of this {@code Connection}
     *
     * @return The port of this {@code Connection}, or -1 if the underlying {@code Socket} is null
     */
    public int getPort() {

        if (socket != null) {

            return socket.getPort();

        } else {

            return -1;
        }
    }

    /**
     * Checks the connection state of this {@code Connection}
     *
     * @return Whether or not this {@code Connection} is connected
     */
    public boolean isConnected() {

        return socket.isConnected();
    }

    /**
     * Checks whether or not this {@code Connection} is closed
     *
     * @return Whether or not this {@code Connection} is closed
     */
    public boolean isClosed() {

        return socket.isClosed();
    }

    /**
     * Checks if the remote side of this connection is closed
     *
     * @return Whether the remote side of this connection is closed
     */
    public boolean isRemoteClosed() {

        return remoteClosed;
    }

    /**
     * Makes the {@code Connection} set itself back to the {@code Receiving}
     * state
     */
    public void setToReceive() {

        if (allowSettingState) {

            this.nextState = State.RECEIVING;

        } else {

            Errors.disallowedForcedStateChange(listenable, new NetworkException(""));
        }
    }

    /**
     * Makes the {@code Connection} set itself back to the {@code Sending} state
     */
    public void setToSend() {

        if (allowSettingState) {

            this.nextState = State.SENDING;

        } else {

            Errors.disallowedForcedStateChange(listenable, new NetworkException(""));
        }
    }

    /**
     * Sets the timeout on reading from the {@code Connection}
     *
     * @param timeout The timeout in milliseconds
     */
    public void setReadTimeout(int timeout) {

        this.readTimeout = timeout;
    }

    /**
     * Gets the timeout on reading from the {@code Connection}
     *
     * @return The timeout in milliseconds
     */
    public int getReadTimeout() {

        return readTimeout;
    }

    /**
     * Attempts to close this {@code Connection}
     *
     * @throws IOException If an I/O error occurs while shutting down this
     *                     {@code Connection}
     */
    public void close() throws IOException {

        close(false);
    }

    /**
     * Attempts to close this {@code Connection}
     *
     * @param socketClosed Whether or not the socket is already closed (closing it again will cause an error)
     * @throws IOException If an I/O error occurs while shutting down this
     *                     {@code Connection}
     */
    public void close(boolean socketClosed) throws IOException {

        running = false;
        listener.interrupt();
        connCheck.interrupt();

        if (!socket.isClosed() && !socketClosed) {

            socket.close();
        }
    }

    /**
     * Gets the time between the last client-server communication in milliseconds
     *
     * @return The ping of the {@code Connection} in milliseconds
     */
    public long getPing() {

        return ping;
    }

    /**
     * Gets the average time between client-server communications in milliseconds
     *
     * @return The average ping of the {@code Connection} in milliseconds;
     */
    public long getAveragePing() {

        if (pingTimes > 0) {

            return pingTotal / pingTimes;

        } else {

            return 0;
        }
    }

    /**
     * Sets this {@code Connection} to be encrypted with the specified {@code EncryptionKey}
     *
     * @param encrypted {@code true} to encrypt the {@code Connection}, {@code false} to stop encrypting
     * @param key       The {@code EncryptionKey} to use for the encryption
     */
    public void setEncrypted(boolean encrypted, EncryptionKey key) {

        this.encryptionKey = key;
        this.encrypted = encrypted;
    }

    /**
     * Gets whether or not the {@code Connection} is set to be encrypted
     *
     * @return Whether or not this {@code Connection} is set to be encrypted (i.e. the {@code setEncrypted(true, ...)} has been called)
     */
    public boolean getEncrypted() {

        return encrypted;
    }

    /**
     * Gets the {@code EncryptionKey} used to encrypt this {@code Connection}
     *
     * @return The {@code EncryptionKey} used to encrypt this {@code Connection}, or {@code null} if the {@code Connection} is not being encrypted
     */
    public EncryptionKey getEncryptionKey() {

        return encryptionKey;
    }

    /**
     * Retrieves the current {@code State} of this {@code Connection}
     *
     * @return This {@code Connection}'s current {@code State}
     */
    public State getCurrentState() {

        return state;
    }

    /**
     * Gets whether or not this {@code Connection} is initialized.  The only way this would return {@code false} is if the {@code Connection} was created using the default constructor
     *
     * @return Whether or not this {@code Connection} is initialized
     */
    public boolean getInitialized() {

        return initialized;
    }

    public boolean isServerSide() {

        return serverSide;
    }

    public boolean isBranched() {

        return branched;
    }

    public BranchingServerListener getBranchingListener() {

        return branchingListener;
    }

    public boolean branchConnection(String id) {

        if (serverSide) {

            BranchingServerListener l = BranchRegistry.getBranchingListener(id);

            if (l != null) {

                branched = true;
                branchingListener = l;

                return true;

            } else {

                Errors.branchingFailed(listenable, new NullPointerException("Listener was null!"));
            }

        } else {

            Errors.branchingNotServerSide(listenable, new NetworkException(""));
        }

        return false;
    }

    private void listenerRun() {

        try {

            while (running && socket != null && socket.isConnected() && !socket.isClosed() && !remoteClosed) {

                if (mainState == State.MUTUAL) {

                    // Check if this is a server connection and the first packet sent
                    if (firstSend && serverSide) {

                        state = State.SENDING;

                        // Check if there are packets waiting to be read
                    } else if (new InputStreamReader(
                            socket.getInputStream()).ready()) {

                        state = State.RECEIVING;

                        // Check if there are packets waiting to be written
                    } else if (waiting != null) {

                        state = State.SENDING;

                        // Default the state back to mutual
                    } else {

                        state = State.MUTUAL;
                    }
                }

                if (state == State.SENDING && socket != null && !socket.isOutputShutdown()) {

                    // Add a delay between attempts to send packages to avoid locking the thread
                    if (!(firstSend && serverSide) && waiting == null && vitalDropped.size() == 0) {

                        // Wait until there is a waiting packet (there cannot be any vital dropped packets until there is a waiting one, which is why this check is all we need)
                        while (waiting == null) {

                            try {

                                Thread.sleep(100);

                            } catch (InterruptedException e) {

                                // Ignore sleep interruptions
                            }
                        }
                    }

                    Packet p = new Packet();
                    boolean skip = false;

                    if (firstSend && serverSide) {

                        if (listenable instanceof Server) {

                            p = ((Server) listenable).getInformationPacket();
                        }

                        p = ((ServerListenable) listenable).fireListenerOnConnect(this, p);

                        if (p == null) {

                            p = new Packet();
                        }

                        firstSend = false;

                    } else if (waiting != null) {

                        p = waiting;
                        waiting = null;

                    } else if (vitalDropped.size() > 0) {

                        p = vitalDropped.get(0);
                        vitalDropped.remove(0);

                    } else {

                        skip = true;

                        if (nextState != null) {

                            state = nextState;
                            nextState = null;
                        }
                    }

                    // Check to make sure that one of the above conditions was true
                    if (!skip) {

                        if (p.isEmpty()) {

                            p.putData(Packet.EMPTY_PACKET);
                        }

                        String toWrite;
                        Throwable[] errors;

                        p = handleQueries(p);

                        if (getEncrypted()) {

                            toWrite = encryptedWriter.getPacketAsWriteableString(this, p);
                            errors = encryptedWriter.getErrors();

                        } else {

                            toWrite = packetWriter.getPacketAsWriteableString(this, p);
                            errors = packetWriter.getErrors();
                        }

                        send(toWrite);

                        if (qrySent) {

                            qrySent = false;
                        }

                        if (shouldRespond) {

                            pingStart = System.currentTimeMillis();
                            pingShouldRespond = true;
                        }

                        for (Throwable t : errors) {

                            listenable.report(t);
                        }

                        if (nextState != null) {

                            state = nextState;
                            nextState = null;

                        } else {

                            state = State.RECEIVING;
                        }
                    }

                } else if (state == State.RECEIVING && socket != null && !socket.isInputShutdown()) {

                    InputStream stream = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder read = new StringBuilder();
                    String inTemp;

                    try {

                        if (shouldRespond) {

                            shouldRespond = false;
                            socket.setSoTimeout(readTimeout);

                        } else {

                            socket.setSoTimeout(0);
                        }

                        if ((inTemp = reader.readLine()) != null) {

                            read.append(inTemp);

                        } else {

                            remoteClosed = true;
                        }

                        if (pingShouldRespond) {

                            ping = System.currentTimeMillis() - pingStart;
                            pingTotal += ping;
                            pingTimes++;
                        }

                    } catch (Exception e) {

                        if (e instanceof SocketTimeoutException) {

                            timeoutQueries();
                            listenable.fireListenerOnTimeout(this);

                        } else if (!(e instanceof SocketException)) {

                            listenable.report(e);
                        }
                    }

                    String readStr = read.toString();

                    if (!readStr.equals(EMPTY_PLACEHOLDER) && !readStr.equals("")) {

                        Packet p;
                        Throwable[] errors;

                        if (readStr.startsWith(":ENC:")) {

                            p = encryptedReader.getPacketFromString(this, readStr);
                            errors = encryptedReader.getErrors();

                        } else {

                            p = packetReader.getPacketFromString(this, readStr);
                            errors = packetReader.getErrors();
                        }

                        for (Throwable t : errors) {

                            listenable.report(t);
                        }

                        if (p.getData().length == 1 && p.getData()[0] == Packet.EMPTY_PACKET) {

                            p.clearData();
                        }

                        // Handle branching requests
                        if (p.hasData(PacketKeys.BRANCH_CONNECTION)) {

                            branchConnection(p.getData(PacketKeys.BRANCH_CONNECTION).toString());
                        }

                        // Handle queries
                        Map<String, Object> requests = p.getAllForType(Query.class);

                        if (requests == null) {

                            requests = new ConcurrentHashMap<String, Object>();
                        }

                        for (String s : requests.keySet()) {

                            Query q = (Query) requests.get(s);
                            Object result;
                            QueryPolicy policy = policies.get(q.getType());

                            if (policy == null || policy.getPolicyDecider().allow(getConnectionInfo())) {

                                result = q.getType().query(this, listenable, q.getParameters());

                            } else {

                                result = "qry-rej:" + policy.getMessage();
                            }

                            completedQueries.put(q.getId(), result);
                        }

                        p.removeAllForType(Query.class);

                        if (completedQueries.size() > 0 && waiting == null) {

                            waiting = new Packet();
                            waiting.setVital(true);
                            shouldRespond = false;
                            qrySent = true;
                        }

                        Map<String, Object> completions = p.getAllForNamePrefix("qry-resp:");

                        if (completions == null) {

                            completions = new ConcurrentHashMap<String, Object>();
                        }

                        for (String id : completions.keySet()) {

                            if (queries.containsKey(id)) {

                                Query q = queries.get(id);

                                if (q != null) {

                                    Object result = completions.get(id);

                                    if (result instanceof String && ((String) result).startsWith("qry-rej:")) {

                                        q.setValue(null);
                                        q.setStatus(QueryStatus.getRejectedStatus(((String) result).substring("qry-rej:".length())));

                                    } else {

                                        q.setValue(result);
                                        q.setStatus(QueryStatus.getSuccessfulStatus(""));
                                    }
                                }

                                queries.remove(id);
                            }
                        }

                        p.removeAllForNamePrefix("qry-resp:");

                        if (p.hasData(":QRYONLY:")) {

                            p.clearData();
                            state = State.SENDING;

                        } else {

                            if (firstReceive && !serverSide) {

                                ((ClientListenable) listenable).fireListenerOnConnect(p);

                                state = State.SENDING;
                                firstReceive = false;

                            } else {

                                listenable.fireListenerOnReceive(this, p);

                                if (nextState != null) {

                                    state = nextState;
                                    nextState = null;

                                } else {

                                    state = State.SENDING;
                                }
                            }
                        }
                    }
                }
            }

            close();

        } catch (Exception e) {

            boolean close = false;

            if (e instanceof SocketException) {

                if (socket.isClosed() || e.getMessage().equalsIgnoreCase("socket closed")) {

                    close = true;
                }

            } else if (e instanceof IOException) {

                if (socket.isClosed() || e.getMessage().equalsIgnoreCase("socket closed")) {

                    close = true;

                } else {

                    remoteClosed = true;
                }
            }

            listenable.report(e);

            try {

                close(close);

            } catch (Exception e1) {

                listenable.report(e1);
            }
        }
    }

    private void timeoutQueries() {

        for (String id : toQuery.keySet()) {

            Query q = toQuery.get(id);
            q.setStatus(QueryStatus.getTimedOutStatus((serverSide ? "Client" : "Server") + " timed out!"));
            toQuery.remove(id);
        }

        for (String id : queries.keySet()) {

            Query q = queries.get(id);
            q.setStatus(QueryStatus.getTimedOutStatus((serverSide ? "Client" : "Server") + " timed out!"));
            queries.remove(id);
        }
    }

    private Packet handleQueries(Packet p) {

        if (qrySent) {

            p.putData(":QRYONLY:", "null");
        }

        for (String id : toQuery.keySet()) {

            Query q = toQuery.get(id);

            if (q != null) {

                p.putData(q.getId(), q);
                queries.put(id, q);
            }

            toQuery.remove(id);
        }

        for (String id : completedQueries.keySet()) {

            Object result = completedQueries.get(id);
            p.putData("qry-resp:" + id, result);
            completedQueries.remove(id);
        }

        if (queries.size() > 0) {

            shouldRespond = true;
            p.setVital(true);
        }

        return p;
    }

    private void send(String data) throws IOException {

        if (socket != null && !socket.isClosed() && !socket.isOutputShutdown()) {

            OutputStream stream = socket.getOutputStream();

            PrintWriter out = new PrintWriter(stream, true);

            out.println(data);
        }
    }

    /**
     * Creates and executes a {@code Query} against the remote side of this {@code Connection}
     *
     * @param id     The id to use for the {@code Query}
     * @param type   The type of query to request
     * @param params The parameters to pass to the {@code Query}
     * @return A {@code Query} object to be used to obtain the results of the query request
     */
    public Query query(String id, QueryType type, Map<String, Object> params) {

        Query q = new Query(id, type, params);
        toQuery.put(id, q);

        if (waiting == null) {

            waiting = new Packet();
            waiting.setVital(true);
            shouldRespond = true;
            qrySent = true;
        }

        return q;
    }

    /**
     * Retrieves a {@code ConnectionInfo} object containing data about this {@code Connection}<br>
     * This data includes:<br>
     * - The {@code IP} of this {@code Connection}<br>
     * - Whether or not this {@code Connection} is encrypted<br>
     * - Whether or not this {@code Connection} is open<br>
     * - Whether or not this {@code Connection} is initialized ({@code true} unless this {@code Connection} was created by using the default constructor)<br>
     *
     * @return A {@code ConnectionInfo} object containing data about this {@code Connection}
     */
    public ConnectionInfo getConnectionInfo() {

        return new ConnectionInfo(this);
    }

    /**
     * Gets the IP of the local machine
     *
     * @return The local IP as a {@code String}
     */
    public static String getLocalIp() {

        try {

            return InetAddress.getLocalHost().getHostAddress();

        } catch (Exception e) {

            return "null";
        }
    }

    /**
     * Gets an uninitialized instance of {@code Connection} with no open ports or listening {@code Threads}
     *
     * @return An uninitialized {@code Connection} with no open ports or listening {@code Threads}
     */
    public static Connection getUninitializedConnection() {

        return new Connection();
    }
}
