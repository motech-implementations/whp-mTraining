CREATE TABLE mtraining.call_log(
id bigint,
provider_remedy_id varchar(40),
caller_id bigint,
unique_id varchar(40),
session_id varchar(40),
node_id varchar(40),
node_version INTEGER ,
node_type varchar(20),
start_time TIMESTAMP WITH TIME ZONE,
end_time TIMESTAMP WITH TIME ZONE,
status varchar(10),
  PRIMARY KEY(id)
)
