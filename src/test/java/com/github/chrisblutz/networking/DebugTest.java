package com.github.chrisblutz.networking;

import com.github.chrisblutz.networking.debugging.Debugger;
import com.github.chrisblutz.networking.exceptions.reporters.ErrorReporterFactory;
import com.github.chrisblutz.networking.listeners.ClientListener;
import com.github.chrisblutz.networking.listeners.ServerListener;
import com.github.chrisblutz.networking.packets.Packet;
import com.github.chrisblutz.networking.packets.encryption.EncryptionKey;
import com.github.chrisblutz.networking.sockets.Connection;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;


public class DebugTest extends TestCase {

    private int timesConnected = 0;
    private boolean errored = false;
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

        final EncryptionKey encKey = new EncryptionKey("THISISATESTKEY12", null);

        final Server server = new Server(12357, "DebugTest");
        server.addErrorReporter(ErrorReporterFactory.newInstance());
        server.setEncrypted(false, encKey);
        server.addNetworkListener(new ServerListener() {

            @Override
            public Packet onConnect(Connection c, Packet data) {

                if (timesConnected % 2 == 0) {

                    c.setEncrypted(true, encKey);

                    data.putData("encrypted", true);
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

            client.addErrorReporter(ErrorReporterFactory.newInstance());
            client.setEncrypted(false, encKey);
            client.addNetworkListener(new ClientListener() {

                @Override
                public void onConnect(Packet packet) {

                    if(packet.hasData("encrypted") && (Boolean) packet.getData("encrypted") == true){

                        client.getConnection().setEncrypted(true, encKey);
                    }

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

                // Ignore AES decryption errors.  They are a side effect of this test and do not appear anywhere else in the testing process
                // This block also ignores sleep interrupt exceptions
            }
        }

        try {

            for(Client client : clientList){

                client.close();
            }

            server.close();

        } catch (Exception e) {

            e.printStackTrace();
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
