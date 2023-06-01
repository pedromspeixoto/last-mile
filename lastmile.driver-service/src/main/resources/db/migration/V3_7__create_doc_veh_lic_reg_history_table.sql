CREATE TABLE IF NOT EXISTS document_vehicle_license_registrations_history (
    action varchar(20),
    id serial,
    vehicle_license_reg_identification varchar(255),
    vehicle_identification varchar(255),
    document_front_file_id varchar(255),
    document_back_file_id varchar(255),
    validated boolean,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);

-- create trigger function
CREATE OR REPLACE FUNCTION document_vehicle_license_registrations_history_trigger_function() RETURNS trigger AS $document_vehicle_license_registrations_history$

    BEGIN
        -- if the operation is a delete
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO document_vehicle_license_registrations_history SELECT 'DELETE', OLD.*;
        -- if the operation is an update
        ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO document_vehicle_license_registrations_history SELECT 'UPDATE', NEW.*;
        -- if the operation is an insert
        ELSIF (TG_OP = 'INSERT') THEN
            INSERT INTO document_vehicle_license_registrations_history SELECT 'INSERT', NEW.*;
        END IF;
        RETURN NULL;
    END;

$document_vehicle_license_registrations_history$ 
LANGUAGE plpgsql;

-- create trigger
CREATE TRIGGER document_vehicle_license_registrations_history_trigger
    AFTER INSERT OR UPDATE OR DELETE ON document_vehicle_license_registrations
        FOR EACH ROW EXECUTE PROCEDURE document_vehicle_license_registrations_history_trigger_function();