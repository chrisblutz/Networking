package com.lutz.networking;

import java.util.ArrayList;
import java.util.List;

import com.lutz.networking.listeners.NetworkListener;
import com.lutz.networking.listeners.ServerListener;
import com.lutz.networking.packets.Packet;
import com.lutz.networking.sockets.Connection;

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
