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
package timers;

import es.bsc.distrostreamlib.api.objects.ObjectDistroStream;
import es.bsc.distrostreamlib.exceptions.BackendException;

import java.util.List;

import timers.types.MyObject;


public class RuntimeTimersImpl {

    public static final void normalTask(MyObject[] objs) {
        System.out.println("Size: " + objs.length);
    }

    public static final void streamTask(ObjectDistroStream<MyObject> ods) throws BackendException {
        List<MyObject> objs = ods.poll();
        System.out.println("Size: " + objs.size());
    }

}
