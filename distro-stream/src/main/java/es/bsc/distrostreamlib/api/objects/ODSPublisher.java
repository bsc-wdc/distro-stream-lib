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

import es.bsc.distrostreamlib.exceptions.BackendException;
import es.bsc.distrostreamlib.loggers.Loggers;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Kafka publisher wrapper for distributed object streams of the given type.
 *
 * @param <T> Internal object distributed stream type.
 */
public class ODSPublisher<T> {

    // Logger
    private static final Logger LOGGER = LogManager.getLogger(Loggers.ODS_PUBLISHER);

    // Internal Kafka producer
    private final KafkaProducer<String, T> kafkaProducer;


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
        properties.putAll(ODSProperties.DEFAULT_PRODUCER_PROPERTIES);

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
        LOGGER.debug("Publishing Message to " + topic + " ...");
        ProducerRecord<String, T> record = new ProducerRecord<>(topic, message);
        this.kafkaProducer.send(record);
        LOGGER.debug("DONE Publishing Message");
    }

}
