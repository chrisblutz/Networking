package com.github.chrisblutz.relay;

import com.github.chrisblutz.Client;
import com.github.chrisblutz.states.State;


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
     * @param ip         The IP to connect to
     * @param port       The port on the IP to connect to
     * @param clientName The name of this {@code RelayClient}
     */
    public RelayClient(String ip, int port, String clientName) {

        super(ip, port, clientName);
        this.setDefaultConnectionState(State.MUTUAL);
    }
}
