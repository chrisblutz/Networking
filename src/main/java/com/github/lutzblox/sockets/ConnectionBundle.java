package com.github.lutzblox.sockets;


import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;


public class ConnectionBundle implements Iterable<Connection> {

    private CopyOnWriteArrayList<Connection> connections = new CopyOnWriteArrayList<Connection>();

    public ConnectionBundle() {

    }

    public ConnectionBundle(Connection... connections) {

        this.connections.addAll(Arrays.asList(connections));
    }

    public void add(Connection connection) {

        connections.add(connection);
    }

    public boolean remove(Connection connection) {

        return connections.remove(connection);
    }

    public Connection[] getConnectionsAsArray() {

        return connections.toArray(new Connection[connections.size()]);
    }

    public int size(){

        return connections.size();
    }

    @Override
    public Iterator<Connection> iterator() {

        return connections.iterator();
    }

    public Connection getConnectionForIp(String ip) {

        for (Connection c : connections) {

            if (c.getIp().equals(ip)) {

                return c;
            }
        }

        return null;
    }

    public void setAllToReceive(){

        for(Connection c : connections){

            c.setToReceive();
        }
    }

    public void setAllToSend(){

        for(Connection c : connections){

            c.setToSend();
        }
    }

    public void setAllEncrypted(boolean encrypted){

        for(Connection c : connections){

            c.setEncrypted(encrypted);
        }
    }
}
