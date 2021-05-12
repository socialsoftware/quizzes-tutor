#! /bin/bash -e

for database_name in authdb tutormicrodb;
do
  user=${database_name}_user
  password=${database_name}_password
  psql -c "CREATE USER ${user} WITH PASSWORD '${password}'"
  psql -c "ALTER USER ${user} WITH SUPERUSER"
  psql -c "DROP DATABASE IF EXISTS ${database_name}"
  psql -c "CREATE DATABASE ${database_name}"
  psql -d ${database_name} -c "CREATE SCHEMA ${database_name}_schema"
  #psql -d ${database_name} -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA ${database_name}_schema TO ${user} WITH GRANT OPTION"
  #psql -d ${database_name} -c "GRANT ALL PRIVILEGES ON DATABASE ${database_name} TO ${user} WITH GRANT OPTION"
  #psql -d ${database_name} -c "GRANT USAGE ON SCHEMA ${database_name}_schema TO ${user}"
  #psql -d ${database_name} -c "GRANT CONNECT ON DATABASE ${database_name} TO ${user}"
  #psql -d ${database_name} -c "GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA ${database_name}_schema TO ${user}"
  #psql -c "ALTER DATABASE ${database_name} OWNER TO ${user};"
  #psql -d ${database_name} -c "ALTER DEFAULT PRIVILEGES FOR USER  ON ALL TABLES IN SCHEMA ${database_name}_schema TO ${user}"
  echo "Creating eventuate tables for ${database_name}"
  psql -d ${database_name} -f /init_script/${database_name}_eventuate_tables.sql
  #psql -c "ALTER DATABASE ${database_name} OWNER TO ${user};"
  #psql -c "GRANT CONNECT ON DATABASE ${database_name} TO ${user}"
  #psql -c "GRANT CONNECT ON DATABASE ${database_name}"
  #psql -d ${database_name} -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA ${database_name}_schema TO ${user} WITH GRANT OPTION"
  #psql -d ${database_name} -c "ALTER DEFAULT PRIVILEGES FOR USER ${user} IN SCHEMA ${database_name}_schema GRANT ALL PRIVILEGES ON TABLES TO ${user} WITH GRANT OPTION"
  #psql -d ${database_name} -c "GRANT USAGE ON SCHEMA ${database_name}_schema TO ${user}"
  #psql -d ${database_name} -c "ALTER DEFAULT PRIVILEGES FOR USER ${user} IN SCHEMA ${database_name}_schema GRANT USAGE TO ${user} WITH GRANT OPTION"
  #psql -d ${database_name} -c "ALTER DEFAULT PRIVILEGES FOR USER ${user} IN SCHEMA ${database_name}_schema GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO ${user}"

  #echo "Creating eventuate tables for ${database_name} in postgres"
  #psql -c "CREATE SCHEMA ${database_name}_schema"
  #psql -f /init_script/${database_name}_eventuate_tables.sql
  #echo "Creating eventuate tables for ${database_name} in eventuate"
  #psql -d eventuate -c "CREATE SCHEMA ${database_name}_schema"
  #psql -d eventuate -f /init_script/${database_name}_eventuate_tables.sql
done