# This script was based on the scripts from this repository 
# https://github.com/eventuate-foundation/eventuate-common/tree/master/postgres

FROM postgres:13

ENV WAL2JSON_COMMIT_ID=1527dfc56a54d5b7c15b2fb6ec2ab8d7776402ec

# Install the packages which will be required to get everything to compile
RUN  apt-get update  && apt-get -y install git build-essential postgresql-server-dev-13

RUN git clone https://github.com/eulerto/wal2json -b master --single-branch \
    && cd /wal2json \
    && git checkout $WAL2JSON_COMMIT_ID \
    && make && make install \
    && cd / \
    && rm -rf wal2json \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Copy the custom configuration which will be passed down to the server (using a .sample file is the preferred way of doing it by
# the base Docker image)
COPY postgresql.conf.sample /usr/share/postgresql/postgresql.conf.sample

# Copy the script which will initialize the replication permissions
COPY docker-entrypoint-initdb.d /docker-entrypoint-initdb.d

# Initialize schema
COPY 0.activate-additional-scripts.sh /docker-entrypoint-initdb.d

COPY eventuate-schema.sql /init_script/eventuate-schema.sql
COPY 2.compile-schema-per-service.sh /docker-entrypoint-initdb.d/2.compile-schema-per-service.sh
COPY eventuate_tables.sql /init_script

RUN mkdir additional-scripts
RUN chown postgres -R /additional-scripts /docker-entrypoint-initdb.d

COPY 3.initialize-database-json.sql /additional-scripts
COPY 4.initialize-database-db-id.sql /additional-scripts

RUN touch /docker-entrypoint-initdb.d/3.initialize-database-json.sql
RUN touch /docker-entrypoint-initdb.d/4.initialize-database-db-id.sql