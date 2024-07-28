#!/usr/bin/env bash

# Check if docker is installed
if ! hash docker &>/dev/null; then
	echo "Install docker first"
	exit 1
fi

# Check if docker compose v2 or docker compose v1 is installed
if ! docker compose version &>/dev/null || ! hash docker-compose &>/dev/null; then
	echo "Install docker compose first"
	exit 1
fi

# Check if the docker daemon is running
if ! docker info &>/dev/null; then
	echo "Docker is not running"
	exit 1
fi
