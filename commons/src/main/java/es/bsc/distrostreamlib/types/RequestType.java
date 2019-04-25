package es.bsc.distrostreamlib.types;

/**
 * Client-Server request types.
 */
public enum RequestType {
    REGISTER_CLIENT, // Registers a new client
    UNREGISTER_CLIENT, // Unregisters a client
    REGISTER_STREAM, // Registers a stream
    POLL, // Polls changes on a given stream
    STOP; // Marks the server to stop

}
