CREATE TABLE mtraining.question_history (
    id bigint,
    quiz_history_id bigint references mtraining.quiz_history (id),
    question_id varchar (40),
    question_version bigint,
    invalid_inputs varchar(40),
    selected_option varchar(40),
    status boolean,
    PRIMARY KEY(id)
);