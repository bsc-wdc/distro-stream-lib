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
    local ver=$3
    local prod_time=$4
    local proc_time=$5

    echo "$job_id   $ver    $prod_time   $proc_time" >> "${job_log_file}"
  }


  #
  # MAIN
  #

  # Define script constants
  SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

  log_file="${1:-experiments.log}"
  echo "JOB_ID  VERSION PROD_TIME   PROC_TIME" > "${log_file}"

  # Define environment
  module load TrunkCRC

  # Increase production, same process rate
  simulation_sleep_times="100 200 400 500 1000 2000"
  process_sleep_time=60000
  job_id=None
  for sim_sleep in ${simulation_sleep_times}; do
    "${SCRIPT_DIR}"/launch_normal.sh "${job_id}" "${sim_sleep}" "${process_sleep_time}"
    wait_and_get_job_id
    log_information "${log_file}" "${job_id}" "normal" "${sim_sleep}" "${process_sleep_time}"
    "${SCRIPT_DIR}"/launch_streams.sh "${job_id}" "${sim_sleep}" "${process_sleep_time}"
    wait_and_get_job_id
    log_information "${log_file}" "${job_id}" "streams" "${sim_sleep}" "${process_sleep_time}"
  done

  # Increase process rate, same production
  simulation_sleep_time=100
  process_sleep_times="1000 5000 10000 20000 30000 40000 50000 60000"

  for proc_sleep in ${process_sleep_times}; do
    "${SCRIPT_DIR}"/launch_normal.sh "${job_id}" "${simulation_sleep_time}" "${proc_sleep}"
    wait_and_get_job_id
    log_information "${log_file}" "${job_id}" "normal" "${simulation_sleep_time}" "${proc_sleep}"
    "${SCRIPT_DIR}"/launch_streams.sh "${job_id}" "${simulation_sleep_time}" "${proc_sleep}"
    wait_and_get_job_id
    log_information "${log_file}" "${job_id}" "streams" "${simulation_sleep_time}" "${proc_sleep}"
  done
