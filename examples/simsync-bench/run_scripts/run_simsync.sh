#!/bin/bash -e
  
  # Define script constants
  SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

  # Define application arguments
  app_exec="simsync.SimSync"
  num_iters=2
  proc_time=1000

  # Run job
  runcompss \
    --project="${SCRIPT_DIR}/../xmls/project.xml" \
    --resources="${SCRIPT_DIR}/../xmls/resources.xml" \
    --classpath="${SCRIPT_DIR}/../target/simsync-bench.jar" \
    --jvm_workers_opts="-Dcompss.worker.removeWD=false" \
    \
    --streaming=OBJECTS \
    \
    -d \
    -t \
    -g \
    --summary \
    \
    "${app_exec}" "${num_iters}" "${proc_time}"
