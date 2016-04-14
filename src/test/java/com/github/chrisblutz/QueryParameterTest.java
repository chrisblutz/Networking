package com.github.chrisblutz;

import com.github.chrisblutz.exceptions.reporters.ErrorReporterFactory;
import com.github.chrisblutz.listeners.ClientListener;
import com.github.chrisblutz.listeners.ServerListener;
import com.github.chrisblutz.packets.Packet;
import com.github.chrisblutz.query.Query;
import com.github.chrisblutz.query.QueryListener;
import com.github.chrisblutz.query.QueryStatus;
import com.github.chrisblutz.query.QueryType;
import com.github.chrisblutz.sockets.Connection;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.HashMap;
import java.util.Map;


public class QueryParameterTest extends TestCase {

    private boolean errored = false;
    private String errorMessage = "";

    public QueryParameterTest(String name) {

        super(name);
    }

    public static TestSuite suite() {

        return new TestSuite(QueryParameterTest.class);
    }

    public void testQueryParameter() {

        final QueryType TEST_TYPE = QueryType.createQueryType("test_query", new QueryListener() {

            @Override
            public Object onQuery(Connection connection, Listenable listenable, Map<String, Object> params) {

                if (params.containsKey("testparam")) {

                    if(params.get("testparam").equals("testvalue")){

                        return true;

                    }else {

                        errorMessage = "Parameters contained 'testparam' but the value was not 'testvalue'!";

                        return false;
                    }

                } else {

                    errorMessage = "Parameters did not contain 'testparam'!";

                    return false;
                }
            }
        });

        final Server server = new Server(12358, "QueryParameterTest");
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

        final Client client = new Client("0.0.0.0", 12358, "TestClient");
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

            System.out.println("Attempting test query...");

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("testparam", "testvalue");

            Query q = client.getConnection().query("test-query", TEST_TYPE, params);

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

                if (q.getValue() instanceof Boolean && (Boolean) q.getValue() == true) {

                    System.out.println("Query returned true!");

                } else {

                    System.out.println("Query returned false!");
                    errored = true;
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
