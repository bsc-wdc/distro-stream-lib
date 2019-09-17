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

import java.util.List;

import es.bsc.distrostreamlib.api.objects.ObjectDistroStream;
import es.bsc.distrostreamlib.exceptions.BackendException;
import simsync.types.State;


public class SimSyncImpl {

    private static final boolean DEBUG = false;


    public static final State initializeState() {
        State currentState = new State();
        return currentState;
    }

    public static final void simulationIter(State myState, int processTime) {
        // Sleep to fake simulation
        if (DEBUG) {
            System.out.println("[DEBUG] Performing local simulation");
        }
        myState.accumulateLocal(processTime, 1);
    }

    public static final void exchangeInfo(State myState, State otherState, int processTime) {
        myState.accumulateForeign(processTime, 1);
        otherState.accumulateForeign(processTime, 1);
    }

    public static final State fullSimulation(ObjectDistroStream<State> myStates, ObjectDistroStream<State> theirStates,
        int numIterations, int processTime) throws BackendException {

        // Perform simulation iterations
        if (DEBUG) {
            System.out.println("[INFO] Performing simulation iterations");
        }
        State currentState = new State();
        for (int i = 0; i < numIterations; ++i) {
            if (DEBUG) {
                System.out.println("[DEBUG] Begin iteration " + i);
            }

            // Sleep to fake simulation
            if (DEBUG) {
                System.out.println("[DEBUG] Performing local simulation");
            }
            currentState.accumulateLocal(processTime, 1);

            // Communicate TO other simulations
            if (DEBUG) {
                System.out.println("[DEBUG] Communicating TO other simulations");
            }
            theirStates.publish(currentState);

            // Update FROM other simulations
            if (DEBUG) {
                System.out.println("[DEBUG] Communicating FROM other simulations");
            }
            List<State> incommingElements = null;
            while (incommingElements == null || incommingElements.isEmpty()) {
                incommingElements = myStates.poll();
            }
            for (State elem : incommingElements) {
                if (DEBUG) {
                    System.out.println("[DEBUG] Received element: " + elem);
                }
                currentState.accumulateForeign(processTime, 1);
            }
        }

        // Close stream
        myStates.close();

        if (DEBUG) {
            System.out.println("[INFO] Simulation finished");
        }
        return currentState;
    }

}
