CREATE TABLE IF NOT EXISTS drivers_history (
    action varchar(20),
    id serial,
    user_identification varchar(255),
    driver_identification varchar(255),
    fiscal_entity_identification varchar(255),
    latitude double precision,
    longitude double precision,
    driver_rating int,
    status varchar(255),
    entity_validated boolean,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);


-- create trigger function
CREATE OR REPLACE FUNCTION drivers_history_trigger_function() RETURNS trigger AS $drivers_history$

    BEGIN
        -- if the operation is a delete
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO drivers_history SELECT 'DELETE', OLD.*;
        -- if the operation is an update
        ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO drivers_history SELECT 'UPDATE', NEW.*;
        -- if the operation is an insert
        ELSIF (TG_OP = 'INSERT') THEN
            INSERT INTO drivers_history SELECT 'INSERT', NEW.*;
        END IF;
        RETURN NULL;
    END;

$drivers_history$ 
LANGUAGE plpgsql;

-- create trigger
CREATE TRIGGER drivers_history_trigger
    AFTER INSERT OR UPDATE OR DELETE ON drivers
        FOR EACH ROW EXECUTE PROCEDURE drivers_history_trigger_function();