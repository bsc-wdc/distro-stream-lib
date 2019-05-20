package es.bsc.distrostreamlib.api.objects;

import java.util.HashMap;
import java.util.Map;


public class ODSProperties {

    public static final long TIMEOUT = 200; // ms

    // Kafka producer default properties
    public static final Map<String, String> DEFAULT_PRODUCER_PROPERTIES;

    // Kafka consumer default properties
    public static final Map<String, String> DEFAULT_CONSUMER_PROPERTIES;

    // Fill Kafka default properties
    private static final long COMMIT_INTERVAL = TIMEOUT / 2; // ms

    static {
        DEFAULT_PRODUCER_PROPERTIES = new HashMap<>();
        DEFAULT_PRODUCER_PROPERTIES.put("acks", "all");
        DEFAULT_PRODUCER_PROPERTIES.put("retries", "0");
        DEFAULT_PRODUCER_PROPERTIES.put("batch.size", "16384");
        DEFAULT_PRODUCER_PROPERTIES.put("auto.commit.interval.ms", String.valueOf(COMMIT_INTERVAL));
        DEFAULT_PRODUCER_PROPERTIES.put("linger.ms", "0");
        DEFAULT_PRODUCER_PROPERTIES.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        DEFAULT_PRODUCER_PROPERTIES.put("value.serializer",
                "es.bsc.distrostreamlib.api.objects.serializer.KafkaObjectSerializer");
        DEFAULT_PRODUCER_PROPERTIES.put("block.on.buffer.full", "true");

        DEFAULT_CONSUMER_PROPERTIES = new HashMap<>();
        DEFAULT_CONSUMER_PROPERTIES.put("enable.auto.commit", "true");
        DEFAULT_CONSUMER_PROPERTIES.put("auto.commit.interval.ms", String.valueOf(COMMIT_INTERVAL));
        DEFAULT_CONSUMER_PROPERTIES.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        DEFAULT_CONSUMER_PROPERTIES.put("value.deserializer",
                "es.bsc.distrostreamlib.api.objects.serializer.KafkaObjectDeserializer");
        DEFAULT_CONSUMER_PROPERTIES.put("auto.offset.reset", "earliest");
        DEFAULT_CONSUMER_PROPERTIES.put("session.timeout.ms", "10000");
        DEFAULT_CONSUMER_PROPERTIES.put("fetch.min.bytes", "1");
        DEFAULT_CONSUMER_PROPERTIES.put("receive.buffer.bytes", "262144");
        DEFAULT_CONSUMER_PROPERTIES.put("max.partition.fetch.bytes", "2097152");
    }

}
