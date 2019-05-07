package es.bsc.distrostreamlib.requests;

import es.bsc.distrostreamlib.types.RequestType;


public class CloseStreamRequest extends Request {

    private final String streamId;


    /**
     * Creates a new instance for CLOSE_STREAM requests.
     */
    public CloseStreamRequest(String streamId) {
        super(RequestType.CLOSE_STREAM);
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
