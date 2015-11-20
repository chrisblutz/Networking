package com.github.lutzblox;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.github.lutzblox.exceptions.reporters.ErrorReporterFactory;
import com.github.lutzblox.listeners.ClientListener;
import com.github.lutzblox.listeners.ServerListener;
import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.sockets.Connection;


public class SendRecieveTest extends TestCase {

    private boolean finished = false, errored = false;
    private String errorMessage = "";
    private int timesSent = 1;

    public SendRecieveTest(String name) {

        super(name);
    }

    public static TestSuite suite() {

        return new TestSuite(SendRecieveTest.class);
    }

    public void testSendReceive() {

        final Server server = new Server(12349, "SendRecieveTest");
        server.addErrorReporter(ErrorReporterFactory.newInstance());
        server.addNetworkListener(new ServerListener() {

            @Override
            public void onReceive(Connection connection, Packet packet) {

                System.out.println("Server: Successfully received packet!");

                if (timesSent < 10) {

                    timesSent++;

                    server.sendPacket(new Packet(), true);
                }
            }

            @Override
            public Packet onConnect(Connection c, Packet data) {

                System.out.println("Server: Connection received from IP "
                        + c.getIp());

                data.putData("server-name", server.getServerName());

                return data;
            }

            @Override
            public void onTimeout(Connection connection) {

                errored = true;
                errorMessage = "Connection timed out!";

                finished = true;
            }

            @Override
            public void onClientFailure(Connection c) {

            }
        });

        final Client client = new Client("0.0.0.0", 12349);
        client.addErrorReporter(ErrorReporterFactory.newInstance());
        client.addNetworkListener(new ClientListener() {

            @Override
            public void onReceive(Connection connection, Packet packet) {

                System.out.println("Client: Successfully received packet!");

                if (timesSent < 10) {

                    timesSent++;

                    client.sendPacket(new Packet(), true);
                }
            }

            @Override
            public void onConnect(Packet packet) {

                System.out.println("Client: Successfully received packet!");

                if (timesSent < 10) {

                    timesSent++;

                    client.sendPacket(new Packet(), true);
                }
            }

            @Override
            public void onTimeout(Connection connection) {

                errored = true;
                errorMessage = "Connection timed out!";

                finished = true;
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

                Thread.sleep(100);

            } catch (InterruptedException e) {
            }

            if (finished || timesSent >= 10) {

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
