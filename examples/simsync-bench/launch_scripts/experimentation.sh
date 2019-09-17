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
    local num_iter=$3
    local proc_time=$4

    echo "$job_id   ${num_iter}  ${proc_time}" >> "${job_log_file}"
  }


  #
  # MAIN
  #

  # Define script constants
  SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

  log_file="${1:-experiments.log}"
  echo "JOB_ID  NUM_ITER    PROC_TIME" > "${log_file}"

  # Define environment
  module load TrunkCRC
  
  # Experimentation
  num_iters="1 2 4 8 16 32 64 128"
  proc_time=2000

  job_id=None
  for i in ${num_iters}; do
    "${SCRIPT_DIR}"/launch_simsync.sh "${job_id}" "${i}" "${proc_time}"
    wait_and_get_job_id
    log_information "${log_file}" "${job_id}" "${i}" "${proc_time}"
  done
