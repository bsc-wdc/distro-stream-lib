#!/bin/bash -e
  
  # Define script constants
  SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

  # Define customizable application constraints
  export NUM_FILES=50

  export RUNCOMPSS=$(which runcompss)

  export NESTED_COMPUTING_NODES=1
  export CORES_SENSOR=8
  export CORES_BIG_FILTER=4
  export CORES_EXTRACT=1
  export CORES_TF=8

  # Define application arguments
  app_exec="mains.NestedHybrid"

  sensor_num_files=${NUM_FILES}
  sensor_base_sleep_time=300
  sensor_sleep_random_range=50
  filter_batch_size=10
  filter_base_sleep_time=2000
  filter_sleep_random_range=200
  extract_base_sleep_time=500
  extract_sleep_random_range=100
  tf_depth=2
  tf_base_sleep_time=1000
  tf_sleep_random_range=50

  # Run job
  runcompss \
    --project="${SCRIPT_DIR}/../xmls/project.xml" \
    --resources="${SCRIPT_DIR}/../xmls/resources.xml" \
    --classpath="${SCRIPT_DIR}/../target/nested-hybrid-java.jar" \
    --jvm_workers_opts="-Dcompss.worker.removeWD=false" \
    --streaming=OBJECTS \
    --streaming_master_port=49049 \
    -d \
    -t \
    -g \
    --summary \
    "${app_exec}" \
    "${sensor_num_files}" "${sensor_base_sleep_time}" "${sensor_sleep_random_range}" \
    "${filter_batch_size}" "${filter_base_sleep_time}" "${filter_sleep_random_range}" \
    "${extract_base_sleep_time}" "${extract_sleep_random_range}" \
    "${tf_depth}" "${tf_base_sleep_time}" "${tf_sleep_random_range}"
