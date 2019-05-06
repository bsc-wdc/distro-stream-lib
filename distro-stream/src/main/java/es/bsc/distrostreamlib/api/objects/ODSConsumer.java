package es.bsc.distrostreamlib.api.objects;

import es.bsc.distrostreamlib.exceptions.BackendException;
import es.bsc.distrostreamlib.loggers.Loggers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ODSConsumer<T> {

    private static final Logger LOGGER = LogManager.getLogger(Loggers.ODS_CONSUMER);

    private static final String PROPERTIES_FILE_PATH_CONSUMER = "$BASE_DIR/src/main/resources/consumer.props";

    private final KafkaConsumer<String, T> kafkaConsumer;


    /**
     * Creates a new ODSConsumer instance.
     * 
     * @throws BackendException When a exception in the backend occurs.
     */
    public ODSConsumer() throws BackendException {
        LOGGER.debug("Creating Consumer...");

        // Parse configuration file
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(new File(PROPERTIES_FILE_PATH_CONSUMER))) {
            properties.load(fis);
        } catch (IOException ioe) {
            throw new BackendException("ERROR: Cannot open properties file", ioe);
        }

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
