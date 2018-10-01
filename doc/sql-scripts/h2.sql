create table replication_event (
  id bigint generated by default as identity,
  event_type varchar(255),
  object_class varchar(255),
  payload clob,
  source varchar(255),
  timestamp timestamp,
  primary key(id)
);