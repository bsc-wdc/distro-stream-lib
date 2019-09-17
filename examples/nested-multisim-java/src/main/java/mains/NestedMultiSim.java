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

import compss.NESTED;

import es.bsc.distrostreamlib.api.objects.ObjectDistroStream;
import es.bsc.distrostreamlib.types.ConsumerMode;

import java.util.ArrayList;
import java.util.List;


public class NestedMultiSim {

    private static final int NUM_SIMULATIONS = 3;


    public static void main(String[] args) throws Exception {
        // Start application
        System.out.println("[INFO] Starting application");
        long start = System.currentTimeMillis();

        // Parse arguments
        System.out.println("[INFO] Parsing application arguments");
        NestedMultiSimArguments nmargs = new NestedMultiSimArguments(args);

        // Create streams
        System.out.println("[INFO] Creating streams");
        List<ObjectDistroStream<State>> streams = new ArrayList<>();
        for (int i = 0; i < NUM_SIMULATIONS; ++i) {
            String streamAlias = "Simulation" + i;
            ObjectDistroStream<State> ods = new ObjectDistroStream<State>(streamAlias, ConsumerMode.AT_MOST_ONCE);
            streams.add(ods);
        }

        // Spawn simulations
        System.out.println("[INFO] Launching simulations");
        Integer[] evs = new Integer[NUM_SIMULATIONS];
        evs[0] = NESTED.simulation(streams.get(0), streams.get(1), streams.get(2), nmargs.getNumIterations(),
                nmargs.getSimulationSleepBaseTime(), nmargs.getSimulationSleepRandomRange());
        evs[1] = NESTED.simulation(streams.get(1), streams.get(0), streams.get(2), nmargs.getNumIterations(),
                nmargs.getSimulationSleepBaseTime(), nmargs.getSimulationSleepRandomRange());
        evs[2] = NESTED.simulation(streams.get(2), streams.get(0), streams.get(1), nmargs.getNumIterations(),
                nmargs.getSimulationSleepBaseTime(), nmargs.getSimulationSleepRandomRange());

        // Synchronize
        System.out.println("[INFO] Waiting for simulations to finish");
        for (int i = 0; i < NUM_SIMULATIONS; ++i) {
            if (evs[i] != 0) {
                throw new Exception("ERROR: Nested simulation failed");
            }
        }

        // End
        System.out.println("DONE");
        long end = System.currentTimeMillis();
        long elapsedTime = end - start;
        System.out.println("[TIME] TOTAL ELAPSED: " + elapsedTime);
    }

}
