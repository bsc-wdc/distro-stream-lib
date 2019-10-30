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
package es.bsc.distrostreamlib.loggers;

import es.bsc.distrostreamlib.exceptions.NonInstantiableException;


/**
 * Loggers' names for DistroStreamLib components.
 */
public class Loggers {

    // Root logger
    public static final String DISTRO_STREAM_LIB = "es.bsc.distroStreamLib";

    // Component loggers
    public static final String DSL_SERVER = DISTRO_STREAM_LIB + ".server";
    public static final String DSL_CLIENT = DISTRO_STREAM_LIB + ".client";
    public static final String DSL_STREAM = DISTRO_STREAM_LIB + ".stream";

    // Server components

    // Client components

    // Stream components
    public static final String DISTRO_STREAM = DSL_STREAM + ".DistroStream";
    public static final String FILE_DISTRO_STREAM = DSL_STREAM + ".FileDistroStream";
    public static final String OBJECT_DISTRO_STREAM = DSL_STREAM + ".ObjectDistroStream";
    public static final String PSCO_DISTRO_STREAM = DSL_STREAM + ".PscoDistroStream";
    public static final String ODS_PUBLISHER = OBJECT_DISTRO_STREAM + ".Publisher";
    public static final String ODS_CONSUMER = OBJECT_DISTRO_STREAM + ".Consumer";


    private Loggers() {
        throw new NonInstantiableException("Loggers");
    }

}
