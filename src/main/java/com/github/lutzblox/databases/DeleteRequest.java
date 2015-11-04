package com.github.lutzblox.databases;

/**
 * A class representing a delete request for a {@code DatabaseServer}
 *
 * @author Christopher Lutz
 */
public class DeleteRequest {

    /**
     * The {@code Packet} data key representing a delete request key
     */
    public static final String DELETE_REQUEST_KEY_KEY = "net:delreqkey";

    private String dataKey;

    /**
     * Creates a new database {@code DeleteRequest} to send across a
     * {@code DatabaseClient}
     *
     * @param dataKey The key of the data to delete
     */
    public DeleteRequest(String dataKey) {

        this.dataKey = dataKey;
    }

    /**
     * Gets the key of the data to delete
     *
     * @return The key of the data
     */
    public String getDataKey() {

        return dataKey;
    }
}
