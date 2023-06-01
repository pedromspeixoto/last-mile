CREATE TABLE IF NOT EXISTS pricing_base_configuration (
    id serial PRIMARY KEY,
    execution_order integer not null,
    fee_name varchar(255) not null,
    fee_type varchar(255) not null,
    fee_value numeric,
    fee_cap numeric,
    reference_column varchar(255),
    reference_column_value varchar(255),
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);