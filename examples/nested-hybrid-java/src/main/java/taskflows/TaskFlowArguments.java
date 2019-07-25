package taskflows;

import java.util.LinkedList;
import java.util.List;

import mains.MyElement;


public class TaskFlowArguments {

    private final List<MyElement> elements;
    private final int depth;
    private final int sleepBaseTime;
    private final int sleepRandomRange;


    public TaskFlowArguments(String[] args) {
        assert (args.length >= 3);

        this.elements = new LinkedList<>();
        for (int i = 0; i < args.length - 3; ++i) {
            MyElement newElement = buildElement(args[i]);
            this.elements.add(newElement);
        }

        this.depth = Integer.valueOf(args[args.length - 3]);
        this.sleepBaseTime = Integer.valueOf(args[args.length - 2]);
        this.sleepRandomRange = Integer.valueOf(args[args.length - 1]);
    }

    private MyElement buildElement(String elementStr) {
        // Parse input string
        String[] entries = elementStr.split("=");
        String[] inputEntries = entries[1].substring(1, entries[1].length() - 3).split(",");
        List<String> inputFiles = new LinkedList<>();
        for (String in : inputEntries) {
            inputFiles.add(in);
        }
        boolean processed = entries[entries.length - 1].startsWith("true");

        // Build element object and return it
        MyElement element = new MyElement(inputFiles, processed);
        System.out.println("[DEBUG] Loaded: " + element);
        return element;
    }

    public int getNumElements() {
        return this.elements.size();
    }

    public MyElement getElement(int pos) {
        if (pos < this.elements.size()) {
            return this.elements.get(pos);
        }
        return null;
    }

    public int getDepth() {
        return this.depth;
    }

    public int getSleepBaseTime() {
        return this.sleepBaseTime;
    }

    public int getSleepRandomRange() {
        return this.sleepRandomRange;
    }
}
