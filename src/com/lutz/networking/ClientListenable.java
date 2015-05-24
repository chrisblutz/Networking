package com.lutz.networking;

import java.util.ArrayList;
import java.util.List;

import com.lutz.networking.listeners.ClientListener;
import com.lutz.networking.listeners.NetworkListener;

public class ClientListenable extends Listenable {

	@Override
	public void addNetworkListener(NetworkListener listener) {

		lists.add(listener);
	}

	@Override
	public NetworkListener[] getNetworkListeners() {

		return lists.toArray(new NetworkListener[] {});
	}
	
	public ClientListener[] getClientListeners(){
		
		List<ClientListener> l = new ArrayList<ClientListener>();
		
		for(NetworkListener n : lists){
			
			if(n instanceof ClientListener){
				
				l.add((ClientListener) n);
			}
		}
		
		return l.toArray(new ClientListener[]{});
	}

	public void fireListenerOnConnect(String serverName) {

		for (ClientListener l : getClientListeners()) {

			l.onConnect(serverName);
		}
	}
}
