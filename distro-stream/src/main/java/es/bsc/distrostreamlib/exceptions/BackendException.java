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
package es.bsc.distrostreamlib.exceptions;

/**
 * Exception for Backend errors.
 */
public class BackendException extends Exception {

    /**
     * Exception Version UID are 2L in all Runtime.
     */
    private static final long serialVersionUID = 3L;


    /**
     * Creates a new exception for registration with the given message {@code msg}.
     * 
     * @param msg Exception message.
     */
    public BackendException(String msg) {
        super(msg);
    }

    /**
     * Creates a new exception for registration with the given message {@code msg} and nested exception {@code e}.
     * 
     * @param msg Exception message.
     * @param e Nested exception.
     */
    public BackendException(String msg, Exception e) {
        super(msg, e);
    }

}
