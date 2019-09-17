#!/bin/bash -e
  
  # Define script constants
  SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

  # Define application arguments
  app_exec="main.ReadWrite"

  job_dep=$1
  num_writers=$2
  num_readers=$3

  # Run job
  enqueue_compss \
    --num_nodes=2 \
    --worker_in_master_cpus=0 \
    --exec_time=30 \
    --job_dependency="${job_dep}" \
    \
    --qos=debug \
    \
    --classpath="${SCRIPT_DIR}/../target/scalability-bench.jar" \
    --jvm_workers_opts="-Dcompss.worker.removeWD=false" \
    \
    --streaming=OBJECTS \
    \
    -t \
    --summary \
    \
    "${app_exec}" "${num_writers}" "${num_readers}"
