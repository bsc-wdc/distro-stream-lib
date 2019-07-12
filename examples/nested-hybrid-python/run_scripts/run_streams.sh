#!/bin/bash -e
  
  # Define script constants
  SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

  # Define customizable application constraints
  export TASK_FLOW_PY="${SCRIPT_DIR}/../task_flow.py"
  export BIG_FILTER_PY="${SCRIPT_DIR}/../big_filter.py"

  export CORES_SENSOR=8
  export CORES_BIG_FILTER=4
  #export CORES_FILTER=1
  export CORES_EXTRACT=1
  export CORES_TF=8
  #export CORES_TF_TASK=1
  #export CORES_TF_MERGE=1
  export NUM_FILES=50

  # Define application arguments
  app_exec="${SCRIPT_DIR}/../hybrid_dftf_streams.py"
  stream_base_dir="${SCRIPT_DIR}/exec/"

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

  rm -rf "${stream_base_dir}"
  mkdir -p "${stream_base_dir}"

  # Run job
  runcompss \
    --project="${SCRIPT_DIR}/../xmls/project.xml" \
    --resources="${SCRIPT_DIR}/../xmls/resources.xml" \
    --jvm_workers_opts="-Dcompss.worker.removeWD=false" \
    --pythonpath="${SCRIPT_DIR}/../" \
    --streaming=FILES \
    --streaming_master_port=49049 \
    -d \
    -t \
    -g \
    --summary \
    "${app_exec}" \
    "${stream_base_dir}" \
    "${sensor_num_files}" "${sensor_base_sleep_time}" "${sensor_sleep_random_range}" \
    "${filter_batch_size}" "${filter_base_sleep_time}" "${filter_sleep_random_range}" \
    "${extract_base_sleep_time}" "${extract_sleep_random_range}" \
    "${tf_depth}" "${tf_base_sleep_time}" "${tf_sleep_random_range}"
