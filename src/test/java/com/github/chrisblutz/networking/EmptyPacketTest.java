package com.github.chrisblutz.networking;

import com.github.chrisblutz.networking.exceptions.reporters.ErrorReporterFactory;
import com.github.chrisblutz.networking.listeners.ClientListener;
import com.github.chrisblutz.networking.listeners.ServerListener;
import com.github.chrisblutz.networking.packets.Packet;
import com.github.chrisblutz.networking.sockets.Connection;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class EmptyPacketTest extends TestCase {

    private boolean finished = false, errored = false;
    private String errorMessage = "";

    public EmptyPacketTest(String name) {

        super(name);
    }

    public static TestSuite suite() {

        return new TestSuite(EmptyPacketTest.class);
    }

    public void testEmptyPacket() {

        final Server server = new Server(12345, "EmptyPacketTest");
        server.addErrorReporter(ErrorReporterFactory.newInstance());
        server.addNetworkListener(new ServerListener() {

            @Override
            public void onReceive(Connection connection, Packet packet) {

            }

            @Override
            public Packet onConnect(Connection c, Packet data) {

                System.out.println("Server: Connection received from IP "
                        + c.getIp());
                data.clearData();

                return data;
            }

            @Override
            public void onTimeout(Connection connection) {

            }

            @Override
            public void onClientFailure(Connection c) {

            }
        });

        final Client client = new Client("0.0.0.0", 12345, "TestClient");
        client.addErrorReporter(ErrorReporterFactory.newInstance());
        client.addNetworkListener(new ClientListener() {

            @Override
            public void onReceive(Connection connection, Packet packet) {

            }

            @Override
            public void onConnect(Packet packet) {

                System.out
                        .println("Client: Successfully received empty packet!");

                finished = true;
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

            client.close();
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
