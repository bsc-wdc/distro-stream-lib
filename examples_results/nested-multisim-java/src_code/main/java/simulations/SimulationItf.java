package simulations;

import es.bsc.compss.types.annotations.Constraints;
import es.bsc.compss.types.annotations.Parameter;
import es.bsc.compss.types.annotations.task.Method;


public interface SimulationItf {

    @Constraints(computingUnits = "1")
    @Method(declaringClass = "mains.State")
    void accumulateLocal(
        @Parameter() int sleepBaseTime, 
        @Parameter() int sleepRandomRange
    );

    @Constraints(computingUnits = "1")
    @Method(declaringClass = "mains.State")
    void accumulateForeign(
        @Parameter() int sleepBaseTime, 
        @Parameter() int sleepRandomRange
    );

}