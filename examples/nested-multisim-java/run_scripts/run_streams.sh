#!/bin/bash -e
  
  # Define script constants
  SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

  # Define customizable application constraints
  RUNCOMPSS=$(which runcompss)
  export RUNCOMPSS=$RUNCOMPSS

  export NESTED_COMPUTING_NODES=1
  export CORES_SIMULATION=4

  # Define application arguments
  app_exec="mains.NestedMultiSim"

  num_iterations=5
  simulation_base_sleep_time=10000
  simulation_sleep_random_range=5000

  # Run job
  runcompss \
    --project="${SCRIPT_DIR}/../xmls/project.xml" \
    --resources="${SCRIPT_DIR}/../xmls/resources.xml" \
    --classpath="${SCRIPT_DIR}/../target/nested-multisim-java.jar" \
    --jvm_workers_opts="-Dcompss.worker.removeWD=false" \
    --streaming=OBJECTS \
    --streaming_master_port=49049 \
    -d \
    -t \
    -g \
    --summary \
    "${app_exec}" \
    "${num_iterations}" \
    "${simulation_base_sleep_time}" "${simulation_sleep_random_range}"
