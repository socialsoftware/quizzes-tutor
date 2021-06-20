-- SQLINES DEMO ***  tables

DROP table IF EXISTS events;
DROP table IF EXISTS  entities;
DROP table IF EXISTS  snapshots;
DROP table IF EXISTS cdc_monitoring;

CREATE TABLE events
(
  event_id VARCHAR(1000) PRIMARY KEY,
  event_type VARCHAR(1000),
  event_data VARCHAR(1000) NOT NULL,
  entity_type VARCHAR(1000) NOT NULL,
  entity_id VARCHAR(1000) NOT NULL,
  triggering_event VARCHAR(1000),
  metadata VARCHAR(1000),
  published SMALLINT DEFAULT 0
);
CREATE INDEX events_idx ON events(entity_type, entity_id, event_id);
CREATE INDEX events_published_idx ON events(published, event_id);

CREATE TABLE entities
(
  entity_type VARCHAR(1000),
  entity_id VARCHAR(1000),
  entity_version VARCHAR(1000) NOT NULL,
  PRIMARY KEY(entity_type, entity_id)
);
CREATE INDEX entities_idx ON events(entity_type, entity_id);

create table snapshots
(
  entity_type VARCHAR(1000),
  entity_id VARCHAR(1000),
  entity_version VARCHAR(1000),
  snapshot_type VARCHAR(1000) NOT NULL,
  snapshot_json VARCHAR(1000) NOT NULL,
  triggering_events VARCHAR(1000),
  PRIMARY KEY(entity_type, entity_id, entity_version)
);

create table cdc_monitoring
(
  reader_id VARCHAR(1000) PRIMARY KEY,
  last_time BIGINT
);

-- SQLINES DEMO *** ed tables
DROP Table IF Exists message;
DROP Table IF Exists received_messages;
DROP Table IF Exists offset_store;

CREATE TABLE message
(
  id VARCHAR(767) PRIMARY KEY,
  destination VARCHAR(1000) NOT NULL,
  headers VARCHAR(3000) NOT NULL,
  payload VARCHAR(10000) NOT NULL,
  published SMALLINT DEFAULT 0,
  creation_time BIGINT
);
CREATE INDEX message_published_idx ON message(published, id);

CREATE TABLE received_messages
(
  consumer_id VARCHAR(1000),
  message_id VARCHAR(1000),
  creation_time BIGINT,
  PRIMARY KEY(consumer_id, message_id)
);

CREATE TABLE offset_store
(
  client_name VARCHAR(255) NOT NULL PRIMARY KEY,
  serialized_offset VARCHAR(255)
);

-- SQLINES DEMO *** tables
DROP Table IF Exists saga_instance_participants;
DROP Table IF Exists saga_instance;
DROP Table IF Exists saga_lock_table;
DROP Table IF Exists saga_stash_table;

CREATE TABLE saga_instance_participants
(
  saga_type VARCHAR(255) NOT NULL,
  saga_id VARCHAR(100) NOT NULL,
  destination VARCHAR(100) NOT NULL,
  resource VARCHAR(100) NOT NULL,
  PRIMARY KEY(saga_type, saga_id, destination, resource)
);

CREATE TABLE saga_instance
(
  saga_type VARCHAR(255) NOT NULL,
  saga_id VARCHAR(100) NOT NULL,
  state_name VARCHAR(100) NOT NULL,
  last_request_id VARCHAR(100),
  end_state BOOLEAN,
  compensating BOOLEAN,
  saga_data_type VARCHAR(255) NOT NULL,
  saga_data_json VARCHAR(10000) NOT NULL,
  PRIMARY KEY(saga_type, saga_id)
);

CREATE TABLE saga_lock_table
(
  target VARCHAR(100) PRIMARY KEY,
  saga_type VARCHAR(255) NOT NULL,
  saga_Id VARCHAR(100) NOT NULL
);

CREATE TABLE saga_stash_table
(
  message_id VARCHAR(100) PRIMARY KEY,
  target VARCHAR(100) NOT NULL,
  saga_type VARCHAR(255) NOT NULL,
  saga_id VARCHAR(100) NOT NULL,
  message_headers VARCHAR(3000) NOT NULL,
  message_payload VARCHAR(10000) NOT NULL
);