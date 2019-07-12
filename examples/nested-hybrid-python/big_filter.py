#!/usr/bin/python

# -*- coding: utf-8 -*-

# For better print formatting
from __future__ import print_function

# PyCOMPSs imports
from pycompss.api.task import task
from pycompss.api.constraint import constraint
from pycompss.api.parameter import STREAM_OUT
from pycompss.api.api import compss_barrier

from pycompss.streams.distro_stream import FileDistroStream

# Project imports
from datetime import datetime
import time
import random
import os

#
# CONSTANTS DEFINITION
#

CORES_FILTER = os.getenv("CORES_FILTER", 1)
TIME_BETWEEN_POLLS = 5  # seconds


#
# TASKS DEFINITION
#

@constraint(computing_units=CORES_FILTER)
@task(output_fds=STREAM_OUT)
def filter_files(files_to_process, output_fds, base_sleep_time, sleep_random_range):
    if __debug__:
        print("[DEBUG] Polling new files")

    output_base_path = output_fds.base_dir

    for sensor_file in files_to_process:
        if __debug__:
            print("[DEBUG] Processing file " + sensor_file)

        # Print input file content
        with open(sensor_file, "r") as f:
            print(f.readlines())

        # Sleep to simulate time spent to process the file
        sleep_time = base_sleep_time + random.randint(0, sleep_random_range)
        sleep_time = sleep_time / 1000  # from ms to s
        time.sleep(sleep_time)

        # Generate filtered file
        basename = os.path.basename(sensor_file)
        output_file = output_base_path + "/filtered_file_" + basename
        if __debug__:
            print("[DEBUG] Generating filtered file " + output_file)
        current_time = datetime.now()
        with open(output_file, "w") as f:
            f.write("Output image generated at " + str(current_time) + "\n")
            f.write("Output image generated from " + str(sensor_file) + "\n")
            f.flush()

    # Close stream
    if __debug__:
        print("[DEBUG] Closing stream")
    output_fds.close()


#
# HELPER METHODS
#

class Arguments(object):
    """
    Store command line arguments in a single object.
    """

    def __init__(self, sensor_stream_dir, filtered_stream_dir, batch_size, filter_base_sleep_time,
                 filter_sleep_random_range):
        """
        Initializes the command line arguments instance with the given values.
        """
        self.sensor_stream_dir = sensor_stream_dir
        self.filtered_stream_dir = filtered_stream_dir
        self.batch_size = int(batch_size)
        self.filter_base_sleep_time = int(filter_base_sleep_time)
        self.filter_sleep_random_range = int(filter_sleep_random_range)


def parse_arguments():
    """
    Parses the command line arguments.

    :return: The parsed command line arguments stored in a single object.
        + type: Arguments
    """
    import sys

    if len(sys.argv) != 6:
        print("ERROR: Not enough input arguments.")
        raise Exception("Invalid arguments")

    a = Arguments(sys.argv[1],  # sensor_stream_base_dir
                  sys.argv[2],  # filtered_stream_base_dir
                  sys.argv[3],  # Batch size
                  sys.argv[4],  # filter_base_sleep_time
                  sys.argv[5],  # filter_sleep_random_range
                  )
    return a


def process_files(input_fds, output_fds, files_to_process, app_args, forced):
    # Poll files
    new_files = input_fds.poll()
    files_to_process.extend(new_files)

    # Send batch to execution if necessary
    while len(files_to_process) > app_args.batch_size:
        files_batch = files_to_process[:app_args.batch_size]
        if __debug__:
            print("[DEBUG] Launch filter task")
        filter_files(files_batch,
                     output_fds,
                     app_args.filter_base_sleep_time,
                     app_args.filter_sleep_random_range)

        files_to_process = files_to_process[app_args.batch_size:]

    if forced:
        # Even if we not use the ful batch, force the task spawn
        if __debug__:
            print("[DEBUG] Launch forced filter task")
        filter_files(files_to_process,
                     output_fds,
                     app_args.filter_base_sleep_time,
                     app_args.filter_sleep_random_range)
        files_to_process = []

    return files_to_process


#
# MAIN
#

def main():
    # Start
    print("[INFO] Starting application")
    start_time = datetime.now()

    # Parse arguments
    print("[INFO] Parsing application arguments")
    app_args = parse_arguments()

    # Process input stream elements
    print("[INFO] Initializing streams")
    fds_sensor = FileDistroStream(alias="sensor", base_dir=app_args.sensor_stream_dir)
    fds_filtered = FileDistroStream(alias="filtered", base_dir=app_args.filtered_stream_dir)

    # Process input stream elements
    print("[INFO] Processing input stream elements")
    files_to_process = []
    while not fds_sensor.is_closed():
        # Poll files and send batch to execution if necessary
        files_to_process = process_files(fds_sensor, fds_filtered, files_to_process, app_args, False)

        # Sleep between polls
        time.sleep(TIME_BETWEEN_POLLS)

    # Poll one last time (and force the task to execute
    process_files(fds_sensor, fds_filtered, files_to_process, app_args, True)

    # Synchronize
    print("[INFO] Waiting for all batch tasks to finish")
    compss_barrier()

    # Close output stream (because this app is itself a task)
    fds_filtered.close()

    # End
    print("[INFO] DONE")
    end_time = datetime.now()
    elapsed_time = end_time - start_time
    print("[TIME] TOTAL ELAPSED: " + str(elapsed_time.total_seconds()))


#
# ENTRY POINT
#

if __name__ == '__main__':
    main()
