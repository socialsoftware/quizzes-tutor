FROM postgres:latest
#COPY replication.cnf /etc/mysql/conf.d

COPY common-schema.sql /docker-entrypoint-initdb.d/3.common-schema.sql
COPY compile-schema-per-service.sh /docker-entrypoint-initdb.d/4.compile-schema-per-service.sh
COPY template.sql /init_script/template.sql


#RUN touch /docker-entrypoint-initdb.d/5.schema-per-service.sql
#RUN chown mysql -R /docker-entrypoint-initdb.d