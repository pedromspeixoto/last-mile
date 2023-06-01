-- create table
CREATE TABLE IF NOT EXISTS addresses_history (
    action varchar(20),
    id serial,
    address_identification varchar(255),
    entity_identification varchar(255),
    entity_type varchar(255),
    address_type varchar(255),
    address_line_1 varchar(255),
    address_line_2 varchar(255),
    street_number varchar(255),
    floor varchar(255),
    zip_code varchar(255),
    city varchar(255),
    country varchar(255),
    address_notes varchar(255),
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);

-- create trigger function
CREATE OR REPLACE FUNCTION addresses_history_trigger_function() RETURNS trigger AS $addresses_history$

    BEGIN
        -- if the operation is a delete
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO addresses_history SELECT 'DELETE', OLD.*;
        -- if the operation is an update
        ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO addresses_history SELECT 'UPDATE', NEW.*;
        -- if the operation is an insert
        ELSIF (TG_OP = 'INSERT') THEN
            INSERT INTO addresses_history SELECT 'INSERT', NEW.*;
        END IF;
        RETURN NULL;
    END;

$addresses_history$ 
LANGUAGE plpgsql;

-- create trigger
CREATE TRIGGER addresses_history_trigger
    AFTER INSERT OR UPDATE OR DELETE ON addresses
        FOR EACH ROW EXECUTE PROCEDURE addresses_history_trigger_function();