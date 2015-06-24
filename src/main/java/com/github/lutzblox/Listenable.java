package com.github.lutzblox;

import java.util.ArrayList;
import java.util.List;

import com.github.lutzblox.exceptions.reporters.ErrorReporter;
import com.github.lutzblox.listeners.NetworkListener;
import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.sockets.Connection;
import com.github.lutzblox.states.State;

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
	 * @param state
	 *            The default {@code State}
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
	 * @param reporter
	 *            The {@code ErrorReporter} to add
	 */
	public void addErrorReporter(ErrorReporter reporter) {

		reporters.add(reporter);
	}

	/**
	 * Gets all of the {@code ErrorReporters} attached to this
	 * {@code Listenable}
	 * 
	 * @return An {@code ErrorReporter[]} containing all {@code ErrorReporters}
	 *         attached to this {@code Listenable}
	 */
	public ErrorReporter[] getErrorReporters() {

		return reporters.toArray(new ErrorReporter[] {});
	}

	/**
	 * Reports an error ({@code Throwable}) through the {@code ErrorReporters}
	 * attached to this {@code Listenable}
	 * 
	 * @param t
	 *            The {@code Throwable} to report
	 */
	public void report(Throwable t) {

		for (ErrorReporter reporter : getErrorReporters()) {

			reporter.report(t);
		}
	}

	/**
	 * Attaches a {@code NetworkListener} to this {@code Listenable}
	 * 
	 * @param listener
	 *            The listener to add
	 */
	public void addNetworkListener(NetworkListener listener) {

		lists.add(listener);
	}

	/**
	 * Gets all of the {@code NetworkListener} objects attached to this
	 * {@code Listenable}
	 * 
	 * @return A {@code NetworkListener[]} containing all listeners attached to
	 *         this {@code Listenable}
	 */
	public NetworkListener[] getNetworkListeners() {

		return lists.toArray(new NetworkListener[] {});
	}

	/**
	 * Fires the {@code onRecieve()} method in all of the attached
	 * {@code NetworkListener} objects
	 * 
	 * @param connection
	 *            The {@code Connection} responsible for the {@code Packet}
	 * @param packet
	 *            The {@code Packet} to pass to the listeners
	 */
	public void fireListenerOnReceive(Connection connection, Packet packet) {

		for (NetworkListener l : getNetworkListeners()) {

			l.onReceive(connection, packet);
		}
	}

	/**
	 * Fires the {@code onTimeout()} method in all of the attached
	 * {@code NetworkListener} objects
	 * 
	 * @param connection
	 *            The {@code Connection} responsible for the timeout
	 */
	public void fireListenerOnTimeout(Connection connection) {

		for (NetworkListener l : getNetworkListeners()) {

			l.onTimeout(connection);
		}
	}
}
