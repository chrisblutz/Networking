package com.github.lutzblox.relay;

import com.github.lutzblox.Client;
import com.github.lutzblox.states.State;

public class RelayClient extends Client {

	public RelayClient(String ip, int port) {

		super(ip, port);
		this.setDefaultConnectionState(State.MUTUAL);
	}
}
