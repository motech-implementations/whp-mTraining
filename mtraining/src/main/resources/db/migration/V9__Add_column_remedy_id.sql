TRUNCATE TABLE mtraining.provider;
ALTER TABLE mtraining.provider ADD remedy_id varchar(25) NOT NULL UNIQUE;