#!/bin/bash -e
  
  # Define script constants
  SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

  # Define customizable application constraints
  export CORES_SIMULATION=48
  export CORES_PROCESS=1
  export CORES_MERGE=12

  # Define application arguments
  app_exec="${SCRIPT_DIR}/../simulation_streams.py"
  stream_base_dir="${SCRIPT_DIR}/exec/"
  num_simulations=1
  num_files=500
  simulation_sleep_random_range=1
  process_sleep_random_range=1
  merge_sleep_time=10000
  merge_sleep_random_range=1

  job_dep=$1
  simulation_sleep_time=$2
  process_sleep_time=$3

  # Enqueue job
  enqueue_compss \
    --num_nodes=2 \
    --worker_in_master_cpus=36 \
    --exec_time=60 \
    --job_dependency="${job_dep}" \
    \
    --qos=debug \
    \
    --jvm_workers_opts="-Dcompss.worker.removeWD=false" \
    --worker_working_dir=gpfs \
    --pythonpath="${SCRIPT_DIR}/../" \
    \
    --streaming=FILES \
    \
    -t \
    --summary \
    \
    "${app_exec}" \
    "${stream_base_dir}" \
    "${num_simulations}" "${num_files}" \
    "${simulation_sleep_time}" "${simulation_sleep_random_range}" \
    "${process_sleep_time}" "${process_sleep_random_range}" \
    "${merge_sleep_time}" "${merge_sleep_random_range}"
