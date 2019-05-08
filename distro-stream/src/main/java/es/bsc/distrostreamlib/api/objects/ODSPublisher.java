package es.bsc.distrostreamlib.api.objects;

import es.bsc.distrostreamlib.exceptions.BackendException;
import es.bsc.distrostreamlib.loggers.Loggers;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ODSPublisher<T> {

    // Logger
    private static final Logger LOGGER = LogManager.getLogger(Loggers.ODS_PUBLISHER);

    // Kafka producer default properties
    private static final Map<String, String> DEFAULT_PRODUCER_PROPERTIES;

    // Internal Kafka producer
    private final KafkaProducer<String, T> kafkaProducer;

    static {
        DEFAULT_PRODUCER_PROPERTIES = new HashMap<>();
        DEFAULT_PRODUCER_PROPERTIES.put("acks", "all");
        DEFAULT_PRODUCER_PROPERTIES.put("retries", "0");
        DEFAULT_PRODUCER_PROPERTIES.put("batch.size", "16384");
        DEFAULT_PRODUCER_PROPERTIES.put("auto.commit.interval.ms", "1000");
        DEFAULT_PRODUCER_PROPERTIES.put("linger.ms", "0");
        DEFAULT_PRODUCER_PROPERTIES.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        DEFAULT_PRODUCER_PROPERTIES.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        DEFAULT_PRODUCER_PROPERTIES.put("block.on.buffer.full", "true");
    }


    /**
     * Creates a new ODSPublisher instance.
     * 
     * @param bootstrapServer Server name and port of the bootstrap server.
     * @throws BackendException When an internal error occurs in the backend.
     */
    public ODSPublisher(String bootstrapServer) throws BackendException {
        LOGGER.debug("Creating Publisher...");

        // Parse configuration file
        Properties properties = new Properties();
        properties.put("bootstrap.servers", bootstrapServer);
        properties.putAll(DEFAULT_PRODUCER_PROPERTIES);

        // Create internal producer
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
