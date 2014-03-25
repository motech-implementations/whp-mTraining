create table mtraining.certification_course(
    id bigint,
    name varchar(50),
    content_Id varchar(50),
    version integer,
    description text,
    modified_by varchar(20),
    date_modified timestamp,
    PRIMARY KEY(id)
);