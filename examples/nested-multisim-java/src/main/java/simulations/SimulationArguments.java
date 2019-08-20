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
package simulations;

import java.util.Arrays;
import java.util.List;


public class SimulationArguments {

    private final String readStreamAlias;
    private final List<String> writeStreamAliases;

    private final int numIterations;

    private final int sleepBaseTime;
    private final int sleepRandomRange;


    public SimulationArguments(String[] args) {
        assert (args.length == 6);

        this.readStreamAlias = args[0];
        this.writeStreamAliases = Arrays.asList(args[1], args[2]);
        this.numIterations = Integer.valueOf(args[3]);
        this.sleepBaseTime = Integer.valueOf(args[4]);
        this.sleepRandomRange = Integer.valueOf(args[5]);
    }

    public String getReadStreamAlias() {
        return this.readStreamAlias;
    }

    public List<String> getWriteStreamAliases() {
        return this.writeStreamAliases;
    }

    public int getNumIterations() {
        return this.numIterations;
    }

    public int getSleepBaseTime() {
        return this.sleepBaseTime;
    }

    public int getSleepRandomRange() {
        return this.sleepRandomRange;
    }
}
