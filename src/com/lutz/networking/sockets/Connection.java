package com.lutz.networking.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.lutz.networking.ClientListenable;
import com.lutz.networking.Listenable;
import com.lutz.networking.Server;
import com.lutz.networking.packets.Packet;
import com.lutz.networking.states.State;

public class Connection {

	private Listenable listenable;

	private Socket socket;

	private Thread listener;

	private State state;

	private List<Packet> dropped = new ArrayList<Packet>();

	private Packet waiting = null;

	private boolean running = false, serverSide = false, firstReceive = true,
			firstSend = true;

	public Connection(Listenable listenable, Socket socket, State state,
			boolean serverSide) {

		this.listenable = listenable;
		this.socket = socket;
		this.state = state;
		this.serverSide = serverSide;

		listener = new Thread() {

			@Override
			public void run() {

				listenerRun();
			}
		};
		running = true;
		listener.start();
	}

	public void sendPacket(Packet p) {

		if (waiting != null) {

			dropped.add(waiting);
		}

		waiting = p;
	}

	public Packet[] getDroppedPackets() {

		return dropped.toArray(new Packet[] {});
	}

	public String getIp() {

		return socket.getInetAddress().getHostAddress();
	}

	public boolean isConnected() {

		return socket.isConnected();
	}

	public boolean isClosed() {

		return socket.isClosed();
	}

	public void close() throws IOException {

		listener.interrupt();
		socket.close();
	}

	private final void listenerRun() {

		try {

			while (running && socket.isConnected()) {

				if (state == State.SENDING) {

					OutputStream stream = socket.getOutputStream();

					PrintWriter out = new PrintWriter(stream, true);

					if (firstSend && serverSide && socket.isConnected()) {

						Packet p = new Packet(new String[] { "server-name" },
								new Object[] { ((Server) listenable)
										.getServerName() });

						out.println(p.toString());

						state = State.RECEIVING;

						firstSend = false;

					} else if (waiting != null && socket.isConnected()) {

						out.println(waiting.toString());

						if (!waiting.shouldSkipSending()) {

							state = State.RECEIVING;
						}
					}

				} else if (state == State.RECEIVING) {

					InputStream stream = socket.getInputStream();

					BufferedReader in = new BufferedReader(
							new InputStreamReader(stream));

					String inTemp = null;

					String read = "";

					if (in.ready() && (inTemp = in.readLine()) != null) {

						read += inTemp;
					}

					if (!read.equals("")) {

						final Packet p = Packet.getPacketFromString(read);

						if (firstReceive && !serverSide) {

							if (p.hasData("server-name")) {

								String name = p.getData("server-name")
										.toString();

								((ClientListenable) listenable)
										.fireListenerOnConnect(name);
							}

							state = State.SENDING;

							firstReceive = false;

						} else {

							listenable.fireListenerOnReceive(p);

							if (!p.shouldSkipSending()) {

								state = State.SENDING;
							}
						}
					}
				}
			}

		} catch (Throwable e) {

			e.printStackTrace();

			running = false;
			listener.interrupt();
		}
	}
}
