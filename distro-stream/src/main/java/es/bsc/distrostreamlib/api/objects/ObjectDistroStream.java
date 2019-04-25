package es.bsc.distrostreamlib.api.objects;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import es.bsc.distrostreamlib.DistroStream;
import es.bsc.distrostreamlib.exceptions.BackendException;
import es.bsc.distrostreamlib.exceptions.RegistrationException;
import es.bsc.distrostreamlib.loggers.Loggers;
import es.bsc.distrostreamlib.types.ConsumerMode;
import es.bsc.distrostreamlib.types.StreamType;


public class ObjectDistroStream<T> extends DistroStream<T> {

    private static final Logger LOGGER = LogManager.getLogger(Loggers.OBJECT_DISTRO_STREAM);

    private ODSPublisher<T> publisher;
    private ODSConsumer<T> consumer;


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
            this.publisher = new ODSPublisher<>();
        }
    }

    private void registerConsumer() throws BackendException {
        if (this.consumer == null) {
            this.consumer = new ODSConsumer<>();
        }
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
    }

    @Override
    public void writeExternal(ObjectOutput oo) throws IOException {
        super.writeExternal(oo);
        // Do not serialize publisher nor consumer because we will force to instantiate them upon transfer.
        // oo.writeObject(this.publisher);
        // oo.writeObject(this.consumer);
    }

}
