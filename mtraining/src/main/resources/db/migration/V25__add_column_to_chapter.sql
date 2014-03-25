ALTER TABLE mtraining.chapter ADD COLUMN quiz_id BIGINT REFERENCES mtraining.quiz (id);
