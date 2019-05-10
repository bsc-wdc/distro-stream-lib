package es.bsc.distrostreamlib.types;

/**
 * Consumer access modes.
 */
public enum ConsumerMode {
    AT_MOST_ONCE, // Messages are read only once
    AT_LEAST_ONCE; // Messages can be read many times
}
