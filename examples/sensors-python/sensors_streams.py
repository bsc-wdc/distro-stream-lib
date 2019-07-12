#!/usr/bin/python

# -*- coding: utf-8 -*-

# For better print formatting
from __future__ import print_function

# PyCOMPSs imports
from pycompss.api.task import task
from pycompss.api.constraint import constraint
from pycompss.api.parameter import STREAM_IN
from pycompss.api.parameter import STREAM_OUT
from pycompss.api.parameter import INOUT
from pycompss.api.api import compss_wait_on

from pycompss.streams.distro_stream import FileDistroStream

# Project imports
from datetime import datetime
import time
import random
import os

from my_element import MyElement

#
# CONSTANTS DEFINITION
#

CORES_SENSOR = os.getenv("CORES_SENSOR")
CORES_FILTER = os.getenv("CORES_FILTER")
CORES_EXTRACT = os.getenv("CORES_EXTRACT")
CORES_TF_TASK = os.getenv("CORES_TF_TASK")
CORES_TF_MERGE = os.getenv("CORES_TF_MERGE")

NUM_FILES = os.getenv("NUM_FILES")

SENSOR_INITIAL_DELAY = 10  # seconds
TIME_BETWEEN_POLLS = 5  # seconds
EXTRACT_ACCEPT_RATE = 5  # int
NUM_EXTRACT = int(NUM_FILES) / EXTRACT_ACCEPT_RATE if NUM_FILES is not None else None


#
# TASKS DEFINITION
#

@constraint(computing_units=CORES_SENSOR)
@task(fds=STREAM_OUT)
def sensor(fds, num_files, base_sleep_time, sleep_random_range):
    if __debug__:
        print("[DEBUG] Starting sensor")

    # Initial sensor delay
    time.sleep(SENSOR_INITIAL_DELAY)

    base_path = fds.base_dir

    for i in range(num_files):
        # Generate file
        file_path = base_path + "/file" + str(i)
        current_time = datetime.now()
        if __debug__:
            print("[DEBUG] (" + str(current_time) + ") Generating file " + str(file_path))
        with open(file_path, "w") as f:
            f.write("File generated at " + str(current_time) + "\n")
            f.flush()

        # Sleep between generation to simulate time spent to generate the next file
        sleep_time = base_sleep_time + random.randint(0, sleep_random_range)
        sleep_time = sleep_time / 1000  # from ms to s
        time.sleep(sleep_time)

    # Close stream
    if __debug__:
        print("[DEBUG] Notifying stream closure")
    fds.close()

    if __debug__:
        print("[DEBUG] Stopping sensor")


@constraint(computing_units=CORES_FILTER)
@task(fds_sensor=STREAM_IN, fds_filtered=STREAM_OUT)
def big_filter(fds_sensor, fds_filtered, base_sleep_time, sleep_random_range):
    if __debug__:
        print("[DEBUG] Starting big_filter")

    while not fds_sensor.is_closed():
        # Sleep between polls
        time.sleep(TIME_BETWEEN_POLLS)

        # Process sensor files
        get_and_filter(fds_sensor, fds_filtered, base_sleep_time, sleep_random_range)

    # Process sensor files one last time
    get_and_filter(fds_sensor, fds_filtered, base_sleep_time, sleep_random_range)

    # Close stream
    if __debug__:
        print("[DEBUG] Notifying stream closure")
    fds_filtered.close()

    if __debug__:
        print("[DEBUG] Stopping big_filter")


def get_and_filter(fds_sensor, fds_filtered, base_sleep_time, sleep_random_range):
    if __debug__:
        print("[DEBUG] Polling new files")
    new_files = fds_sensor.poll()

    output_base_path = fds_filtered.base_dir

    for sensor_file in new_files:
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


@constraint(computing_units=CORES_EXTRACT)
@task(fds_filtered=STREAM_IN, returns=NUM_EXTRACT)
def extract_info(fds_filtered, base_sleep_time, sleep_random_range):
    if __debug__:
        print("[DEBUG] Starting extract_info")

    num_processed_elements = 0
    valid_elements = []
    while not fds_filtered.is_closed():
        # Sleep between polls
        time.sleep(TIME_BETWEEN_POLLS)

        # Process filtered files
        new_elements, num_processed_elements = get_and_extract(fds_filtered, num_processed_elements, base_sleep_time,
                                                               sleep_random_range)
        valid_elements.extend(new_elements)

    # Process sensor files one last time
    new_elements, num_processed_elements = get_and_extract(fds_filtered, num_processed_elements, base_sleep_time,
                                                           sleep_random_range)
    valid_elements.extend(new_elements)

    # Return valid elements
    if __debug__:
        print("[DEBUG] Detected valid elements: " + str(valid_elements))
    return valid_elements


def get_and_extract(fds_filtered, num_processed_elements, base_sleep_time, sleep_random_range):
    if __debug__:
        print("[DEBUG] Polling new files")
    new_files = fds_filtered.poll()

    valid_elements = []
    for filtered_file in new_files:
        if __debug__:
            print("[DEBUG] Processing file " + filtered_file)

        # Print input file content
        with open(filtered_file, "r") as f:
            print(f.readlines())

        # Process 1 every NUM_EXTRACT_ELEMENTS elements
        if num_processed_elements % EXTRACT_ACCEPT_RATE == 0:
            # Sleep to simulate time spent to process the file
            sleep_time = base_sleep_time + random.randint(0, sleep_random_range)
            sleep_time = sleep_time / 1000  # from ms to s
            time.sleep(sleep_time)

            # Generate valid element
            elem = MyElement(filtered_file)
            valid_elements.append(elem)
        num_processed_elements = num_processed_elements + 1

    return valid_elements, num_processed_elements


def task_flow_computation(elements, depth, base_sleep_time, sleep_random_range):
    outs = []
    for e in elements:
        o = None
        for _ in range(depth):
            o = tf_task(e, base_sleep_time, sleep_random_range)
        outs.append(o)

    if len(outs) > 0:
        while len(outs) >= 2:
            o1 = outs.pop(0)
            o2 = outs.pop(0)
            new_o = tf_merge(o1, o2, base_sleep_time, sleep_random_range)
            outs.append(new_o)

        return outs[0]
    else:
        return MyElement("FAIL")


@constraint(computing_units=CORES_TF_TASK)
@task(e=INOUT, returns=1)
def tf_task(e, base_sleep_time, sleep_random_range):
    # Sleep to simulate computation
    sleep_time = base_sleep_time + random.randint(0, sleep_random_range)
    sleep_time = sleep_time / 1000  # from ms to s
    time.sleep(sleep_time)

    # Modify inout object
    e.set_processed()

    # Return object
    o = MyElement(e.input_files[0])
    return o


@constraint(computing_units=CORES_TF_MERGE)
@task(returns=1)
def tf_merge(o1, o2, base_sleep_time, sleep_random_range):
    # Sleep to simulate computation
    sleep_time = base_sleep_time + random.randint(0, sleep_random_range)
    sleep_time = sleep_time / 1000  # from ms to s
    time.sleep(sleep_time)

    # Create new object and return it
    new_o = MyElement("merge")
    new_o.add_inputs(o1.input_files)
    new_o.add_inputs(o2.input_files)
    return new_o


#
# HELPER METHODS
#

class Arguments(object):
    """
    Store command line arguments in a single object.
    """

    def __init__(self, stream_base_dir, sensor_num_files, sensor_base_sleep_time, sensor_sleep_random_range,
                 num_filters, filter_base_sleep_time, filter_sleep_random_range, extract_base_sleep_time,
                 extract_sleep_random_range, tf_depth, tf_base_sleep_time, tf_sleep_random_range):
        """
        Initializes the command line arguments instance with the given values.
        """
        self.stream_base_dir = stream_base_dir
        self.sensor_num_files = int(sensor_num_files)
        self.sensor_base_sleep_time = int(sensor_base_sleep_time)
        self.sensor_sleep_random_range = int(sensor_sleep_random_range)
        self.num_filters = int(num_filters)
        self.filter_base_sleep_time = int(filter_base_sleep_time)
        self.filter_sleep_random_range = int(filter_sleep_random_range)
        self.extract_base_sleep_time = int(extract_base_sleep_time)
        self.extract_sleep_random_range = int(extract_sleep_random_range)
        self.tf_depth = int(tf_depth)
        self.tf_base_sleep_time = int(tf_base_sleep_time)
        self.tf_sleep_random_range = int(tf_sleep_random_range)


def parse_arguments():
    """
    Parses the command line arguments.

    :return: The parsed command line arguments stored in a single object.
        + type: Arguments
    """
    import sys

    if len(sys.argv) != 13:
        print("ERROR: Not enough input arguments.")
        raise Exception("Invalid arguments")

    a = Arguments(sys.argv[1],  # stream_base_dir
                  sys.argv[2],  # sensor_num_files
                  sys.argv[3],  # sensor_base_sleep_time
                  sys.argv[4],  # sensor_sleep_random_range
                  sys.argv[5],  # num_filters
                  sys.argv[6],  # filter_base_sleep_time
                  sys.argv[7],  # filter_sleep_random_range
                  sys.argv[8],  # extract_base_sleep_time
                  sys.argv[9],  # extract_sleep_random_range
                  sys.argv[10],  # tf_depth
                  sys.argv[11],  # tf_base_sleep_time
                  sys.argv[12],  # tf_sleep_random_range
                  )
    return a


def remove_and_create(folder):
    """
    Creates a new folder in the given loaction. If the location already exists, removes it and cleans all its content.

    :param folder: Location of the folder to create.
        + type: str
    :return: None
    """
    import shutil

    # Clean folder if exists
    if os.path.exists(folder) and os.path.isdir(folder):
        shutil.rmtree(folder)

    # Create folder
    os.makedirs(folder)


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

    # Create streams
    print("[INFO] Creating streams")
    sensor_base_dir = app_args.stream_base_dir + "/sensor/"
    filter_base_dir = app_args.stream_base_dir + "/filter/"
    remove_and_create(app_args.stream_base_dir)
    os.makedirs(sensor_base_dir)
    os.makedirs(filter_base_dir)

    fds_sensor = FileDistroStream(base_dir=sensor_base_dir)
    fds_filtered = FileDistroStream(base_dir=filter_base_dir)

    # Create sensor
    print("[INFO] Launching sensor")
    sensor(fds_sensor,
           app_args.sensor_num_files,
           app_args.sensor_base_sleep_time,
           app_args.sensor_sleep_random_range)

    # Create filters
    print("[INFO] Launching filters")
    for _ in range(app_args.num_filters):
        big_filter(fds_sensor,
                   fds_filtered,
                   app_args.filter_base_sleep_time,
                   app_args.filter_sleep_random_range)

    # Create extract
    print("[INFO] Launching extract")
    elements = extract_info(fds_filtered,
                            app_args.extract_base_sleep_time,
                            app_args.extract_sleep_random_range)

    # Launch task flow computation
    print("[INFO] Launching task flow computation")
    res = task_flow_computation(elements,
                                app_args.tf_depth,
                                app_args.tf_base_sleep_time,
                                app_args.tf_sleep_random_range)

    # Synchronize
    print("[INFO] Syncrhonizing final output")
    res = compss_wait_on(res)
    print("Computation result is: " + str(res))

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
