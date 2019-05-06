package es.bsc.distrostreamlib;

import es.bsc.distrostreamlib.client.DistroStreamClient;
import es.bsc.distrostreamlib.exceptions.BackendException;
import es.bsc.distrostreamlib.exceptions.RegistrationException;
import es.bsc.distrostreamlib.loggers.Loggers;
import es.bsc.distrostreamlib.requests.RegisterStreamRequest;
import es.bsc.distrostreamlib.types.ConsumerMode;
import es.bsc.distrostreamlib.types.StreamType;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public abstract class DistroStream<T> implements Externalizable {

    private static final Logger LOGGER = LogManager.getLogger(Loggers.DISTRO_STREAM);

    protected String id;
    private ConsumerMode mode;


    /**
     * Creates a new DistroStream instance for serialization.
     */
    public DistroStream() {
        // Nothing to do, only for serialization
    }

    /**
     * Creates a new DistroStream instance.
     * 
     * @param streamType DistroStream internal type.
     * @param accessMode DistroStream consumer mode.
     * @param internalStreamInfo Specific information about hte DistroStream implementation to be stored in the server
     * @throws RegistrationException When server cannot register the stream.
     */
    public DistroStream(StreamType streamType, ConsumerMode accessMode, List<String> internalStreamInfo)
            throws RegistrationException {
        LOGGER.info("Registering new Stream...");
        // Register stream creation and get stream id
        RegisterStreamRequest req = new RegisterStreamRequest(streamType, accessMode, internalStreamInfo);
        DistroStreamClient.request(req);

        req.waitProcessed();
        int error = req.getErrorCode();
        if (error != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("ERROR: Cannot register stream").append("\n");
            sb.append("  - Error Code: ").append(error).append("\n");
            sb.append("  - Nested Error Message: ").append(req.getErrorMessage()).append("\n");
            throw new RegistrationException(sb.toString());
        }
        this.id = req.getResponseMessage();

        // Store access mode
        this.mode = accessMode;

        LOGGER.info("New Stream registered with ID = " + this.id);
    }

    /*
     * PUBLISH METHODS
     */

    /**
     * Publishes the given message {@code message} to the current stream.
     * 
     * @param message T object to publish.
     * @throws RegistrationException Raised if there is an internal error when creating the stream handler.
     */
    public abstract void publish(T message) throws BackendException;

    /**
     * Publishes the given messages {@code messages} to the current stream.
     * 
     * @param messages List of T objects to publish.
     * @throws RegistrationException Raised if there is an internal error when creating the stream handler.
     */
    public abstract void publish(List<T> messages) throws BackendException;

    /*
     * POLL METHODS
     */

    /**
     * Retrieves all the unread messages of the current stream.
     * 
     * @return List of unread messages. Can be an empty list if any message have been published.
     */
    public abstract List<T> poll() throws BackendException;

    /*
     * SERIALIZATION METHODS
     */

    @Override
    public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {
        this.id = (String) oi.readObject();
        this.mode = (ConsumerMode) oi.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput oo) throws IOException {
        oo.writeObject(this.id);
        oo.writeObject(this.mode);
    }

}
