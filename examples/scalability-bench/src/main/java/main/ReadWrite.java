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
package main;

import es.bsc.compss.api.COMPSs;
import es.bsc.distrostreamlib.api.objects.ObjectDistroStream;
import es.bsc.distrostreamlib.types.ConsumerMode;

import simsync.types.Element;


public class ReadWrite {

    private static final int NUM_TOTAL_ELEMENTS = 100;


    public static void main(String[] args) throws Exception {
        // Start application
        System.out.println("[INFO] Starting application");
        final long start = System.currentTimeMillis();

        // Get arguments
        final int numWriters = Integer.parseInt(args[0]);
        final int numReaders = Integer.parseInt(args[1]);

        System.out.println("[INFO] WRITERS: " + numWriters);
        System.out.println("[INFO] READERS: " + numReaders);

        // Create stream
        ObjectDistroStream<Element> ods = new ObjectDistroStream<>(ConsumerMode.AT_MOST_ONCE);

        // Launch writers
        final int elemsPerWriter = NUM_TOTAL_ELEMENTS / numWriters;
        for (int i = 0; i < numWriters; ++i) {
            ReadWriteImpl.write(ods, elemsPerWriter);
        }

        // Launch readers
        Integer[] processedElemsPerReader = new Integer[numReaders];
        for (int i = 0; i < numReaders; ++i) {
            processedElemsPerReader[i] = ReadWriteImpl.read(ods);
        }

        // Sync
        int total = 0;
        for (int i = 0; i < numReaders; ++i) {
            System.out.println("Reader " + i + " processed " + processedElemsPerReader[i] + " elements");
            total = total + processedElemsPerReader[i];
        }
        System.out.println("[INFO] TOTAL ELEMENTS: " + total);

        // End
        COMPSs.barrier();
        System.out.println("[INFO] DONE");
        final long end = System.currentTimeMillis();
        final long elapsedTime = end - start;
        System.out.println("[TIME] TOTAL ELAPSED: " + elapsedTime + " ms");
    }

}
