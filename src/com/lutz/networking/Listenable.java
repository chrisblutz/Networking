package com.lutz.networking;

import java.util.ArrayList;
import java.util.List;

import com.lutz.networking.listeners.NetworkListener;
import com.lutz.networking.packets.Packet;

public class Listenable {

	protected List<NetworkListener> lists = new ArrayList<NetworkListener>();

	public void addNetworkListener(NetworkListener listener) {

		lists.add(listener);
	}

	public NetworkListener[] getNetworkListeners() {

		return lists.toArray(new NetworkListener[] {});
	}

	public void fireListenerOnReceive(Packet packet) {

		for (NetworkListener l : getNetworkListeners()) {

			l.onReceive(packet);
		}
	}
}
