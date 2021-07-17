#! /bin/bash

if [[ "${USE_JSON_PAYLOAD_AND_HEADERS}" == "true" ]]; then
  rm /docker-entrypoint-initdb.d/3.initialize-database-json.sql
  cp /additional-scripts/3.initialize-database-json.sql docker-entrypoint-initdb.d
  echo "3.initialize-database-json.sql is activated"
fi

if [[ "${USE_DB_ID}" == "true" ]]; then
  rm /docker-entrypoint-initdb.d/4.initialize-database-db-id.sql
  cp /additional-scripts/4.initialize-database-db-id.sql docker-entrypoint-initdb.d
  echo "4.initialize-database-db-id.sql is activated"
fi

psql -c "DROP DATABASE IF EXISTS eventuate"
psql -c "CREATE DATABASE eventuate"
psql -d eventuate -c "CREATE SCHEMA eventuate"
psql -d eventuate -f /init_script/eventuate-schema.sql