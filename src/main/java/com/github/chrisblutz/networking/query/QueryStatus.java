package com.github.chrisblutz.networking.query;

/**
 * A class representing statuses for {@code Queries}
 *
 * @author Christopher Lutz
 */
public class QueryStatus {

    /**
     * Holds possible statuses for {@code Queries}
     */
    public enum Status {

        /**
         * The status that is set when the {@code Query} completes successfully
         */
        SUCCESSFUL,
        /**
         * The status that is set when the {@code Query} is still waiting for a response
         */
        WORKING,
        /**
         * The status that is set when the {@code Query} is rejected
         */
        REJECTED,
        /**
         * The status that is set when the {@code Query} times out
         */
        TIMED_OUT;
    }


    private Status status;
    private String message;

    private QueryStatus(Status status, String message) {

        this.status = status;
        this.message = message;
    }

    /**
     * Gets the {@code Status} associated with this {@code QueryStatus}
     *
     * @return The {@code Status} for this {@code QueryStatus}
     */
    public Status getStatus() {

        return status;
    }

    /**
     * Gets the message associated with this {@code QueryStatus}
     *
     * @return The message associated with this {@code QueryStatus}
     */
    public String getMessage() {

        return message;
    }

    /**
     * Creates a new {@code QueryStatus} with the {@code SUCCESSFUL} status and the specified message
     *
     * @param message The message to assign to this {@code QueryStatus}.  Because this is a successful {@code QueryStatus}, this will never actually be seen.
     * @return The new {@code QueryStatus}
     */
    public static QueryStatus getSuccessfulStatus(String message) {

        return new QueryStatus(Status.SUCCESSFUL, message);
    }

    /**
     * Creates a new {@code QueryStatus} with the {@code REJECTED} status and the specified message
     *
     * @param message The message to assign to this {@code QueryStatus}.
     * @return The new {@code QueryStatus}
     */
    public static QueryStatus getRejectedStatus(String message) {

        return new QueryStatus(Status.REJECTED, message);
    }

    /**
     * Creates a new {@code QueryStatus} with the {@code WORKING} status and the specified message
     *
     * @param message The message to assign to this {@code QueryStatus}.
     * @return The new {@code QueryStatus}
     */
    public static QueryStatus getWorkingStatus(String message) {

        return new QueryStatus(Status.WORKING, message);
    }

    /**
     * Creates a new {@code QueryStatus} with the {@code TIMED_OUT} status and the specified message
     *
     * @param message The message to assign to this {@code QueryStatus}.
     * @return The new {@code QueryStatus}
     */
    public static QueryStatus getTimedOutStatus(String message) {

        return new QueryStatus(Status.TIMED_OUT, message);
    }
}
