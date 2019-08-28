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
package simsync.types;

import java.io.Serializable;
import java.util.Random;


public class State implements Serializable {

    private static final long serialVersionUID = 4L;

    private int localValue;
    private int foreignValue;


    public State() {
        this.localValue = 0;
        this.foreignValue = 0;
    }

    public int getLocalValue() {
        return this.localValue;
    }

    public int getForeignValue() {
        return this.foreignValue;
    }

    public void accumulateLocal(int sleepBaseTime, int sleepRandomRange) {
        // Accumulate
        this.localValue++;

        // Sleep to simulate time spent to process the object
        try {
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(sleepRandomRange);
            int sleepTime = sleepBaseTime + randomInt;
            Thread.sleep(sleepTime);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    public void accumulateForeign(int sleepBaseTime, int sleepRandomRange) {
        // Accumulate
        this.foreignValue++;

        // Sleep to simulate time spent to process the object
        try {
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(sleepRandomRange);
            int sleepTime = sleepBaseTime + randomInt;
            Thread.sleep(sleepTime);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("State[");
        sb.append("Local=").append(this.localValue);
        sb.append(",");
        sb.append("Foreign=").append(this.foreignValue);
        sb.append("]");

        return sb.toString();
    }

}
