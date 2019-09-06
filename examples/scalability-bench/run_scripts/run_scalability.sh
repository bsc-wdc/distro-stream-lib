#!/bin/bash -e
  
  # Define script constants
  SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

  # Define application arguments
  app_exec="main.ReadWrite"
  num_writers=1
  num_readers=2

  # Run job
  runcompss \
    --project="${SCRIPT_DIR}/../xmls/project.xml" \
    --resources="${SCRIPT_DIR}/../xmls/resources.xml" \
    --classpath="${SCRIPT_DIR}/../target/scalability-bench.jar" \
    --jvm_workers_opts="-Dcompss.worker.removeWD=false" \
    \
    --streaming=OBJECTS \
    \
    -d \
    -t \
    -g \
    --summary \
    \
    "${app_exec}" "${num_writers}" "${num_readers}"
