/*
 *  Copyright 2002-2019 Barcelona Supercomputing Center (www.bsc.es)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package es.bsc.distrostreamlib.test;

import static org.junit.Assert.assertEquals;

import es.bsc.distrostreamlib.client.DistroStreamClient;
import es.bsc.distrostreamlib.exceptions.DistroStreamClientInitException;
import es.bsc.distrostreamlib.requests.RegisterClientRequest;
import es.bsc.distrostreamlib.requests.UnregisterClientRequest;
import es.bsc.distrostreamlib.server.types.StreamBackend;

import org.junit.Test;


public class CommunicationTest {

    @Test
    public void communicationClientServer() throws DistroStreamClientInitException {
        // Start server
        CommonMethods.startServer(StreamBackend.FILES);

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
