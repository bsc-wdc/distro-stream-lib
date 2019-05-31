package es.bsc.distrostreamlib.requests;

import es.bsc.distrostreamlib.types.RequestType;


/**
 * Request to stop the client.
 */
public class StopRequest extends Request {

    /**
     * Creates a new instance for STOP requests.
     */
    public StopRequest() {
        super(RequestType.STOP);
    }

    @Override
    public String getRequestMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.rt.name());

        return sb.toString();
    }

}
