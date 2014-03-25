create table mtraining.quiz(
    id bigint,
    name varchar(50),
    content_Id varchar(50),
    version integer,
    pass_percentage  numeric ,
    number_of_questions integer ,
    description text,
    modified_by varchar(20),
    date_modified timestamp with time zone ,
    PRIMARY KEY(id)
);