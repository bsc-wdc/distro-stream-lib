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
package filters;

public class FilterArguments {

    private final String aliasSensor;
    private final String aliasFiltered;
    private final int batchSize;
    private final int sleepBaseTime;
    private final int sleepRandomRange;


    /**
     * Parses the command line arguments to an internal structure.
     * 
     * @param args Command line arguments.
     */
    public FilterArguments(String[] args) {
        assert (args.length == 5);

        this.aliasSensor = args[0];
        this.aliasFiltered = args[1];
        this.batchSize = Integer.valueOf(args[2]);
        this.sleepBaseTime = Integer.valueOf(args[3]);
        this.sleepRandomRange = Integer.valueOf(args[4]);
    }

    /**
     * Returns the sensor's alias.
     * 
     * @return The sensor's alias.
     */
    public String getAliasSensor() {
        return this.aliasSensor;
    }

    /**
     * Returns the filter alias.
     * 
     * @return The filter alias.
     */
    public String getAliasFiltered() {
        return this.aliasFiltered;
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
     * Returns the sleep base time.
     * 
     * @return The sleep base time.
     */
    public int getSleepBaseTime() {
        return this.sleepBaseTime;
    }

    /**
     * Returns the sleep random range.
     * 
     * @return The sleep random range.
     */
    public int getSleepRandomRange() {
        return this.sleepRandomRange;
    }
}
