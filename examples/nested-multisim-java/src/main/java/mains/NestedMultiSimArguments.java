package mains;

public class NestedMultiSimArguments {

    private final int numIterations;

    private final int simulationSleepBaseTime;
    private final int simulationSleepRandomRange;


    public NestedMultiSimArguments(String[] args) {
        assert (args.length == 3);

        this.numIterations = Integer.valueOf(args[0]);
        this.simulationSleepBaseTime = Integer.valueOf(args[1]);
        this.simulationSleepRandomRange = Integer.valueOf(args[2]);
    }

    public int getNumIterations() {
        return this.numIterations;
    }

    public int getSimulationSleepBaseTime() {
        return this.simulationSleepBaseTime;
    }

    public int getSimulationSleepRandomRange() {
        return this.simulationSleepRandomRange;
    }
}
