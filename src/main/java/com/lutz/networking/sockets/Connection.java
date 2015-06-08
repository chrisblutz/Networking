package com.lutz.networking.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import com.lutz.networking.ClientListenable;
import com.lutz.networking.Listenable;
import com.lutz.networking.ServerListenable;
import com.lutz.networking.packets.Packet;
import com.lutz.networking.states.State;

/**
 * A wrapper for a {@code Socket}, used to send/receive {@code Packets}
 * 
 * @author Christopher Lutz
 */
public class Connection {

	private Listenable listenable;

	private Socket socket;

	private Thread listener;

	private State state, nextState = null;

	private List<Packet> dropped = new ArrayList<Packet>();

	private List<Packet> vitalDropped = new ArrayList<Packet>();

	private Packet waiting = null;

	private boolean running = false, serverSide = false, firstReceive = true,
			firstSend = true, shouldRespond = false, remoteClosed = false;

	private int readTimeout = 8000;

	private char nextChar = '\0';

	/**
	 * Creates a new {@code Connection} with the specified parameters
	 * 
	 * @param listenable
	 *            The {@code Listenable} object that created this
	 *            {@code Connection}
	 * @param socket
	 *            The {@code Socket} to wrap in this {@code Connection}
	 * @param state
	 *            The beginning {@code State} of this {@code Connection}
	 * @param serverSide
	 *            Whether or not this {@code Connection} represents a server
	 *            connection
	 */
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
		listener.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread arg0, Throwable arg1) {

				System.err.println(arg0.getName() + " has errored: "
						+ arg1.getClass().getName());
				arg1.printStackTrace();
			}
		});
		listener.setName("Packet Listener: "
				+ (serverSide ? "Server" : "Client") + " on IP " + getIp());
		running = true;
		shouldRespond = serverSide ? false : true;
		listener.start();
	}

	/**
	 * Sends a {@code Packet} across the connection
	 * 
	 * @param p
	 *            The {@code Packet} to send
	 * @param expectResponse
	 *            Whether or not the {@code Connection} should wait for a
	 *            response (decides whether or not to timeout the {@code read()}
	 *            calls
	 */
	public void sendPacket(Packet p, boolean expectResponse) {

		if (waiting != null) {

			dropped.add(waiting);

			if (waiting.isVital()) {

				vitalDropped.add(waiting);
			}
		}

		if (p.isEmpty()) {

			p.putData(Packet.EMPTY_PACKET);
		}

		waiting = p;
		this.shouldRespond = expectResponse;
	}

	/**
	 * Gets all {@code Packets} dropped by this {@code Connection}
	 * 
	 * @return A {@code Packet[]} containing all dropped {@code Packets}
	 */
	public Packet[] getDroppedPackets() {

		return dropped.toArray(new Packet[] {});
	}

	/**
	 * Gets the IP of this {@code Connection}
	 * 
	 * @return The IP of this {@code Connection}
	 */
	public String getIp() {

		return socket.getInetAddress().getHostAddress();
	}

	/**
	 * Checks the connection state of this {@code Connection}
	 * 
	 * @return Whether or not this {@code Connection} is connected
	 */
	public boolean isConnected() {

		return socket.isConnected();
	}

	/**
	 * Checks whether or not this {@code Connection} is closed
	 * 
	 * @return Whether or not this {@code Connection} is closed
	 */
	public boolean isClosed() {

		return socket.isClosed();
	}

	/**
	 * Checks if the remote side of this connection is closed
	 * 
	 * @return Whether the remote side of this connection is closed
	 */
	public boolean isRemoteClosed() {

		return remoteClosed;
	}

	/**
	 * Makes the {@code Connection} set itself back to the {@code Receiving}
	 * state
	 */
	public void setToReceive() {

		this.nextState = State.RECEIVING;
	}

	/**
	 * Makes the {@code Connection} set itself back to the {@code Sending} state
	 */
	public void setToSend() {

		this.nextState = State.SENDING;
	}

	/**
	 * Sets the timeout on reading from the {@code Connection}
	 * 
	 * @param timeout
	 *            The timeout in milliseconds
	 */
	public void setReadTimeout(int timeout) {

		this.readTimeout = timeout;
	}

	/**
	 * Gets the timeout on reading from the {@code Connection}
	 * 
	 * @return The timeout in milliseconds
	 */
	public int getReadTimeout() {

		return readTimeout;
	}

	/**
	 * Attempts to close this {@code Connection}
	 * 
	 * @throws IOException
	 *             If an I/O error occurs while shutting down this
	 *             {@code Connection}
	 */
	public void close() throws IOException {

		running = false;
		listener.interrupt();

		if (!socket.isClosed()) {

			socket.close();
		}
	}

	private final void listenerRun() {

		try {

			while (running && socket.isConnected() && !socket.isClosed()) {

				if (state == State.SENDING) {

					if (!socket.isClosed() && socket.isConnected()
							&& !socket.isOutputShutdown()) {

						OutputStream stream = socket.getOutputStream();

						PrintWriter out = new PrintWriter(stream, true);

						if (firstSend && serverSide) {

							Packet p = new Packet();

							p = ((ServerListenable) listenable)
									.fireListenerOnConnect(this, p);

							if (p.isEmpty()) {

								p.putData(Packet.EMPTY_PACKET);
							}

							out.println(p.toString());

							if (nextState != null) {

								state = nextState;
								nextState = null;

							} else {

								state = State.RECEIVING;
							}

							firstSend = false;

						} else if (waiting != null) {

							out.println(waiting.toString());

							waiting = null;

							if (nextState != null) {

								state = nextState;
								nextState = null;

							} else {

								state = State.RECEIVING;
							}

						} else if (waiting == null && vitalDropped.size() > 0) {

							out.println(vitalDropped.get(0).toString());

							vitalDropped.remove(0);

						} else {

							if (nextState != null) {

								state = nextState;
								nextState = null;
							}
						}
					}

				} else if (state == State.RECEIVING) {

					if (!socket.isClosed() && socket.isConnected()
							&& !socket.isInputShutdown()) {

						InputStream stream = socket.getInputStream();

						BufferedReader reader = new BufferedReader(
								new InputStreamReader(stream));

						String inTemp = null;

						StringBuilder read = new StringBuilder();

						try {

							if (shouldRespond) {

								shouldRespond = false;
								socket.setSoTimeout(readTimeout);

							} else {

								socket.setSoTimeout(0);
							}

							if (!socket.isClosed()
									&& (inTemp = reader.readLine()) != null) {

								read.append(inTemp);

							} else if (inTemp == null) {

								remoteClosed = true;
							}

						} catch (Exception e) {

							if (e instanceof SocketTimeoutException) {

								listenable.fireListenerOnTimeout(this);

							} else if (!(e instanceof SocketException)) {

								e.printStackTrace();
							}
						}

						String readStr = read.toString();

						if (nextChar != '\0') {

							readStr = nextChar + readStr;
						}

						if (!readStr.equals("::REMCL")) {

							if (!readStr.toString().equals("")) {

								final Packet p = Packet
										.getPacketFromString(readStr.toString());

								if (p.getData().length == 1) {

									if (p.getData()[0] == Packet.EMPTY_PACKET) {

										p.clearData();
									}
								}

								if (firstReceive && !serverSide) {

									((ClientListenable) listenable)
											.fireListenerOnConnect(p);

									state = State.SENDING;

									firstReceive = false;

								} else {

									listenable.fireListenerOnReceive(this, p);

									if (nextState != null) {

										state = nextState;
										nextState = null;

									} else {

										state = State.SENDING;
									}
								}
							}
						}
					}
				}
			}

			close();

		} catch (Throwable e) {

			boolean close = true;

			if (e instanceof SocketException) {

				if (socket.isClosed()
						|| e.getMessage().equalsIgnoreCase("socket closed")) {

					close = false;
				}

			} else if (e instanceof IOException) {

				remoteClosed = true;
			}

			if (close) {

				e.printStackTrace();

				running = false;
				listener.interrupt();
			}
		}
	}
}
