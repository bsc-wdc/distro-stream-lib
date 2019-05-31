package es.bsc.distrostreamlib.requests;

import es.bsc.distrostreamlib.types.RequestType;


/**
 * Request to retrieve the status of the given stream.
 */
public class StreamStatusRequest extends Request {

    private final String streamId;


    /**
     * Creates a new instance for STREAM_STATUS requests.
     * 
     * @param streamId Associated stream Id.
     */
    public StreamStatusRequest(String streamId) {
        super(RequestType.STREAM_STATUS);
        this.streamId = streamId;
    }

    @Override
    public String getRequestMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.rt.name()).append(" ");
        sb.append(this.streamId);

        return sb.toString();
    }

}
