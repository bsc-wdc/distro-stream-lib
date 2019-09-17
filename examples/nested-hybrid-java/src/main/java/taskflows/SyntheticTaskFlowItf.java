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
package taskflows;

import es.bsc.compss.types.annotations.Constraints;
import es.bsc.compss.types.annotations.Parameter;
import es.bsc.compss.types.annotations.parameter.Direction;
import es.bsc.compss.types.annotations.parameter.Type;
import es.bsc.compss.types.annotations.task.Method;
import mains.MyElement;


public interface SyntheticTaskFlowItf {

    @Constraints(computingUnits = "1")
    @Method(declaringClass = "taskflows.SyntheticTaskFlowTasks")
    MyElement mapTask(
        @Parameter(type = Type.OBJECT, direction = Direction.INOUT) MyElement input,
        @Parameter() int sleepBaseTime, 
        @Parameter() int sleepRandomRange
    );

    @Constraints(computingUnits = "1")
    @Method(declaringClass = "taskflows.SyntheticTaskFlowTasks")
    void mergeTask(
        @Parameter(type = Type.OBJECT, direction = Direction.INOUT) MyElement elem1,
        @Parameter(type = Type.OBJECT, direction = Direction.IN) MyElement elem2, 
        @Parameter() int sleepBaseTime,
        @Parameter() int sleepRandomRange
    );

}
