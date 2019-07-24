package mains;

import compss.NESTED;

import es.bsc.distrostreamlib.api.objects.ObjectDistroStream;
import es.bsc.distrostreamlib.types.ConsumerMode;

import java.util.ArrayList;
import java.util.List;


public class NestedMultiSim {

    private static final int NUM_SIMULATIONS = 3;


    public static void main(String[] args) throws Exception {
        // Start application
        System.out.println("[INFO] Starting application");
        long start = System.currentTimeMillis();

        // Parse arguments
        System.out.println("[INFO] Parsing application arguments");
        NestedMultiSimArguments nmargs = new NestedMultiSimArguments(args);

        // Create streams
        System.out.println("[INFO] Creating streams");
        List<ObjectDistroStream<State>> streams = new ArrayList<>();
        for (int i = 0; i < NUM_SIMULATIONS; ++i) {
            String streamAlias = "Simulation" + i;
            ObjectDistroStream<State> ods = new ObjectDistroStream<State>(streamAlias, ConsumerMode.AT_MOST_ONCE);
            streams.add(ods);
        }

        // Spawn simulations
        System.out.println("[INFO] Launching simulations");
        Integer[] evs = new Integer[NUM_SIMULATIONS];
        evs[0] = NESTED.simulation(streams.get(0), streams.get(1), streams.get(2), nmargs.getNumIterations(),
                nmargs.getSimulationSleepBaseTime(), nmargs.getSimulationSleepRandomRange());
        evs[1] = NESTED.simulation(streams.get(1), streams.get(0), streams.get(2), nmargs.getNumIterations(),
                nmargs.getSimulationSleepBaseTime(), nmargs.getSimulationSleepRandomRange());
        evs[2] = NESTED.simulation(streams.get(2), streams.get(0), streams.get(1), nmargs.getNumIterations(),
                nmargs.getSimulationSleepBaseTime(), nmargs.getSimulationSleepRandomRange());

        // Synchronize
        System.out.println("[INFO] Waiting for simulations to finish");
        for (int i = 0; i < NUM_SIMULATIONS; ++i) {
            if (evs[i] != 0) {
                throw new Exception("ERROR: Nested simulation failed");
            }
        }

        // End
        System.out.println("DONE");
        long end = System.currentTimeMillis();
        long elapsedTime = end - start;
        System.out.println("[TIME] TOTAL ELAPSED: " + elapsedTime);
    }

}
