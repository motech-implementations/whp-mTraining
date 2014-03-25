create table mtraining.module(
    id bigint,
    name varchar(50),
    content_Id varchar(50),
    version integer,
    module_order integer,
    description text,
    modified_by varchar(20),
    date_modified timestamp,
    course_id bigint references mtraining.certification_course (id),
    PRIMARY KEY(id)
);