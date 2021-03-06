package com.github.chrisblutz.networking;

import com.github.chrisblutz.networking.exceptions.reporters.ErrorReporterFactory;
import com.github.chrisblutz.networking.listeners.ClientListener;
import com.github.chrisblutz.networking.listeners.ServerListener;
import com.github.chrisblutz.networking.packets.Packet;
import com.github.chrisblutz.networking.sockets.Connection;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class MultipleClientsTest extends TestCase {

    private boolean finished1 = false, finished2 = false,
            errored = false;
    private String errorMessage = "";

    public MultipleClientsTest(String name) {

        super(name);
    }

    public static TestSuite suite() {

        return new TestSuite(MultipleClientsTest.class);
    }

    public void testMultipleClients() {

        final Server server = new Server(12346, "MultipleClientsTest");
        server.addErrorReporter(ErrorReporterFactory.newInstance());
        server.addNetworkListener(new ServerListener() {

            @Override
            public void onReceive(Connection connection, Packet packet) {

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

        final Client client1 = new Client("0.0.0.0", 12346, "TestClient1");
        client1.addErrorReporter(ErrorReporterFactory.newInstance());
        client1.addNetworkListener(new ClientListener() {

            @Override
            public void onReceive(Connection connection, Packet packet) {

            }

            @Override
            public void onConnect(Packet packet) {

                System.out.println("Client 1: Successfully received packet!");

                finished1 = true;
            }

            @Override
            public void onTimeout(Connection connection) {

            }
        });

        final Client client2 = new Client("0.0.0.0", 12346, "TestClient2");
        client2.addErrorReporter(ErrorReporterFactory.newInstance());
        client2.addNetworkListener(new ClientListener() {

            @Override
            public void onReceive(Connection connection, Packet packet) {

            }

            @Override
            public void onConnect(Packet packet) {

                System.out.println("Client 2: Successfully received packet!");

                finished2 = true;
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

        } catch (Exception e) {

            e.printStackTrace();

            errored = true;
            errorMessage = e.getClass().getName();

            finished1 = true;
            finished2 = true;
        }

        while (true) {

            try {

                Thread.sleep(1000);

            } catch (InterruptedException e) {
            }

            if (finished1 && finished2) {

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
