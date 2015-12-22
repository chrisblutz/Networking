package com.github.lutzblox.query;

/**
 * @author Christopher Lutz
 */
public class Query {

    private QueryStatus.Status status;
    private String statusMessage;
    private Object value;
    private String id;
    private QueryType type;

    public Query(String id, QueryType type) {

        setStatus(QueryStatus.getWorkingStatus("Working..."));
        this.value = null;
        this.id = id;
        this.type = type;
    }

    public QueryStatus.Status getStatus() {

        return status;
    }

    public void setStatus(QueryStatus status) {

        this.status = status.getStatus();
        this.statusMessage = status.getMessage();
    }

    public boolean isWorking(){

        return getStatus() == QueryStatus.Status.WORKING;
    }

    public String getStatusMessage(){

        return statusMessage;
    }

    public Object getValue() {

        return value;
    }

    public void setValue(Object value) {

        this.value = value;
    }

    public String getId() {

        return id;
    }

    public QueryType getType(){

        return type;
    }
}
