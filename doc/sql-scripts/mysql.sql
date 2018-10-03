create table replication_event (
  replication_event_id bigint not null auto_increment,
  event_type varchar(255),
  object_class varchar(255),
  payload longtext,
  source varchar(255),
  timestamp timestamp,
  primary key(id)
);