#!/usr/bin/python
#
#  Copyright 2002-2019 Barcelona Supercomputing Center (www.bsc.es)
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#

# -*- coding: utf-8 -*-

# For better print formatting
from __future__ import print_function

# PyCOMPSs imports
from pycompss.api.task import task
from pycompss.api.constraint import constraint
from pycompss.api.parameter import FILE_IN
from pycompss.api.parameter import FILE_OUT
from pycompss.api.api import compss_wait_on_file

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
@task(varargs_type=FILE_OUT)
def simulation(num_files, base_sleep_time, sleep_random_range, *args):
    if __debug__:
        print("[DEBUG] Starting simulation")

    for file_path in args:
        # Generate file
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

    # Add delay
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

    def __init__(self, output_base_dir, num_simulations, num_files, simulation_sleep_time,
                 simulation_sleep_random_range, process_sleep_time, process_sleep_random_range, merge_sleep_time,
                 merge_sleep_random_range):
        """
        Initializes the command line arguments instance with the given values.
        """
        self.output_base_dir = output_base_dir
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

    a = Arguments(sys.argv[1],  # output_base_dir
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
    print("[INFO] Creating simulation and image file paths")
    remove_and_create(app_args.output_base_dir)
    sim_files = [[] for _ in range(app_args.num_simulations)]
    output_images = [[] for _ in range(app_args.num_simulations)]
    for i in range(app_args.num_simulations):
        output_dir = app_args.output_base_dir + "/stream" + str(i) + "/"
        remove_and_create(output_dir)
        for j in range(app_args.num_files):
            sim_file_path = output_dir + "file" + str(j)
            sim_files[i].append(sim_file_path)
            output_image_path = output_dir + "file" + str(j) + ".out"
            output_images[i].append(output_image_path)

    # Create file paths for output
    print("[INFO] Creating output paths for final gifs")
    output_gifs = [None for _ in range(app_args.num_simulations)]
    for i in range(app_args.num_simulations):
        output_gifs[i] = app_args.output_base_dir + "/output_gif_" + str(i)

    # Launch simulations
    print("[INFO] Launching simulations")
    for i in range(app_args.num_simulations):
        if __debug__:
            print("[DEBUG] Launching simulation for " + str(i))
        simulation(app_args.num_files,
                   app_args.simulation_sleep_time,
                   app_args.simulation_sleep_random_range,
                   *sim_files[i])

    # Process generated files
    print("[INFO] Processing generated files")
    for i in range(app_args.num_simulations):
        if __debug__:
            print("[DEBUG] Launching process for " + str(i))
        for j in range(app_args.num_files):
            if i % 2 == 0:
                process_sim_file1(sim_files[i][j],
                                  output_images[i][j],
                                  app_args.process_sleep_time,
                                  app_args.process_sleep_random_range)
            else:
                process_sim_file2(sim_files[i][j],
                                  output_images[i][j],
                                  app_args.process_sleep_time,
                                  app_args.process_sleep_random_range)

    # Launch merge phase on remaining streams if any
    for i in range(app_args.num_simulations):
        if __debug__:
            print("[DEBUG] Launching merge_reduce for " + str(output_gifs[i]))
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
