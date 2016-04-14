package com.github.chrisblutz;

import com.github.chrisblutz.exceptions.reporters.ErrorReporterFactory;
import com.github.chrisblutz.handshake.HandshakeClient;
import com.github.chrisblutz.handshake.HandshakeServer;
import com.github.chrisblutz.listeners.ClientListener;
import com.github.chrisblutz.listeners.ServerListener;
import com.github.chrisblutz.packets.Packet;
import com.github.chrisblutz.sockets.Connection;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class HandshakeTest extends TestCase {

    private long communications = 0;
    private boolean finished = false, errored = false;
    private String errorMessage = "";

    public HandshakeTest(String name) {

        super(name);
    }

    public static TestSuite suite() {

        return new TestSuite(HandshakeTest.class);
    }

    public void testHandshake() {

        final HandshakeServer server = new HandshakeServer(12356, "HandshakeTest");
        server.addErrorReporter(ErrorReporterFactory.newInstance());
        server.addNetworkListener(new ServerListener() {

            @Override
            public void onReceive(Connection connection, Packet packet) {

                Packet p = new Packet();
                p.putData("test", "test");

                server.sendPacket(connection, p);
            }

            @Override
            public Packet onConnect(Connection c, Packet data) {

                System.out.println("Server: Connection received from IP "
                        + c.getIp() + "!");

                data.putData("test", "test");

                return data;
            }

            @Override
            public void onTimeout(Connection connection) {

                System.out.println("Timed out!");

                errored = true;
                errorMessage = "Connection timed out!";
            }

            @Override
            public void onClientFailure(Connection c) {

            }
        });

        final HandshakeClient client = new HandshakeClient("0.0.0.0", 12356, "TestClient");
        client.addErrorReporter(ErrorReporterFactory.newInstance());
        client.addNetworkListener(new ClientListener() {

            @Override
            public void onReceive(Connection connection, Packet packet) {

                communications++;

                Packet p = new Packet();
                p.putData("test", "test");

                client.sendPacket(p, true);
            }

            @Override
            public void onConnect(Packet packet) {

                System.out
                        .println("Connected!");

                Packet p = new Packet();
                p.putData("test", "test");

                client.sendPacket(p, true);
            }

            @Override
            public void onTimeout(Connection connection) {

                System.out.println("Timed out!");

                errored = true;
                errorMessage = "Connection timed out!";
            }
        });

        try {

            System.out.println("Starting server...");

            server.start();

            System.out.println("Starting client...");

            client.connect();

            System.out.println("Waiting for 5 seconds...");

            Thread.sleep(5000);

            finished = true;

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

        System.out.println("Number of Non-Empty Client-Server Communications: " + communications);

        if (communications == 0) {

            errored = true;
            errorMessage = "0 communications!";
        }

        if (errored) {

            System.out.println("Errored - " + errorMessage);
            fail(errorMessage);

        } else {

            System.out.println("Success!");
        }
    }
}
