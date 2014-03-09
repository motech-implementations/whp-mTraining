CREATE TABLE mtraining.course (
    id     bigint,
    course_id varchar (50),
    version    integer ,
    published_to_ivr boolean ,
    publish_attempted_on timestamp ,
    PRIMARY KEY(id)
);