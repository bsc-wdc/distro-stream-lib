#!/bin/bash -e

  # Define script constants
  SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"


  #
  # HELPER FUNCTIONS
  #
  
  process() {
    # Define args
    local job_id=$1
    local version=$2
    local prod_time=$3
    local proc_time=$4
  
    output_file="${SCRIPT_DIR}/compss-${job_id}.out"

    # Get execution time
    texec=$(grep "TOTAL ELAPSED" "${output_file}" | awk '{ print $NF }')
  
    # Print results
    echo -e "${job_id}\\t${version}\\t${prod_time}\\t${proc_time}\\t${texec}"
  }
  
  
  #
  # MAIN
  #
  
  main() {
    # Define args
    experimentation_file=$1

    echo -e "JOB_ID\\tVERSION\\tPROD_TIME\\tPROC_TIME\\tT_EXEC"
  
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
      version=$(echo "${job_info}" | awk '{ print $2 }')
      prod_time=$(echo "${job_info}" | awk '{ print $3 }')
      proc_time=$(echo "${job_info}" | awk '{ print $4 }')
  
      process "${job_id}" "${version}" "${prod_time}" "${proc_time}"
    done < "${experimentation_file}"
  }
  
  
  #
  # ENTRY POINT
  #
  
  main "$@"
