ALTER TABLE mtraining.certification_course RENAME TO course;

ALTER TABLE mtraining.module DROP CONSTRAINT module_course_id_fkey;
ALTER TABLE mtraining.module ADD CONSTRAINT module_course_id_fkey FOREIGN KEY (course_id) REFERENCES mtraining.course (id);