package es.bsc.distrostreamlib.requests;

import es.bsc.distrostreamlib.types.RequestType;


/**
 * To request the closure of a given stream.
 */
public class AddStreamWriterRequest extends Request {

    private final String streamId;


    /**
     * Creates a new instance for CLOSE_STREAM requests.
     * 
     * @param streamId Associated stream Id.
     */
    public AddStreamWriterRequest(String streamId) {
        super(RequestType.ADD_STREAM_WRITER);
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
