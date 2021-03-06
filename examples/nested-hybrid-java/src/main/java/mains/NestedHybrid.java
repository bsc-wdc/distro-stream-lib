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

import es.bsc.compss.api.COMPSs;
import es.bsc.distrostreamlib.api.objects.ObjectDistroStream;
import es.bsc.distrostreamlib.types.ConsumerMode;

import java.util.LinkedList;


public class NestedHybrid {

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

        // Parse arguments
        System.out.println("[INFO] Parsing application arguments");
        NestedHybridArguments nhargs = new NestedHybridArguments(args);

        // Create streams
        System.out.println("[INFO] Creating streams");
        final ObjectDistroStream<MyElement> odsSensor =
            new ObjectDistroStream<MyElement>("sensor", ConsumerMode.AT_MOST_ONCE);
        final ObjectDistroStream<MyElement> odsFiltered =
            new ObjectDistroStream<MyElement>("filtered", ConsumerMode.AT_MOST_ONCE);

        // Create sensor
        System.out.println("[INFO] Launching sensor");
        NestedHybridTasks.sensor(odsSensor, nhargs.getSensorNumFiles(), nhargs.getSensorSleepBaseTime(),
            nhargs.getSensorSleepRandomRange());

        // Create filters
        System.out.println("[INFO] Launching filter nested");
        NESTED.nestedBigFilter(odsSensor, odsFiltered, nhargs.getBatchSize(), nhargs.getFilterSleepBaseTime(),
            nhargs.getFilterSleepRandomRange());

        // Create extract
        System.out.println("[INFO] Launching extract");
        LinkedList<MyElement> elems = NestedHybridTasks.extractInfo(odsFiltered, nhargs.getExtractSleepBaseTime(),
            nhargs.getExtractSleepRandomRange());

        // Launch task flow computation
        System.out.println("[INFO] Launching task flow computation");
        Integer res = NESTED.nestedTaskFlow(elems, nhargs.getTaskFlowDepth(), nhargs.getTaskFlowSleepBaseTime(),
            nhargs.getTaskFlowSleepRandomRange());

        // Synchronize
        System.out.println("[INFO] Synchronizing final output");
        System.out.println("[INFO] TF ended with status: " + res);

        COMPSs.barrier();

        // End
        System.out.println("DONE");
        long end = System.currentTimeMillis();
        long elapsedTime = end - start;
        System.out.println("[TIME] TOTAL ELAPSED: " + elapsedTime);
    }

}
