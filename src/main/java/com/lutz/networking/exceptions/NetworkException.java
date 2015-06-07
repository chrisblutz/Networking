package com.lutz.networking.exceptions;

/**
 * A generic {@code Exception} class used by the Networking library
 * 
 * @author Christopher Lutz
 */
public class NetworkException extends RuntimeException {

	private static final long serialVersionUID = -2101644076998281656L;

	/**
	 * Creates a new {@code NetworkException} with the specified message
	 * 
	 * @param message
	 *            The message for the {@code Exception}
	 */
	public NetworkException(String message) {

		super(message);
	}
}
