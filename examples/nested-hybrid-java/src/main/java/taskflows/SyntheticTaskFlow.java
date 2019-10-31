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
package taskflows;

import java.util.LinkedList;
import java.util.Queue;

import mains.MyElement;


public class SyntheticTaskFlow {

    private static MyElement taskFlowComputation(TaskFlowArguments tfargs) {
        // Launch map tasks (only register as output the last one)
        int numElements = tfargs.getNumElements();
        MyElement[] outputs = new MyElement[numElements];
        for (int i = 0; i < numElements; ++i) {
            MyElement input = tfargs.getElement(i);
            for (int j = 0; j < tfargs.getDepth(); ++j) {
                outputs[i] =
                    SyntheticTaskFlowTasks.mapTask(input, tfargs.getSleepBaseTime(), tfargs.getSleepRandomRange());
            }
        }

        // Launch merge phase
        Queue<Integer> pendingIndexes = new LinkedList<>();
        for (int i = 0; i < numElements; ++i) {
            pendingIndexes.add(i);
        }

        while (pendingIndexes.size() >= 2) {
            int firstIndex = pendingIndexes.poll();
            int secondIndex = pendingIndexes.poll();
            SyntheticTaskFlowTasks.mergeTask(outputs[firstIndex], outputs[secondIndex], tfargs.getSleepBaseTime(),
                tfargs.getSleepRandomRange());
            pendingIndexes.add(firstIndex);
        }

        return outputs[pendingIndexes.poll()];
    }

    /**
     * Main function.
     * 
     * @param args Command line arguments.
     * @throws Exception When the example raises any exception.
     */
    public static void main(String[] args) {
        // Start application
        System.out.println("[INFO] Starting application");
        long start = System.currentTimeMillis();

        // Parse arguments
        System.out.println("[INFO] Parsing application arguments");
        TaskFlowArguments tfargs = new TaskFlowArguments(args);

        // Launch task flow computation
        System.out.println("[INFO] Launching task flow computation");
        MyElement res = taskFlowComputation(tfargs);

        // Synchronize
        System.out.println("[INFO] Syncrhonizing final output");
        System.out.println("Computation result is: " + res);

        // End
        System.out.println("DONE");
        long end = System.currentTimeMillis();
        long elapsedTime = end - start;
        System.out.println("[TIME] TOTAL ELAPSED: " + elapsedTime);
    }

}
