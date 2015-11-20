package com.github.lutzblox;

import com.github.lutzblox.exceptions.reporters.ErrorReporterFactory;
import com.github.lutzblox.listeners.ClientListener;
import com.github.lutzblox.listeners.ServerListener;
import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.packets.encryption.EncryptionKey;
import com.github.lutzblox.sockets.Connection;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class EncryptedPacketTest extends TestCase {

    private boolean finished = false, errored = false;
    private String errorMessage = "";

    public EncryptedPacketTest(String name) {

        super(name);
    }

    public static TestSuite suite() {

        return new TestSuite(EncryptedPacketTest.class);
    }

    public void testEncryptedPacket() {

        EncryptionKey.setKey("THISISATESTKEY12");

        final Server server = new Server(12354, "EncryptedPacketTest");
        server.addErrorReporter(ErrorReporterFactory.newInstance());
        server.addNetworkListener(new ServerListener() {

            @Override
            public void onReceive(Connection connection, Packet packet) {

            }

            @Override
            public Packet onConnect(Connection c, Packet data) {

                System.out.println("Server: Connection received from IP "
                        + c.getIp());

                data.putData("testKey", "testValue");
                data.setEncrypt(true);

                return data;
            }

            @Override
            public void onTimeout(Connection connection) {

            }

            @Override
            public void onClientFailure(Connection c) {

            }
        });

        final Client client = new Client("localhost", 12354);
        client.addErrorReporter(ErrorReporterFactory.newInstance());
        client.addNetworkListener(new ClientListener() {

            @Override
            public void onReceive(Connection connection, Packet packet) {

            }

            @Override
            public void onConnect(Packet packet) {

                if (packet.hasData("testKey") && packet.getData("testKey").equals("testValue")) {

                    System.out
                            .println("Client: Successfully received encrypted packet!");

                } else {

                    errored = true;
                    errorMessage = packet.hasData("testKey") ? "Incorrect value received!" : "No 'testKey' in the packet!";
                }

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

                Thread.sleep(100);

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
