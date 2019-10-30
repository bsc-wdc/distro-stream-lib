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
package mains;

public class NestedMultiSimArguments {

    private final int numIterations;

    private final int simulationSleepBaseTime;
    private final int simulationSleepRandomRange;


    /**
     * Parses the command line arguments to an internal structure.
     * 
     * @param args Command line arguments.
     */
    public NestedMultiSimArguments(String[] args) {
        assert (args.length == 3);

        this.numIterations = Integer.valueOf(args[0]);
        this.simulationSleepBaseTime = Integer.valueOf(args[1]);
        this.simulationSleepRandomRange = Integer.valueOf(args[2]);
    }

    /**
     * Returns the number of iterations.
     * 
     * @return The number of iterations.
     */
    public int getNumIterations() {
        return this.numIterations;
    }

    /**
     * Returns the simulation base time.
     * 
     * @return The simulation base time.
     */
    public int getSimulationSleepBaseTime() {
        return this.simulationSleepBaseTime;
    }

    /**
     * Returns the simulation random range.
     * 
     * @return The simulation random range.
     */
    public int getSimulationSleepRandomRange() {
        return this.simulationSleepRandomRange;
    }
}
