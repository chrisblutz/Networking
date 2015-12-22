package com.github.lutzblox;

import com.github.lutzblox.exceptions.reporters.ErrorReporterFactory;
import com.github.lutzblox.listeners.ClientListener;
import com.github.lutzblox.listeners.ServerListener;
import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.query.Query;
import com.github.lutzblox.query.QueryPolicy;
import com.github.lutzblox.query.QueryStatus;
import com.github.lutzblox.query.QueryType;
import com.github.lutzblox.sockets.Connection;
import junit.framework.TestCase;
import junit.framework.TestSuite;


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
                        + c.getIp()+"!");

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

        final Client client = new Client("0.0.0.0", 12355);
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

            Query q = client.getConnection().query("test-query", QueryType.CONNECTED_IPS);

            while (q.isWorking()) {

                Thread.sleep(100);
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

            Query q2 = client.getConnection().query("test-query2", QueryType.NUMBER_OF_CURRENT_CONNECTIONS);

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
