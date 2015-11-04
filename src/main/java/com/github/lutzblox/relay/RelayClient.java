package com.github.lutzblox.relay;

import com.github.lutzblox.Client;
import com.github.lutzblox.states.State;


/**
 * A class representing the client-side portion of a relay connection
 *
 * @author Christopher Lutz
 */
public class RelayClient extends Client {

    /**
     * Creates a new {@code RelayClient} instance that will attempt to connect
     * to the specified port on the specified IP
     *
     * @param ip   The IP to connect to
     * @param port The port on the IP to connect to
     */
    public RelayClient(String ip, int port) {

        super(ip, port);
        this.setDefaultConnectionState(State.MUTUAL);
    }
}
