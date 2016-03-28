package com.github.lutzblox;

import com.github.lutzblox.debugging.Debugger;
import com.github.lutzblox.exceptions.reporters.ErrorReporterFactory;
import com.github.lutzblox.listeners.ClientListener;
import com.github.lutzblox.listeners.ServerListener;
import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.packets.encryption.EncryptionKey;
import com.github.lutzblox.packets.encryption.EncryptionKeyResetListener;
import com.github.lutzblox.sockets.Connection;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;


public class DebugTest extends TestCase {

    private int timesConnected = 0;
    private boolean finished = false, errored = false;
    private String errorMessage = "";

    private List<Client> clientList = new ArrayList<Client>();

    public DebugTest(String name) {

        super(name);
    }

    public static TestSuite suite() {

        return new TestSuite(DebugTest.class);
    }

    public void testDebug() {

        System.setProperty("networkingdebug", "true");

        final EncryptionKey encKey = new EncryptionKey("THISISATESTKEY12", new EncryptionKeyResetListener() {

            @Override
            public boolean resetKey() {

                return false;
            }
        });

        final Server server = new Server(12357, "DebugTest");
        server.addErrorReporter(ErrorReporterFactory.newInstance());
        server.addNetworkListener(new ServerListener() {

            @Override
            public Packet onConnect(Connection c, Packet data) {

                if (timesConnected % 2 == 0) {

                    c.setEncrypted(true, encKey);
                }

                timesConnected++;

                return data;
            }

            @Override
            public void onClientFailure(Connection c) {

            }

            @Override
            public void onReceive(Connection connection, Packet packet) {

                connection.sendPacket(new Packet(), true);
            }

            @Override
            public void onTimeout(Connection connection) {

            }
        });

        for (int i = 0; i < 10; i++) {

            final Client client = new Client("0.0.0.0", 12357, "TestClient" + (clientList.size() + 1));

            if (clientList.size() % 2 == 0) {

                client.setEncrypted(true, encKey);
            }

            client.addErrorReporter(ErrorReporterFactory.newInstance());
            client.addNetworkListener(new ClientListener() {

                @Override
                public void onConnect(Packet packet) {

                    client.sendPacket(new Packet(), true);
                }

                @Override
                public void onReceive(Connection connection, Packet packet) {

                    connection.sendPacket(new Packet(), true);
                }

                @Override
                public void onTimeout(Connection connection) {

                }
            });

            clientList.add(client);
        }

        try {

            server.start();

            for(Client client : clientList){

                client.connect();
            }

        } catch (Exception e) {

            fail("Could not open connection!");
        }

        while (Debugger.isDebugWindowVisible()) {

            try {

                Thread.sleep(1000);

            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        try {

            for(Client client : clientList){

                client.close();
            }

            server.close();

        } catch (Exception e) {

        }

        System.setProperty("networkingdebug", "false");

        if (errored) {

            fail(errorMessage);

        } else {

            System.out.println("Success!");
        }
    }

    public static void main(String[] args) {

        new DebugTest("").testDebug();
    }
}
