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
 * Request to publish a new message to a given stream.
 */
public class PublishRequest extends Request {

    private final String streamId;
    private final String msg;


    /**
     * Creates a new instance for PUBLISH requests.
     * 
     * @param streamId Associated stream Id.
     * @param msg Message to publish.
     */
    public PublishRequest(String streamId, String msg) {
        super(RequestType.PUBLISH);
        this.streamId = streamId;
        this.msg = msg;
    }

    @Override
    public String getRequestMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.rt.name()).append(" ");
        sb.append(this.streamId).append(" ");
        sb.append(this.msg);

        return sb.toString();
    }

}
