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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import es.bsc.distrostreamlib.api.objects.ObjectDistroStream;
import es.bsc.distrostreamlib.exceptions.BackendException;
import mains.MyElement;


public class NestedHybridTasks {

    private static final int SENSOR_INITIAL_DELAY = 10_000; // ms
    private static final int TIME_BETWEEN_POLLS = 5_000; // ms
    private static final int EXTRACT_ACCEPT_RATE = 5;


    public static void sensor(ObjectDistroStream<MyElement> odsSensor, int numFiles, int sleepBaseTime,
            int sleepRandomRange) throws BackendException, InterruptedException {
        System.out.println("[DEBUG] Starting sensor");

        // Initial sensor delay
        Thread.sleep(SENSOR_INITIAL_DELAY);

        for (int i = 0; i < numFiles; ++i) {
            // Generate object
            List<String> inputFiles = Arrays.asList("object" + i);
            MyElement e = new MyElement(inputFiles);

            // Publish object
            System.out.println("[DEBUG] Publishing object " + e);
            odsSensor.publish(e);

            // Sleep between generation to simulate time spent to generate the next object
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(sleepRandomRange);
            int sleepTime = sleepBaseTime + randomInt;
            Thread.sleep(sleepTime);
        }

        // Close stream
        System.out.println("[DEBUG] Notifying stream closure");
        odsSensor.close();

        System.out.println("[DEBUG] Stopping sensor");
    }

    public static LinkedList<MyElement> extractInfo(ObjectDistroStream<MyElement> odsFiltered, int sleepBaseTime,
            int sleepRandomRange) throws InterruptedException, BackendException {

        System.out.println("[DEBUG] Starting extractInfo");

        LinkedList<MyElement> elements = new LinkedList<>();
        while (!odsFiltered.isClosed()) {
            // Sleep between polls
            Thread.sleep(TIME_BETWEEN_POLLS);

            // Process filtered files
            List<MyElement> newElements = getAndExtract(odsFiltered, sleepBaseTime, sleepRandomRange);
            elements.addAll(newElements);
        }

        // Process filtered files one last time
        List<MyElement> newElements = getAndExtract(odsFiltered, sleepBaseTime, sleepRandomRange);
        elements.addAll(newElements);

        System.out.println("[DEBUG] Detected valid elements: " + elements);
        return elements;
    }

    private static List<MyElement> getAndExtract(ObjectDistroStream<MyElement> odsFiltered, int sleepBaseTime,
            int sleepRandomRange) throws InterruptedException, BackendException {

        System.out.println("[DEBUG] Polling new files");
        List<MyElement> newElements = odsFiltered.poll();

        List<MyElement> validElements = new LinkedList<>();
        for (MyElement elem : newElements) {
            System.out.println("[DEBUG] Processing object " + elem);

            // Filter 1 every NUM_EXTRACT_ELEMENTS
            Random randomGenerator = new Random();
            int randomFilterExtract = randomGenerator.nextInt(10);
            if (randomFilterExtract >= EXTRACT_ACCEPT_RATE) {
                // Sleep to simulate time spent to process the file
                int randomInt = randomGenerator.nextInt(sleepRandomRange);
                int sleepTime = sleepBaseTime + randomInt;
                Thread.sleep(sleepTime);

                // Generate a valid element
                validElements.add(elem);
            }
        }
        return validElements;
    }

}
