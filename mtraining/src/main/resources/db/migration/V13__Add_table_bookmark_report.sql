CREATE TABLE mtraining.bookmark_report (
    id     bigint,
    provider_remedy_id varchar(25),
    course_id varchar(40),
    course_version bigint,
    module_id varchar(40),
    module_version bigint,
    chapter_id varchar(40),
    chapter_version bigint,
    message_id varchar(40),
    message_version bigint,
    date_modified timestamp,
    PRIMARY KEY(id)
);