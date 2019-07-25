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
            flags = "-d -g --summary --streaming=OBJECTS --streaming_master_name=localhost --streaming_master_port=49049", 
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