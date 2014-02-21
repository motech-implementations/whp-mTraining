CREATE TABLE mtraining.call_log (
    id     bigint,
    caller_id bigint,
    unique_id    varchar(40),
    session_id    varchar(40),
    error_code    varchar(20),
    bookmark    text ,
    PRIMARY KEY(id)
);