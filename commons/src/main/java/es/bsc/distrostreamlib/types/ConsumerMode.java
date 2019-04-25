package es.bsc.distrostreamlib.types;

/**
 * Consumer access modes.
 */
public enum ConsumerMode {
    READ_BY_ANY, // Each message is read once by any consumer
    READ_BY_ALL; // Each message is read once per consumer
}
