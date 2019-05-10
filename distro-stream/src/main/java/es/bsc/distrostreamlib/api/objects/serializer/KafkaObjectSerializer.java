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

        return null;
    }

    @Override
    public void close() {
        // Nothing to do
    }

}
