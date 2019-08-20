/*
 *  Copyright 2002-2019 Barcelona Supercomputing Center (www.bsc.es)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package es.bsc.distrostreamlib.api.objects;

import java.util.HashMap;
import java.util.Map;


/**
 * Kafka wrapper properties for object distributed streams.
 */
public class ODSProperties {

    // Kafka producer default properties
    protected static final Map<String, String> DEFAULT_PRODUCER_PROPERTIES;

    // Kafka consumer default properties
    protected static final Map<String, String> DEFAULT_CONSUMER_PROPERTIES;

    protected static final long TIMEOUT = 200; // ms
    protected static final long COMMIT_INTERVAL = TIMEOUT / 2; // ms

    // Fill Kafka default properties
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


    private ODSProperties() {
        // Nothing to do
    }

}
