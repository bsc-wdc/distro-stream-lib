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
package es.bsc.distrostreamlib.server.types;

import es.bsc.distrostreamlib.types.ConsumerMode;
import es.bsc.distrostreamlib.types.StreamType;

import java.util.List;


/**
 * Class containing the registered streams information.
 */
public class StreamInfo {

    private final String id;
    private final String alias;
    private final StreamType streamType;
    private final ConsumerMode accessMode;
    private final List<String> internalStreamInfo;
    private long pollTimestamp;
    private int numWriters;
    private boolean isClosed;


    /**
     * Creates a new StreamInfo instance.
     * 
     * @param id Associated stream id.
     * @param alias Associated stream alias.
     * @param streamType Stream type.
     * @param accessMode Stream consumer access mode.
     * @param internalStreamInfo Internal stream information.
     */
    public StreamInfo(String id, String alias, StreamType streamType, ConsumerMode accessMode,
        List<String> internalStreamInfo) {

        this.id = id;
        this.alias = alias;
        this.streamType = streamType;
        this.accessMode = accessMode;
        this.internalStreamInfo = internalStreamInfo;
        this.pollTimestamp = -1;
        this.numWriters = 0;
        this.isClosed = false;
    }

    /**
     * Returns the associated stream Id.
     * 
     * @return The associated stream Id.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Returns the associated stream alias.
     * 
     * @return The associated stream alias.
     */
    public String getAlias() {
        return this.alias;
    }

    /**
     * Returns the stream type.
     * 
     * @return The stream type.
     */
    public StreamType getStreamType() {
        return this.streamType;
    }

    /**
     * Returns the consumer access mode.
     * 
     * @return The consumer access mode.
     */
    public ConsumerMode getAccessMode() {
        return this.accessMode;
    }

    /**
     * Returns the specific stream information.
     * 
     * @return The specific stream information. Empty list if no specific information was registered.
     */
    public List<String> getInternalStreamInfo() {
        return this.internalStreamInfo;
    }

    /**
     * Adds a new specific stream information.
     * 
     * @param msg New specific stream information.
     */
    public void addInternalStreamInfo(String msg) {
        this.internalStreamInfo.add(msg);
    }

    /**
     * Clears all the specific stream information.
     */
    public void clearInternalStreamInfo() {
        this.internalStreamInfo.clear();
    }

    /**
     * Returns the last poll's timestamp.
     * 
     * @return The last poll's timestamp.
     */
    public long getLastPollTimestamp() {
        return this.pollTimestamp;
    }

    /**
     * Sets now as the new poll timestamp.
     */
    public void setPollTimestamp() {
        this.pollTimestamp = System.currentTimeMillis();
    }

    /**
     * Sets the poll's timestamp to the given value {@code timestamp}.
     * 
     * @param timestamp New timestamp.
     */
    public void setPollTimestamp(long timestamp) {
        this.pollTimestamp = timestamp;
    }

    /**
     * Adds a new writer to the stream.
     */
    public void addWriter() {
        this.numWriters++;
    }

    /**
     * Removes a writer of the stream.
     */
    public void removeWriter() {
        this.numWriters--;
    }

    /**
     * Returns whether the stream has active writers or not.
     * 
     * @return {@literal true} if the stream has active writers, {@literal false} otherwise.
     */
    public boolean hasWriters() {
        return this.numWriters > 0;
    }

    /**
     * Returns whether the stream has been marked to be closed or not.
     * 
     * @return {@code true} if the stream has been marked to be closed, {@code false} otherwise.
     */
    public boolean isStreamClosed() {
        return this.isClosed;
    }

    /**
     * Marks the stream as closed.
     */
    public void markAsClosed() {
        this.isClosed = true;
    }

}
