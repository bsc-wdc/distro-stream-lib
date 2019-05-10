package es.bsc.distrostreamlib.api.objects;

import es.bsc.distrostreamlib.DistroStream;
import es.bsc.distrostreamlib.client.DistroStreamClient;
import es.bsc.distrostreamlib.exceptions.BackendException;
import es.bsc.distrostreamlib.exceptions.RegistrationException;
import es.bsc.distrostreamlib.loggers.Loggers;
import es.bsc.distrostreamlib.requests.BootstrapServerRequest;
import es.bsc.distrostreamlib.types.ConsumerMode;
import es.bsc.distrostreamlib.types.StreamType;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ObjectDistroStream<T> extends DistroStream<T> {

    private static final Logger LOGGER = LogManager.getLogger(Loggers.OBJECT_DISTRO_STREAM);
    private static final boolean DEBUG = LOGGER.isDebugEnabled();

    private ODSPublisher<T> publisher;
    private ODSConsumer<T> consumer;
    private String bootstrapServer;


    /**
     * Creates a new ObjectDistroStream instance for serialization.
     */
    public ObjectDistroStream() {
        // Nothing to do, only for serialization
    }

    /**
     * Creates a new ObjectDistroStream instance.
     * 
     * @param mode DistroStream consumer mode.
     * @throws RegistrationException When server cannot register stream.
     */
    public ObjectDistroStream(ConsumerMode mode) throws RegistrationException {
        super(StreamType.OBJECT, mode, new LinkedList<>());

        this.publisher = null;
        this.consumer = null;
        this.bootstrapServer = null;
    }

    /*
     * PUBLISH METHODS
     */

    @Override
    public final void publish(T message) throws BackendException {
        LOGGER.info("Publishing new Object message...");
        registerPublisher();
        this.publisher.publish(ODSTopics.TOPIC_REGULAR_MESSAGES, message);
        LOGGER.info("Published new Object message");
    }

    @Override
    public final void publish(List<T> messages) throws BackendException {
        LOGGER.info("Publishing new List of Object messages");
        registerPublisher();
        for (T msg : messages) {
            this.publisher.publish(ODSTopics.TOPIC_REGULAR_MESSAGES, msg);
        }
        LOGGER.info("Published new List of Object messages");
    }

    /*
     * POLL METHODS
     */

    @Override
    public final List<T> poll() throws BackendException {
        LOGGER.info("Polling new stream entries...");
        registerConsumer();
        return this.consumer.pollRegularMessages();
    }

    /*
     * INTERNAL REGISTRATION METHODS
     */

    private void registerPublisher() throws BackendException {
        if (this.publisher == null) {
            if (this.bootstrapServer == null) {
                this.bootstrapServer = requestBoostrapServerInformation();
            }

            // Instantiate internal producer
            LOGGER.info("Creating internal producer...");
            this.publisher = new ODSPublisher<>(this.bootstrapServer);
        }
    }

    private void registerConsumer() throws BackendException {
        if (this.consumer == null) {
            if (this.bootstrapServer == null) {
                this.bootstrapServer = requestBoostrapServerInformation();
            }

            // Instantiate internal consumer
            LOGGER.info("Creating internal consumer...");
            this.consumer = new ODSConsumer<>(this.bootstrapServer);
        }
    }

    private String requestBoostrapServerInformation() throws BackendException {
        // Request the server through the client for the bootstrap server name and port
        LOGGER.info("Requesting bootstrap server...");
        BootstrapServerRequest req = new BootstrapServerRequest();
        DistroStreamClient.request(req);

        req.waitProcessed();
        int error = req.getErrorCode();
        if (error != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("ERROR: Cannot request bootstrap server name and port").append("\n");
            sb.append("  - Error Code: ").append(error).append("\n");
            sb.append("  - Nested Error Message: ").append(req.getErrorMessage()).append("\n");
            throw new BackendException(sb.toString());
        }

        String response = req.getResponseMessage();
        if (DEBUG) {
            LOGGER.debug("Retrieved bootstrap server information: " + response);
        }

        return response;
    }

    /*
     * SERIALIZATION METHODS
     */

    @Override
    public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {
        super.readExternal(oi);
        // Do not serialize publisher nor consumer because we will force to instantiate them upon transfer.
        this.publisher = null;
        this.consumer = null;
        this.bootstrapServer = null;
    }

    @Override
    public void writeExternal(ObjectOutput oo) throws IOException {
        super.writeExternal(oo);
        // Do not serialize publisher nor consumer because we will force to instantiate them upon transfer.
        // oo.writeObject(this.publisher);
        // oo.writeObject(this.consumer);
    }

}
