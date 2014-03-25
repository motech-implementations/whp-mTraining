ALTER TABLE mtraining.bookmark_request ALTER COLUMN created_on TYPE TIMESTAMP WITH TIME ZONE;
ALTER TABLE mtraining.bookmark_request ALTER COLUMN bookmark_modified_on TYPE TIMESTAMP WITH TIME ZONE;
ALTER TABLE mtraining.course ALTER COLUMN publish_attempted_on TYPE TIMESTAMP WITH TIME ZONE;
ALTER TABLE mtraining.certification_course ALTER COLUMN date_modified TYPE TIMESTAMP WITH TIME ZONE;
ALTER TABLE mtraining.module ALTER COLUMN date_modified TYPE TIMESTAMP WITH TIME ZONE;
ALTER TABLE mtraining.chapter ALTER COLUMN date_modified TYPE TIMESTAMP WITH TIME ZONE;
ALTER TABLE mtraining.message ALTER COLUMN date_modified TYPE TIMESTAMP WITH TIME ZONE;