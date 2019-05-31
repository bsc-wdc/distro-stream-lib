package es.bsc.distrostreamlib.requests;

import es.bsc.distrostreamlib.types.RequestType;


/**
 * Request to register a new client.
 */
public class RegisterClientRequest extends Request {

    private final String clientIP;


    /**
     * Creates a new instance for REGISTER_CLIENT requests.
     * 
     * @param clientIP Associated client IP.
     */
    public RegisterClientRequest(String clientIP) {
        super(RequestType.REGISTER_CLIENT);
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
