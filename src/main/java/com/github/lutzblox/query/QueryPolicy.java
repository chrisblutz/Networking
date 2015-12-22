package com.github.lutzblox.query;

import com.github.lutzblox.sockets.ConnectionInfo;


/**
 * @author Christopher Lutz
 */
public class QueryPolicy {

    public enum Action {

        REJECT, DECIDE, ACCEPT;
    }

    public interface PolicyDecider {

        boolean allow(ConnectionInfo info);
    }

    private Action action;
    private String message;
    private PolicyDecider decider;

    private QueryPolicy(Action action, String message, PolicyDecider decider) {

        this.action = action;
        this.message = message;
        this.decider = decider;
    }

    public Action getAction() {

        return action;
    }

    public String getMessage() {

        return message;
    }

    public PolicyDecider getPolicyDecider() {

        return decider;
    }

    public static QueryPolicy getAcceptancePolicy() {

        return new QueryPolicy(Action.ACCEPT, "", new PolicyDecider() {

            @Override
            public boolean allow(ConnectionInfo info) {

                return true;
            }
        });
    }

    public static QueryPolicy getDecisionPolicy(PolicyDecider decider, String rejectionMessage) {

        return new QueryPolicy(Action.DECIDE, rejectionMessage, decider);
    }

    public static QueryPolicy getRejectionPolicy(String message) {

        return new QueryPolicy(Action.REJECT, message, new PolicyDecider() {

            @Override
            public boolean allow(ConnectionInfo info) {

                return false;
            }
        });
    }
}
