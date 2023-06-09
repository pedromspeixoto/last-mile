CREATE TABLE IF NOT EXISTS document_driver_licenses (
    id serial PRIMARY KEY,
    driver_license_identification varchar(255) not null,
    driver_identification varchar(255) not null REFERENCES drivers(driver_identification),
    name varchar(255),
    surname varchar(255),
    birth_date date,
    issue_date date,
    expiry_date date,
    issuing_authority varchar(255),
    personal_number varchar(255),
    license_number varchar(255),
    license_address varchar(255),
    license_categories varchar(255),
    document_front_file_id varchar(255),
    document_back_file_id varchar(255),
    validated boolean,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp,
    UNIQUE(driver_license_identification)
);