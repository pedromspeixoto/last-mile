CREATE TABLE IF NOT EXISTS account_devices (
    id serial PRIMARY KEY,
    user_identification varchar(255) not null,
    external_entity varchar(255) not null,
    external_entity_token varchar(255) not null,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp,
    UNIQUE(user_identification)
);