CREATE TABLE IF NOT EXISTS drivers_vehicles_history (
    action varchar(20),
    id serial,
    vehicle_identification varchar(255),
    driver_identification varchar(255),
    make varchar(255),
    model varchar(255),
    category varchar(255),
    year int,
    vehicle_type varchar(255),
    license_plate varchar(255),
    vehicle_active boolean,
    entity_validated boolean,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);

-- create trigger function
CREATE OR REPLACE FUNCTION drivers_vehicles_history_trigger_function() RETURNS trigger AS $drivers_vehicles_history$

    BEGIN
        -- if the operation is a delete
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO drivers_vehicles_history SELECT 'DELETE', OLD.*;
        -- if the operation is an update
        ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO drivers_vehicles_history SELECT 'UPDATE', NEW.*;
        -- if the operation is an insert
        ELSIF (TG_OP = 'INSERT') THEN
            INSERT INTO drivers_vehicles_history SELECT 'INSERT', NEW.*;
        END IF;
        RETURN NULL;
    END;

$drivers_vehicles_history$ 
LANGUAGE plpgsql;

-- create trigger
CREATE TRIGGER drivers_vehicles_history_trigger
    AFTER INSERT OR UPDATE OR DELETE ON drivers_vehicles
        FOR EACH ROW EXECUTE PROCEDURE drivers_vehicles_history_trigger_function();