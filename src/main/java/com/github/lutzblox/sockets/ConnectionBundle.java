package com.github.lutzblox.sockets;


import com.github.lutzblox.packets.encryption.EncryptionKey;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * This class represents a group of {@code Connections}, and allows actions to be performed on all of those {@code Connections} at the same time.
 *
 * @author Christopher Lutz
 */
public class ConnectionBundle implements Iterable<Connection> {

    private CopyOnWriteArrayList<Connection> connections = new CopyOnWriteArrayList<Connection>();

    /**
     * Creates an empty {@code ConnectionBundle} that contains no {@code Connections}
     */
    public ConnectionBundle() {

    }

    /**
     * Creates a {@code ConnectionBundle} containing the given {@code Connections}
     *
     * @param connections The {@code Connections} to add to the bundle
     */
    public ConnectionBundle(Connection... connections) {

        this.connections.addAll(Arrays.asList(connections));
    }

    /**
     * Adds a {@code Connection} to this {@code ConnectionBundle}
     *
     * @param connection The {@code Connection} to add
     */
    public void add(Connection connection) {

        connections.add(connection);
    }

    /**
     * Removes a {@code Connection} from this {@code ConnectionBundle}
     *
     * @param connection The {@code Connection} to remove
     * @return Whether or not the {@code Connection} was removed successfully (or ever existed in the first place)
     */
    public boolean remove(Connection connection) {

        return connections.remove(connection);
    }

    /**
     * Converts this {@code ConnectionBundle} into a {@code Connection[]}
     *
     * @return The array of {@code Connections} representing this {@code ConnectionBundle}
     */
    public Connection[] getConnectionsAsArray() {

        return connections.toArray(new Connection[connections.size()]);
    }

    /**
     * Retrieves the number of {@code Connections} contained within this {@code ConnectionBundle}
     *
     * @return The size of this {@code ConnectionBundle}
     */
    public int size(){

        return connections.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Connection> iterator() {

        return connections.iterator();
    }

    /**
     * Retrieves the {@code Connection} with the specified IP from this {@code ConnectionBundle}
     *
     * @param ip The IP to check for
     * @return The {@code Connection} with the specified IP or {@code null} if no {@code Connection} was found with the specified IP
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
     * Sets the state for all {@code Connections} in this {@code ConnectionBundle} to {@code RECEIVING}
     */
    public void setAllToReceive(){

        for(Connection c : connections){

            c.setToReceive();
        }
    }

    /**
     * Sets the state for all {@code Connections} in this {@code ConnectionBundle} to {@code SENDING}
     */
    public void setAllToSend(){

        for(Connection c : connections){

            c.setToSend();
        }
    }

    /**
     * Sets all {@code Connections} in this {@code ConnectionBundle} to be encrypted with the specified {@code EncryptionKey}
     *
     * @param encrypted Whether or not to encrypt the {@code Connections}
     * @param key The {@code EncryptionKey} to use for the encryption
     */
    public void setAllEncrypted(boolean encrypted, EncryptionKey key){

        for(Connection c : connections){

            c.setEncrypted(encrypted, key);
        }
    }
}
