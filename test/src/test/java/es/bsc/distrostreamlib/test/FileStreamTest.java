package es.bsc.distrostreamlib.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import org.junit.Test;

import es.bsc.distrostreamlib.api.files.FileDistroStream;
import es.bsc.distrostreamlib.exceptions.BackendException;
import es.bsc.distrostreamlib.exceptions.DistroStreamClientInitException;
import es.bsc.distrostreamlib.exceptions.RegistrationException;


public class FileStreamTest {

    private static final String TEST_PATH = "/tmp/file_stream/";
    private static final String BASE_FILENAME = "file";
    private static final int NUM_POLLS = 2;
    private static final int NUM_FILES_PER_POLL = 2;


    @Test
    public void test()
            throws DistroStreamClientInitException, RegistrationException, BackendException, FileNotFoundException {

        // Clean and create base test path
        removeDirectory(TEST_PATH);
        createDirectory(TEST_PATH);

        // Start server
        CommonMethods.startServer();

        // Start client
        CommonMethods.startClient();

        // Create stream
        FileDistroStream fds = null;
        try {
            fds = new FileDistroStream(TEST_PATH);
        } catch (RegistrationException re) {
            System.err.println(re.getMessage());
            throw re;
        }

        // Create files and poll several times
        for (int poll = 0; poll < NUM_POLLS; ++poll) {
            // Create some files
            for (int i = NUM_FILES_PER_POLL * poll; i < NUM_FILES_PER_POLL * (poll + 1); ++i) {
                try (PrintWriter writer = new PrintWriter(TEST_PATH + File.separator + BASE_FILENAME + i)) {
                    writer.write("Test " + String.valueOf(i));
                } catch (FileNotFoundException fnfe) {
                    System.err.println(fnfe.getMessage());
                    throw fnfe;
                }
            }

            // Make a poll to see the results
            List<String> newFiles;
            try {
                newFiles = fds.poll();
            } catch (BackendException be) {
                System.err.println(be.getMessage());
                throw be;
            }
            System.out.println(newFiles);
            assertEquals(newFiles.size(), NUM_FILES_PER_POLL);
        }

        // Send client stop
        CommonMethods.stopClient();

        // Send server stop
        CommonMethods.stopServer();
    }

    private static final void removeDirectory(String baseDirPath) {
        File baseDir = new File(baseDirPath);
        File[] files = baseDir.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
            baseDir.delete();
        }
    }

    private static final void createDirectory(String baseDirPath) {
        File baseDir = new File(baseDirPath);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
    }

}