CREATE TABLE IF NOT EXISTS pricing_custom_configuration (
    id serial PRIMARY KEY,
    entity_identification varchar(255) not null,
    entity_type varchar(255) not null,
    execution_order integer not null,
    fee_name varchar(255) not null,
    fee_type varchar(255) not null,
    fee_value numeric not null,
    fee_cap numeric not null,
    reference_column varchar(255),
    reference_column_value varchar(255),
    approved boolean not null,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);