ALTER TABLE mtraining.provider DROP COLUMN location_id, DROP COLUMN first_name, DROP COLUMN last_name;
DROP  TABLE mtraining.location;
ALTER TABLE mtraining.provider ADD COLUMN district varchar(20), ADD COLUMN block varchar(20), ADD COLUMN state varchar(20);
