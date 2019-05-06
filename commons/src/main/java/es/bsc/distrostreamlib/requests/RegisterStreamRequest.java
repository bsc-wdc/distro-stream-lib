package es.bsc.distrostreamlib.requests;

import es.bsc.distrostreamlib.types.ConsumerMode;
import es.bsc.distrostreamlib.types.RequestType;
import es.bsc.distrostreamlib.types.StreamType;

import java.util.List;


public class RegisterStreamRequest extends Request {

    private final StreamType streamType;
    private final ConsumerMode accessMode;
    private final List<String> internalStreamInfo;


    /**
     * Creates a new instance for REGISTER_STREAM requests.
     * 
     * @param streamType Stream type.
     * @param accessMode Consumer access mode.
     */
    public RegisterStreamRequest(StreamType streamType, ConsumerMode accessMode, List<String> internalStreamInfo) {
        super(RequestType.REGISTER_STREAM);
        this.streamType = streamType;
        this.accessMode = accessMode;
        this.internalStreamInfo = internalStreamInfo;
    }

    @Override
    public String getRequestMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.rt.name()).append(" ");
        sb.append(this.streamType).append(" ");
        sb.append(this.accessMode);
        for (String si : this.internalStreamInfo) {
            sb.append(" ").append(si);
        }

        return sb.toString();
    }

}
