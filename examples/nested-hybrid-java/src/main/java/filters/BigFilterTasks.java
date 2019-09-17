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

import java.util.List;
import java.util.Random;

import es.bsc.distrostreamlib.api.objects.ObjectDistroStream;
import es.bsc.distrostreamlib.exceptions.BackendException;
import mains.MyElement;


public class BigFilterTasks {

    public static void filterObjects(List<MyElement> pendingObjects, ObjectDistroStream<MyElement> odsFiltered,
            int sleepBaseTime, int sleepRandomRange) throws BackendException {

        for (MyElement e : pendingObjects) {
            // Print input object content
            System.out.println("[DEBUG] Processing object " + e);

            // Sleep to simulate time spent to process the object
            try {
                Random randomGenerator = new Random();
                int randomInt = randomGenerator.nextInt(sleepRandomRange);
                int sleepTime = sleepBaseTime + randomInt;
                Thread.sleep(sleepTime);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }

            // Generate filtered object
            System.out.println("Generating filtered object");
            MyElement out = new MyElement(e.getInputFiles());
            odsFiltered.publish(out);
        }

        // Close filtered stream
        odsFiltered.close();
    }

}
