package mains;

import es.bsc.compss.types.annotations.Constraints;
import es.bsc.compss.types.annotations.Parameter;
import es.bsc.compss.types.annotations.parameter.Direction;
import es.bsc.compss.types.annotations.parameter.Type;
import es.bsc.compss.types.annotations.task.COMPSs;
import es.bsc.distrostreamlib.api.objects.ObjectDistroStream;


public interface NestedMultiSimItf {

    @COMPSs(runcompss = "$RUNCOMPSS", 
            appName = "simulations.Simulation", 
            flags = "-d -g -t --summary --streaming=OBJECTS --streaming_master_name=localhost --streaming_master_port=49049",
            computingNodes = "${NESTED_COMPUTING_NODES}")
    @Constraints(computingUnits = "${CORES_SIMULATION}")
    Integer simulation(
        @Parameter(type = Type.STREAM, direction = Direction.IN) ObjectDistroStream<State> odsRead,
        @Parameter(type = Type.STREAM, direction = Direction.OUT) ObjectDistroStream<State> odsWrite1,
        @Parameter(type = Type.STREAM, direction = Direction.OUT) ObjectDistroStream<State> odsWrite2,
        @Parameter() int numIters, 
        @Parameter() int sleepBaseTime, 
        @Parameter() int sleepRandomRange
    );

}