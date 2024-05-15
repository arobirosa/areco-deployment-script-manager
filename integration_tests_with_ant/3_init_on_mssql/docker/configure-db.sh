#!/usr/bin/env bash

timeout=60
interval=5

# Function to check the database state
is_sql_server_up_and_running() {
	/opt/mssql-tools/bin/sqlcmd -U sa -P "$MSSQL_SA_PASSWORD" -Q "SELECT 1" -b -o /dev/null
	return $?
}

setup_areco() {
	echo
	echo "**************************************************************"
	echo "Setting up areco database"
	echo "**************************************************************"
	echo
	/opt/mssql-tools/bin/sqlcmd -U sa -P "$MSSQL_SA_PASSWORD" -d master -i setup.sql
}

elapsed=0
while [ $elapsed -lt $timeout ]; do
	if is_sql_server_up_and_running -eq 0; then
		setup_areco
		exit 0
	fi
	echo
	echo "**************************************************************"
	echo "SQL Server is not online. Checking again in $interval seconds."
	echo "**************************************************************"
	echo
	sleep $interval
	elapsed=$((elapsed + interval))
done

echo
echo "**************************************************************"
echo "Timed out or something went wrong. Exiting."
echo "**************************************************************"
echo
exit 1
