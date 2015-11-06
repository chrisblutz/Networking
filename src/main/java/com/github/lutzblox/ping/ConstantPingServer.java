package com.github.lutzblox.ping;


import com.github.lutzblox.Server;
import com.github.lutzblox.listeners.NetworkListener;
import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.sockets.Connection;
import com.github.lutzblox.states.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConstantPingServer extends Server {

    private Map<Connection, List<Packet>> waiting = new HashMap<Connection, List<Packet>>();

    public ConstantPingServer(int port, String serverName) {

        super(port, serverName);
    }

    public ConstantPingServer(int port, String serverName, int maxConnections) {

        super(port, serverName, maxConnections);
    }

    public ConstantPingServer(int port, String serverName, long failCheck) {

        super(port, serverName, failCheck);
    }

    public ConstantPingServer(int port, String serverName, int maxConnections, long failCheck) {

        super(port, serverName, maxConnections, failCheck);
    }

    /**
     * Sends a {@code Packet} across all connections to the clients on the
     * receiving ends
     *
     * @param p              The {@code Packet} to send
     * @param expectResponse {@code ConstantPingServer} disregards this parameter, because it always expects a response
     */
    @Override
    public void sendPacket(Packet p, boolean expectResponse) {

        for(Connection c : getConnections()){

            if(!waiting.containsKey(c)){

                waiting.put(c, new ArrayList<Packet>());
            }

            waiting.get(c).add(p);
        }
    }

    /**
     * This method has no effect in a {@code ConstantPingServer}
     */
    @Override
    public void setToReceive() {

    }

    /**
     * This method has no effect in a {@code ConstantPingServer}
     */
    @Override
    public void setToSend() {

    }

    /**
     * This method has no effect in {@code ConstantPingServer} because the default state is locked on {@code SENDING}
     *
     * @param state The default {@code State}
     */
    @Override
    public void setDefaultConnectionState(State state) {

    }

    @Override
    public void fireListenerOnReceive(Connection connection, Packet packet) {

        if (!packet.isEmpty()) {

            for (NetworkListener l : getNetworkListeners()) {

                l.onReceive(connection, packet);
            }
        }

        Packet toSend = new Packet();

        if(waiting.containsKey(connection)) {

            List<Packet> waitingList = waiting.get(connection);

            if (waitingList != null && waitingList.size() > 0) {

                for (Packet p : waitingList) {

                    if (p.isVital())
                        toSend.setVital(true);

                    toSend.putData(p.getData());
                }

                waitingList.clear();
            }
        }

        connection.sendPacket(toSend, true);
    }
}
