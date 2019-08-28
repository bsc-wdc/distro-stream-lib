#!/usr/bin/python

# -*- coding: utf-8 -*-

# For better print formatting
from __future__ import print_function

# PyCOMPSs imports
from pycompss.api.task import task
from pycompss.api.constraint import constraint
from pycompss.api.parameter import STREAM_OUT
from pycompss.api.parameter import FILE_IN
from pycompss.api.parameter import FILE_OUT
from pycompss.api.api import compss_wait_on_file

from pycompss.streams.distro_stream import FileDistroStream

# Project imports
from datetime import datetime
import time
import random
import os

#
# CONSTANTS DEFINITION
#

CORES_SIMULATION = os.getenv("CORES_SIMULATION")
CORES_PROCESS = os.getenv("CORES_PROCESS")
CORES_MERGE = os.getenv("CORES_MERGE")
TIME_BETWEEN_POLLS = 5
TIME_DELAY = 10


#
# TASKS DEFINITION
#

@constraint(computing_units=CORES_SIMULATION)
@task(fds=STREAM_OUT)
def simulation(fds, num_files, base_sleep_time, sleep_random_range):
    if __debug__:
        print("[DEBUG] Starting simulation")

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
        sleep_time = sleep_time / 1000.0  # from ms to s
        time.sleep(sleep_time)

    # Close stream
    fds.close()

    # Add extra delay
    time.sleep(TIME_DELAY)


@constraint(computing_units=CORES_PROCESS)
@task(input_file=FILE_IN, output_image=FILE_OUT)
def process_sim_file1(input_file, output_image, base_sleep_time, sleep_random_range):
    process_sim_file(input_file, output_image, base_sleep_time, sleep_random_range)


@constraint(computing_units=CORES_PROCESS)
@task(input_file=FILE_IN, output_image=FILE_OUT)
def process_sim_file2(input_file, output_image, base_sleep_time, sleep_random_range):
    process_sim_file(input_file, output_image, base_sleep_time, sleep_random_range)


def process_sim_file(input_file, output_image, base_sleep_time, sleep_random_range):
    if __debug__:
        print("[DEBUG] Processing file " + input_file)

    # Print input file content
    with open(input_file, "r") as f:
        print(f.readlines())

    # Sleep to simulate time spent to process the file
    sleep_time = base_sleep_time + random.randint(0, sleep_random_range)
    sleep_time = sleep_time / 1000.0  # from ms to s
    time.sleep(sleep_time)

    # Return the generated image
    current_time = datetime.now()
    with open(output_image, "w") as f:
        f.write("Output image generated at " + str(current_time) + "\n")
        f.write("Output image generated from " + str(input_file) + "\n")
        f.flush()


@constraint(computing_units=CORES_MERGE)
@task(output_gif=FILE_OUT, varargs_type=FILE_IN)
def merge_reduce(output_gif, base_sleep_time, sleep_random_range, *args):
    if __debug__:
        print("[DEBUG] Generating " + str(output_gif))
        for input_image in args:
            print("   - Input file: " + str(input_image))

    # Sleep to simulate gif generation time
    sleep_time = base_sleep_time + random.randint(0, sleep_random_range)
    sleep_time = sleep_time / 1000.0  # from ms to s
    time.sleep(sleep_time)

    # Return the generated gif
    current_time = datetime.now()
    with open(output_gif, "w") as f:
        f.write("Output gif generated at " + str(current_time) + "\n")
        for input_image in args:
            f.write("Containing image " + str(input_image) + "\n")
        f.flush()


#
# HELPER METHODS
#

class Arguments(object):
    """
    Store command line arguments in a single object.
    """

    def __init__(self, stream_base_dir, num_simulations, num_files, simulation_sleep_time,
                 simulation_sleep_random_range, process_sleep_time, process_sleep_random_range, merge_sleep_time,
                 merge_sleep_random_range):
        """
        Initializes the command line arguments instance with the given values.
        """
        self.stream_base_dir = stream_base_dir
        self.num_simulations = int(num_simulations)
        self.num_files = int(num_files)
        self.simulation_sleep_time = int(simulation_sleep_time)
        self.simulation_sleep_random_range = int(simulation_sleep_random_range)
        self.process_sleep_time = int(process_sleep_time)
        self.process_sleep_random_range = int(process_sleep_random_range)
        self.merge_sleep_time = int(merge_sleep_time)
        self.merge_sleep_random_range = int(merge_sleep_random_range)


def parse_arguments():
    """
    Parses the command line arguments.

    :return: The parsed command line arguments stored in a single object.
        + type: Arguments
    """
    import sys

    if len(sys.argv) != 10:
        print("ERROR: Not enough input arguments.")
        raise Exception("Invalid arguments")

    a = Arguments(sys.argv[1],  # stream_base_dir
                  sys.argv[2],  # num_simulations
                  sys.argv[3],  # num_files
                  sys.argv[4],  # simulation_sleep_time
                  sys.argv[5],  # simulation_sleep_random_range
                  sys.argv[6],  # process_sleep_time
                  sys.argv[7],  # process_sleep_random_range
                  sys.argv[8],  # merge_sleep_time
                  sys.argv[9],  # merge_sleep_random_range
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

    # Create input streams
    print("[INFO] Creating input streams")
    remove_and_create(app_args.stream_base_dir)
    input_streams = [None for _ in range(app_args.num_simulations)]
    for i in range(app_args.num_simulations):
        stream_dir = app_args.stream_base_dir + "/stream" + str(i) + "/"
        remove_and_create(stream_dir)
        input_streams[i] = FileDistroStream(base_dir=stream_dir)

    # Create file paths for output
    print("[INFO] Creating output paths for final gifs")
    output_gifs = [None for _ in range(app_args.num_simulations)]
    for i in range(app_args.num_simulations):
        output_gifs[i] = app_args.stream_base_dir + "/output_gif_" + str(i)

    # Launch simulations
    print("[INFO] Launching simulations")
    for i in range(app_args.num_simulations):
        if __debug__:
            print("[DEBUG] Launching simulation for " + str(input_streams[i]))
        simulation(input_streams[i],
                   app_args.num_files,
                   app_args.simulation_sleep_time,
                   app_args.simulation_sleep_random_range)

    # Process generated files
    print("[INFO] Processing generated files")
    output_images = [[] for _ in range(app_args.num_simulations)]
    closed_streams = [False for _ in range(app_args.num_simulations)]
    mr_launch_streams = [False for _ in range(app_args.num_simulations)]
    while False in closed_streams:
        # Sleep between polls
        time.sleep(TIME_BETWEEN_POLLS)

        # Process opened streams
        for i in range(app_args.num_simulations):
            if not closed_streams[i]:
                # Update stream status
                closed_streams[i] = input_streams[i].is_closed()

                # Process new files
                new_files = input_streams[i].poll()
                for input_file in new_files:
                    if __debug__:
                        print("[DEBUG] Launching task process_sim_file for " + str(input_file))
                    output_image = input_file + ".out"
                    output_images[i].append(output_image)
                    if i % 2 == 0:
                        process_sim_file1(input_file,
                                          output_image,
                                          app_args.process_sleep_time,
                                          app_args.process_sleep_random_range)
                    else:
                        process_sim_file2(input_file,
                                          output_image,
                                          app_args.process_sleep_time,
                                          app_args.process_sleep_random_range)
            else:
                # If the stream is closed, we can launch the merge phase
                if not mr_launch_streams[i]:
                    if __debug__:
                        print("[DEBUG] Launching merge_reduce for " + str(output_gifs[i]))
                    mr_launch_streams[i] = True
                    merge_reduce(output_gifs[i],
                                 app_args.merge_sleep_time,
                                 app_args.merge_sleep_random_range,
                                 *output_images[i])

    # Launch merge phase on remaining streams if any
    for i in range(app_args.num_simulations):
        if not mr_launch_streams[i]:
            if __debug__:
                print("[DEBUG] Launching merge_reduce for " + str(output_gifs[i]))
            mr_launch_streams[i] = True
            merge_reduce(output_gifs[i],
                         app_args.merge_sleep_time,
                         app_args.merge_sleep_random_range,
                         *output_images[i])

    # Synchronize files
    print("[INFO] Syncrhonizing final output gifs")
    for i in range(app_args.num_simulations):
        output_gifs[i] = compss_wait_on_file(output_gifs[i])
        print("Output gif generated at: " + str(output_gifs[i]))

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
