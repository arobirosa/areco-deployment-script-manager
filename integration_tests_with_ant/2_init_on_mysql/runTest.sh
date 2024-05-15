#!/usr/bin/env bash

# exit when any command fails and trace commands
# set -e -x
# exit when any command fails
set -e

# Check for docker setup otherwise exit
source ../common/docker_checks.sh

# Initialize, run tests & QA
source ../common/run_test_base.sh

run "areco-mysql"

# EOF
