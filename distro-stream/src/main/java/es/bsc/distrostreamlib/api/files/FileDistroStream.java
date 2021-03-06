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
package es.bsc.distrostreamlib.api.files;

import es.bsc.distrostreamlib.api.impl.DistroStreamImpl;
import es.bsc.distrostreamlib.client.DistroStreamClient;
import es.bsc.distrostreamlib.exceptions.BackendException;
import es.bsc.distrostreamlib.exceptions.RegistrationException;
import es.bsc.distrostreamlib.loggers.Loggers;
import es.bsc.distrostreamlib.requests.PollRequest;
import es.bsc.distrostreamlib.types.ConsumerMode;
import es.bsc.distrostreamlib.types.StreamType;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Distributed Stream implementation for Files.
 */
public class FileDistroStream extends DistroStreamImpl<String> {

    private static final Logger LOGGER = LogManager.getLogger(Loggers.FILE_DISTRO_STREAM);
    private static final boolean DEBUG = LOGGER.isDebugEnabled();

    private static final String WARN_PUBLISH = "WARN: Unnecessary call on publish on FileDistroStream";

    private static final ConsumerMode DEFAULT_CONSUMER_MODE = ConsumerMode.AT_MOST_ONCE;

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
        super(null, StreamType.FILE, DEFAULT_CONSUMER_MODE, Arrays.asList(baseDir));
        this.baseDir = baseDir;
    }

    /**
     * Creates a new FileDistroStream instance.
     * 
     * @param alias Stream alias.
     * @param baseDir Absolute path of the base directory.
     * @throws RegistrationException When server cannot register the stream.
     */
    public FileDistroStream(String alias, String baseDir) throws RegistrationException {
        super(alias, StreamType.FILE, DEFAULT_CONSUMER_MODE, Arrays.asList(baseDir));
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
        super(null, StreamType.FILE, mode, Arrays.asList(baseDir));
        this.baseDir = baseDir;
    }

    /**
     * Creates a new FileDistroStream instance.
     * 
     * @param alias Stream alias.
     * @param baseDir Absolute path of the base directory.
     * @param mode DistroStream consumer mode.
     * @throws RegistrationException When server cannot register the stream.
     */
    public FileDistroStream(String alias, String baseDir, ConsumerMode mode) throws RegistrationException {
        super(alias, StreamType.FILE, mode, Arrays.asList(baseDir));
        this.baseDir = baseDir;
    }

    /**
     * Returns the base directory of the file distributed stream.
     * 
     * @return The base directory of the file distributed stream.
     */
    public String getBaseDir() {
        return this.baseDir;
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
        return pollFiles();
    }

    @Override
    public final List<String> poll(long timeout) throws BackendException {
        // TODO: Ignoring timeout for File Distro Streams
        LOGGER.warn("WARN: Ignoring timeout for FileDistroStreams");
        return pollFiles();
    }

    private List<String> pollFiles() throws BackendException {
        LOGGER.info("Polling new stream items...");
        PollRequest req = new PollRequest(this.id);
        DistroStreamClient.request(req);

        req.waitProcessed();
        int error = req.getErrorCode();
        if (error != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("ERROR: Cannot poll stream").append("\n");
            sb.append(ERR_CODE_PREFIX).append(error).append("\n");
            sb.append(ERR_MSG_PREFIX).append(req.getErrorMessage()).append("\n");
            throw new BackendException(sb.toString());
        }
        String response = req.getResponseMessage();
        if (DEBUG) {
            LOGGER.debug("Retrieved stream items: " + response);
        }
        if (response != null && !response.isEmpty()) {
            return Arrays.asList(response.split(" "));
        } else {
            return new ArrayList<>();
        }
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
