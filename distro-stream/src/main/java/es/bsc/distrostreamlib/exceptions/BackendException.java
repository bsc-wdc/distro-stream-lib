package es.bsc.distrostreamlib.exceptions;

public class BackendException extends Exception {

    /**
     * Exceptions Version UID are 3L in all Tests
     */
    private static final long serialVersionUID = 3L;


    /**
     * Creates a new exception for registration with the given message {@code msg}.
     * 
     * @param msg Exception message.
     * @param e Nested exception.
     */
    public BackendException(String msg) {
        super(msg);
    }

    /**
     * Creates a new exception for registration with the given message {@code msg} and nested exception {@code e}.
     * 
     * @param msg Exception message.
     * @param e Nested exception.
     */
    public BackendException(String msg, Exception e) {
        super(msg, e);
    }

}
