#! /bin/bash -e

psql -c "CREATE USER ars WITH PASSWORD 'admin'";
for schema in authmicrodb tutormicrodb
do
  psql -c "DROP DATABASE IF EXISTS ${schema}";
  psql -c "CREATE DATABASE ${schema}";
  psql -c "GRANT ALL PRIVILEGES ON DATABASE ${schema} TO ars";
  echo "Creating eventuate tables for ${schema}"
  psql -d ${schema} -f /init_script/template.sql;
done