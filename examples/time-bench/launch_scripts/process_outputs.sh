#!/bin/bash -e

  # Define script constants
  SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"


  #
  # HELPER FUNCTIONS
  #
  
  process() {
    # Define args
    local job_id=$1
    local num_objs=$2
    local obj_size=$3
  
    output_file="${SCRIPT_DIR}/compss-${job_id}.out"
    worker_file=$(ls $HOME/.COMPSs/${job_id}/workers/ | grep "out")
    worker_out="$HOME/.COMPSs/${job_id}/workers/${worker_file}"
  
    # Get TA time
    ta_normal=$(bc <<< "scale=6; $(grep "\\[TIMER\\] TA" "${output_file}" | head -n 100 | tail -n 99 | awk '{ print $(NF-1) }' | paste -sd+ | bc)/99")
    ta_stream=$(bc <<< "scale=6; $(grep "\\[TIMER\\] TA" "${output_file}" | tail -n 99 | awk '{ print $(NF-1) }' | paste -sd+ | bc)/99")
  
    # Get TD time
    td_normal=$(bc <<< "scale=6; $(grep "\\[TIMER\\] TD" "${output_file}" | head -n 100 | tail -n 99 | awk '{ print $(NF-1) }' | paste -sd+ | bc)/99")
    td_stream=$(bc <<< "scale=6; $(grep "\\[TIMER\\] TD" "${output_file}" | tail -n 99 | awk '{ print $(NF-1) }' | paste -sd+ | bc)/99")
  
    # Get exec time
    texec_normal=$(bc <<< "scale=6; $(grep "\\[TIMER\\] Execute job" "${worker_out}" | head -n 100 | tail -n 99 | awk '{ print $(NF-1) }' | paste -sd+ | bc)/99")
    texec_stream=$(bc <<< "scale=6; $(grep "\\[TIMER\\] Execute job" "${worker_out}" | tail -n 99 | awk '{ print $(NF-1) }' | paste -sd+ | bc)/99")

    # Get transfer time
    ttransf_normal=$(bc <<< "scale=6; $(grep "\\[TIMER\\] Transfers for task" "${worker_out}" | head -n 100 | tail -n 99 | awk '{ print $(NF-1) }' | paste -sd+ | bc)/99")
    ttransf_stream=$(bc <<< "scale=6; $(grep "\\[TIMER\\] Transfers for task" "${worker_out}" | tail -n 99 | awk '{ print $(NF-1) }' | paste -sd+ | bc)/99")

    # Compute total exec time
    texectotal_normal=$(bc <<< "scale=6; ${texec_normal} + ${ttransf_normal}")
    texectotal_stream=$(bc <<< "scale=6; ${texec_stream} + ${ttransf_stream}")

    # Normal and stream times
    ttotal_normal=$(grep "NORMAL ELAPSED" "${output_file}" | awk '{ print $(NF - 1) }')
    ttotal_stream=$(grep "STREAMS ELAPSED" "${output_file}" | awk '{ print $(NF - 1) }')
    
    # Print results
    echo -e "${job_id}\\t${num_objs}\\t${obj_size}\\t${ta_normal}\\t${td_normal}\\t${texec_normal}\\t${ttransf_normal}\\t${texectotal_normal}\\t${ta_stream}\\t${td_stream}\\t${texec_stream}\\t${ttransf_stream}\\t${texectotal_stream}\\t${ttotal_normal}\\t${ttotal_stream}"
  }
  
  
  #
  # MAIN
  #
  
  main() {
    # Define args
    experimentation_file=$1

    echo -e "JOB_ID\\tNUM_OBJ\\tOBJ_SIZE\\tTA_NORMAL\\tTD_NORMAL\\tTEXEC_NORMAL\\tTTRANSF_NORMAL\\tTEXECTOTAL_NORMAL\\tTA_STEAM\\tTD_STREAM\\tTEXEC_STREAM\\tTTRANSF_STREAM\\tTEXECTOTAL_STREAM"
  
    # Process each experiment
    i=0
    while read -r job_info; do
      # Skip header line
      if [ "$i" -eq 0 ]; then
        i=1
        continue
      fi

      # Process line
      job_id=$(echo "${job_info}" | awk '{ print $1 }')
      num_objs=$(echo "${job_info}" | awk '{ print $2 }')
      obj_size=$(echo "${job_info}" | awk '{ print $3 }')
  
      process "${job_id}" "${num_objs}" "${obj_size}"
    done < "${experimentation_file}"
  }
  
  
  #
  # ENTRY POINT
  #
  
  main "$@"
