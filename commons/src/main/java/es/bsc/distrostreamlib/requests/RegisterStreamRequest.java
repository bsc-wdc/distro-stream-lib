package es.bsc.distrostreamlib.requests;

import es.bsc.distrostreamlib.types.ConsumerMode;
import es.bsc.distrostreamlib.types.RequestType;
import es.bsc.distrostreamlib.types.StreamType;


public class RegisterStreamRequest extends Request {

    private final StreamType streamType;
    private final ConsumerMode accessMode;


    /**
     * Creates a new instance for REGISTER_STREAM requests.
     * 
     * @param streamType Stream type.
     * @param accessMode Consumer access mode.
     */
    public RegisterStreamRequest(StreamType streamType, ConsumerMode accessMode) {
        super(RequestType.REGISTER_STREAM);
        this.streamType = streamType;
        this.accessMode = accessMode;
    }

    @Override
    public String getRequestMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.rt.name());
        sb.append(this.streamType);
        sb.append(this.accessMode);

        return sb.toString();
    }

}
