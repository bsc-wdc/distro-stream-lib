#!/bin/bash -e

  # Define script constants
  SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"


  #
  # HELPER FUNCTIONS
  #
  
  process() {
    # Define args
    local job_id=$1
    local num_iters=$2
    local proc_time=$3
  
    output_file="${SCRIPT_DIR}/compss-${job_id}.out"

    # Get execution time
    texec_normal=$(grep "NORMAL ELAPSED" "${output_file}" | awk '{ print $(NF-1) }' | head -n 1)
    texec_streams=$(grep "NORMAL ELAPSED" "${output_file}" | awk '{ print $(NF-1) }' | tail -n 1) # App update: STEAMS ELAPSED
  
    # Print results
    echo -e "${job_id}\\t${num_iters}\\t${proc_time}\\t${texec_normal}\\t${texec_streams}"
  }
  
  
  #
  # MAIN
  #
  
  main() {
    # Define args
    experimentation_file=$1

    echo -e "JOB_ID\\tNUM_ITERS\\tPROC_TIME\\t\\tT_EXEC_NORMAL\\tT_EXEC_STEAMS"
  
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
      num_iters=$(echo "${job_info}" | awk '{ print $2 }')
      proc_time=$(echo "${job_info}" | awk '{ print $3 }')
  
      process "${job_id}" "${num_iters}" "${proc_time}"
    done < "${experimentation_file}"
  }
  
  
  #
  # ENTRY POINT
  #
  
  main "$@"
