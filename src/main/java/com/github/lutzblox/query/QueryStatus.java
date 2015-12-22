package com.github.lutzblox.query;

/**
 * @author Christopher Lutz
 */
public class QueryStatus {

    public enum Status {

        SUCCESSFUL, WORKING, REJECTED, TIMED_OUT;
    }


    private Status status;
    private String message;

    private QueryStatus(Status status, String message){

        this.status = status;
        this.message = message;
    }

    public Status getStatus(){

        return status;
    }

    public String getMessage(){

        return message;
    }

    public static QueryStatus getSuccessfulStatus(String message){

        return new QueryStatus(Status.SUCCESSFUL, message);
    }

    public static QueryStatus getRejectedStatus(String message){

        return new QueryStatus(Status.REJECTED, message);
    }

    public static QueryStatus getWorkingStatus(String message){

        return new QueryStatus(Status.WORKING, message);
    }

    public static QueryStatus getTimedOutStatus(String message){

        return new QueryStatus(Status.TIMED_OUT, message);
    }
}
