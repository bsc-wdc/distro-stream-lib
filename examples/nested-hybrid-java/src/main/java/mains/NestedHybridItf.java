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

import es.bsc.compss.types.annotations.Constraints;
import es.bsc.compss.types.annotations.Parameter;
import es.bsc.compss.types.annotations.parameter.Direction;
import es.bsc.compss.types.annotations.parameter.Type;
import es.bsc.compss.types.annotations.task.COMPSs;
import es.bsc.compss.types.annotations.task.Method;
import es.bsc.distrostreamlib.api.objects.ObjectDistroStream;

import java.util.LinkedList;
import java.util.List;


public interface NestedHybridItf {

    @Constraints(computingUnits = "${CORES_SENSOR}")
    @Method(declaringClass = "mains.NestedHybridTasks")
    void sensor(
        @Parameter(type = Type.STREAM, direction = Direction.OUT) ObjectDistroStream<MyElement> odsSensor,
        @Parameter() int numFiles, 
        @Parameter() int sleepBaseTime, 
        @Parameter() int sleepRandomRange
    );
    
    @COMPSs(runcompss = "$RUNCOMPSS", 
            appName = "filters.BigFilter", 
            flags = "-d -g --summary --streaming=OBJECTS --streaming_master_name=localhost" 
            + " --streaming_master_port=49049", 
            computingNodes = "${NESTED_COMPUTING_NODES}")
    @Constraints(computingUnits = "${CORES_BIG_FILTER}")
    void nestedBigFilter(
        @Parameter(type = Type.STREAM, direction = Direction.IN) ObjectDistroStream<MyElement> odsSensor,
        @Parameter(type = Type.STREAM, direction = Direction.OUT) ObjectDistroStream<MyElement> odsFiltered,
        @Parameter() int batchSize, 
        @Parameter() int sleepTime, 
        @Parameter() int sleepRandomRange
    );

    @Constraints(computingUnits = "${CORES_EXTRACT}")
    @Method(declaringClass = "mains.NestedHybridTasks")
    LinkedList<MyElement> extractInfo(
        @Parameter(type = Type.STREAM, direction = Direction.IN) ObjectDistroStream<MyElement> odsFiltered,
        @Parameter() int sleepBaseTime, 
        @Parameter() int sleepRandomRange
    );
    
    @COMPSs(runcompss = "$RUNCOMPSS", 
            appName = "taskflows.SyntheticTaskFlow", 
            flags = "-d -g --summary", 
            computingNodes = "${NESTED_COMPUTING_NODES}")
    @Constraints(computingUnits = "${CORES_TF}")
    Integer nestedTaskFlow(
        @Parameter(type = Type.OBJECT, direction = Direction.IN) List<MyElement> elements, 
        @Parameter() int depth, 
        @Parameter() int sleepBaseTime, 
        @Parameter() int sleepRandomRange
    );

}
