package es.bsc.distrostreamlib.exceptions;

public class RegistrationException extends Exception {

    /**
     * Exception Version UID are 2L in all Runtime.
     */
    private static final long serialVersionUID = 3L;


    /**
     * Creates a new exception for registration with the given message {@code msg}.
     * 
     * @param msg Exception message.
     */
    public RegistrationException(String msg) {
        super(msg);
    }

}
