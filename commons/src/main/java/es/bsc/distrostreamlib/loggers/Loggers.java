package es.bsc.distrostreamlib.loggers;

import es.bsc.distrostreamlib.exceptions.NonInstantiableException;


/**
 * Loggers' names for DistroStreamLib components
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
    public static final String ODS_PUBLISHER = OBJECT_DISTRO_STREAM + ".Publisher";
    public static final String ODS_CONSUMER = OBJECT_DISTRO_STREAM + ".Consumer";


    private Loggers() {
        throw new NonInstantiableException("Loggers");
    }

}
