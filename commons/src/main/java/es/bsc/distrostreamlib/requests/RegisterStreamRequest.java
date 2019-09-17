/*
 *  Copyright 2002-2019 Barcelona Supercomputing Center (www.bsc.es)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
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
