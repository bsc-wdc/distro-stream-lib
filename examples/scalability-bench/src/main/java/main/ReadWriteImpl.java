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
package main;

import es.bsc.distrostreamlib.api.objects.ObjectDistroStream;
import es.bsc.distrostreamlib.exceptions.BackendException;

import java.util.List;

import simsync.types.Element;


public class ReadWriteImpl {

    private static final int TIME_WRITE = 100; // ms
    private static final int TIME_READ = 1_000; // ms
    private static final int TIME_BETWEEN_POLLS = 300; // ms


    /**
     * Writes the given number of elements to the stream.
     * 
     * @param ods Output stream.
     * @param numObjects Number of elements to write.
     * @throws BackendException When a backend error exception occurs.
     * @throws InterruptedException When the current thread is interrupted.
     */
    public static void write(ObjectDistroStream<Element> ods, int numObjects)
        throws BackendException, InterruptedException {

        for (int i = 0; i < numObjects; ++i) {
            // Publish object
            System.out.println("Publish object " + i);
            Element state = new Element();
            ods.publish(state);

            // Sleep to simulate process
            Thread.sleep(TIME_WRITE);
        }

        ods.close();
    }

    /**
     * Reads the available objects in the given stream.
     *
     * @param ods Input stream.
     * @return List of new elements.
     * @throws BackendException When a backend error exception occurs.
     * @throws InterruptedException When the current thread is interrupted.
     */
    public static Integer read(ObjectDistroStream<Element> ods) throws BackendException, InterruptedException {
        int totalElems = 0;
        while (!ods.isClosed()) {
            // Retrieve objects
            totalElems += processObjects(ods);

            // Sleep between polls
            Thread.sleep(TIME_BETWEEN_POLLS);
        }
        totalElems += processObjects(ods);

        return totalElems;
    }

    private static Integer processObjects(ObjectDistroStream<Element> ods)
        throws BackendException, InterruptedException {
        List<Element> newElems = ods.poll();

        // Simulate processing for each element
        for (Element e : newElems) {
            System.out.println("Processing " + e);
            Thread.sleep(TIME_READ);
        }

        return newElems.size();
    }

}
