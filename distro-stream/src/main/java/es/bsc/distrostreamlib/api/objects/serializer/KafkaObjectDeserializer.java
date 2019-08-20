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
package es.bsc.distrostreamlib.api.objects.serializer;

import es.bsc.distrostreamlib.loggers.Loggers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Custom deserializer for Kafka objects.
 */
public class KafkaObjectDeserializer implements Deserializer<Object> {

    private static final Logger LOGGER = LogManager.getLogger(Loggers.OBJECT_DISTRO_STREAM);


    @Override
    public void configure(Map<String, ?> arg0, boolean arg1) {
        // Nothing to do
    }

    @Override
    public Object deserialize(String arg0, byte[] arg1) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(arg1); ObjectInput in = new ObjectInputStream(bis)) {

            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error("ERROR: Exception deserializing object " + Arrays.hashCode(arg1), e);
        }

        return null;
    }

    @Override
    public void close() {
        // Nothing to do
    }

}
