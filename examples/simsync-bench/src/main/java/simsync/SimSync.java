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
package simsync;

import es.bsc.compss.api.COMPSs;
import es.bsc.distrostreamlib.api.objects.ObjectDistroStream;
import es.bsc.distrostreamlib.exceptions.BackendException;
import es.bsc.distrostreamlib.exceptions.RegistrationException;
import es.bsc.distrostreamlib.types.ConsumerMode;
import simsync.types.State;

import java.util.ArrayList;
import java.util.List;


public class SimSync {

    private static final int NUM_SIMULATIONS = 2;


    public static void main(String[] args) throws Exception {
        // Start application
        System.out.println("[INFO] Starting application");
        final long start = System.currentTimeMillis();

        // Get arguments
        final int numIterations = Integer.parseInt(args[0]);
        final int processTime = Integer.parseInt(args[1]);

        // Launch normal process
        final long timeNormalStart = System.currentTimeMillis();
        normalSims(numIterations, processTime);
        COMPSs.barrier();
        final long timeNormalEnd = System.currentTimeMillis();
        final long timeNormalElapsed = timeNormalEnd - timeNormalStart;
        System.out.println("[TIME] NORMAL ELAPSED: " + timeNormalElapsed + " ms");

        // Launch stream process
        final long timeStreamStart = System.currentTimeMillis();
        streamSims(numIterations, processTime);
        COMPSs.barrier();
        final long timeStreamEnd = System.currentTimeMillis();
        final long timeStreamElapsed = timeStreamEnd - timeStreamStart;
        System.out.println("[TIME] STREAMS ELAPSED: " + timeStreamElapsed + " ms");

        // End
        System.out.println("DONE");
        final long end = System.currentTimeMillis();
        final long elapsedTime = end - start;
        System.out.println("[TIME] TOTAL ELAPSED: " + elapsedTime + " ms");
    }

    private static void normalSims(int numIterations, int processTime) {
        // Create initial states
        State[] states = new State[NUM_SIMULATIONS];
        for (int i = 0; i < NUM_SIMULATIONS; ++i) {
            states[i] = SimSyncImpl.initializeState();
        }

        // Perform iterations
        for (int i = 0; i < numIterations; ++i) {
            for (int j = 0; j < NUM_SIMULATIONS; ++j) {
                SimSyncImpl.simulationIter(states[j], processTime);
            }

            // Exchange simulation info
            SimSyncImpl.exchangeInfo(states[0], states[1], processTime);
        }

        // Sync
        System.out.println("END. Printing final states");
        for (int i = 0; i < NUM_SIMULATIONS; ++i) {
            System.out.println("State " + i + ": " + states[i]);
        }
    }

    private static void streamSims(int numIterations, int processTime) throws RegistrationException, BackendException {
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
        State[] finalStates = new State[NUM_SIMULATIONS];
        finalStates[0] = SimSyncImpl.fullSimulation(streams.get(0), streams.get(1), numIterations, processTime);
        finalStates[1] = SimSyncImpl.fullSimulation(streams.get(1), streams.get(0), numIterations, processTime);

        // Synchronize
        System.out.println("[INFO] Waiting for simulations to finish");
        for (int i = 0; i < NUM_SIMULATIONS; ++i) {
            System.out.println("State " + i + ": " + finalStates[i]);
        }
    }

}
