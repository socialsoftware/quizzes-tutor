--CREATE USER postgres WITH SUPERUSER LOGIN PASSWORD 'root';
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA eventuate TO postgres WITH GRANT OPTION;

DROP TABLE IF EXISTS eventuate.cdc_monitoring;
CREATE TABLE eventuate.cdc_monitoring
(
  reader_id VARCHAR(1000) PRIMARY KEY,
  last_time BIGINT
);