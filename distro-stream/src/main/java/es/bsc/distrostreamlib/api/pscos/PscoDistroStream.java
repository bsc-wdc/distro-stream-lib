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
package es.bsc.distrostreamlib.api.pscos;

import es.bsc.distrostreamlib.api.impl.DistroStreamImpl;
import es.bsc.distrostreamlib.client.DistroStreamClient;
import es.bsc.distrostreamlib.exceptions.BackendException;
import es.bsc.distrostreamlib.exceptions.RegistrationException;
import es.bsc.distrostreamlib.loggers.Loggers;
import es.bsc.distrostreamlib.requests.PollRequest;
import es.bsc.distrostreamlib.requests.PublishRequest;
import es.bsc.distrostreamlib.types.ConsumerMode;
import es.bsc.distrostreamlib.types.StreamType;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import storage.StorageException;
import storage.StorageItf;
import storage.StubItf;


/**
 * Distributed Stream implementation for PSCO objects.
 *
 * @param <T> Internal Object Stream type.
 */
public class PscoDistroStream<T> extends DistroStreamImpl<T> {

    private static final Logger LOGGER = LogManager.getLogger(Loggers.PSCO_DISTRO_STREAM);
    private static final boolean DEBUG = LOGGER.isDebugEnabled();


    /**
     * Creates a new PscoDistroStream instance for serialization.
     */
    public PscoDistroStream() {
        // Nothing to do, only for serialization
    }

    /**
     * Creates a new PscoDistroStream instance.
     * 
     * @param mode DistroStream consumer mode.
     * @throws RegistrationException When server cannot register stream.
     */
    public PscoDistroStream(ConsumerMode mode) throws RegistrationException {
        super(null, StreamType.PSCO, mode, new LinkedList<>());
    }

    /**
     * Creates a new PscoDistroStream instance.
     * 
     * @param alias Stream alias.
     * @param mode DistroStream consumer mode.
     * @throws RegistrationException When server cannot register stream.
     */
    public PscoDistroStream(String alias, ConsumerMode mode) throws RegistrationException {
        super(alias, StreamType.PSCO, mode, new LinkedList<>());
    }

    /*
     * PUBLISH METHODS
     */

    @Override
    public final void publish(T object) throws BackendException {
        LOGGER.info("Publishing new PSCO object...");
        publishPsco(object);
        LOGGER.info("Published new PSCO object");
    }

    @Override
    public final void publish(List<T> objects) throws BackendException {
        LOGGER.info("Publishing new List of PSCOs...");
        for (T obj : objects) {
            publishPsco(obj);
        }
        LOGGER.info("Published new List of PSCOs");
    }

    private void publishPsco(T object) throws BackendException {
        // Persist the object
        LOGGER.debug("Persisting user PSCO...");
        StubItf stub = (StubItf) object;
        if (stub.getID() == null) {
            String alias = UUID.randomUUID().toString();
            stub.makePersistent(alias);
        }
        String pscoId = stub.getID();

        // Register it on the server
        LOGGER.debug("Registering PSCO publish...");
        PublishRequest req = new PublishRequest(this.id, pscoId);
        DistroStreamClient.request(req);

        req.waitProcessed();
        int error = req.getErrorCode();
        if (error != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("ERROR: Cannot publish stream").append("\n");
            sb.append(ERR_CODE_PREFIX).append(error).append("\n");
            sb.append(ERR_MSG_PREFIX).append(req.getErrorMessage()).append("\n");
            throw new BackendException(sb.toString());
        }
        String response = req.getResponseMessage();
        if (DEBUG) {
            LOGGER.debug("Publish stream answer: " + response);
        }
    }

    /*
     * POLL METHODS
     */

    @Override
    public final List<T> poll() throws BackendException {
        return pollPscos();
    }

    @Override
    public final List<T> poll(long timeout) throws BackendException {
        // TODO: Ignoring timeout for PSCO Distro Streams
        LOGGER.warn("WARN: Ignoring timeout for PscoDistroStream");
        return pollPscos();
    }

    private List<T> pollPscos() throws BackendException {
        // Poll PSCO ids
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

        // Parse received ids
        List<T> newPscos = new ArrayList<>();
        if (response != null && !response.isEmpty()) {
            // Retrieve the objects from its ids
            for (String pscoId : response.split(" ")) {
                try {
                    @SuppressWarnings("unchecked")
                    T object = (T) StorageItf.getByID(pscoId);
                    newPscos.add(object);
                } catch (StorageException se) {
                    throw new BackendException("ERROR: Cannot getById PSCO with id = " + pscoId, se);
                }
            }
        }
        return newPscos;
    }

    /*
     * SERIALIZATION METHODS
     */

    @Override
    public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {
        super.readExternal(oi);
    }

    @Override
    public void writeExternal(ObjectOutput oo) throws IOException {
        super.writeExternal(oo);
    }

}
