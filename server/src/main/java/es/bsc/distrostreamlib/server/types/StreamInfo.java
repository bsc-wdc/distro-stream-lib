package es.bsc.distrostreamlib.server.types;

import es.bsc.distrostreamlib.types.ConsumerMode;
import es.bsc.distrostreamlib.types.StreamType;

import java.util.List;
import java.util.UUID;


public class StreamInfo {

    private final UUID id;
    private final StreamType streamType;
    private final ConsumerMode accessMode;
    private final List<String> internalStreamInfo;
    private long pollTimestamp;


    /**
     * Creates a new StreamInfo instance.
     * 
     * @param id Associated stream id.
     * @param streamType Stream type.
     * @param accessMode Stream consumer access mode.
     */
    public StreamInfo(UUID id, StreamType streamType, ConsumerMode accessMode, List<String> internalStreamInfo) {
        this.id = id;
        this.streamType = streamType;
        this.accessMode = accessMode;
        this.internalStreamInfo = internalStreamInfo;
        this.pollTimestamp = -1;
    }

    /**
     * Returns the associated stream Id.
     * 
     * @return The associated stream Id.
     */
    public UUID getId() {
        return this.id;
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

}
