CREATE TABLE IF NOT EXISTS document_identification_cards (
    id serial PRIMARY KEY,
    identification_card_identification varchar(255) not null,
    driver_identification varchar(255) not null REFERENCES drivers(driver_identification),
    name varchar(255),
    surname varchar(255),
    birth_date date,
    document_number varchar(255),
    document_expiry_date date,
    document_front_file_id varchar(255),
    document_back_file_id varchar(255),
    validated boolean,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp,
    UNIQUE(identification_card_identification)
);