#!/bin/bash -e
  
  # Define script constants
  SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

  # Define application arguments
  app_exec="timers.RuntimeTimers"
  num_objects=$1
  object_size=$2

  # Run job
  runcompss \
    --project="${SCRIPT_DIR}/../xmls/project.xml" \
    --resources="${SCRIPT_DIR}/../xmls/resources.xml" \
    --classpath="${SCRIPT_DIR}/../target/time-bench.jar" \
    --jvm_master_opts="-Dcompss.timers=true" \
    --jvm_workers_opts="-Dcompss.timers=true -Dcompss.worker.removeWD=false" \
    --streaming=OBJECTS \
    --log_level=info \
    -t \
    -g \
    --summary \
    "${app_exec}" "${num_objects}" "${object_size}"

