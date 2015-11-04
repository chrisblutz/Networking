package com.github.lutzblox.databases;

/**
 * A class representing a put request for a {@code DatabaseServer}
 *
 * @author Christopher Lutz
 */
public class PutRequest {

    /**
     * The {@code Packet} data key representing a put request key
     */
    public static final String PUT_REQUEST_KEY_KEY = "net:putreqkey";
    /**
     * The {@code Packet} data key representing a put request value
     */
    public static final String PUT_REQUEST_VALUE_KEY = "net:putreqval";

    private String dataKey;
    private Object value;

    /**
     * Creates a new database {@code PutRequest} to send across a
     * {@code DatabaseClient}
     *
     * @param dataKey The key of the data to send
     * @param value   The value of the data to send
     */
    public PutRequest(String dataKey, Object value) {

        this.dataKey = dataKey;
        this.value = value;
    }

    /**
     * Gets the key of the data to send
     *
     * @return The key of the data
     */
    public String getDataKey() {

        return dataKey;
    }

    /**
     * Gets the value of the data to send
     *
     * @return The value of the data
     */
    public Object getValue() {

        return value;
    }
}
