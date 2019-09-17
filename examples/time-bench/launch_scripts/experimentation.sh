#!/bin/bash -e

  #
  # HELPER FUNCTIONS
  #

  wait_and_get_job_id() {
    sleep 6s
    job_id=$(squeue | grep "$(whoami)" | sort -n - | tail -n 1 | awk '{ print $1 }')
    echo "Last Job ID: ${job_id}"
  }

  log_information() {
    local job_log_file=$1
    local job_id=$2
    local num_obj=$3
    local obj_size=$4

    echo "$job_id   ${num_obj}  ${obj_size}" >> "${job_log_file}"
  }


  #
  # MAIN
  #

  # Define script constants
  SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

  log_file="experiments.log"
  echo "JOB_ID  NUM_OBJ OBJ_SIZE" > "${log_file}"

  # Define environment
  module load TrunkCRC
  
  # Experimentation
  NUM_OBJECTS="1 2 4 8 16"
  OBJECT_SIZES="8388608" # "1048576 2097152 4194304 8388608 16777216 33554432 67108864 134217728" #"8388608"

  job_id=None
  for num_obj in ${NUM_OBJECTS}; do
    for obj_s in ${OBJECT_SIZES}; do
      "${SCRIPT_DIR}"/launch_timers.sh "${job_id}" "${num_obj}" "${obj_s}"
      wait_and_get_job_id
      log_information "${log_file}" "${job_id}" "${num_obj}" "${obj_s}"
    done
  done
