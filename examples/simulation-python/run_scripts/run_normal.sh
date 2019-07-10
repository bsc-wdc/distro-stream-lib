#!/bin/bash -e
  
  # Define script constants
  SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

  # Define customizable application constraints
  export CORES_SIMULATION=8
  export CORES_PROCESS=1
  export CORES_MERGE=2

  # Define application arguments
  app_exec="${SCRIPT_DIR}/../simulation_normal.py"
  output_base_dir="${SCRIPT_DIR}/exec/"
  num_simulations=2
  num_files=50
  simulation_sleep_time=1000
  simulation_sleep_random_range=200
  process_sleep_time=2000
  process_sleep_random_range=100
  merge_sleep_time=10000
  merge_sleep_random_range=1000

  # Run job
  runcompss \
    --project="${SCRIPT_DIR}/../xmls/project.xml" \
    --resources="${SCRIPT_DIR}/../xmls/resources.xml" \
    --jvm_workers_opts="-Dcompss.worker.removeWD=false" \
    --pythonpath="${SCRIPT_DIR}/../" \
    -d \
    -g \
    -t \
    --summary \
    "${app_exec}" \
    "${output_base_dir}" \
    "${num_simulations}" "${num_files}" \
    "${simulation_sleep_time}" "${simulation_sleep_random_range}" \
    "${process_sleep_time}" "${process_sleep_random_range}" \
    "${merge_sleep_time}" "${merge_sleep_random_range}"
