ALTER TABLE mtraining.quiz_attempt RENAME COLUMN status to passed;
ALTER TABLE mtraining.quiz_attempt add COLUMN course_id varchar(40);
ALTER TABLE mtraining.quiz_attempt add COLUMN course_version bigint;
ALTER TABLE mtraining.quiz_attempt add COLUMN module_id varchar(40);
ALTER TABLE mtraining.quiz_attempt add COLUMN module_version bigint;
ALTER TABLE mtraining.quiz_attempt add COLUMN chapter_id varchar(40);
ALTER TABLE mtraining.quiz_attempt add COLUMN chapter_version bigint;