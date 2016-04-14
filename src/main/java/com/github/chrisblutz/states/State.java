package com.github.chrisblutz.states;

/**
 * The states used by {@code Connections} to represent status of
 * sending/receiving {@code Packets}
 *
 * @author Christopher Lutz
 */
public enum State {

    /**
     * The state used for {@code Connections} while they are waiting for a
     * {@code Packet} to be sent to them
     */
    RECEIVING,
    /**
     * The state used for {@code Connections} while they are waiting to send a
     * {@code Packet}
     */
    SENDING,
    /**
     * The state used for {@code Connections} when they can either send or
     * receive {@code Packets}
     */
    MUTUAL;
}
