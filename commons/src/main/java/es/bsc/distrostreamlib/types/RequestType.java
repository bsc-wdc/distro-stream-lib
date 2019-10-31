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
package es.bsc.distrostreamlib.types;

/**
 * Client-Server request types.
 */
public enum RequestType {
    REGISTER_CLIENT, // Registers a new client
    UNREGISTER_CLIENT, // Unregisters a client
    BOOTSTRAP_SERVER, // Retrieve the bootstrap server information
    REGISTER_STREAM, // Registers a stream
    STREAM_STATUS, // Returns the stream status (open or closed)
    CLOSE_STREAM, // Marks an stream to be closed
    ADD_STREAM_WRITER, // Adds a new writer to the stream
    POLL, // Polls changes on a given stream
    PUBLISH, // Publishes a new element on a given stream
    STOP; // Marks the server to stop
}
