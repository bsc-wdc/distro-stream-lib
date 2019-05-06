package es.bsc.distrostreamlib.api.objects;

import es.bsc.distrostreamlib.exceptions.BackendException;
import es.bsc.distrostreamlib.loggers.Loggers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ODSPublisher<T> {

    private static final Logger LOGGER = LogManager.getLogger(Loggers.ODS_PUBLISHER);

    private static final String PROPERTIES_FILE_PATH_PRODUCER = "$BASE_DIR/src/main/resources/producer.props";

    private final KafkaProducer<String, T> kafkaProducer;


    /**
     * Creates a new ODSPublisher instance.
     * 
     * @throws BackendException When an internal error occurs in the backend.
     */
    public ODSPublisher() throws BackendException {
        LOGGER.debug("Creating Publisher...");
        // Create internal producer
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(new File(PROPERTIES_FILE_PATH_PRODUCER))) {
            properties.load(fis);
        } catch (IOException ioe) {
            throw new BackendException("ERROR: Cannot open properties file", ioe);
        }
        this.kafkaProducer = new KafkaProducer<>(properties);
        LOGGER.debug("DONE Creating Publisher");
    }

    /**
     * Publishes the given message {@code message} in the stream associated to the given topic {@code topic}.
     * 
     * @param topic Message topic.
     * @param message Message content.
     */
    public void publish(String topic, T message) {
        LOGGER.debug("Publishing Message...");
        ProducerRecord<String, T> record = new ProducerRecord<>(topic, message);
        this.kafkaProducer.send(record);
        LOGGER.debug("DONE Publishing Message");
    }

}
