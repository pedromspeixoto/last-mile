CREATE TABLE IF NOT EXISTS document_driver_licenses_history (
    action varchar(20),
    id serial,
    driver_license_identification varchar(255),
    driver_identification varchar(255),
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
    last_modified_date timestamp
);

-- create trigger function
CREATE OR REPLACE FUNCTION document_driver_licenses_history_trigger_function() RETURNS trigger AS $document_driver_licenses_history$

    BEGIN
        -- if the operation is a delete
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO document_driver_licenses_history SELECT 'DELETE', OLD.*;
        -- if the operation is an update
        ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO document_driver_licenses_history SELECT 'UPDATE', NEW.*;
        -- if the operation is an insert
        ELSIF (TG_OP = 'INSERT') THEN
            INSERT INTO document_driver_licenses_history SELECT 'INSERT', NEW.*;
        END IF;
        RETURN NULL;
    END;

$document_driver_licenses_history$ 
LANGUAGE plpgsql;

-- create trigger
CREATE TRIGGER document_driver_licenses_history_trigger
    AFTER INSERT OR UPDATE OR DELETE ON document_driver_licenses
        FOR EACH ROW EXECUTE PROCEDURE document_driver_licenses_history_trigger_function();