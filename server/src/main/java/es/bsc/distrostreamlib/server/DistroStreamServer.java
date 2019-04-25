package es.bsc.distrostreamlib.server;

import es.bsc.distrostreamlib.loggers.Loggers;
import es.bsc.distrostreamlib.server.types.StreamInfo;
import es.bsc.distrostreamlib.types.ConsumerMode;
import es.bsc.distrostreamlib.types.RequestType;
import es.bsc.distrostreamlib.types.StreamType;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DistroStreamServer extends Thread {

    private static final Logger LOGGER = LogManager.getLogger(Loggers.DSL_SERVER);
    private static final boolean DEBUG = LOGGER.isDebugEnabled();

    private static final int DEFAULT_SERVER_PORT = 49_049;

    // Static access to server
    private static DistroStreamServer SERVER;

    private final int serverPort;
    private boolean keepRunning;
    private List<String> registeredClients;
    private Map<UUID, StreamInfo> registeredStreams;


    /**
     * Creates a new DistroStream Server instance.
     * 
     * @param serverPort Server port.
     */
    public DistroStreamServer(Integer serverPort) {
        if (serverPort == null) {
            serverPort = DEFAULT_SERVER_PORT;
        }
        this.serverPort = serverPort;

        this.keepRunning = true;
        this.registeredClients = new LinkedList<>();
        this.registeredStreams = new HashMap<>();
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
                assert (content.length == 2);
                String regClientIP = content[1].trim();
                this.registeredClients.add(regClientIP);
                answer = "DONE";
                break;
            case UNREGISTER_CLIENT:
                assert (content.length == 2);
                String unregClientIP = content[1].trim();
                this.registeredClients.remove(unregClientIP);
                answer = "DONE";
                break;
            case REGISTER_STREAM:
                assert (content.length == 3);
                StreamType streamType = StreamType.valueOf(content[1].trim().toUpperCase());
                ConsumerMode accessMode = ConsumerMode.valueOf(content[2].trim().toUpperCase());

                UUID id = UUID.randomUUID();
                StreamInfo streamInfo = new StreamInfo(id, streamType, accessMode);
                this.registeredStreams.put(id, streamInfo);
                answer = id.toString();
                break;
            case POLL:
                assert (content.length == 2);
                UUID streamId = UUID.fromString(content[1].trim());
                answer = pollFromStream(streamId);
                break;
            case STOP:
                // Should never receive this request from clients
                LOGGER.warn("Received STOP request from client. Skipping");
                answer = null;
                break;
        }

        return answer;
    }

    private String pollFromStream(UUID streamId) {
        StreamInfo streamInfo = this.registeredStreams.get(streamId);

        // Check if stream is registered
        if (streamInfo == null) {
            LOGGER.warn("Skipping poll on unregistered stream with ID = " + streamId.toString());
            return "";
        }

        // Check stream type
        switch (streamInfo.getStreamType()) {
            case FILE:
                return pollFileStream(streamInfo);
            case OBJECT:
                LOGGER.warn("Skipping poll on OBJECT stream with ID = " + streamId.toString());
                return "";
            case PSCO:
                LOGGER.warn("Skipping poll on PSCO stream with ID = " + streamId.toString());
                return "";
        }

        return "";
    }

    private String pollFileStream(StreamInfo streamInfo) {
        // TODO
        return "";
    }

    private void setStopFlag() {
        LOGGER.info("DS Server marked to stop");
        this.keepRunning = false;
    }

    /*
     * INTERFACE METHODS
     */

    /**
     * Initializes and starts the DistroStream Server.
     * 
     * @param masterPort Master port to use.
     */
    public static void initAndStart(Integer masterPort) {
        SERVER = new DistroStreamServer(masterPort);
        SERVER.setName("DistroStreamServer");
        SERVER.start();

    }

    /**
     * Marks the server to stop as soon as possible.
     */
    public static void setStop() {
        SERVER.setStopFlag();
    }

}