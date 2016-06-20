package com.github.chrisblutz.networking;

import com.github.chrisblutz.networking.exceptions.reporters.ErrorReporterFactory;
import com.github.chrisblutz.networking.listeners.ClientListener;
import com.github.chrisblutz.networking.listeners.ServerListener;
import com.github.chrisblutz.networking.listeners.branching.BranchRegistry;
import com.github.chrisblutz.networking.listeners.branching.BranchingServerListener;
import com.github.chrisblutz.networking.packets.Packet;
import com.github.chrisblutz.networking.sockets.Connection;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class BranchedConnectionTest extends TestCase {

    private boolean finished = false, errored = false;
    private String errorMessage = "";

    public BranchedConnectionTest(String name) {

        super(name);
    }

    public static TestSuite suite() {

        return new TestSuite(BranchedConnectionTest.class);
    }

    public void testBranchedConnection() {

        final Server server = new Server(12359, "BranchedConnectionTest");
        server.addErrorReporter(ErrorReporterFactory.newInstance());
        server.addNetworkListener(new ServerListener() {

            @Override
            public void onReceive(Connection connection, Packet packet) {

                System.out.println("Branching listener did not intercept the received packet!");

                errored = true;
                errorMessage = "Branching listener did not intercept the packet!";
                finished = true;
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

        BranchRegistry.registerBranchId("testbranch", new BranchingServerListener() {

            @Override
            public void onClientFailure(Connection c) {

            }

            @Override
            public void onReceive(Connection connection, Packet packet) {

                System.out.println("Branching listener intercepted received packet!");

                finished = true;
            }

            @Override
            public void onTimeout(Connection connection) {

            }
        });

        final Client client = new Client("0.0.0.0", 12359, "TestClient");
        client.addErrorReporter(ErrorReporterFactory.newInstance());
        client.addNetworkListener(new ClientListener() {

            @Override
            public void onReceive(Connection connection, Packet packet) {

            }

            @Override
            public void onConnect(Packet packet) {

                client.sendPacket(Client.addBranchCommandPacketData(new Packet(), "testbranch"), false);
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
