package es.bsc.distrostreamlib.api.objects;

import es.bsc.distrostreamlib.exceptions.BackendException;
import es.bsc.distrostreamlib.loggers.Loggers;
import es.bsc.distrostreamlib.types.ConsumerMode;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DeleteRecordsResult;
import org.apache.kafka.clients.admin.DeletedRecords;
import org.apache.kafka.clients.admin.RecordsToDelete;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicPartition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ODSConsumer<T> {

    // Logger
    private static final Logger LOGGER = LogManager.getLogger(Loggers.ODS_CONSUMER);
    private static final boolean DEBUG = LOGGER.isDebugEnabled();

    // Stream properties
    private final String topicName;
    private final ConsumerMode mode;

    // Internal Kafka instances
    private final AdminClient adminClient;
    private final KafkaConsumer<String, T> kafkaConsumer;


    /**
     * Creates a new ODSConsumer instance.
     * 
     * @param bootstrapServer Server name and port of the bootstrap server.
     * @param topicName Name of the stream topic.
     * @param mode Consumer mode.
     * @throws BackendException When a exception in the backend occurs.
     */
    public ODSConsumer(String bootstrapServer, String topicName, ConsumerMode mode) throws BackendException {
        LOGGER.debug("Creating Consumer...");

        this.topicName = topicName;
        this.mode = mode;

        this.adminClient = registerAdminClient(bootstrapServer);

        // Parse configuration file
        Properties properties = new Properties();
        properties.put("bootstrap.servers", bootstrapServer);
        properties.put("group.id", "compss" + UUID.randomUUID());
        properties.putAll(ODSProperties.DEFAULT_CONSUMER_PROPERTIES);

        // Create internal consumer
        this.kafkaConsumer = new KafkaConsumer<>(properties);

        // Subscribe consumer
        List<String> allTopics = Arrays.asList(this.topicName, ODSTopics.TOPIC_SYSTEM_MESSAGES);
        this.kafkaConsumer.subscribe(allTopics);
        LOGGER.debug("DONE Creating Consumer");
    }

    private AdminClient registerAdminClient(String bootstrapServer) {
        Map<String, Object> adminConf = new HashMap<>();
        adminConf.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        adminConf.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "5000");

        return AdminClient.create(adminConf);
    }

    /**
     * Polls the regular messages.
     * 
     * @return A list containing the processed regular messages.
     */
    public final List<T> pollMessages() {
        LOGGER.debug("Polling Messages from " + this.topicName + " ...");

        // Retrieve published messages
        ConsumerRecords<String, T> records;
        // TODO: Sync to avoid concurrent accesses to kafka consumer copy in the same worker
        synchronized (this.kafkaConsumer) {
            records = this.kafkaConsumer.poll(Duration.ofMillis(ODSProperties.TIMEOUT));
        }

        // Parse messages
        List<T> messages = new LinkedList<>();
        Map<TopicPartition, RecordsToDelete> toDelete = new HashMap<>();
        for (ConsumerRecord<String, T> record : records) {
            if (record.topic().equals(this.topicName)) {
                // Store received message
                T msg = record.value();
                messages.add(msg);
                // Mark record to delete
                if (this.mode.equals(ConsumerMode.AT_MOST_ONCE)) {
                    toDelete.put(new TopicPartition(record.topic(), record.partition()),
                            RecordsToDelete.beforeOffset(record.offset()));
                }
            } else if (record.topic().equals(ODSTopics.TOPIC_SYSTEM_MESSAGES)) {
                processSystemValue(record.value());
            } else {
                processUnknownTopicValue(record.topic(), record.value());
            }
        }

        // Erase parsed messages
        if (this.mode.equals(ConsumerMode.AT_MOST_ONCE) && !toDelete.isEmpty()) {
            eraseRecords(toDelete);
        }

        if (DEBUG) {
            LOGGER.debug("DONE Polling Messages (" + messages.size() + " elements)");
        }

        // Return processed messages
        return messages;
    }

    private void eraseRecords(Map<TopicPartition, RecordsToDelete> toDelete) {
        LOGGER.debug("Deleting processed records...");
        // Delete record so no other consumer parses them
        DeleteRecordsResult deleted = this.adminClient.deleteRecords(toDelete);
        for (Entry<TopicPartition, KafkaFuture<DeletedRecords>> recordToDelete : deleted.lowWatermarks().entrySet()) {
            try {
                recordToDelete.getValue().get().lowWatermark();
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("ERROR: Cannot erase record, trying to proceed anyway...", e);
            }
        }
        LOGGER.debug("DONE Deleting processed records");
    }

    private void processSystemValue(T value) {
        LOGGER.info("Received [Topic=System] : " + value);
    }

    private void processUnknownTopicValue(String topic, T value) {
        LOGGER.info("Received [Topic=" + topic + "] : " + value);
    }

}
