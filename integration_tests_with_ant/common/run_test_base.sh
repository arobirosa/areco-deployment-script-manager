#!/usr/bin/env bash

# exit when any command fails and trace commands
# set -e -x
# exit when any command fails
set -e

run() {

	db_name=${1}

	areco_current_test_folder=$(dirname "$(realpath "$0")")
	areco_hybris_dir=$(realpath "$areco_current_test_folder"/../../hybris)
	export COMPOSE_TLS_VERSION=TLSv1_2

	docker_compose_down() {
		docker-compose --file "$areco_current_test_folder"/docker-compose.yml down
	}

	docker_compose_up() {
		docker-compose --file "$areco_current_test_folder"/docker-compose.yml up -d
	}

	wait_for_db() {
		"$areco_current_test_folder"/../utils/wait-for-container-with-healthcheck.sh --container-name "$db_name"
		if [ $? -eq 3 ]; then
			docker_compose_down
		fi
	}

	[[ -f $areco_hybris_dir/bin/platform/hybrisserver.sh ]] || (echo "Please configure ARECO_HYBRIS_DIR with the directory where SAP commerce is located." && exit 1)
	cp -v "$areco_current_test_folder"/../dbdriver/*.jar "$areco_hybris_dir"/bin/platform/lib/dbdriver/

	echo "Remove DB Container"
	docker_compose_down

	echo "Starting the DB container"
	docker_compose_up

	wait_for_db

	echo "DB is up"

	echo "Configuring database connection and other properties"
	export HYBRIS_OPT_CONFIG_DIR=$areco_current_test_folder
	cd ../..
	. ./setantenv.sh
	echo "START TESTS AND ANYLYSIS"

	echo "Cleaning SAP Commerce data folder"
	rm -rf "$areco_hybris_dir"/data/*

	echo "Run all the tests on master tenant"
	ant clean all yunitinit qa

	echo
	echo "Remove DB Container"
	docker_compose_down

	echo "TESTS AND ANYLYSIS FINISHED"

}
