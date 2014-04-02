CREATE TABLE mtraining.quiz_history (
    id bigint,
    provider_remedy_id varchar(25),
    session_id varchar(40),
    unique_id varchar(40),
    caller_id varchar(40),
    quiz_id varchar(40),
    quiz_version bigint,
    start_time timestamp with time zone ,
    end_time timestamp with time zone ,
    status boolean,
    score numeric,
    PRIMARY KEY(id)
);