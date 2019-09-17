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

import java.util.Random;

import mains.MyElement;


public class SyntheticTaskFlowTasks {

    public static MyElement mapTask(MyElement input, int sleepBaseTime, int sleepRandomRange) {
        // Sleep to simulate computation
        try {
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(sleepRandomRange);
            int sleepTime = sleepBaseTime + randomInt;
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Create output object
        MyElement output = new MyElement(input.getInputFiles());
        output.setProcessed();
        return output;
    }

    public static void mergeTask(MyElement elem1, MyElement elem2, int sleepBaseTime, int sleepRandomRange) {
        // Sleep to simulate computation
        try {
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(sleepRandomRange);
            int sleepTime = sleepBaseTime + randomInt;
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Update element 1
        elem1.addInputFiles(elem2.getInputFiles());
        elem1.setProcessed();
    }

}
