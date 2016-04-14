package com.github.chrisblutz;

import com.github.chrisblutz.exceptions.reporters.ErrorReporterFactory;
import com.github.chrisblutz.listeners.ClientListener;
import com.github.chrisblutz.listeners.ServerListener;
import com.github.chrisblutz.packets.Packet;
import com.github.chrisblutz.packets.encryption.EncryptionKey;
import com.github.chrisblutz.sockets.Connection;
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

        final EncryptionKey key = new EncryptionKey("THISISATESTKEY12", null);

        final Server server = new Server(12354, "EncryptedPacketTest");
        server.getConnections().setAllEncrypted(true, key);
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

                return data;
            }

            @Override
            public void onTimeout(Connection connection) {

            }

            @Override
            public void onClientFailure(Connection c) {

            }
        });

        final Client client = new Client("0.0.0.0", 12354, "TestClient");
        client.getConnection().setEncrypted(true, key);
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
