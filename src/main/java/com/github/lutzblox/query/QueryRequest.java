package com.github.lutzblox.query;

/**
 * A class that represents the result of a {@code Query} while it is in transit over a {@code Connection}
 *
 * @author Christopher Lutz
 */
public class QueryRequest {

    private String id;
    private QueryType type;

    /**
     * Creates a new {@code QueryRequest} using the specified id and {@code QueryType}
     *
     * @param id   The id to use
     * @param type The {@code QueryType} to use
     */
    public QueryRequest(String id, QueryType type) {

        this.id = id;
        this.type = type;
    }

    /**
     * Gets the id of this {@code QueryRequest}
     *
     * @return The id of this {@code QueryRequest}
     */
    public String getId() {

        return id;
    }

    /**
     * Gets the {@code QueryType} of this {@code QueryRequest}
     *
     * @return The {@code QueryType} of this {@code QueryRequest}
     */
    public QueryType getType() {

        return type;
    }
}
