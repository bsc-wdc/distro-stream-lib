package es.bsc.distrostreamlib.api.objects;

import es.bsc.distrostreamlib.exceptions.BackendException;
import es.bsc.distrostreamlib.loggers.Loggers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ODSConsumer<T> {

    // Logger
    private static final Logger LOGGER = LogManager.getLogger(Loggers.ODS_CONSUMER);

    // Kafka consumer default properties
    private static final Map<String, String> DEFAULT_CONSUMER_PROPERTIES;

    // Internal Kafka consumer
    private final KafkaConsumer<String, T> kafkaConsumer;

    static {
        DEFAULT_CONSUMER_PROPERTIES = new HashMap<>();
        DEFAULT_CONSUMER_PROPERTIES.put("group.id", "test");
        DEFAULT_CONSUMER_PROPERTIES.put("enable.auto.commit", "true");
        DEFAULT_CONSUMER_PROPERTIES.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        DEFAULT_CONSUMER_PROPERTIES.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        DEFAULT_CONSUMER_PROPERTIES.put("session.timeout.ms", "10000");
        DEFAULT_CONSUMER_PROPERTIES.put("fetch.min.bytes", "50000");
        DEFAULT_CONSUMER_PROPERTIES.put("receive.buffer.bytes", "262144");
        DEFAULT_CONSUMER_PROPERTIES.put("max.partition.fetch.bytes", "2097152");
    }


    /**
     * Creates a new ODSConsumer instance.
     * 
     * @param bootstrapServer Server name and port of the bootstrap server.
     * @throws BackendException When a exception in the backend occurs.
     */
    public ODSConsumer(String bootstrapServer) throws BackendException {
        LOGGER.debug("Creating Consumer...");

        // Parse configuration file
        Properties properties = new Properties();
        properties.put("bootstrap.servers", bootstrapServer);
        properties.putAll(DEFAULT_CONSUMER_PROPERTIES);

        // Create internal consumer
        this.kafkaConsumer = new KafkaConsumer<>(properties);

        // Subscribe consumer
        this.kafkaConsumer.subscribe(ODSTopics.ALL_TOPICS);
        LOGGER.debug("DONE Creating Consumer");
    }

    /**
     * Polls the regular messages.
     * 
     * @return A list containing the processed regular messages.
     */
    public final List<T> pollRegularMessages() {
        LOGGER.debug("Polling Messages...");
        // No timeout to avoid hanging
        final int timeout = 0; // ms

        // Retrieve published messages
        ConsumerRecords<String, T> records = this.kafkaConsumer.poll(timeout);

        // Parse messages
        List<T> messages = new LinkedList<>();
        for (ConsumerRecord<String, T> record : records) {
            switch (record.topic()) {
                case ODSTopics.TOPIC_REGULAR_MESSAGES:
                    T msg = record.value();
                    messages.add(msg);
                    break;
                case ODSTopics.TOPIC_SYSTEM_MESSAGES:
                    processSystemValue(record.value());
                    break;
                default:
                    processUnknownTopicValue(record.topic(), record.value());
            }
        }
        LOGGER.debug("DONE Polling Messages");
        return messages;
    }

    private void processSystemValue(T value) {
        LOGGER.info("Received [Topic=System] : " + value);
    }

    private void processUnknownTopicValue(String topic, T value) {
        LOGGER.info("Received [Topic=" + topic + "] : " + value);
    }

}
