create table mtraining.question(
    id bigint,
    name varchar(50),
    content_Id varchar(50),
    version integer,
    description text,
    modified_by varchar(20),
    date_modified timestamp with time zone ,
    question_audio_file_name varchar(50),
    answer_audio_file_name varchar(50),
    valid_answer_options varchar(50),
    correct_option varchar(50),
    quiz_id bigint references mtraining.quiz (id),
    question_order integer ,
    PRIMARY KEY(id)
);