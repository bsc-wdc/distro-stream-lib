package es.bsc.distrostreamlib.types;

/**
 * Client-Server request types.
 */
public enum RequestType {
    REGISTER_CLIENT, // Registers a new client
    UNREGISTER_CLIENT, // Unregisters a client
    REGISTER_STREAM, // Registers a stream
    STREAM_STATUS, // Returns the stream status (open or closed)
    CLOSE_STREAM, // Marks an stream to be closed
    POLL, // Polls changes on a given stream
    STOP; // Marks the server to stop
}
