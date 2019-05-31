package es.bsc.distrostreamlib.requests;

import es.bsc.distrostreamlib.types.RequestType;


/**
 * To request the Bootstrap server information.
 */
public class BootstrapServerRequest extends Request {

    /**
     * Creates a new instance for BOOTSTRAP_SERVER requests.
     */
    public BootstrapServerRequest() {
        super(RequestType.BOOTSTRAP_SERVER);
    }

    @Override
    public String getRequestMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.rt.name());

        return sb.toString();
    }

}
