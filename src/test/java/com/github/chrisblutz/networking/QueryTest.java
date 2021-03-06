package com.github.chrisblutz.networking;

import com.github.chrisblutz.networking.exceptions.reporters.ErrorReporterFactory;
import com.github.chrisblutz.networking.listeners.ClientListener;
import com.github.chrisblutz.networking.listeners.ServerListener;
import com.github.chrisblutz.networking.packets.Packet;
import com.github.chrisblutz.networking.query.Query;
import com.github.chrisblutz.networking.query.QueryPolicy;
import com.github.chrisblutz.networking.query.QueryStatus;
import com.github.chrisblutz.networking.query.QueryType;
import com.github.chrisblutz.networking.sockets.Connection;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.HashMap;


public class QueryTest extends TestCase {

    private boolean errored = false;
    private String errorMessage = "";

    public QueryTest(String name) {

        super(name);
    }

    public static TestSuite suite() {

        return new TestSuite(QueryTest.class);
    }

    public void testQuery() {

        final Server server = new Server(12355, "QueryTest");
        server.addErrorReporter(ErrorReporterFactory.newInstance());
        server.addNetworkListener(new ServerListener() {

            @Override
            public void onReceive(Connection connection, Packet packet) {

            }

            @Override
            public Packet onConnect(Connection c, Packet data) {

                System.out.println("Server: Connection received from IP "
                        + c.getIp() + "!");

                return data;
            }

            @Override
            public void onTimeout(Connection connection) {

            }

            @Override
            public void onClientFailure(Connection c) {

            }
        });
        server.setQueryPolicy(QueryType.CONNECTED_IPS, QueryPolicy.getAcceptancePolicy());
        server.setQueryPolicy(QueryType.NUMBER_OF_CURRENT_CONNECTIONS, QueryPolicy.getRejectionPolicy("Test rejection!"));

        final Client client = new Client("0.0.0.0", 12355, "TestClient");
        client.addErrorReporter(ErrorReporterFactory.newInstance());
        client.addNetworkListener(new ClientListener() {

            @Override
            public void onReceive(Connection connection, Packet packet) {

            }

            @Override
            public void onConnect(Packet packet) {

                System.out
                        .println("Client: Connected!");
            }

            @Override
            public void onTimeout(Connection connection) {

                System.out.println("Timed out client!");
            }
        });

        try {

            System.out.println("Starting server...");

            server.start();

            System.out.println("Starting client...");

            client.connect();

            System.out.println("Attempting query CONNECTED_IPS, expecting acceptance...");

            Query q = client.getConnection().query("test-query", QueryType.CONNECTED_IPS, new HashMap<String, Object>());

            while (q.isWorking()) {

                Thread.sleep(1000);
            }

            if (q.getStatus() == QueryStatus.Status.REJECTED) {

                System.out.println("Rejected: " + q.getStatusMessage());
                errored = true;
                errorMessage = "Query rejected!";

            } else if (q.getStatus() == QueryStatus.Status.TIMED_OUT) {

                System.out.println("Timed Out: " + q.getStatusMessage());
                errored = true;
                errorMessage = "Connection timed out!";

            } else if (q.getStatus() == QueryStatus.Status.SUCCESSFUL) {

                System.out.println("Success!");

                Object result = q.getValue();

                if (result instanceof String[]) {

                    String[] ips = (String[]) result;

                    System.out.print("Result: ");

                    for (int i = 0; i < ips.length; i++) {

                        System.out.print(ips[i]);

                        if (i < ips.length - 1) {

                            System.out.print(", ");
                        }
                    }

                    System.out.println();

                } else {

                    System.out.println("Incorrect type!  Expected: String[], Received: " + result.getClass().getSimpleName());
                    errored = true;
                    errorMessage = "Incorrect type received!";
                }
            }

            System.out.println("Attempting query NUMBER_OF_CURRENT_CONNECTIONS, expecting rejection...");

            Query q2 = client.getConnection().query("test-query2", QueryType.NUMBER_OF_CURRENT_CONNECTIONS, new HashMap<String, Object>());

            while (q2.isWorking()) {

                Thread.sleep(100);
            }

            if (q2.getStatus() == QueryStatus.Status.REJECTED) {

                System.out.println("Rejected (Test success): " + q2.getStatusMessage());

            } else if (q2.getStatus() == QueryStatus.Status.TIMED_OUT) {

                System.out.println("Timed Out: " + q2.getStatusMessage());
                errored = true;
                errorMessage = "Connection timed out!";

            } else if (q2.getStatus() == QueryStatus.Status.SUCCESSFUL) {

                System.out.println("Success (test fail)!");

                Object result = q2.getValue();

                if (result instanceof String[]) {

                    String[] ips = (String[]) result;

                    for (int i = 0; i < ips.length; i++) {

                        System.out.print(ips[i]);

                        if (i < ips.length - 1) {

                            System.out.print(", ");
                        }
                    }

                    System.out.println();
                    errored = true;
                    errorMessage = "Expected rejection!";

                } else {

                    System.out.println("Incorrect type!  Expected: String[], Received: " + result.getClass().getSimpleName());
                    errored = true;
                    errorMessage = "Incorrect type received, expected rejection!";
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            errored = true;
            errorMessage = e.getClass().getName();
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
