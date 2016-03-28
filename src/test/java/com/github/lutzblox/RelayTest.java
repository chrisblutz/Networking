package com.github.lutzblox;

import com.github.lutzblox.exceptions.reporters.ErrorReporterFactory;
import com.github.lutzblox.listeners.ClientListener;
import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.relay.RelayClient;
import com.github.lutzblox.relay.RelayServer;
import com.github.lutzblox.relay.listeners.RelayListener;
import com.github.lutzblox.sockets.Connection;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class RelayTest extends TestCase {

    private boolean finished = false, errored = false;
    private String errorMessage = "";

    public RelayTest(String name) {

        super(name);
    }

    public static TestSuite suite() {

        return new TestSuite(RelayTest.class);
    }

    public void testRelay() {

        final RelayServer server = new RelayServer(12352, "RelayTest");
        server.addErrorReporter(ErrorReporterFactory.newInstance());
        server.addRelayListener(new RelayListener() {

            @Override
            public void onReceive(RelayServer server, Connection c, Packet data) {

                System.out.println("Server received client 2's packet!");
            }

            @Override
            public void onTimeout(RelayServer server, Connection c) {

            }

            @Override
            public Packet onConnect(RelayServer server, Connection c,
                                    Packet data) {

                System.out.println("Connection received from IP " + c.getIp());

                if (server.getConnections().size() > 0
                        && c != server.getConnections().getConnectionsAsArray()[0]
                        && server.getGroups().length == 0) {

                    server.group("test", c, server.getConnections().getConnectionsAsArray()[0]);

                } else if (server.getConnections().size() > 1
                        && server.getGroups().length == 0) {

                    server.group(
                            "test",
                            c,
                            c != server.getConnections().getConnectionsAsArray()[0] ? server
                                    .getConnections().getConnectionsAsArray()[0] : server
                                    .getConnections().getConnectionsAsArray()[1]);
                }

                return data;
            }
        });

        final RelayClient client1 = new RelayClient("0.0.0.0", 12352, "TestClient1");
        client1.addErrorReporter(ErrorReporterFactory.newInstance());
        client1.addNetworkListener(new ClientListener() {

            @Override
            public void onReceive(Connection connection, Packet packet) {

                System.out.println("Received packet successfully in client 1!");

                finished = true;
            }

            @Override
            public void onConnect(Packet packet) {

            }

            @Override
            public void onTimeout(Connection connection) {

            }
        });

        final RelayClient client2 = new RelayClient("0.0.0.0", 12352, "TestClient2");
        client2.addErrorReporter(ErrorReporterFactory.newInstance());
        client2.addNetworkListener(new ClientListener() {

            @Override
            public void onReceive(Connection connection, Packet packet) {

                errored = true;
                errorMessage = "Client 2 received a packet when it shouldn't have!";

                finished = true;
            }

            @Override
            public void onConnect(Packet packet) {

            }

            @Override
            public void onTimeout(Connection connection) {

            }
        });

        try {

            System.out.println("Starting server...");

            server.start();

            System.out.println("Starting client 1...");

            client1.connect();

            System.out.println("Starting client 2...");

            client2.connect();

            System.out.println("Waiting...");

            while (server.getConnections().size() < 2) {

                try {

                    Thread.sleep(1000);

                } catch (Exception e) {
                }
            }

            System.out.println("Sending packet via client 2...");

            client2.sendPacket(new Packet(), false);

        } catch (Exception e) {

            e.printStackTrace();

            errored = true;
            errorMessage = e.getClass().getName();

            finished = true;
        }

        while (true) {

            try {

                Thread.sleep(1000);

            } catch (InterruptedException e) {
            }

            if (finished) {

                break;
            }
        }

        try {

            client1.close();
            client2.close();
            server.close();

        } catch (Exception e) {

            e.printStackTrace();

            errored = true;
            errorMessage = e.getClass().getName();
        }

        if (errored) {

            System.out.println("Errored - " + errorMessage);
            fail(errorMessage);

        } else {

            System.out.println("Success!");
        }
    }
}
