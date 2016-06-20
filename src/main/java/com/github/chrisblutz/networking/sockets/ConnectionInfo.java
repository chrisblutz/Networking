package com.github.chrisblutz.networking.sockets;

/**
 * A class containing information about a {@code Connection}
 *
 * @author Christopher Lutz
 */
public class ConnectionInfo {

    private String ip;
    private boolean encrypted, open, initialized;

    /**
     * Creates a new {@code ConnectionInfo} object with the specified information
     *
     * @param ip          The IP of the {@code Connection}
     * @param encrypted   Whether or not the {@code Connection} is encrypted
     * @param open        Whether or not the {@code Connection} is open
     * @param initialized Whether or not the {@code Connection} is initialized ({@code true} unless the {@code Connection} was created with the default {@code Connection} constructor)
     */
    public ConnectionInfo(String ip, boolean encrypted, boolean open, boolean initialized) {

        this.ip = ip;
        this.encrypted = encrypted;
        this.open = open;
        this.initialized = initialized;
    }

    /**
     * Creates a new {@code ConnectionInfo} object with the information from the specified {@code Connection}
     *
     * @param c The {@code Connection} to pull information from to create this {@code ConnectionInfo} object
     */
    public ConnectionInfo(Connection c) {

        this(c.getIp(), c.getEncrypted(), !c.isClosed() && c.isConnected() && !c.isRemoteClosed(), c.getInitialized());
    }

    /**
     * Gets the IP of the {@code Connection}
     *
     * @return The IP of the {@code Connection}
     */
    public String getIp() {

        return ip;
    }

    /**
     * Gets whether or not the {@code Connection} is encrypted
     *
     * @return Whether or not the {@code Connection} is encrypted
     */
    public boolean getEncrypted() {

        return encrypted;
    }

    /**
     * Gets whether or not the {@code Connection} is open
     *
     * @return Whether or not the {@code Connection} is open
     */
    public boolean getOpen() {

        return open;
    }

    /**
     * Gets whether or not the {@code Connection} is initialized
     *
     * @return Whether or not the {@code Connection} is initialized ({@code true} unless the {@code Connection} was created with the default {@code Connection} constructor)
     */
    public boolean getInitialized() {

        return initialized;
    }
}
