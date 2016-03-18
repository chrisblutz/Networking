package com.github.lutzblox;

import com.github.lutzblox.exceptions.reporters.ErrorReporterFactory;
import com.github.lutzblox.listeners.ClientListener;
import com.github.lutzblox.listeners.ServerListener;
import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.sockets.Connection;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class FailedClientTest extends TestCase {

    private boolean errored = false, closedClient = false;
    private String errorMessage = "";

    public FailedClientTest(String name) {

        super(name);
    }

    public static TestSuite suite() {

        return new TestSuite(FailedClientTest.class);
    }

    public void testFailedClient() {

        final Server server = new Server(12348, "FailedClientTest");
        server.addErrorReporter(ErrorReporterFactory.newInstance());
        server.addNetworkListener(new ServerListener() {

            @Override
            public void onReceive(Connection connection, Packet packet) {

                server.sendPacket(new Packet(), false);
            }

            @Override
            public Packet onConnect(Connection c, Packet data) {

                System.out.println("Server: Connection received from IP "
                        + c.getIp());

                return data;
            }

            @Override
            public void onTimeout(Connection connection) {

            }

            @Override
            public void onClientFailure(Connection c) {

            }
        });

        final Client client = new Client("0.0.0.0", 12348);
        client.addErrorReporter(ErrorReporterFactory.newInstance());
        client.addNetworkListener(new ClientListener() {

            @Override
            public void onReceive(Connection connection, Packet packet) {

            }

            @Override
            public void onConnect(Packet packet) {

                System.out.println("Sending packet expecting response to trigger timeout...");

                client.sendPacket(new Packet(), false);
            }

            @Override
            public void onTimeout(Connection connection) {

            }
        });

        try {

            System.out.println("Starting server...");

            server.start();

            System.out.println("Starting client...");

            client.connect();

            System.out.println("Waiting...");

            while (true) {

                try {

                    Thread.sleep(100);

                } catch (InterruptedException e) {
                }

                if (server.getConnections().size() >= 1) {

                    break;
                }
            }

            System.out.println("Closing client... (Note: If the check rate for failed clients is low for the server, you may see a bit of a delay here.  Be patient.)");

            client.close();

            closedClient = true;

        } catch (Exception e) {

            e.printStackTrace();

            errored = true;
            errorMessage = e.getClass().getName();
        }

        while (true) {

            try {

                Thread.sleep(100);

            } catch (InterruptedException e) {
            }

            if (closedClient && server.getConnections().size() == 0) {

                break;
            }
        }

        try {

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
