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

import java.util.concurrent.Semaphore;


/**
 * Abstract request implementation.
 */
public abstract class Request {

    protected final RequestType rt;

    private boolean hasBeenProcessed;
    private final Semaphore waitSem;

    private int errorCode;
    private String errorMessage;
    private String responseMessage;


    /**
     * Creates a new request of the given type {@code rt}.
     * 
     * @param rt Request type.
     */
    public Request(RequestType rt) {
        this.rt = rt;

        this.hasBeenProcessed = false;
        this.waitSem = new Semaphore(0);

        this.errorCode = -1;
        this.errorMessage = null;
        this.responseMessage = null;
    }

    /**
     * Returns the request type.
     * 
     * @return The request type.
     */
    public final RequestType getType() {
        return this.rt;
    }

    /**
     * Returns whether the request has been processed or not.
     * 
     * @return {@code true} if the request has been processed, {@code false} otherwise.
     */
    public final boolean isProcessed() {
        return this.hasBeenProcessed;
    }

    /**
     * Blocks the current thread until the request has been processed.
     */
    public final void waitProcessed() {
        try {
            this.waitSem.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Returns the error code of the request. {@code null} if no error was raised.
     * 
     * @return The error code of the request. {@code null} if no error was raised.
     */
    public final int getErrorCode() {
        return this.errorCode;
    }

    /**
     * Returns the error message of the request. {@code null} if no error was raised.
     * 
     * @return The error message of the request. {@code null} if no error was raised.
     */
    public final String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Returns the response message of the server to the given request. {@code null} if response has not been received
     * yet or there was an error.
     * 
     * @return The response message of the server to the given request. {@code null} if response has not been received
     *         yet or there was an error.
     */
    public final String getResponseMessage() {
        return this.responseMessage;
    }

    /**
     * Sets a new error message and code.
     * 
     * @param errorCode Error code.
     * @param errorMessage Error message.
     */
    public final void setError(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * Sets a new response message.
     * 
     * @param message New response message.
     */
    public final void setResponse(String message) {
        this.errorCode = 0;
        this.responseMessage = message;
    }

    /**
     * Marks the request as processed.
     */
    public final void setProcessed() {
        this.hasBeenProcessed = true;
        this.waitSem.release();
    }

    /**
     * Returns the request message to send to the server.
     * 
     * @return The request message to send to the server.
     */
    public abstract String getRequestMessage();

    @Override
    public String toString() {
        return this.rt.name();
    }

}
