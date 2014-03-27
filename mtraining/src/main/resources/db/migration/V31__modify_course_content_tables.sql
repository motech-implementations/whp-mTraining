ALTER TABLE mtraining.course add column is_active boolean;
ALTER TABLE mtraining.module add column is_active boolean;
ALTER TABLE mtraining.chapter add column is_active boolean;
ALTER TABLE mtraining.message add column is_active boolean;
ALTER TABLE mtraining.quiz add column is_active boolean;
ALTER TABLE mtraining.question add column is_active boolean;

ALTER TABLE mtraining.course RENAME COLUMN date_modified TO created_on;
ALTER TABLE mtraining.course RENAME COLUMN modified_by TO created_by;
ALTER TABLE mtraining.module RENAME COLUMN date_modified TO created_on;
ALTER TABLE mtraining.module RENAME COLUMN modified_by TO created_by;
ALTER TABLE mtraining.chapter RENAME COLUMN date_modified TO created_on;
ALTER TABLE mtraining.chapter RENAME COLUMN modified_by TO created_by;
ALTER TABLE mtraining.message RENAME COLUMN date_modified TO created_on;
ALTER TABLE mtraining.message RENAME COLUMN modified_by TO created_by;
ALTER TABLE mtraining.quiz RENAME COLUMN date_modified TO created_on;
ALTER TABLE mtraining.quiz RENAME COLUMN modified_by TO created_by;
ALTER TABLE mtraining.question RENAME COLUMN date_modified TO created_on;
ALTER TABLE mtraining.question RENAME COLUMN modified_by TO created_by;

ALTER TABLE mtraining.quiz drop column description;

