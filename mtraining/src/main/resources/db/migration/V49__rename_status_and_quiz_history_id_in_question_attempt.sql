ALTER TABLE mtraining.question_attempt RENAME COLUMN status to valid_answer;
ALTER TABLE mtraining.question_attempt RENAME COLUMN quiz_history_id to quiz_attempt_id;