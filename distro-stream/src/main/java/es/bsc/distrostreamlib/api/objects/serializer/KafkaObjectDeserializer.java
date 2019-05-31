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
