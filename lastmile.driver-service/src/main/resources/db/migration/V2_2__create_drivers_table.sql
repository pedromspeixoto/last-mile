CREATE TABLE IF NOT EXISTS drivers (
    id serial PRIMARY KEY,
    user_identification varchar(255) not null,
    driver_identification varchar(255) not null,
    fiscal_entity_identification varchar(255) REFERENCES fiscal_entities(fiscal_entity_identification),
    latitude double precision,
    longitude double precision,
    driver_rating int,
    status varchar(255) not null,
    entity_validated boolean not null,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp,
    UNIQUE(driver_identification)
);