#!/usr/bin/env bash

# exit when any command fails and trace commands
# set -e -x
# exit when any command fails
set -e

# Check for docker setup otherwise exit
source ../common/docker_checks.sh

# Setup MSSQL Container
docker_image="areco-mssql:2017-latest"
if [ -z "$(docker images -q "$docker_image" 2>/dev/null)" ]; then
	echo "Build custom MSSQL image"
	pushd ./docker
	docker build --no-cache -t "$docker_image" .
	popd
fi

# Initialize, run tests & QA
source ../common/run_test_base.sh

run "areco-mssql"

# EOF
