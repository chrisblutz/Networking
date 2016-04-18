package com.github.chrisblutz;

import com.github.chrisblutz.listeners.NetworkListener;
import com.github.chrisblutz.listeners.ServerListener;
import com.github.chrisblutz.listeners.branching.BranchingServerListener;
import com.github.chrisblutz.packets.Packet;
import com.github.chrisblutz.sockets.Connection;

import java.util.ArrayList;
import java.util.List;


/**
 * This class extends the {@code Listenable} class to make it specifically for
 * the {@code Server} side of a connection
 *
 * @author Christopher Lutz
 */
public class ServerListenable extends Listenable {

    /**
     * Gets all of the {@code ServerListener} objects attached to the
     * {@code Server}
     *
     * @return A {@code ServerListener[]} containing all listeners attached to
     * the {@code Server}
     */
    public ServerListener[] getServerListeners() {

        List<ServerListener> l = new ArrayList<ServerListener>();

        for (NetworkListener n : lists) {

            if (n instanceof ServerListener) {

                l.add((ServerListener) n);
            }
        }

        return l.toArray(new ServerListener[]{});
    }

    /**
     * Fires the {@code onConnect()} method in all of the {@code ServerListener}
     * objects attached to the {@code Server}
     *
     * @param c    The {@code Connection} that was connected
     * @param data The {@code Packet} to pass to the listener
     * @return The packet after passing through the listener methods
     */
    public Packet fireListenerOnConnect(Connection c, Packet data) {

        for (ServerListener l : getServerListeners()) {

            data = l.onConnect(c, data);
        }

        return data;
    }

    /**
     * Fires the {@code onClientFailure()} method in all of the
     * {@code ServerListener} objects attached to the {@code Server} (or the appropriate {@code BranchingServerListener} if the {@code Connection} is branched)
     *
     * @param c The {@code Connection} that failed
     */
    public void fireListenerOnClientFailure(Connection c) {

        if (c.isServerSide() && c.isBranched()) {

            BranchingServerListener branchingListener = c.getBranchingListener();

            if (branchingListener != null) {

                branchingListener.onClientFailure(c);
            }

        } else {

            for (ServerListener l : getServerListeners()) {

                l.onClientFailure(c);
            }
        }
    }
}
