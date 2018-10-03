create table replication_event (
  replication_event_id serial primary key,
  event_type varchar(255),
  object_class varchar(255),
  payload text,
  source varchar(255),
  timestamp timestamp
);