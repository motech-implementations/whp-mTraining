ALTER TABLE mtraining.call_log RENAME COLUMN node_id TO content_id;
ALTER TABLE mtraining.call_log RENAME COLUMN node_version TO content_version;
ALTER TABLE mtraining.call_log RENAME COLUMN node_type TO content_type;