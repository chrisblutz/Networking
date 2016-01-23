package com.github.lutzblox.sockets;

import com.github.lutzblox.ClientListenable;
import com.github.lutzblox.Listenable;
import com.github.lutzblox.Server;
import com.github.lutzblox.ServerListenable;
import com.github.lutzblox.exceptions.Errors;
import com.github.lutzblox.exceptions.NetworkException;
import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.packets.PacketReader;
import com.github.lutzblox.packets.PacketWriter;
import com.github.lutzblox.packets.encryption.EncryptedPacketReader;
import com.github.lutzblox.packets.encryption.EncryptedPacketWriter;
import com.github.lutzblox.query.*;
import com.github.lutzblox.states.State;

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

    private Packet waiting = null, response = null;
    private boolean responseTimedOut = false;

    private long ping = -1, pingStart = 0, pingTotal = 0, pingTimes = 0;

    private boolean encrypted = false, allowSettingState = true, running = false, serverSide = false, firstReceive = true,
            firstSend = true, shouldRespond = false, remoteClosed = false, canExecute = true, canGetInput = true, canOutput = true, initialized = false, qrySent = false;

    private int readTimeout = 8000;

    private PacketReader packetReader;
    private PacketWriter packetWriter;
    private EncryptedPacketReader encryptedReader;
    private EncryptedPacketWriter encryptedWriter;

    private Map<QueryType, QueryPolicy> policies = new ConcurrentHashMap<QueryType, QueryPolicy>();

    private Map<String, Query> toQuery = new ConcurrentHashMap<String, Query>();
    private Map<String, Query> queries = new ConcurrentHashMap<String, Query>();
    private Map<String, Object> completedQueries = new ConcurrentHashMap<String, Object>();

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

    public Connection(Listenable listenable, Socket socket, State state, boolean serverSide, boolean allowSettingState, Map<QueryType, QueryPolicy> policies) {

        this(listenable, socket, state, serverSide, allowSettingState);
        this.policies = policies;
    }

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
    public Connection(Listenable listenable, Socket socket, State state, boolean serverSide, boolean allowSettingState) {

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

                if (socket != null) {

                    if (socket.isClosed() || !socket.isConnected()) {

                        canExecute = false;
                    }

                    if (socket.isInputShutdown()) {

                        canGetInput = false;
                    }

                    if (socket.isOutputShutdown()) {

                        canOutput = false;
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
        shouldRespond = serverSide ? false : true;
        listener.start();
        connCheck.start();
    }

    /**
     * Creates an uninitialized Connection with default values
     */
    private Connection() {

        this.listenable = null;
        this.socket = null;
        this.mainState = State.MUTUAL;
        this.state = State.MUTUAL;
        this.serverSide = false;
    }

    public void setQueryPolicy(QueryType type, QueryPolicy policy) {

        policies.put(type, policy);
    }

    public Map<QueryType, QueryPolicy> getQueryPolicies() {

        return policies;
    }

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

        return socket.getInetAddress().getHostAddress();
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

        running = false;
        listener.interrupt();

        if (!socket.isClosed()) {

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

    public void setEncrypted(boolean encrypted) {

        this.encrypted = encrypted;
    }

    public boolean getEncrypted() {

        return encrypted;
    }

    public boolean getInitialized() {

        return initialized;
    }

    private final void listenerRun() {

        try {

            while (running && canExecute) {

                if (mainState == State.MUTUAL) {

                    if (firstSend && serverSide) {

                        state = State.SENDING;

                    } else if (new InputStreamReader(
                            socket.getInputStream()).ready()) {

                        state = State.RECEIVING;

                    } else if (waiting != null) {

                        state = State.SENDING;

                    } else {

                        state = State.MUTUAL;
                    }
                }

                if (state == State.SENDING) {

                    if (canExecute && canOutput) {

                        if (!(firstSend && serverSide) && vitalDropped.size() == 0 && waiting == null) {

                            try {

                                while (waiting == null) {

                                    Thread.sleep(100);
                                }

                            } catch (Exception e) {

                                // Ignore interrupted sleep exceptions
                            }
                        }

                        if (firstSend && serverSide) {

                            Packet p;

                            if (listenable instanceof Server) {

                                p = ((Server) listenable).getInformationPacket();

                            } else {

                                p = new Packet();
                            }

                            p = ((ServerListenable) listenable)
                                    .fireListenerOnConnect(this, p);

                            if(p == null){

                                p = new Packet();
                            }

                            if (p.isEmpty()) {

                                p.putData(Packet.EMPTY_PACKET);
                            }

                            String toWrite;
                            Throwable[] errors;

                            p = handleQueries(p);

                            if (p.getEncrypted() || encrypted) {

                                toWrite = encryptedWriter.getPacketAsWriteableString(p);
                                errors = encryptedWriter.getErrors();

                            } else {

                                toWrite = packetWriter
                                        .getPacketAsWriteableString(p);
                                errors = packetWriter.getErrors();
                            }

                            send(toWrite);

                            if (qrySent) {

                                qrySent = false;
                            }

                            if (shouldRespond) {

                                pingStart = System.currentTimeMillis();
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

                            firstSend = false;

                        } else if (waiting != null) {

                            String toWrite;
                            Throwable[] errors;

                            waiting = handleQueries(waiting);

                            if (waiting.getEncrypted() || encrypted) {

                                toWrite = encryptedWriter.getPacketAsWriteableString(waiting);
                                errors = encryptedWriter.getErrors();

                            } else {

                                toWrite = packetWriter
                                        .getPacketAsWriteableString(waiting);
                                errors = packetWriter.getErrors();
                            }

                            send(toWrite);

                            if (qrySent) {

                                qrySent = false;
                            }

                            for (Throwable t : errors) {

                                listenable.report(t);
                            }

                            waiting = null;

                            if (nextState != null) {

                                state = nextState;
                                nextState = null;

                            } else {

                                state = State.RECEIVING;
                            }

                        } else if (waiting == null && vitalDropped.size() > 0) {

                            String toWrite;
                            Throwable[] errors;

                            Packet p = vitalDropped.get(0);

                            p = handleQueries(p);

                            if (p.getEncrypted() || encrypted) {

                                toWrite = encryptedWriter.getPacketAsWriteableString(p);
                                errors = encryptedWriter.getErrors();

                            } else {

                                toWrite = packetWriter
                                        .getPacketAsWriteableString(p);
                                errors = packetWriter.getErrors();
                            }

                            send(toWrite);

                            if (qrySent) {

                                qrySent = false;
                            }

                            for (Throwable t : errors) {

                                listenable.report(t);
                            }

                            vitalDropped.remove(0);

                        } else {

                            if (nextState != null) {

                                state = nextState;
                                nextState = null;
                            }
                        }
                    }

                } else if (state == State.RECEIVING) {

                    if (canExecute && canGetInput) {

                        InputStream stream = socket.getInputStream();

                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(stream));

                        String inTemp = null;

                        StringBuilder read = new StringBuilder();

                        try {

                            if (shouldRespond) {

                                shouldRespond = false;
                                socket.setSoTimeout(readTimeout);

                            } else {

                                socket.setSoTimeout(0);
                            }

                            if (canExecute && canGetInput
                                    && (inTemp = reader.readLine()) != null) {

                                read.append(inTemp);

                            } else if (inTemp == null) {

                                remoteClosed = true;
                            }

                            if (shouldRespond) {

                                ping = System.currentTimeMillis() - pingStart;
                                pingTotal += ping;
                                pingTimes++;
                            }

                        } catch (Exception e) {

                            if (e instanceof SocketTimeoutException) {

                                responseTimedOut = true;
                                timeoutQueries();
                                listenable.fireListenerOnTimeout(this);

                            } else if (!(e instanceof SocketException)) {

                                listenable.report(e);
                            }
                        }

                        String readStr = read.toString();

                        if (!readStr.equals("::REMCL")) {

                            if (!readStr.toString().equals("")) {

                                Packet p;
                                Throwable[] errors;

                                if (readStr.startsWith(":ENC:")) {

                                    p = encryptedReader.getPacketFromString(readStr);
                                    errors = encryptedReader.getErrors();

                                } else {

                                    p = packetReader
                                            .getPacketFromString(readStr);
                                    errors = packetReader.getErrors();
                                }

                                for (Throwable t : errors) {

                                    listenable.report(t);
                                }

                                if (p.getData().length == 1) {

                                    if (p.getData()[0] == Packet.EMPTY_PACKET) {

                                        p.clearData();
                                    }
                                }

                                Map<String, Object> requests = p.getAllForType(QueryRequest.class);

                                if (requests == null) {

                                    requests = new ConcurrentHashMap<String, Object>();
                                }

                                for (String s : requests.keySet()) {

                                    QueryRequest q = (QueryRequest) requests.get(s);
                                    Object result;

                                    QueryPolicy policy = policies.get(q.getType());
                                    if (policy != null && policy.getPolicyDecider().allow(getConnectionInfo())) {

                                        result = q.getType().query(listenable);

                                    } else {

                                        result = "qry-rej:" + policy.getMessage();
                                    }

                                    completedQueries.put(q.getId(), result);
                                }

                                p.removeAllForType(QueryRequest.class);

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
                                        Object result = completions.get(id);

                                        if (result instanceof String) {

                                            String resStr = (String) result;

                                            if (resStr.startsWith("qry-rej:")) {

                                                q.setValue(null);
                                                q.setStatus(QueryStatus.getRejectedStatus(resStr.substring("qry-rej:".length())));

                                            } else {

                                                q.setValue(result);
                                                q.setStatus(QueryStatus.getSuccessfulStatus(""));
                                            }

                                        } else {

                                            q.setValue(result);
                                            q.setStatus(QueryStatus.getSuccessfulStatus(""));
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

                                        ((ClientListenable) listenable)
                                                .fireListenerOnConnect(p);

                                        state = State.SENDING;

                                        firstReceive = false;

                                    } else {

                                        if (shouldRespond) {

                                            response = p;
                                        }

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
                }
            }

            close();

        } catch (Throwable e) {

            boolean close = true;

            if (e instanceof SocketException) {

                if (socket.isClosed()
                        || e.getMessage().equalsIgnoreCase("socket closed")) {

                    close = false;
                }

            } else if (e instanceof IOException) {

                if (socket.isClosed()
                        || e.getMessage().equalsIgnoreCase("socket closed")) {

                    close = false;

                } else {

                    remoteClosed = true;
                }
            }

            if (close) {

                listenable.report(e);

                try {

                    close();

                } catch (Exception e1) {

                    listenable.report(e1);
                }
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
            p.putData(q.getId(), new QueryRequest(q.getId(), q.getType()));
            toQuery.remove(id);
            queries.put(id, q);
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

        OutputStream stream = socket.getOutputStream();

        PrintWriter out = new PrintWriter(stream, true);

        response = null;
        responseTimedOut = false;

        out.println(data);
    }

    public Query query(String id, QueryType type) {

        Query q = new Query(id, type);
        toQuery.put(id, q);

        if (waiting == null) {

            waiting = new Packet();
            waiting.setVital(true);
            shouldRespond = true;
            qrySent = true;
        }

        return q;
    }

    public ConnectionInfo getConnectionInfo() {

        return new ConnectionInfo(getIp(), getEncrypted(), !socket.isClosed() && socket.isConnected() && !remoteClosed, getInitialized());
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
