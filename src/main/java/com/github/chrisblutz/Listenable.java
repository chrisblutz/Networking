package com.github.chrisblutz;

import com.github.chrisblutz.exceptions.reporters.ErrorReporter;
import com.github.chrisblutz.listeners.NetworkListener;
import com.github.chrisblutz.listeners.branching.BranchingServerListener;
import com.github.chrisblutz.packets.Packet;
import com.github.chrisblutz.sockets.Connection;
import com.github.chrisblutz.states.State;

import java.util.ArrayList;
import java.util.List;


/**
 * A class that holds listeners for sides of a connection
 *
 * @author Christopher Lutz
 */
public class Listenable {

    protected List<NetworkListener> lists = new ArrayList<NetworkListener>();
    protected List<ErrorReporter> reporters = new ArrayList<ErrorReporter>();
    private State defaultState = null;

    /**
     * Sets the default {@code State} to use for {@code Connections} based off
     * this {@code Listenable}
     *
     * @param state The default {@code State}
     */
    public void setDefaultConnectionState(State state) {

        this.defaultState = state;
    }

    /**
     * Gets the default {@code State} to use for {@code Connections} based off
     * this {@code Listenable}
     *
     * @return The default {@code State}
     */
    public State getDefaultConnectionState() {

        return defaultState;
    }

    /**
     * Attaches an {@code ErrorListener} to this {@code Listenable}
     *
     * @param reporter The {@code ErrorReporter} to add
     */
    public void addErrorReporter(ErrorReporter reporter) {

        reporters.add(reporter);
    }

    /**
     * Gets all of the {@code ErrorReporters} attached to this
     * {@code Listenable}
     *
     * @return An {@code ErrorReporter[]} containing all {@code ErrorReporters}
     * attached to this {@code Listenable}
     */
    public ErrorReporter[] getErrorReporters() {

        return reporters.toArray(new ErrorReporter[]{});
    }

    /**
     * Reports an error ({@code Throwable}) through the {@code ErrorReporters}
     * attached to this {@code Listenable}
     *
     * @param t The {@code Throwable} to report
     */
    public void report(Throwable t) {

        for (ErrorReporter reporter : getErrorReporters()) {

            reporter.report(t);
        }
    }

    /**
     * Attaches a {@code NetworkListener} to this {@code Listenable}
     *
     * @param listener The listener to add
     */
    public void addNetworkListener(NetworkListener listener) {

        lists.add(listener);
    }

    /**
     * Gets all of the {@code NetworkListener} objects attached to this
     * {@code Listenable}
     *
     * @return A {@code NetworkListener[]} containing all listeners attached to
     * this {@code Listenable}
     */
    public NetworkListener[] getNetworkListeners() {

        return lists.toArray(new NetworkListener[]{});
    }

    /**
     * Fires the {@code onRecieve()} method in all of the attached
     * {@code NetworkListener} objects (or the appropriate {@code BranchingServerListener} if the {@code Connection} is server-side and branched)
     *
     * @param connection The {@code Connection} responsible for the {@code Packet}
     * @param packet     The {@code Packet} to pass to the listeners
     */
    public void fireListenerOnReceive(Connection connection, Packet packet) {

        if (connection.isServerSide() && connection.isBranched()) {

            BranchingServerListener branchingListener = connection.getBranchingListener();

            if (branchingListener != null) {

                branchingListener.onReceive(connection, packet);
            }

        } else {

            for (NetworkListener l : getNetworkListeners()) {

                l.onReceive(connection, packet);
            }
        }
    }

    /**
     * Fires the {@code onTimeout()} method in all of the attached
     * {@code NetworkListener} objects (or the appropriate {@code BranchingServerListener} if the {@code Connection} is server-side and branched)
     *
     * @param connection The {@code Connection} responsible for the timeout
     */
    public void fireListenerOnTimeout(Connection connection) {

        if (connection.isServerSide() && connection.isBranched()) {

            BranchingServerListener branchingListener = connection.getBranchingListener();

            if (branchingListener != null) {

                branchingListener.onTimeout(connection);
            }

        } else {

            for (NetworkListener l : getNetworkListeners()) {

                l.onTimeout(connection);
            }
        }
    }
}
