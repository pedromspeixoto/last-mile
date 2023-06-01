CREATE TABLE IF NOT EXISTS document_criminal_records (
    id serial PRIMARY KEY,
    criminal_record_identification varchar(255) not null,
    driver_identification varchar(255) not null REFERENCES drivers(driver_identification),
    document_front_file_id varchar(255),
    document_back_file_id varchar(255),
    validated boolean,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp,
    UNIQUE(criminal_record_identification)
);