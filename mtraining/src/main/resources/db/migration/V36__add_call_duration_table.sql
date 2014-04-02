CREATE TABLE mtraining.call_duration(
 id bigint,
 provider_remedy_id varchar(40),
 caller_id bigint,
 unique_id varchar(40),
 session_id varchar(40),
 call_start_time TIMESTAMP WITH TIME ZONE,
 call_end_time TIMESTAMP WITH TIME ZONE,
 PRIMARY KEY(id)
 );