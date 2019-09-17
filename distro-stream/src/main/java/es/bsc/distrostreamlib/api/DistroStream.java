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
package es.bsc.distrostreamlib.api;

import es.bsc.distrostreamlib.exceptions.BackendException;
import es.bsc.distrostreamlib.exceptions.RegistrationException;
import es.bsc.distrostreamlib.types.StreamType;

import java.io.Externalizable;
import java.util.List;


/**
 * Abstract class for Distributed Streams of the given type.
 * 
 * @param <T> Internal Stream type.
 */
public interface DistroStream<T> extends Externalizable {

    /*
     * METADATA METHODS
     */

    /**
     * Returns the stream Id.
     * 
     * @return The stream Id.
     */
    public String getId();

    /**
     * Returns the stream alias.
     * 
     * @return The stream alias.
     */
    public String getAlias();

    /**
     * Returns the stream type.
     * 
     * @return The stream type.
     */
    public StreamType getStreamType();

    /*
     * PUBLISH METHODS
     */

    /**
     * Publishes the given message {@code message} to the current stream.
     * 
     * @param message T object to publish.
     * @throws RegistrationException Raised if there is an internal error when creating the stream handler.
     */
    public void publish(T message) throws BackendException;

    /**
     * Publishes the given messages {@code messages} to the current stream.
     * 
     * @param messages List of T objects to publish.
     * @throws RegistrationException Raised if there is an internal error when creating the stream handler.
     */
    public void publish(List<T> messages) throws BackendException;

    /*
     * POLL METHODS
     */

    /**
     * Retrieves all the unread messages of the current stream.
     * 
     * @return List of unread messages. Can be an empty list if any message have been published.
     */
    public List<T> poll() throws BackendException;

    /**
     * Retrieves all the unread messages of the current stream.
     * 
     * @param timeout Polling timeout in milliseconds.
     * @return List of unread messages. Can be an empty list if any message have been published.
     */
    public List<T> poll(long timeout) throws BackendException;

    /*
     * CONTROL METHODS
     */

    /**
     * Returns whether the stream is closed or not.
     * 
     * @return {@code true} if the stream is closed, {@code false} otherwise.
     */
    public boolean isClosed();

    /**
     * Marks the stream as closed.
     */
    public void close();

}
