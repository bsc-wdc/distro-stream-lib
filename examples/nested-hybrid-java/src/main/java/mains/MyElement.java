package mains;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


public class MyElement implements Serializable {

    private static final long serialVersionUID = 4L;

    private final List<String> inputFiles;
    private boolean processed;


    public MyElement() {
        // Only for externalization
        this.inputFiles = new LinkedList<>();
    }

    public MyElement(List<String> inputFiles) {
        this.inputFiles = inputFiles;
        this.processed = false;
    }

    public MyElement(List<String> inputFiles, boolean processed) {
        this.inputFiles = inputFiles;
        this.processed = processed;
    }

    public List<String> getInputFiles() {
        return this.inputFiles;
    }

    public boolean isProcessed() {
        return this.processed;
    }

    public void addInputFiles(List<String> inputFiles) {
        this.inputFiles.addAll(inputFiles);
    }

    public void setProcessed() {
        this.processed = true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MyElement[");
        sb.append("IF=[");
        for (String input : this.inputFiles) {
            sb.append(input).append(",");
        }
        sb.append("]");
        sb.append(",");
        sb.append("P=").append(this.processed);
        sb.append("]");

        return sb.toString();
    }

}
