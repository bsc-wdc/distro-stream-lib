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

import es.bsc.compss.api.COMPSs;
import es.bsc.distrostreamlib.api.objects.ObjectDistroStream;
import es.bsc.distrostreamlib.exceptions.BackendException;
import es.bsc.distrostreamlib.exceptions.RegistrationException;
import es.bsc.distrostreamlib.types.ConsumerMode;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import mains.MyElement;


public class BigFilter {

    private static final int TIME_BETWEEN_POLLS = 5_000; // ms


    private static void processObjects(ObjectDistroStream<MyElement> odsSensor,
        ObjectDistroStream<MyElement> odsFiltered, Queue<MyElement> pendingObjects, FilterArguments fargs,
        boolean forced) throws BackendException {

        // Poll objects
        List<MyElement> newObjects = odsSensor.poll();
        pendingObjects.addAll(newObjects);

        // Send batch to execution if necessary
        while (pendingObjects.size() > fargs.getBatchSize()) {
            // Get batch and update pending objects
            List<MyElement> batchObjects = new LinkedList<>();
            for (int i = 0; i < fargs.getBatchSize(); ++i) {
                MyElement next = pendingObjects.poll();
                batchObjects.add(next);
            }
            // Launch task
            System.out.println("[DEBUG] Launch filter task with " + batchObjects.size() + " elements");
            BigFilterTasks.filterObjects(batchObjects, odsFiltered, fargs.getSleepBaseTime(),
                fargs.getSleepRandomRange());
        }

        if (forced) {
            // Spawn filter task even if we don't have a full batch
            List<MyElement> batchObjects = (LinkedList<MyElement>) pendingObjects;
            System.out.println("[DEBUG] Launch filter task with " + batchObjects.size() + " elements");
            BigFilterTasks.filterObjects(batchObjects, odsFiltered, fargs.getSleepBaseTime(),
                fargs.getSleepRandomRange());
            pendingObjects.clear();
        }
    }

    /**
     * Main application code.
     * 
     * @param args Application arguments.
     * @throws RegistrationException When a registration exception occurs.
     * @throws BackendException When a backend exception occurs.
     * @throws InterruptedException When the thread is interrupted.
     */
    public static void main(String[] args) throws RegistrationException, BackendException, InterruptedException {
        // Start application
        System.out.println("[INFO] Starting application");
        final long start = System.currentTimeMillis();

        // Parse arguments
        System.out.println("[INFO] Parsing application arguments");
        FilterArguments fargs = new FilterArguments(args);

        // Initialize streams
        System.out.println("[INFO] Initializing streams");
        ObjectDistroStream<MyElement> odsSensor =
            new ObjectDistroStream<MyElement>(fargs.getAliasSensor(), ConsumerMode.AT_MOST_ONCE);
        ObjectDistroStream<MyElement> odsFiltered =
            new ObjectDistroStream<MyElement>(fargs.getAliasFiltered(), ConsumerMode.AT_MOST_ONCE);

        // Process input stream elements
        System.out.println("[INFO] Processing input stream elements");
        Queue<MyElement> pendingObjects = new LinkedList<>();
        while (!odsSensor.isClosed()) {
            // Poll files and send batch to execution if necessary
            processObjects(odsSensor, odsFiltered, pendingObjects, fargs, false);

            // Sleep between polls
            Thread.sleep(TIME_BETWEEN_POLLS);
        }

        // Poll one last time (and force the task to execute)
        processObjects(odsSensor, odsFiltered, pendingObjects, fargs, true);

        // Synchronize
        System.out.println("[INFO] Waiting for all batch tasks to finish");
        COMPSs.barrier();

        // Close stream (because this app is itself a task)
        odsFiltered.close();

        // End
        System.out.println("DONE");
        long end = System.currentTimeMillis();
        long elapsedTime = end - start;
        System.out.println("[TIME] TOTAL ELAPSED: " + elapsedTime);
    }
}
