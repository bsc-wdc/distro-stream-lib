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
package timers;

import es.bsc.compss.api.COMPSs;
import es.bsc.distrostreamlib.api.objects.ObjectDistroStream;
import es.bsc.distrostreamlib.types.ConsumerMode;

import java.util.LinkedList;
import java.util.List;

import timers.types.MyObject;


public class RuntimeTimers {

    private static final int NUM_TASKS = 101;


    /**
     * Main function.
     * 
     * @param args Command line arguments.
     * @throws Exception When the example raises any exception.
     */
    public static void main(String[] args) throws Exception {
        // Start application
        System.out.println("[INFO] Starting application");
        final long start = System.currentTimeMillis();

        // Get args
        int numObjects = Integer.valueOf(args[0]);
        int objectSize = Integer.valueOf(args[1]);
        System.out.println("[INFO] NUM OBJECTS: " + numObjects);
        System.out.println("[INFO] OBJECTS SIZE: " + numObjects);

        // Create N objects
        List<MyObject[]> objects = new LinkedList<>();
        for (int i = 0; i < NUM_TASKS; ++i) {
            MyObject[] taskObjects = new MyObject[numObjects];
            for (int j = 0; j < numObjects; ++j) {
                taskObjects[j] = new MyObject(objectSize);
            }
            objects.add(taskObjects);
        }
        Thread.sleep(5_000);
        // Launch N tasks without streams
        for (int i = 0; i < NUM_TASKS; ++i) {
            // Submit task
            RuntimeTimersImpl.normalTask(objects.get(i));

            // Barrier
            COMPSs.barrier();
        }
        final long endNormal = System.currentTimeMillis();

        // Create N streams
        List<ObjectDistroStream<MyObject>> streams = new LinkedList<>();
        for (int i = 0; i < NUM_TASKS; ++i) {
            // Create stream
            ObjectDistroStream<MyObject> ods = new ObjectDistroStream<>(ConsumerMode.AT_MOST_ONCE);
            streams.add(ods);
            // Add object to stream and close it
            for (int j = 0; j < numObjects; ++j) {
                ods.publish(objects.get(i)[j]);
            }
            ods.close();
        }
        Thread.sleep(5_000);
        // Create N stream tasks
        for (int i = 0; i < NUM_TASKS; ++i) {
            // Submit task
            RuntimeTimersImpl.streamTask(streams.get(i));

            // Barrier
            COMPSs.barrier();
        }
        final long end = System.currentTimeMillis();

        // End
        System.out.println("DONE");
        final long elapsedNormal = endNormal - start;
        final long elapsedStreams = end - endNormal;
        final long elapsedTotal = end - start;
        System.out.println("[TIME] NORMAL ELAPSED: " + elapsedNormal + " ms");
        System.out.println("[TIME] STREAMS ELAPSED: " + elapsedStreams + " ms");
        System.out.println("[TIME] TOTAL ELAPSED: " + elapsedTotal + " ms");
    }

}
