#!/usr/bin/env bash

set -e

# Function to display usage information
usage() {
	>&2 cat <<EOF
Usage:
  $0 --container-name <name> [--timeout-in-seconds <seconds>]
EOF
	exit 1
}

# Initialize variables
container_name=""
timeout_in_seconds=300

# Parse options
while (("$#")); do
	case "$1" in
	--container-name)
		container_name="$2"
		shift 2
		;;
	--timeout-in-seconds)
		timeout_in_seconds="$2"
		shift 2
		;;
	*)
		>&2 echo Unsupported option: "$1"
		usage
		;;
	esac
done

# Set positional arguments in their proper place
eval set -- "$PARAMS"

# Check if required options are provided
if [[ -z "$container_name" ]]; then
	usage
fi

get_health_state() {
	container_health_state=$(docker inspect -f "{{ .State.Health.Status }}" "${container_name}")
	if [ "$container_health_state" = "healthy" ]; then
		echo "Container [$container_name] healthy"
		return 0
	else
		echo "Container [$container_name] not yet ready"
		return 1
	fi
}

# Set the end time
end=$((SECONDS + timeout_in_seconds))

until get_health_state; do
	sleep 3
	# Check if the current time has exceeded the end time
	if [ $SECONDS -ge $end ]; then
		echo "Could not start container [$container_name], timeout reached."
		exit 3
	fi
done
