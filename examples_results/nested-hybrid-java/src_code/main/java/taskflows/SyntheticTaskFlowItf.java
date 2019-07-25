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