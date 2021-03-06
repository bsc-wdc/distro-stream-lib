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

import es.bsc.distrostreamlib.types.RequestType;


/**
 * Request to retrieve the status of the given stream.
 */
public class StreamStatusRequest extends Request {

    private final String streamId;


    /**
     * Creates a new instance for STREAM_STATUS requests.
     * 
     * @param streamId Associated stream Id.
     */
    public StreamStatusRequest(String streamId) {
        super(RequestType.STREAM_STATUS);
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
