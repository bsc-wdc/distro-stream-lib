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
package compss;

import java.util.List;

import es.bsc.distrostreamlib.api.objects.ObjectDistroStream;
import mains.MyElement;


public class NESTED {

    public static void nestedBigFilter(ObjectDistroStream<MyElement> odsSensor,
            ObjectDistroStream<MyElement> odsFiltered, int batchSize, int sleepTime, int sleepRandomRange) {

    }

    public static Integer nestedTaskFlow(List<MyElement> elements, int depth, int sleepBaseTime, int sleepRandomRange) {
        return -1;
    }

}
