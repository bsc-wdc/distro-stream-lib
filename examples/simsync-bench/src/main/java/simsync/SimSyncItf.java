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
package simsync;

import es.bsc.compss.types.annotations.Parameter;
import es.bsc.compss.types.annotations.parameter.Direction;
import es.bsc.compss.types.annotations.parameter.Type;
import es.bsc.compss.types.annotations.task.Method;
import es.bsc.distrostreamlib.api.objects.ObjectDistroStream;

import simsync.types.State;


public interface SimSyncItf {

    @Method(declaringClass = "simsync.SimSyncImpl")
    State initializeState(
    );

    @Method(declaringClass = "simsync.SimSyncImpl")
    void simulationIter(
        @Parameter(type = Type.OBJECT, direction = Direction.INOUT) State myState,
        @Parameter() int processTime
    );
    
    @Method(declaringClass = "simsync.SimSyncImpl")
    void exchangeInfo(
        @Parameter(type = Type.OBJECT, direction = Direction.INOUT) State myState,
        @Parameter(type = Type.OBJECT, direction = Direction.INOUT) State otherState,
        @Parameter() int processTime
    );

    @Method(declaringClass = "simsync.SimSyncImpl")
    State fullSimulation(
        @Parameter(type = Type.STREAM, direction = Direction.IN) ObjectDistroStream<State> myStates,
        @Parameter(type = Type.STREAM, direction = Direction.OUT) ObjectDistroStream<State> theirStates,
        @Parameter() int numIterations,
        @Parameter() int processTime
    );

}
