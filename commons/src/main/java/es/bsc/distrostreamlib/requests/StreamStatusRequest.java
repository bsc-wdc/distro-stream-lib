package es.bsc.distrostreamlib.requests;

import es.bsc.distrostreamlib.types.RequestType;


public class StreamStatusRequest extends Request {

    private final String streamId;


    /**
     * Creates a new instance for STREAM_STATUS requests.
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
