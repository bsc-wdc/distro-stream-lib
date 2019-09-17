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
    local num_writers=$3
    local num_readers=$4

    echo "$job_id   ${num_writers} ${num_readers}" >> "${job_log_file}"
  }


  #
  # MAIN
  #

  # Define script constants
  SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

  log_file="experiments.log"
  echo "JOB_ID  NUM_WRITERS NUM_READERS" > "${log_file}"

  # Define environment
  module load TrunkCRC
  
  # Experimentation
  nws="1 2 4 8"
  nrs="1 2 4 8"

  job_id=None
  for num_writers in ${nws}; do
    for num_readers in ${nrs}; do
      "${SCRIPT_DIR}"/launch_scalability.sh "${job_id}" "${num_writers}" "${num_readers}"
      wait_and_get_job_id
      log_information "${log_file}" "${job_id}" "${num_writers}" "${num_readers}"
    done
  done
