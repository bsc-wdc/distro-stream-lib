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

public class NestedHybridArguments {

    private final int sensorNumFiles;
    private final int sensorSleepBaseTime;
    private final int sensorSleepRandomRange;

    private final int batchSize;
    private final int filterSleepBaseTime;
    private final int filterSleepRandomRange;

    private final int extractSleepBaseTime;
    private final int extractSleepRandomRange;

    private final int taskFlowDepth;
    private final int taskFlowSleepBaseTime;
    private final int taskFlowSleepRandomRange;


    /**
     * Parses the command line arguments to an internal structure.
     * 
     * @param args Command line arguments.
     */
    public NestedHybridArguments(String[] args) {
        assert (args.length == 11);

        this.sensorNumFiles = Integer.parseInt(args[0]);
        this.sensorSleepBaseTime = Integer.parseInt(args[1]);
        this.sensorSleepRandomRange = Integer.parseInt(args[2]);

        this.batchSize = Integer.parseInt(args[3]);
        this.filterSleepBaseTime = Integer.parseInt(args[4]);
        this.filterSleepRandomRange = Integer.parseInt(args[5]);

        this.extractSleepBaseTime = Integer.parseInt(args[6]);
        this.extractSleepRandomRange = Integer.parseInt(args[7]);

        this.taskFlowDepth = Integer.parseInt(args[8]);
        this.taskFlowSleepBaseTime = Integer.parseInt(args[9]);
        this.taskFlowSleepRandomRange = Integer.parseInt(args[10]);
    }

    /**
     * Returns the number of sensor files.
     * 
     * @return The number of sensor files.
     */
    public int getSensorNumFiles() {
        return this.sensorNumFiles;
    }

    /**
     * Returns the sensor's base sleep time.
     * 
     * @return The sensor's base sleep time.
     */
    public int getSensorSleepBaseTime() {
        return this.sensorSleepBaseTime;
    }

    /**
     * Returns the sesor's sleep random range.
     * 
     * @return The sesor's sleep random range.
     */
    public int getSensorSleepRandomRange() {
        return this.sensorSleepRandomRange;
    }

    /**
     * Returns the batch size.
     * 
     * @return The batch size.
     */
    public int getBatchSize() {
        return this.batchSize;
    }

    /**
     * Returns the filter's base sleep time.
     * 
     * @return The filter's base sleep time.
     */
    public int getFilterSleepBaseTime() {
        return this.filterSleepBaseTime;
    }

    /**
     * Returns the filter's sleep random range.
     * 
     * @return The filter's sleep random range.
     */
    public int getFilterSleepRandomRange() {
        return this.filterSleepRandomRange;
    }

    /**
     * Returns the extract's base sleep time.
     * 
     * @return The extract's base sleep time.
     */
    public int getExtractSleepBaseTime() {
        return this.extractSleepBaseTime;
    }

    /**
     * Returns the extract's sleep random range.
     * 
     * @return The extract's sleep random range.
     */
    public int getExtractSleepRandomRange() {
        return this.extractSleepRandomRange;
    }

    /**
     * Returns the task flow depth.
     * 
     * @return The task flow depth.
     */
    public int getTaskFlowDepth() {
        return this.taskFlowDepth;
    }

    /**
     * Returns the tf's base sleep time.
     * 
     * @return The tf's base sleep time.
     */
    public int getTaskFlowSleepBaseTime() {
        return this.taskFlowSleepBaseTime;
    }

    /**
     * Returns the tf's sleep random range.
     * 
     * @return The tf's sleep random range.
     */
    public int getTaskFlowSleepRandomRange() {
        return this.taskFlowSleepRandomRange;
    }
}
