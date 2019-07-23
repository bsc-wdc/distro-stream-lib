package mains;

public class NestedHybridArguments {

    private final int sensorNumFiles;
    private final int sensorSleepBaseTime;
    private final int sensorSleepRandomRange;

    private final int batchSize;
    private final int filterSleepBaseTime;
    private final int filterSleepRandomRange;

    private final int extractSleepBaseTime;
    private final int extractSleepRandomRange;

    private final int taskFlowDepth;
    private final int taskFlowSleepBaseTime;
    private final int taskFlowSleepRandomRange;


    public NestedHybridArguments(String[] args) {
        assert (args.length == 11);

        this.sensorNumFiles = Integer.parseInt(args[0]);
        this.sensorSleepBaseTime = Integer.parseInt(args[1]);
        this.sensorSleepRandomRange = Integer.parseInt(args[2]);

        this.batchSize = Integer.parseInt(args[3]);
        this.filterSleepBaseTime = Integer.parseInt(args[4]);
        this.filterSleepRandomRange = Integer.parseInt(args[5]);

        this.extractSleepBaseTime = Integer.parseInt(args[6]);
        this.extractSleepRandomRange = Integer.parseInt(args[7]);

        this.taskFlowDepth = Integer.parseInt(args[8]);
        this.taskFlowSleepBaseTime = Integer.parseInt(args[9]);
        this.taskFlowSleepRandomRange = Integer.parseInt(args[10]);
    }

    public int getSensorNumFiles() {
        return this.sensorNumFiles;
    }

    public int getSensorSleepBaseTime() {
        return this.sensorSleepBaseTime;
    }

    public int getSensorSleepRandomRange() {
        return this.sensorSleepRandomRange;
    }

    public int getBatchSize() {
        return this.batchSize;
    }

    public int getFilterSleepBaseTime() {
        return this.filterSleepBaseTime;
    }

    public int getFilterSleepRandomRange() {
        return this.filterSleepRandomRange;
    }

    public int getExtractSleepBaseTime() {
        return this.extractSleepBaseTime;
    }

    public int getExtractSleepRandomRange() {
        return this.extractSleepRandomRange;
    }

    public int getTaskFlowDepth() {
        return this.taskFlowDepth;
    }

    public int getTaskFlowSleepBaseTime() {
        return this.taskFlowSleepBaseTime;
    }

    public int getTaskFlowSleepRandomRange() {
        return this.taskFlowSleepRandomRange;
    }
}
