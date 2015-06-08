package com.github.lutzblox;

import java.util.ArrayList;
import java.util.List;

import com.github.lutzblox.listeners.NetworkListener;
import com.github.lutzblox.listeners.ServerListener;
import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.sockets.Connection;

public class ServerListenable extends Listenable {
	
	public ServerListener[] getServerListeners(){
		
		List<ServerListener> l = new ArrayList<ServerListener>();
		
		for(NetworkListener n : lists){
			
			if(n instanceof ServerListener){
				
				l.add((ServerListener) n);
			}
		}
		
		return l.toArray(new ServerListener[]{});
	}

	public Packet fireListenerOnConnect(Connection c, Packet data) {

		for (ServerListener l : getServerListeners()) {

			data = l.onConnect(c, data);
		}
		
		return data;
	}
}
