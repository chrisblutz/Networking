package com.lutz.networking.listeners;

public interface ClientListener extends NetworkListener {

	public void onConnect(String serverName);
}
