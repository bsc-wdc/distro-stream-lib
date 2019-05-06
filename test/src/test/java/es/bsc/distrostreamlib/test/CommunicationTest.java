package es.bsc.distrostreamlib.test;

import static org.junit.Assert.assertEquals;

import es.bsc.distrostreamlib.client.DistroStreamClient;
import es.bsc.distrostreamlib.exceptions.DistroStreamClientInitException;
import es.bsc.distrostreamlib.requests.RegisterClientRequest;
import es.bsc.distrostreamlib.requests.UnregisterClientRequest;

import org.junit.Test;


public class CommunicationTest {

    @Test
    public void communicationClientServer() throws DistroStreamClientInitException {
        // Start server
        CommonMethods.startServer();

        // Start client
        CommonMethods.startClient();

        // Send request to client
        System.out.println("Register client");
        RegisterClientRequest registerRequest = new RegisterClientRequest(CommonMethods.CLIENT_IP);
        DistroStreamClient.request(registerRequest);
        registerRequest.waitProcessed();
        assertEquals(registerRequest.getErrorCode(), 0);
        CommonMethods.printRequestAnswer(registerRequest);

        // Send request to client
        System.out.println("Unregister client");
        UnregisterClientRequest unregisterRequest = new UnregisterClientRequest(CommonMethods.CLIENT_IP);
        DistroStreamClient.request(unregisterRequest);
        unregisterRequest.waitProcessed();
        assertEquals(unregisterRequest.getErrorCode(), 0);
        CommonMethods.printRequestAnswer(unregisterRequest);

        // Send client stop
        CommonMethods.stopClient();

        // Send server stop
        CommonMethods.stopServer();
    }

}