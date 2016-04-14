package com.github.chrisblutz.query;

import com.github.chrisblutz.sockets.ConnectionInfo;


/**
 * The {@code QueryPolicy} class allows policies to be set for each {@code QueryType} to decide to accept or reject {@code Queries}
 *
 * @author Christopher Lutz
 */
public class QueryPolicy {

    /**
     * These {@code Actions} represent the action to be taken when a {@code Query} is received
     */
    public enum Action {

        /**
         * This {@code Action} represents automatic rejection of the {@code Query}
         */
        REJECT,

        /**
         * This {@code Action} represents that a decision is necessary for every incoming {@code Query}
         */
        DECIDE,

        /**
         * This {@code Action} represents automatic acceptance of the {@code Query}
         */
        ACCEPT;
    }

    /**
     * This interface is used to make a decision on whether or not {@code Queries} should be accepted or denied
     */
    public interface PolicyDecider {

        /**
         * This method is called when a {@code Query} is received and a decision needs to be made to accept or reject it
         *
         * @param info Information surrounding the {@code Connection} that received the request
         * @return Whether or not to accept ({@code true}) or reject ({@code false}) the {@code Query}
         */
        boolean allow(ConnectionInfo info);
    }

    private Action action;
    private String message;
    private PolicyDecider decider;

    /**
     * Creates a new {@code QueryPolicy} with the specified {@code Action}, message, and {@code PolicyDecider}
     *
     * @param action  The {@code Action} that determines the acceptance/rejection of incoming {@code Queries}
     * @param message The message to display when the response is sent (if the {@code Action} is {@code ACCEPT}, this will never be seen)
     * @param decider The {@code PolicyDecider} to use when making the decision to accept/reject the {@code Query}
     */
    private QueryPolicy(Action action, String message, PolicyDecider decider) {

        this.action = action;
        this.message = message;
        this.decider = decider;
    }

    /**
     * Retrieves the {@code Action} represented by this {@code QueryPolicy}
     *
     * @return The {@code Action} that this policy represents
     */
    public Action getAction() {

        return action;
    }

    /**
     * Retrieves the message to be sent by this {@code QueryPolicy} on rejection/acceptance
     *
     * @return The message associated with this policy
     */
    public String getMessage() {

        return message;
    }

    /**
     * Retrieves the {@code PolicyDecider} used by this {@code QueryPolicy} to determine rejection/acceptance
     *
     * @return The {@code PolicyDecider} associated with this {@code QueryPolicy}
     */
    public PolicyDecider getPolicyDecider() {

        return decider;
    }

    /**
     * Creates a new {@code QueryPolicy} that always accepts the {@code Query}
     *
     * @return A {@code QueryPolicy} with the {@code ACCEPT} action
     */
    public static QueryPolicy getAcceptancePolicy() {

        return new QueryPolicy(Action.ACCEPT, "", new PolicyDecider() {

            @Override
            public boolean allow(ConnectionInfo info) {

                return true;
            }
        });
    }

    /**
     * Creates a new {@code QueryPolicy} that decides to accept/reject {@code Queries} based on each unique {@code Query}
     *
     * @param decider          The {@code PolicyDecider} to use to decide whether to accept or reject {@code Queries}
     * @param rejectionMessage The message to send in response to rejected {@code Queries}
     * @return A {@code QueryPolicy} with the {@code DECIDE} action
     */
    public static QueryPolicy getDecisionPolicy(PolicyDecider decider, String rejectionMessage) {

        return new QueryPolicy(Action.DECIDE, rejectionMessage, decider);
    }

    /**
     * Creates a new {@code QueryPolicy} that always rejects the {@code Query} with the specified message
     *
     * @param message The message to send in response to rejected {@code Queries}
     * @return A {@code QueryPolicy} with the {@code REJECT} action
     */
    public static QueryPolicy getRejectionPolicy(String message) {

        return new QueryPolicy(Action.REJECT, message, new PolicyDecider() {

            @Override
            public boolean allow(ConnectionInfo info) {

                return false;
            }
        });
    }
}
