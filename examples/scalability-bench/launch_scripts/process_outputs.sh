#!/bin/bash -e

  # Define script constants
  SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"


  #
  # HELPER FUNCTIONS
  #
  
  process() {
    # Define args
    local job_id=$1
  
    output_file="${SCRIPT_DIR}/compss-${job_id}.out"

    # Get execution time
    num_writers=$(grep "WRITERS" "${output_file}" | awk '{ print $NF }')
    num_readers=$(grep "READERS" "${output_file}" | awk '{ print $NF }')
    total_elems=$(grep "TOTAL ELEMENTS" "${output_file}" | awk '{ print $NF }')
    total_time=$(grep "TOTAL ELAPSED" "${output_file}" | awk '{ print $(NF-1) }')

    tasks_per_reader=$(grep "Reader" "${output_file}" | awk '{ print $(NF-1) }' | tr "\n" " ")
  
    # Print results
    echo -e "${job_id}\\t${num_writers}\\t${num_readers}\\t${total_elems}\\t${total_time}\\t${tasks_per_reader}"
  }
  
  
  #
  # MAIN
  #
  
  main() {
    # Define args
    experimentation_file=$1

    echo -e "JOB_ID\\tNUM_WRITERS\\tNUM_READERS\\tTOTAL_ELEMS\\tTOTAL_TIME\\tTASKS_PER_READER"
  
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
  
      process "${job_id}"
    done < "${experimentation_file}"
  }
  
  
  #
  # ENTRY POINT
  #
  
  main "$@"
