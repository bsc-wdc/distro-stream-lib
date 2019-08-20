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
package es.bsc.distrostreamlib.api.impl;

import es.bsc.distrostreamlib.api.DistroStream;
import es.bsc.distrostreamlib.client.DistroStreamClient;
import es.bsc.distrostreamlib.exceptions.BackendException;
import es.bsc.distrostreamlib.exceptions.RegistrationException;
import es.bsc.distrostreamlib.loggers.Loggers;
import es.bsc.distrostreamlib.requests.CloseStreamRequest;
import es.bsc.distrostreamlib.requests.RegisterStreamRequest;
import es.bsc.distrostreamlib.requests.StreamStatusRequest;
import es.bsc.distrostreamlib.types.ConsumerMode;
import es.bsc.distrostreamlib.types.StreamType;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Abstract class for Distributed Streams of the given type.
 * 
 * @param <T> Internal Stream type.
 */
public abstract class DistroStreamImpl<T> implements DistroStream<T> {

    private static final Logger LOGGER = LogManager.getLogger(Loggers.DISTRO_STREAM);

    protected static final String ERR_CODE_PREFIX = "  - Error Code: ";
    protected static final String ERR_MSG_PREFIX = "  - Nested Error Message: ";

    protected String alias;
    protected String id;
    protected StreamType streamType;
    protected ConsumerMode mode;


    /**
     * Creates a new DistroStream instance for serialization.
     */
    public DistroStreamImpl() {
        // Nothing to do, only for serialization
    }

    /**
     * Creates a new DistroStream instance.
     * 
     * @param alias Stream alias.
     * @param streamType DistroStream internal type.
     * @param accessMode DistroStream consumer mode.
     * @param internalStreamInfo Specific information about hte DistroStream implementation to be stored in the server
     * @throws RegistrationException When server cannot register the stream.
     */
    public DistroStreamImpl(String alias, StreamType streamType, ConsumerMode accessMode,
        List<String> internalStreamInfo) throws RegistrationException {

        this.alias = alias;
        this.streamType = streamType;

        // Register stream creation and get stream id
        LOGGER.info("Registering new Stream...");
        RegisterStreamRequest req = new RegisterStreamRequest(alias, streamType, accessMode, internalStreamInfo);
        DistroStreamClient.request(req);

        req.waitProcessed();
        int error = req.getErrorCode();
        if (error != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("ERROR: Cannot register stream").append("\n");
            sb.append(ERR_CODE_PREFIX).append(error).append("\n");
            sb.append(ERR_MSG_PREFIX).append(req.getErrorMessage()).append("\n");
            throw new RegistrationException(sb.toString());
        }
        this.id = req.getResponseMessage();

        // Store access mode
        this.mode = accessMode;

        LOGGER.info("New Stream registered with ID = " + this.id);
    }

    /*
     * METADATA METHODS
     */

    @Override
    public final String getId() {
        return this.id;
    }

    @Override
    public final String getAlias() {
        return this.alias;
    }

    @Override
    public final StreamType getStreamType() {
        return this.streamType;
    }

    /*
     * PUBLISH METHODS
     */

    @Override
    public abstract void publish(T message) throws BackendException;

    @Override
    public abstract void publish(List<T> messages) throws BackendException;

    /*
     * POLL METHODS
     */

    @Override
    public abstract List<T> poll() throws BackendException;

    @Override
    public abstract List<T> poll(long timeout) throws BackendException;

    /*
     * CONTROL METHODS
     */

    @Override
    public final boolean isClosed() {
        StreamStatusRequest req = new StreamStatusRequest(this.id);
        DistroStreamClient.request(req);

        req.waitProcessed();
        int error = req.getErrorCode();
        if (error != 0) {
            // Only log the error, no need to raise an exception
            StringBuilder sb = new StringBuilder();
            sb.append("ERROR: Cannot retrieve stream status").append("\n");
            sb.append(ERR_CODE_PREFIX).append(error).append("\n");
            sb.append(ERR_MSG_PREFIX).append(req.getErrorMessage()).append("\n");
            LOGGER.error(sb.toString());
            return false;
        }

        // Return stream status
        return Boolean.parseBoolean(req.getResponseMessage());
    }

    @Override
    public final void close() {
        CloseStreamRequest req = new CloseStreamRequest(this.id);
        DistroStreamClient.request(req);

        req.waitProcessed();
        int error = req.getErrorCode();
        if (error != 0) {
            // Only log the error, no need to raise an exception
            StringBuilder sb = new StringBuilder();
            sb.append("ERROR: Cannot close stream").append("\n");
            sb.append(ERR_CODE_PREFIX).append(error).append("\n");
            sb.append(ERR_MSG_PREFIX).append(req.getErrorMessage()).append("\n");
            LOGGER.error(sb.toString());
            return;
        }
        // No need to process the answer message. Checking the error is enough.
    }

    /*
     * SERIALIZATION METHODS
     */

    @Override
    public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {
        this.alias = (String) oi.readObject();
        this.id = (String) oi.readObject();
        this.streamType = (StreamType) oi.readObject();
        this.mode = (ConsumerMode) oi.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput oo) throws IOException {
        oo.writeObject(this.alias);
        oo.writeObject(this.id);
        oo.writeObject(this.streamType);
        oo.writeObject(this.mode);
    }

}
