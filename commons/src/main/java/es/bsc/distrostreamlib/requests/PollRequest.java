package es.bsc.distrostreamlib.requests;

import es.bsc.distrostreamlib.types.RequestType;


/**
 * Request to poll new items from a given stream.
 */
public class PollRequest extends Request {

    private final String streamId;


    /**
     * Creates a new instance for POLL requests.
     * 
     * @param streamId Associated stream Id.
     */
    public PollRequest(String streamId) {
        super(RequestType.POLL);
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
