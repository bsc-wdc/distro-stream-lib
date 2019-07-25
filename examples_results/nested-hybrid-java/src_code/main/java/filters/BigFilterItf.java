package filters;

import java.util.List;

import es.bsc.compss.types.annotations.Constraints;
import es.bsc.compss.types.annotations.Parameter;
import es.bsc.compss.types.annotations.parameter.Direction;
import es.bsc.compss.types.annotations.parameter.Type;
import es.bsc.compss.types.annotations.task.Method;
import es.bsc.distrostreamlib.api.objects.ObjectDistroStream;
import mains.MyElement;


public interface BigFilterItf {

    @Constraints(computingUnits = "1")
    @Method(declaringClass = "filters.BigFilterTasks")
    void filterObjects(
        @Parameter(type = Type.OBJECT, direction = Direction.IN) List<MyElement> pendingObjects,
        @Parameter(type = Type.STREAM, direction = Direction.OUT) ObjectDistroStream<MyElement> odsFiltered,
        @Parameter() int sleepBaseTime, 
        @Parameter() int sleepRandomRange
    );

}