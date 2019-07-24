package simulations;

import es.bsc.compss.api.COMPSs;
import es.bsc.distrostreamlib.api.objects.ObjectDistroStream;
import es.bsc.distrostreamlib.exceptions.BackendException;
import es.bsc.distrostreamlib.exceptions.RegistrationException;
import es.bsc.distrostreamlib.types.ConsumerMode;

import java.util.ArrayList;
import java.util.List;

import mains.State;


public class Simulation {

    private static State simulate(SimulationArguments sArgs) throws RegistrationException, BackendException {
        // Create simulation read stream
        System.out.println("[INFO] Creating read stream");
        String readStreamAlias = sArgs.getReadStreamAlias();
        ObjectDistroStream<State> readStream = new ObjectDistroStream<State>(readStreamAlias,
                ConsumerMode.AT_MOST_ONCE);

        // Create simulation write streams
        System.out.println("[INFO] Creating write streams");
        List<ObjectDistroStream<State>> writeStreams = new ArrayList<>();
        for (String streamAlias : sArgs.getWriteStreamAliases()) {
            ObjectDistroStream<State> stream = new ObjectDistroStream<State>(streamAlias, ConsumerMode.AT_MOST_ONCE);
            writeStreams.add(stream);
        }

        // Perform simulation iterations
        System.out.println("[INFO] Performing simulation iterations");
        State currentState = new State();
        for (int i = 0; i < sArgs.getNumIterations(); ++i) {
            System.out.println("[DEBUG] Begin iteration " + i);

            // Sleep to fake simulation
            System.out.println("[DEBUG] Performing local simulation");
            currentState.accumulateLocal(sArgs.getSleepBaseTime(), sArgs.getSleepRandomRange());
            COMPSs.barrier();

            // Communicate TO other simulations
            System.out.println("[DEBUG] Communicating TO other simulations");
            for (ObjectDistroStream<State> wStream : writeStreams) {
                if (!wStream.isClosed()) {
                    wStream.publish(currentState);
                }
            }

            // Update FROM other simulations
            System.out.println("[DEBUG] Communicating FROM other simulations");
            List<State> incommingElements = readStream.poll();
            if (incommingElements != null) {
                for (State elem : incommingElements) {
                    System.out.println("[DEBUG] Received element: " + elem);
                    currentState.accumulateForeign(sArgs.getSleepBaseTime(), sArgs.getSleepRandomRange());
                }
            }
        }

        System.out.println("[INFO] Simulation finished");
        return currentState;
    }

    public static void main(String[] args) throws RegistrationException, BackendException {
        // Start application
        System.out.println("[INFO] Starting application");
        long start = System.currentTimeMillis();

        // Parse arguments
        System.out.println("[INFO] Parsing application arguments");
        SimulationArguments sArgs = new SimulationArguments(args);

        // Launch task flow computation
        System.out.println("[INFO] Launching simulation");
        State res = simulate(sArgs);

        // Synchronize
        System.out.println("[INFO] Waiting for simulation to end");
        System.out.println("Computation result is: " + res);

        // End
        System.out.println("DONE");
        long end = System.currentTimeMillis();
        long elapsedTime = end - start;
        System.out.println("[TIME] TOTAL ELAPSED: " + elapsedTime);
    }

}
