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
