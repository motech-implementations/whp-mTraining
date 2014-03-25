create table mtraining.message(
    id bigint,
    name varchar(50),
    content_Id varchar(50),
    version integer,
    message_order integer,
    description text,
    modified_by varchar(20),
    date_modified timestamp,
    audio_file_name varchar(50),
    chapter_id bigint references mtraining.chapter (id),
    PRIMARY KEY(id)
);