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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Custom serializer for Kafka objects.
 */
public class KafkaObjectSerializer implements Serializer<Object> {

    private static final Logger LOGGER = LogManager.getLogger(Loggers.OBJECT_DISTRO_STREAM);


    @Override
    public void configure(Map<String, ?> map, boolean b) {
        // Nothing to do
    }

    @Override
    public byte[] serialize(String arg0, Object arg1) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(arg1);
            out.flush();
            return bos.toByteArray();
        } catch (IOException ioe) {
            LOGGER.error("ERROR: Exception serializing object " + arg1.hashCode(), ioe);
        }

        return new byte[0];
    }

    @Override
    public void close() {
        // Nothing to do
    }

}
