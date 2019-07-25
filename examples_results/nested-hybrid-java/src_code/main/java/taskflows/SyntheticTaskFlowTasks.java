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
