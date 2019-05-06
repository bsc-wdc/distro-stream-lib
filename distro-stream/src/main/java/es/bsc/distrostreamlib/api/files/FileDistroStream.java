package es.bsc.distrostreamlib.api.files;

import es.bsc.distrostreamlib.DistroStream;
import es.bsc.distrostreamlib.client.DistroStreamClient;
import es.bsc.distrostreamlib.exceptions.BackendException;
import es.bsc.distrostreamlib.exceptions.RegistrationException;
import es.bsc.distrostreamlib.loggers.Loggers;
import es.bsc.distrostreamlib.requests.PollRequest;
import es.bsc.distrostreamlib.types.ConsumerMode;
import es.bsc.distrostreamlib.types.StreamType;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class FileDistroStream extends DistroStream<String> implements Externalizable {

    private static final Logger LOGGER = LogManager.getLogger(Loggers.FILE_DISTRO_STREAM);
    private static final boolean DEBUG = LOGGER.isDebugEnabled();

    private static final String WARN_PUBLISH = "WARN: Unnecessary call on publish on FileDistroStream";

    private static final ConsumerMode DEFAULT_CONSUMER_MODE = ConsumerMode.READ_BY_ANY;

    private String baseDir;


    /**
     * Creates a new FileDistroStream instance for serialization.
     */
    public FileDistroStream() {
        // Nothing to do, only for serialization
    }

    /**
     * Creates a new FileDistroStream instance.
     * 
     * @param baseDir Absolute path of the base directory.
     * @throws RegistrationException When server cannot register the stream.
     */
    public FileDistroStream(String baseDir) throws RegistrationException {
        super(StreamType.FILE, DEFAULT_CONSUMER_MODE, Arrays.asList(baseDir));
        this.baseDir = baseDir;
    }

    /**
     * Creates a new FileDistroStream instance.
     * 
     * @param baseDir Absolute path of the base directory.
     * @param mode DistroStream consumer mode.
     * @throws RegistrationException When server cannot register the stream.
     */
    public FileDistroStream(String baseDir, ConsumerMode mode) throws RegistrationException {
        super(StreamType.FILE, mode, Arrays.asList(baseDir));
        this.baseDir = baseDir;
    }

    /*
     * PUBLISH METHODS
     */

    @Override
    public final void publish(String message) throws BackendException {
        // Nothing to do since poll will monitor newly created files.
        LOGGER.warn(WARN_PUBLISH);
    }

    @Override
    public final void publish(List<String> messages) throws BackendException {
        // Nothing to do since poll will monitor newly created files.
        LOGGER.warn(WARN_PUBLISH);
    }

    /*
     * POLL METHODS
     */

    @Override
    public final List<String> poll() throws BackendException {
        LOGGER.info("Polling new stream items...");
        PollRequest req = new PollRequest(this.id);
        DistroStreamClient.request(req);

        req.waitProcessed();
        int error = req.getErrorCode();
        if (error != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("ERROR: Cannot poll stream").append("\n");
            sb.append("  - Error Code: ").append(error).append("\n");
            sb.append("  - Nested Error Message: ").append(req.getErrorMessage()).append("\n");
            throw new BackendException(sb.toString());
        }
        String response = req.getResponseMessage();
        if (DEBUG) {
            LOGGER.debug("Retrieved stream items: " + response);
        }
        return Arrays.asList(response.split(" "));
    }

    /*
     * SERIALIZATION METHODS
     */

    @Override
    public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {
        super.readExternal(oi);
        this.baseDir = (String) oi.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput oo) throws IOException {
        super.writeExternal(oo);
        oo.writeObject(this.baseDir);
    }

}
