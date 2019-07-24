package mains;

import java.io.Serializable;
import java.util.Random;


public class State implements Serializable {

    private static final long serialVersionUID = 4L;

    private int localValue;
    private int foreignValue;


    public State() {
        this.localValue = 0;
        this.foreignValue = 0;
    }

    public int getLocalValue() {
        return this.localValue;
    }

    public int getForeignValue() {
        return this.foreignValue;
    }

    public void accumulateLocal(int sleepBaseTime, int sleepRandomRange) {
        // Accumulate
        this.localValue++;

        // Sleep to simulate time spent to process the object
        try {
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(sleepRandomRange);
            int sleepTime = sleepBaseTime + randomInt;
            Thread.sleep(sleepTime);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    public void accumulateForeign(int sleepBaseTime, int sleepRandomRange) {
        // Accumulate
        this.foreignValue++;

        // Sleep to simulate time spent to process the object
        try {
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(sleepRandomRange);
            int sleepTime = sleepBaseTime + randomInt;
            Thread.sleep(sleepTime);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("State[");
        sb.append("Local=").append(this.localValue);
        sb.append(",");
        sb.append("Foreign=").append(this.foreignValue);
        sb.append("]");

        return sb.toString();
    }

}
