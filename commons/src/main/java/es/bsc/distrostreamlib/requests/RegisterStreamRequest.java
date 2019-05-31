package es.bsc.distrostreamlib.requests;

import es.bsc.distrostreamlib.types.ConsumerMode;
import es.bsc.distrostreamlib.types.RequestType;
import es.bsc.distrostreamlib.types.StreamType;

import java.util.List;


/**
 * Request to register a new stream.
 */
public class RegisterStreamRequest extends Request {

    private final String alias;
    private final StreamType streamType;
    private final ConsumerMode accessMode;
    private final List<String> internalStreamInfo;


    /**
     * Creates a new instance for REGISTER_STREAM requests.
     * 
     * @param alias Stream alias
     * @param streamType Stream type.
     * @param accessMode Consumer access mode.
     * @param internalStreamInfo Internal stream implementation information.
     */
    public RegisterStreamRequest(String alias, StreamType streamType, ConsumerMode accessMode,
            List<String> internalStreamInfo) {
        super(RequestType.REGISTER_STREAM);
        this.streamType = streamType;
        this.accessMode = accessMode;
        this.alias = alias;
        this.internalStreamInfo = internalStreamInfo;
    }

    @Override
    public String getRequestMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.rt.name()).append(" ");
        sb.append(this.streamType).append(" ");
        sb.append(this.accessMode).append(" ");
        sb.append(this.alias);
        for (String si : this.internalStreamInfo) {
            sb.append(" ").append(si);
        }

        return sb.toString();
    }

}
