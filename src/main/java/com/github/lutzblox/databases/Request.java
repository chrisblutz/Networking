package com.github.lutzblox.databases;

/**
 * A class representing a request for a {@code DatabaseServer}
 *
 * @author Christopher Lutz
 */
public class Request {

    /**
     * The {@code Packet} data key representing a request key
     */
    public static final String REQUEST_KEY = "net:req";

    private String dataKey;

    /**
     * Creates a new database {@code Request} to send across a
     * {@code DatabaseClient}
     *
     * @param dataKey The key of the data to request
     */
    public Request(String dataKey) {

        this.dataKey = dataKey;
    }

    /**
     * Gets the key of the data to request
     *
     * @return The key of the data
     */
    public String getDataKey() {

        return dataKey;
    }
}
