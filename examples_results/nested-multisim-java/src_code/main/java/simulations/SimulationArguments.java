package simulations;

import java.util.Arrays;
import java.util.List;


public class SimulationArguments {

    private final String readStreamAlias;
    private final List<String> writeStreamAliases;

    private final int numIterations;

    private final int sleepBaseTime;
    private final int sleepRandomRange;


    public SimulationArguments(String[] args) {
        assert (args.length == 6);

        this.readStreamAlias = args[0];
        this.writeStreamAliases = Arrays.asList(args[1], args[2]);
        this.numIterations = Integer.valueOf(args[3]);
        this.sleepBaseTime = Integer.valueOf(args[4]);
        this.sleepRandomRange = Integer.valueOf(args[5]);
    }

    public String getReadStreamAlias() {
        return this.readStreamAlias;
    }

    public List<String> getWriteStreamAliases() {
        return this.writeStreamAliases;
    }

    public int getNumIterations() {
        return this.numIterations;
    }

    public int getSleepBaseTime() {
        return this.sleepBaseTime;
    }

    public int getSleepRandomRange() {
        return this.sleepRandomRange;
    }
}
