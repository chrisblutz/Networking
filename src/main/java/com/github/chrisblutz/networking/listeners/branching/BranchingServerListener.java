package com.github.chrisblutz.networking.listeners.branching;

import com.github.chrisblutz.networking.listeners.ServerListener;
import com.github.chrisblutz.networking.packets.Packet;
import com.github.chrisblutz.networking.sockets.Connection;


/**
 * @author Christopher Lutz
 */
public abstract class BranchingServerListener implements ServerListener {

    @Override
    public Packet onConnect(Connection connection, Packet data){

        return data;
    }
}
