create table mtraining.chapter(
    id bigint,
    name varchar(50),
    content_Id varchar(50),
    version integer,
    chapter_order integer,
    description text,
    modified_by varchar(20),
    date_modified timestamp,
    module_id bigint references mtraining.module (id),
    PRIMARY KEY(id)
);