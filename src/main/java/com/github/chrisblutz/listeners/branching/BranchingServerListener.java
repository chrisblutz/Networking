package com.github.chrisblutz.listeners.branching;

import com.github.chrisblutz.listeners.ServerListener;
import com.github.chrisblutz.packets.Packet;
import com.github.chrisblutz.sockets.Connection;


/**
 * @author Christopher Lutz
 */
public abstract class BranchingServerListener implements ServerListener {

    @Override
    public Packet onConnect(Connection connection, Packet data){

        return data;
    }
}
