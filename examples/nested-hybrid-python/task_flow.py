#!/usr/bin/python

# -*- coding: utf-8 -*-

# For better print formatting
from __future__ import print_function

# PyCOMPSs imports
from pycompss.api.task import task
from pycompss.api.constraint import constraint
from pycompss.api.parameter import INOUT
from pycompss.api.api import compss_wait_on

# Project imports
from datetime import datetime
import time
import random
import os

from my_element import MyElement

#
# CONSTANTS DEFINITION
#

CORES_TF_TASK = os.getenv("CORES_TF_TASK", 1)
CORES_TF_MERGE = os.getenv("CORES_TF_MERGE", 1)


#
# TASKS DEFINITION
#

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

    def __init__(self, file_elements, tf_depth, tf_base_sleep_time, tf_sleep_random_range):
        """
        Initializes the command line arguments instance with the given values.
        """
        from pycompss.util.serializer import deserialize_from_file
        self.elements = deserialize_from_file(file_elements)

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

    if len(sys.argv) != 5:
        print("ERROR: Not enough input arguments.")
        raise Exception("Invalid arguments")

    a = Arguments(sys.argv[1],  # elements (serialized)
                  sys.argv[2],  # tf_depth
                  sys.argv[3],  # tf_base_sleep_time
                  sys.argv[4],  # tf_sleep_random_range
                  )
    return a


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

    # Launch task flow computation
    print("[INFO] Launching task flow computation")
    res = task_flow_computation(app_args.elements,
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
