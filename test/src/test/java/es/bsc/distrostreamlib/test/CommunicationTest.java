package es.bsc.distrostreamlib.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import es.bsc.distrostreamlib.client.DistroStreamClient;
import es.bsc.distrostreamlib.exceptions.DistroStreamClientInitException;
import es.bsc.distrostreamlib.requests.RegisterClientRequest;
import es.bsc.distrostreamlib.requests.Request;
import es.bsc.distrostreamlib.requests.StopRequest;
import es.bsc.distrostreamlib.requests.UnregisterClientRequest;
import es.bsc.distrostreamlib.server.DistroStreamServer;


public class CommunicationTest {

    @Test
    public void communicationClientServer() throws DistroStreamClientInitException {
        // Start server
        System.out.println("Start server");
        final String masterIP = "localhost";
        final int masterPort = 49_049;
        DistroStreamServer.initAndStart(masterPort);

        // Start client
        System.out.println("Start client");
        final String clientIP = "localhost";
        try {
            DistroStreamClient.initAndStart(masterIP, masterPort);
        } catch (DistroStreamClientInitException dcie) {
            System.err.println(dcie.getMessage());
            throw dcie;
        }

        // Send request to client
        System.out.println("Register client");
        RegisterClientRequest registerRequest = new RegisterClientRequest(clientIP);
        DistroStreamClient.request(registerRequest);
        registerRequest.waitProcessed();
        assertEquals(registerRequest.getErrorCode(), 0);
        printRequestAnswer(registerRequest);

        // Send request to client
        System.out.println("Unregister client");
        UnregisterClientRequest unregisterRequest = new UnregisterClientRequest(clientIP);
        DistroStreamClient.request(unregisterRequest);
        unregisterRequest.waitProcessed();
        assertEquals(unregisterRequest.getErrorCode(), 0);
        printRequestAnswer(unregisterRequest);

        // Send client stop
        System.out.println("Stop client");
        StopRequest stopRequest = new StopRequest();
        DistroStreamClient.request(stopRequest);
        stopRequest.waitProcessed();
        assertEquals(stopRequest.getErrorCode(), 0);
        printRequestAnswer(stopRequest);

        // Send server stop
        System.out.println("Stop server");
        DistroStreamServer.setStop();
    }

    private void printRequestAnswer(Request req) {
        System.out.println("ERROR CODE: " + req.getErrorCode());
        System.out.println("ERROR MESSAGE: " + req.getErrorMessage());
        System.out.println("RESPONSE: " + req.getResponseMessage());
    }

}