package filters;

public class FilterArguments {

    private final String aliasSensor;
    private final String aliasFiltered;
    private final int batchSize;
    private final int sleepBaseTime;
    private final int sleepRandomRange;


    public FilterArguments(String[] args) {
        assert (args.length == 5);

        this.aliasSensor = args[0];
        this.aliasFiltered = args[1];
        this.batchSize = Integer.valueOf(args[2]);
        this.sleepBaseTime = Integer.valueOf(args[3]);
        this.sleepRandomRange = Integer.valueOf(args[4]);
    }

    public String getAliasSensor() {
        return this.aliasSensor;
    }

    public String getAliasFiltered() {
        return this.aliasFiltered;
    }

    public int getBatchSize() {
        return this.batchSize;
    }

    public int getSleepBaseTime() {
        return this.sleepBaseTime;
    }

    public int getSleepRandomRange() {
        return this.sleepRandomRange;
    }
}
