package com.github.lutzblox.databases;

/**
 * A class representing a response from a {@code DatabaseServer}
 * 
 * @author Christopher Lutz
 */
public class Response {

	/** The {@code Packet} data key representing a response value */
	public static final String RESPONSE_KEY = "net:res";

	private Object value;

	/**
	 * Creates a new database {@code Response} to send across a
	 * {@code DatabaseServer}
	 * 
	 * @param value
	 *            The value of the data to respond with
	 */
	public Response(Object value) {

		this.value = value;
	}

	/**
	 * Gets the value of the data to respond with
	 * 
	 * @return The value of the data
	 */
	public Object getValue() {

		return value;
	}
}
