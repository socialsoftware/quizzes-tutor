#! /bin/bash -e

for database_name in authdb tutormicrodb tournamentdb;
do
  user=${database_name}_user
  password=${database_name}_password
  psql -c "CREATE USER ${user} WITH PASSWORD '${password}'"
  psql -c "ALTER USER ${user} WITH SUPERUSER"
  psql -c "DROP DATABASE IF EXISTS ${database_name}"
  psql -c "CREATE DATABASE ${database_name}"
  psql -d ${database_name} -c "CREATE SCHEMA ${database_name}_schema"
  echo "Creating eventuate tables for ${database_name}"
  psql -d ${database_name} -f /init_script/eventuate_tables_${database_name}.sql
  psql -c "ALTER DATABASE ${database_name} OWNER TO ${user};"
  psql -c "GRANT CONNECT ON DATABASE ${database_name} TO ${user}"
done