package es.bsc.distrostreamlib.requests;

import es.bsc.distrostreamlib.types.RequestType;


/**
 * Request to unregister a given client.
 */
public class UnregisterClientRequest extends Request {

    private final String clientIP;


    /**
     * Creates a new instance for UNREGISTER_CLIENT requests.
     * 
     * @param clientIP Client IP to unregister.
     */
    public UnregisterClientRequest(String clientIP) {
        super(RequestType.UNREGISTER_CLIENT);
        this.clientIP = clientIP;
    }

    @Override
    public String getRequestMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.rt.name()).append(" ");
        sb.append(this.clientIP);

        return sb.toString();
    }

}
