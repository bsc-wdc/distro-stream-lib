package es.bsc.distrostreamlib.server.types;

/**
 * Stream backends.
 */
public enum StreamBackend {
    FILES, // Only enable streaming for files
    OBJECTS, // Only enable streaming for objects
    PSCOS, // Only enable streaming for PSCOs
    NONE, // Disable any kind of stream
    ALL; // Enable all kinds of streams
}
