package com.github.chrisblutz.networking.listeners;

import com.github.chrisblutz.networking.packets.Packet;


/**
 * A listener to be attached to a {@code Client}
 *
 * @author Christopher Lutz
 */
public interface ClientListener extends NetworkListener {

    /**
     * Called when the first {@code Packet} is received from the {@code Server}
     *
     * @param packet The {@code Packet} received
     */
    public void onConnect(Packet packet);
}
