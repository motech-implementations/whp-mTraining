CREATE TABLE mtraining.call_log (
    id     bigint,
    caller_id bigint,
    unique_id    varchar(50),
    session_id    varchar(50),
    response_code    integer ,
    response_message    varchar(50),
    bookmark    text ,
    PRIMARY KEY(id)
);