package com.github.lutzblox.relay.listeners;

import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.relay.RelayServer;
import com.github.lutzblox.sockets.Connection;

public interface RelayListener {

	public void onReceive(RelayServer server, Connection c, Packet data);

	public void onTimeout(RelayServer server, Connection c);

	public Packet onConnect(RelayServer server, Connection c, Packet data);
}
