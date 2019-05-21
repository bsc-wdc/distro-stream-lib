package es.bsc.distrostreamlib.test;

import static org.junit.Assert.assertEquals;

import es.bsc.distrostreamlib.client.DistroStreamClient;
import es.bsc.distrostreamlib.exceptions.DistroStreamClientInitException;
import es.bsc.distrostreamlib.requests.Request;
import es.bsc.distrostreamlib.requests.StopRequest;
import es.bsc.distrostreamlib.server.DistroStreamServer;
import es.bsc.distrostreamlib.server.types.StreamBackend;

import java.util.Random;


public class CommonMethods {

    private static final String MASTER_IP = "localhost";

    private static final int BASE_MASTER_PORT = 49_049;
    private static final int MP_MAX_RAND = 100;
    private static int masterPort;

    public static final String CLIENT_IP = "localhost";


    /**
     * Starts the server.
     */
    public static void startServer(StreamBackend streamBackend) {
        // Wait for previous sockets to close
        System.out.println("Wait for previous sockets to close");
        try {
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Start server
        Random r = new Random();
        masterPort = BASE_MASTER_PORT + r.nextInt(MP_MAX_RAND);
        System.out.println("Start server at " + masterPort);
        DistroStreamServer.initAndStart(MASTER_IP, masterPort, streamBackend);
    }

    /**
     * Stops the server.
     */
    public static void stopServer() {
        // Send server stop
        System.out.println("Stop server");
        DistroStreamServer.setStop();
    }

    /**
     * Starts the client.
     * 
     * @throws DistroStreamClientInitException When an internal error occurs.
     */
    public static void startClient() throws DistroStreamClientInitException {
        // Start client
        System.out.println("Start client");
        try {
            DistroStreamClient.initAndStart(MASTER_IP, masterPort);
        } catch (DistroStreamClientInitException dcie) {
            System.err.println(dcie.getMessage());
            throw dcie;
        }
    }

    /**
     * Stops the client.
     */
    public static void stopClient() {
        // Send client stop
        System.out.println("Stop client");
        StopRequest stopRequest = new StopRequest();
        DistroStreamClient.request(stopRequest);
        stopRequest.waitProcessed();
        assertEquals(stopRequest.getErrorCode(), 0);
        printRequestAnswer(stopRequest);
    }

    /**
     * Prints the given request answer.
     * 
     * @param req Processed request.
     */
    public static void printRequestAnswer(Request req) {
        System.out.println("ERROR CODE: " + req.getErrorCode());
        System.out.println("ERROR MESSAGE: " + req.getErrorMessage());
        System.out.println("RESPONSE: " + req.getResponseMessage());
    }

}