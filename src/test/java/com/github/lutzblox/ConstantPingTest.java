package com.github.lutzblox;


import com.github.lutzblox.exceptions.reporters.ErrorReporterFactory;
import com.github.lutzblox.ping.ConstantPingClient;
import com.github.lutzblox.ping.ConstantPingServer;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class ConstantPingTest extends TestCase {

    private boolean finished = false, errored = false;
    private String errorMessage = "";

    public ConstantPingTest(String name) {

        super(name);
    }

    public static TestSuite suite() {

        return new TestSuite(ConstantPingTest.class);
    }

    public void testConstantPing() {

        final Server server = new ConstantPingServer(12353, "ConstantPingTest");
        server.addErrorReporter(ErrorReporterFactory.newInstance());

        final Client client = new ConstantPingClient("0.0.0.0", 12353);
        client.addErrorReporter(ErrorReporterFactory.newInstance());

        try {

            System.out.println("Starting server...");

            server.start();

            System.out.println("Starting client...");

            client.connect();

            System.out.println("Waiting for 5 seconds...");

            Thread.sleep(5000);

            finished = true;

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

            if(finished){

                break;
            }
        }

        try{

            client.close();
            server.close();

        }catch(Exception e){

            e.printStackTrace();

            errored = true;
            errorMessage = e.getClass().getName();
        }

        if(errored){

            System.out.println("Errored - "+errorMessage);
            fail(errorMessage);

        }else{

            System.out.println("Average client ping: "+client.getConnection().getAveragePing());
            System.out.println("Success!");
        }
    }
}
