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
package es.bsc.distrostreamlib.server;

import es.bsc.distrostreamlib.loggers.Loggers;
import es.bsc.distrostreamlib.requests.StopRequest;
import es.bsc.distrostreamlib.server.types.StreamBackend;
import es.bsc.distrostreamlib.server.types.StreamInfo;
import es.bsc.distrostreamlib.types.ConsumerMode;
import es.bsc.distrostreamlib.types.RequestType;
import es.bsc.distrostreamlib.types.StreamType;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Distributed Stream Server implementation.
 */
public class DistroStreamServer extends Thread {

    private static final Logger LOGGER = LogManager.getLogger(Loggers.DSL_SERVER);
    private static final boolean DEBUG = LOGGER.isDebugEnabled();

    private static final int DEFAULT_SERVER_PORT = 49_049;
    private static final int DEFAULT_BOOTSTRAP_PORT = 49_001;

    // Static access to server
    private static DistroStreamServer server;

    private final String serverName;
    private final int serverPort;

    private final StreamBackend streamBackend;

    private boolean keepRunning;
    private List<String> registeredClients;
    private Map<String, StreamInfo> registeredStreams;
    private Map<String, String> registeredStreamAlias;


    /**
     * Creates a new DistroStream Server instance.
     * 
     * @param serverName Server name.
     * @param serverPort Server port.
     * @param streamBackend Streaming backend implementation type.
     */
    public DistroStreamServer(String serverName, Integer serverPort, StreamBackend streamBackend) {
        this.serverName = serverName;
        if (serverPort == null) {
            this.serverPort = DEFAULT_SERVER_PORT;
        } else {
            this.serverPort = serverPort;
        }

        this.streamBackend = streamBackend;

        this.keepRunning = true;
        this.registeredClients = new LinkedList<>();
        this.registeredStreams = new HashMap<>();
        this.registeredStreamAlias = new HashMap<>();
    }

    @Override
    public void run() {
        LOGGER.info("DS Server started");

        if (DEBUG) {
            LOGGER.debug("DS Server connecting to socket " + this.serverPort + " ...");
        }
        try (ServerSocket listener = new ServerSocket(this.serverPort)) {
            LOGGER.debug("DS Server connected to socket...");
            processRequests(listener);
        } catch (IOException ioe) {
            LOGGER.error("DS Server raised an exception", ioe);
        } finally {
            LOGGER.debug("DS Server disconnected from socket...");
        }

        LOGGER.info("DS Server stopped");
    }

    public StreamBackend getStreamBackend() {
        return this.streamBackend;
    }

    /**
     * Processes the server requests received through the socket.
     * 
     * @param listener ServerSocket to listen.
     * @throws IOException When accessing the socket.
     */
    private void processRequests(ServerSocket listener) throws IOException {
        LOGGER.debug("Processing requests...");
        while (this.keepRunning) {
            try (Socket socket = listener.accept();
                Scanner in = new Scanner(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                while (in.hasNextLine()) {
                    // Get message
                    String info = in.nextLine();
                    // Process message
                    String answer = processMessage(info);
                    // Return answer
                    out.println(answer);
                    out.flush();
                }
            }
        }
    }

    /**
     * Processes the current message and parses its content.
     * 
     * @param message Message to process.
     * @return Answer for the client process.
     */
    private String processMessage(String message) {
        if (DEBUG) {
            LOGGER.debug("Processing message: " + message);
        }
        String answer = null;

        String[] content = message.split(" ");
        RequestType token = RequestType.valueOf(content[0].trim().toUpperCase());
        switch (token) {
            case REGISTER_CLIENT:
                assert content.length == 2;
                String regClientIP = content[1].trim();
                this.registeredClients.add(regClientIP);
                answer = "DONE";
                break;
            case UNREGISTER_CLIENT:
                assert content.length == 2;
                String unregClientIP = content[1].trim();
                this.registeredClients.remove(unregClientIP);
                answer = "DONE";
                break;
            case BOOTSTRAP_SERVER:
                assert content.length == 1;
                answer = this.serverName + ":" + DEFAULT_BOOTSTRAP_PORT;
                break;
            case REGISTER_STREAM:
                assert content.length >= 4;
                StreamType streamType = StreamType.valueOf(content[1].trim().toUpperCase());
                ConsumerMode accessMode = ConsumerMode.valueOf(content[2].trim().toUpperCase());
                String alias = content[3].trim();
                List<String> internalStreamInfo = parseInternalStreamInfo(content);
                answer = registerStream(alias, streamType, accessMode, internalStreamInfo);
                break;
            case STREAM_STATUS:
                assert content.length == 2;
                String statusStreamId = content[1].trim();
                answer = getStreamStatus(statusStreamId);
                break;
            case ADD_STREAM_WRITER:
                assert content.length == 2;
                String streamWrittenId = content[1].trim();
                answer = addStreamWriter(streamWrittenId);
                break;
            case CLOSE_STREAM:
                assert content.length == 2;
                String closeStreamId = content[1].trim();
                answer = closeStream(closeStreamId);
                break;
            case POLL:
                assert content.length == 2;
                String pollStreamId = content[1].trim();
                answer = pollFromStream(pollStreamId);
                break;
            case PUBLISH:
                assert content.length == 3;
                String publishStreamId = content[1].trim();
                String pubMessage = content[2].trim();
                answer = publishToStream(publishStreamId, pubMessage);
                break;
            case STOP:
                // This request is only self-emitted (never received through clients)
                assert content.length == 1;
                LOGGER.info("Received STOP request, stopping...");
                answer = null;
                break;
        }

        return answer;
    }

    private List<String> parseInternalStreamInfo(String[] content) {
        List<String> internalStreamInfo = new LinkedList<>();
        for (int i = 4; i < content.length; ++i) {
            internalStreamInfo.add(content[i].trim());
        }

        return internalStreamInfo;
    }

    private String registerStream(String alias, StreamType streamType, ConsumerMode accessMode,
        List<String> internalStreamInfo) {
        if (alias != null && !alias.isEmpty() && !"null".equals(alias) && !"None".equals(alias)) {
            // Retrieve stream by alias
            String id = this.registeredStreamAlias.get(alias);
            if (id != null) {
                // Stream was already registered, return its id
                if (DEBUG) {
                    LOGGER.debug("Stream " + alias + " was already registered.");
                    LOGGER.debug("Retrieving stream from registered streams with id = " + id);
                }
                return id;
            } else {
                // Register new stream
                id = addNewStream(alias, streamType, accessMode, internalStreamInfo);
                this.registeredStreamAlias.put(alias, id);
                return id;
            }
        } else {
            // Register new stream (without alias, no need to store it)
            return addNewStream(alias, streamType, accessMode, internalStreamInfo);
        }
    }

    private String addNewStream(String alias, StreamType streamType, ConsumerMode accessMode,
        List<String> internalStreamInfo) {

        String id = UUID.randomUUID().toString();

        if (DEBUG) {
            LOGGER.debug("Registering new Stream with alias = " + alias + " and id " + id);
        }

        StreamInfo streamInfo = new StreamInfo(id, alias, streamType, accessMode, internalStreamInfo);
        this.registeredStreams.put(id, streamInfo);
        return id;
    }

    private String getStreamStatus(String streamId) {
        StreamInfo streamInfo = this.registeredStreams.get(streamId);

        // Check if stream is registered
        if (streamInfo == null) {
            LOGGER.warn("Skipping status on unregistered stream with ID = " + streamId);
            return "";
        }

        // Return stream status
        return String.valueOf(streamInfo.isStreamClosed());
    }

    private String addStreamWriter(String streamId) {
        if (DEBUG) {
            LOGGER.debug("Adding writer to stream with ID = " + streamId);
        }

        StreamInfo streamInfo = this.registeredStreams.get(streamId);
        streamInfo.addWriter();

        // No answer needed
        return "";
    }

    private String closeStream(String streamId) {
        StreamInfo streamInfo = this.registeredStreams.get(streamId);

        // Check if stream is registered
        if (streamInfo == null) {
            LOGGER.warn("Skipping close on unregistered stream with ID = " + streamId);
            return "";
        }

        // Mark stream as closed
        streamInfo.removeWriter();
        if (!streamInfo.hasWriters()) {
            streamInfo.markAsClosed();
        }

        // No answer needed
        return "";
    }

    private String pollFromStream(String streamId) {
        StreamInfo streamInfo = this.registeredStreams.get(streamId);

        // Check if stream is registered
        if (streamInfo == null) {
            LOGGER.warn("Skipping poll on unregistered stream with ID = " + streamId);
            return "";
        }

        // Check stream type
        switch (streamInfo.getStreamType()) {
            case FILE:
                return pollFileStream(streamInfo);
            case OBJECT:
                LOGGER.warn("Skipping poll on OBJECT stream with ID = " + streamId);
                return "";
            case PSCO:
                return pollPscoStream(streamInfo);
        }

        return "";
    }

    private String pollFileStream(StreamInfo streamInfo) {
        List<String> internalStreamInfo = streamInfo.getInternalStreamInfo();
        String baseFolderPath = internalStreamInfo.get(0);
        if (DEBUG) {
            LOGGER.debug("Polling new files at " + baseFolderPath);
        }

        // Search new files
        Path baseFolder = Paths.get(baseFolderPath);
        List<Path> filePaths = new ArrayList<>();
        try {
            filePaths = Files.list(baseFolder).collect(Collectors.toList());
        } catch (IOException ioe) {
            LOGGER.warn("Cannot list files in baseFolder. Returning empty file list.", ioe);
        }
        List<Path> newFilePaths = new ArrayList<>();
        long lastPollTimestamp = streamInfo.getLastPollTimestamp();
        long lastTimestamp = lastPollTimestamp;
        for (Path p : filePaths) {
            long fileModificationTimestamp = lastPollTimestamp;
            try {
                fileModificationTimestamp = Files.getLastModifiedTime(p).toMillis();
            } catch (IOException ioe) {
                LOGGER.warn("Cannot retrieve modification date from " + p.getFileName() + ". Skipping file...", ioe);
            }

            if (fileModificationTimestamp > lastPollTimestamp) {
                // Register file as new file
                newFilePaths.add(p);
                // Update timestamp
                if (fileModificationTimestamp >= lastTimestamp) {
                    lastTimestamp = fileModificationTimestamp;
                }
            }
        }

        // Update the timestamp to the highest timestamp of the registered new files
        streamInfo.setPollTimestamp(lastTimestamp);

        // Log information
        if (DEBUG) {
            LOGGER.debug("Registered new files: " + newFilePaths);
        }

        // Convert list and return as answer
        return newFilePaths.stream().map(s -> String.valueOf(s)).collect(Collectors.joining(" ", "", ""));
    }

    private String pollPscoStream(StreamInfo streamInfo) {
        if (DEBUG) {
            LOGGER.debug("Polling new PSCOs");
        }
        List<String> internalStreamInfo = streamInfo.getInternalStreamInfo();
        String newPscos = String.join(" ", internalStreamInfo);
        streamInfo.clearInternalStreamInfo();

        // Log information
        if (DEBUG) {
            LOGGER.debug("Polled new pscos: " + newPscos);
        }

        return newPscos;
    }

    private String publishToStream(String streamId, String message) {
        StreamInfo streamInfo = this.registeredStreams.get(streamId);

        // Check if stream is registered
        if (streamInfo == null) {
            LOGGER.warn("Skipping publish on unregistered stream with ID = " + streamId);
            return "";
        }

        // Check stream type
        switch (streamInfo.getStreamType()) {
            case FILE:
                LOGGER.warn("Skipping publish on FILE stream with ID = " + streamId);
                return "";
            case OBJECT:
                LOGGER.warn("Skipping poll on OBJECT stream with ID = " + streamId);
                return "";
            case PSCO:
                return publishPscoStream(streamInfo, message);
        }

        return "";
    }

    private String publishPscoStream(StreamInfo streamInfo, String message) {
        if (DEBUG) {
            LOGGER.debug("Adding new PSCO message: " + message);
        }

        streamInfo.addInternalStreamInfo(message);

        // Log information
        if (DEBUG) {
            LOGGER.debug("Registered new PSCO message");
        }
        return "DONE";
    }

    private void setStopFlag() {
        LOGGER.info("DS Server marked to stop");
        this.keepRunning = false;
    }

    private void autoSendStopRequest() {
        // Enqueue petition to wake up internal thread
        StopRequest sr = new StopRequest();
        try (Socket socket = new Socket(this.serverName, this.serverPort);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            // Send petition
            String reqMsg = sr.getRequestMessage();
            out.println(reqMsg);

            // We do not process the socket reply
        } catch (IOException ioe) {
            LOGGER.error("Error auto-sending stop request", ioe);
            sr.setError(1, ioe.getMessage());
        }
    }

    /*
     * INTERFACE METHODS
     */

    /**
     * Initializes and starts the DistroStream Server.
     * 
     * @param serverName Master name to use.
     * @param serverPort Master port to use.
     * @param streamBackend Streaming backend implementation type.
     */
    public static void initAndStart(String serverName, Integer serverPort, StreamBackend streamBackend) {
        server = new DistroStreamServer(serverName, serverPort, streamBackend);
        server.setName("DistroStreamServer");
        server.start();

    }

    /**
     * Marks the server to stop as soon as possible.
     */
    public static void setStop() {
        server.setStopFlag();
        server.autoSendStopRequest();
    }

}
