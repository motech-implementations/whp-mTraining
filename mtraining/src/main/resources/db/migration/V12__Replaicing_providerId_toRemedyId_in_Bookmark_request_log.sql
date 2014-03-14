ALTER TABLE mtraining.bookmark_request_log DROP COLUMN provider_id;
ALTER TABLE mtraining.bookmark_request_log ADD COLUMN provider_remedy_id varchar(25)