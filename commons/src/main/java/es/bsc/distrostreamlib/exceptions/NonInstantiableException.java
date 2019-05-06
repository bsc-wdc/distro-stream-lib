package es.bsc.distrostreamlib.exceptions;

/**
 * Exception for non instantiable classes.
 */
public class NonInstantiableException extends RuntimeException {

    /**
     * Exceptions Version UID are 2L in all Runtime.
     */
    private static final long serialVersionUID = 2L;


    /**
     * Creates a new exception for class {@code className}.
     * 
     * @param className Class name.
     */
    public NonInstantiableException(String className) {
        super("Class " + className + " can not be instantiated.");
    }

}
