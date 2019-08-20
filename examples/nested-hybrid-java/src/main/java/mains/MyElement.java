/*
 *  Copyright 2002-2019 Barcelona Supercomputing Center (www.bsc.es)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
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
