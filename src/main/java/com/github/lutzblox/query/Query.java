package com.github.lutzblox.query;

/**
 * A class representing a query across a {@code Connection}
 *
 * @author Christopher Lutz
 */
public class Query {

    private QueryStatus.Status status;
    private String statusMessage;
    private Object value;
    private String id;
    private QueryType type;

    /**
     * Creates a new {@code Query} of the specified {@code QueryType} using the specified id
     *
     * @param id   The id of the query
     * @param type The {@code QueryType} of this query
     */
    public Query(String id, QueryType type) {

        setStatus(QueryStatus.getWorkingStatus("Working..."));
        this.value = null;
        this.id = id;
        this.type = type;
    }

    /**
     * Gets the current status of this {@code Query}
     *
     * @return The status of this {@code Query}
     */
    public QueryStatus.Status getStatus() {

        return status;
    }

    /**
     * Sets the status of this {@code Query}.  Usually this should be left alone.  It is set by the {@code Connection} when the {@code Query} is completed.
     *
     * @param status The status to set
     */
    public void setStatus(QueryStatus status) {

        this.status = status.getStatus();
        this.statusMessage = status.getMessage();
    }

    /**
     * Gets whether or not the current status of this {@code Query} is {@code WORKING}
     *
     * @return Whether or not the status of this {@code Query} is {@code WORKING}
     */
    public boolean isWorking() {

        return getStatus() == QueryStatus.Status.WORKING;
    }

    /**
     * Gets the status message associated with the current status of this {@code Query}
     *
     * @return The message associated with the current status
     */
    public String getStatusMessage() {

        return statusMessage;
    }

    /**
     * Gets the result value for this {@code Query}.  This method should not be called until the status of this query returns successful, or it may return {@code null}.
     *
     * @return The result of this {@code Query}
     */
    public Object getValue() {

        return value;
    }

    /**
     * Sets the result value of this {@code Query}.  This method is called by a {@code Connection} after it receives a response to the {@code Query}.
     *
     * @param value The value to set
     */
    public void setValue(Object value) {

        this.value = value;
    }

    /**
     * Gets the id of this {@code Query}
     *
     * @return The id of this {@code Query}
     */
    public String getId() {

        return id;
    }

    /**
     * Gets the {@code QueryType} of this {@code Query}
     *
     * @return The {@code QueryType} of this {@code Query}
     */
    public QueryType getType() {

        return type;
    }
}
