package es.bsc.distrostreamlib.test;

import static org.junit.Assert.assertEquals;

import es.bsc.distrostreamlib.client.DistroStreamClient;
import es.bsc.distrostreamlib.exceptions.DistroStreamClientInitException;
import es.bsc.distrostreamlib.requests.Request;
import es.bsc.distrostreamlib.requests.StopRequest;
import es.bsc.distrostreamlib.server.DistroStreamServer;


public class CommonMethods {

    public static final String MASTER_IP = "localhost";
    public static final int MASTER_PORT = 49_049;
    public static final String CLIENT_IP = "localhost";


    public static void startServer() {
        // Wait for previous sockets to close
        System.out.println("Wait for previous sockets to close");
        try {
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Start server
        System.out.println("Start server");
        DistroStreamServer.initAndStart(MASTER_PORT);
    }

    public static void stopServer() {
        // Send server stop
        System.out.println("Stop server");
        DistroStreamServer.setStop();
    }

    public static void startClient() throws DistroStreamClientInitException {
        // Start client
        System.out.println("Start client");
        try {
            DistroStreamClient.initAndStart(MASTER_IP, MASTER_PORT);
        } catch (DistroStreamClientInitException dcie) {
            System.err.println(dcie.getMessage());
            throw dcie;
        }
    }

    public static void stopClient() {
        // Send client stop
        System.out.println("Stop client");
        StopRequest stopRequest = new StopRequest();
        DistroStreamClient.request(stopRequest);
        stopRequest.waitProcessed();
        assertEquals(stopRequest.getErrorCode(), 0);
        printRequestAnswer(stopRequest);
    }

    public static void printRequestAnswer(Request req) {
        System.out.println("ERROR CODE: " + req.getErrorCode());
        System.out.println("ERROR MESSAGE: " + req.getErrorMessage());
        System.out.println("RESPONSE: " + req.getResponseMessage());
    }

}