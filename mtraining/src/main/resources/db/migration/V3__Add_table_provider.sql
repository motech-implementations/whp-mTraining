CREATE TABLE mtraining.provider (
    id     bigint,
    location_id bigint references mtraining.location (id),
    primary_contact_number bigint,
    first_name    varchar(40),
    last_name    varchar(40),
    PRIMARY KEY(id)
);