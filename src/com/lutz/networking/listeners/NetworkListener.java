package com.lutz.networking.listeners;

import com.lutz.networking.packets.Packet;

public interface NetworkListener {

	public void onReceive(Packet packet);
}
