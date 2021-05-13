#!/bin/bash -x

# TODO
# * Add volume for DB

# NOTES
# Becauase the database container may take up to 10 minutes to start, all tests with Oracle are done here. The database schema is removed but not the container.

# exit when any command fails
set -e
### keep track of the last executed command
##trap 'last_command=$current_command; current_command=$BASH_COMMAND' DEBUG
### echo an error message before exiting
##trap 'echo "\"${last_command}\" command filed with exit code $?."' EXIT

ARECO_CURRENT_TEST_FOLDER=`dirname "$(realpath '$0')"`;
ARECO_HYBRIS_DIR=$(realpath $ARECO_CURRENT_TEST_FOLDER/../../hybris);
ARECO_DB_DATA_FOLDER=$(realpath $ARECO_CURRENT_TEST_FOLDER/../docker-volumes/oracle-xe/);
export COMPOSE_TLS_VERSION=TLSv1_2;

[[ -f $ARECO_HYBRIS_DIR/bin/platform/hybrisserver.sh ]] || (echo "Please configure ARECO_HYBRIS_DIR with the directory where SAP commerce is located." && exit 1);
[[ -d $ARECO_DB_DATA_FOLDER ]] || (echo "I can't found the shared directory with the database data" && exit 2);

cp -v $ARECO_CURRENT_TEST_FOLDER/../dbdriver/*.jar $ARECO_HYBRIS_DIR/bin/platform/lib/dbdriver/;

echo "Starting the oracle XE container"
docker-compose --file $ARECO_CURRENT_TEST_FOLDER/docker-compose.yml up -d;
$ARECO_CURRENT_TEST_FOLDER/../utils/wait-for-it.sh --host=127.0.0.1 --port=9500 --timeout=600 -- echo "Waiting for the oracle database to be ready";

start_time=$(date +"%s")
while true
do
    if [[ $(docker-compose --file $ARECO_CURRENT_TEST_FOLDER/docker-compose.yml logs areco-database | grep -c 'DATABASE IS READY TO USE') -ge 1 ]]; then        
        break
    fi
    elapsed_time=$(($(date +"%s") - $start_time))
    if [[ "$elapsed_time" -gt 1200 ]]; then
        echo "Oracle XE did not start after 20 minutes";
        exit 3;
    fi
    echo "Waiting for the database container to be ready. Sleeping for 30 seconds";
    sleep 30;
done
echo "Oracle XE is up";

echo "Dropping user with its objects if it is exist";
docker-compose exec areco-database /opt/oracle/product/18c/dbhomeXE/bin/sqlplus -s /nolog <<EOF
connect SYS/password123@XEPDB1 AS SYSDBA
drop user ARECOMANAGER CASCADE;
create user ARECOMANAGER IDENTIFIED by secret3odks;
grant all privileges to ARECOMANAGER;
quit
EOF

echo "Configuring database connection and other properties";
export HYBRIS_OPT_CONFIG_DIR=$ARECO_CURRENT_TEST_FOLDER;

cd ../..;
. ./setantenv.sh;
echo "START TEST";

echo "Dropping and recreating the user and cleaning data folder"
rm -rf $ARECO_HYBRIS_DIR/data/*;

echo "Run all the tests on master tenant"
ant clean all yunitinit qa;

echo "TEST SUCCESS"
