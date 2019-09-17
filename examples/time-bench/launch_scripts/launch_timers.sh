#!/bin/bash -e
  
  # Define script constants
  SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

  # Define application arguments
  app_exec="timers.RuntimeTimers"
  job_dep=$1
  num_objects=$2
  object_size=$3

  # Run job
  enqueue_compss \
    --num_nodes=2 \
    --worker_in_master_cpus=0 \
    --exec_time=30 \
    --job_dependency="${job_dep}" \
    \
    --qos=debug \
    \
    --classpath="${SCRIPT_DIR}/../target/time-bench.jar" \
    --scheduler="es.bsc.compss.scheduler.data.DataScheduler" \
    --jvm_master_opts="-Xms16000m,-Xmx92000m,-Xmn1600m,-Dcompss.timers=true" \
    --jvm_workers_opts="-Xms16000m,-Xmx92000m,-Xmn1600m,-Dcompss.timers=true,-Dcompss.worker.removeWD=false" \
    \
    --streaming=OBJECTS \
    \
    -d \
    -t \
    --summary \
    \
    "${app_exec}" "${num_objects}" "${object_size}"

