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
package es.bsc.distrostreamlib.client;

import es.bsc.distrostreamlib.exceptions.DistroStreamClientInitException;
import es.bsc.distrostreamlib.loggers.Loggers;
import es.bsc.distrostreamlib.requests.Request;
import es.bsc.distrostreamlib.requests.StopRequest;
import es.bsc.distrostreamlib.types.RequestType;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Implementation of the Distributed Stream client.
 */
public class DistroStreamClient extends Thread {

    private static final Logger LOGGER = LogManager.getLogger(Loggers.DSL_CLIENT);
    private static final boolean DEBUG = LOGGER.isDebugEnabled();

    private static final int MAX_READ_RETRIES = 5;
    private static final int TIME_BETWEEN_READ_RETRIES = 10; // ms

    // Static access to client
    private static DistroStreamClient client;

    // Client internal structures
    private final LinkedBlockingDeque<Request> requests;

    private boolean keepRunning;
    private final String masterIP;
    private final int masterPort;


    /**
     * Creates a new DistroStream Client.
     * 
     * @param masterIP Master IP to connect with.
     * @param masterPort Master port to connect with.
     * @throws DistroStreamClientInitException When the client cannot connect to the given server details.
     */
    private DistroStreamClient(String masterIP, int masterPort) throws DistroStreamClientInitException {
        LOGGER.info("DS Client initialized");
        this.keepRunning = true;

        this.requests = new LinkedBlockingDeque<>();

        this.masterIP = masterIP;
        this.masterPort = masterPort;
    }

    @Override
    public void run() {
        LOGGER.info("DS Client started");

        processRequests();

        LOGGER.info("DS Client stopped");
    }

    /**
     * Processes the client requests.
     */
    private void processRequests() {
        while (this.keepRunning) {
            // Retrieve next request (blocking)
            Request cr = null;
            try {
                cr = this.requests.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Process request
            if (cr != null) {
                if (DEBUG) {
                    LOGGER.debug("Processing request " + cr.getType());
                }

                if (cr.getType().equals(RequestType.STOP)) {
                    LOGGER.info("DS Client asked to stop");
                    this.keepRunning = false;
                    cr.setResponse("DONE");
                } else {
                    processRequest(cr);
                }
                cr.setProcessed();
            }
        }
    }

    /**
     * Process the given request setting its error, errorMessage and response message.
     * 
     * @param cr Client Request.
     */
    private void processRequest(Request cr) {
        try (Socket socket = new Socket(this.masterIP, this.masterPort);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner in = new Scanner(socket.getInputStream())) {
            // Send petition
            String reqMsg = cr.getRequestMessage();
            if (DEBUG) {
                LOGGER.debug("Sending request to server: " + reqMsg);
            }
            out.println(reqMsg);

            // Wait for response
            LOGGER.debug("Receiving answer from server...");
            int numRetries = 0;
            while (!in.hasNextLine() && numRetries < MAX_READ_RETRIES) {
                Thread.sleep(TIME_BETWEEN_READ_RETRIES);
                ++numRetries;
            }

            // Try to process response
            if (in.hasNextLine()) {
                String responseMessage = in.nextLine();
                if (DEBUG) {
                    LOGGER.debug("Received answer from server: " + responseMessage);
                }
                cr.setResponse(responseMessage);
            } else {
                LOGGER.error("Error receiving request answer (timeout)");
                cr.setError(2, "ERROR: Timeout on answer wait");
            }
        } catch (IOException ioe) {
            LOGGER.error("Error sending request", ioe);
            cr.setError(1, ioe.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void addRequest(Request r) {
        if (DEBUG) {
            LOGGER.debug("Adding new request to client queue: " + r.getType());
        }
        boolean success = this.requests.offer(r);
        if (!success) {
            LOGGER.error("Unexpected error adding request to queue. Skipping request.");
        }
    }

    /*
     * INTERFACE METHODS
     */

    /**
     * Initializes and starts the client server.
     * 
     * @param masterIP Master IP to connect with.
     * @param masterPort Master Port to connect with.
     * @throws DistroStreamClientInitException When the client cannot connect to the given server details.
     */
    public static void initAndStart(String masterIP, int masterPort) throws DistroStreamClientInitException {
        try {
            client = new DistroStreamClient(masterIP, masterPort);
        } catch (DistroStreamClientInitException dcie) {
            LOGGER.error("ERROR: Cannot start DistroStream Client", dcie);
            throw dcie;
        }
        client.setName("DistroStreamClient");
        client.start();
    }

    /**
     * Marks the client to stop as soon as possible.
     */
    public static void setStop() {
        StopRequest stopRequest = new StopRequest();
        client.addRequest(stopRequest);
    }

    /**
     * Enqueues a new request to the client.
     * 
     * @param r Client request.
     */
    public static void request(Request r) {
        client.addRequest(r);
    }

}
