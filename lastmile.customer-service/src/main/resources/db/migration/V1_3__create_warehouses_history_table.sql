CREATE TABLE IF NOT EXISTS warehouses_history (
    action varchar(20),
    id serial,
    warehouse_identification varchar(255),
    customer_identification varchar(255),
    name varchar(255),
    description varchar(255),
    address_id varchar(255),
    latitude float,
    longitude float,
    status varchar(255),
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);

-- create trigger function
CREATE OR REPLACE FUNCTION warehouses_history_trigger_function() RETURNS trigger AS $warehouses_history$

    BEGIN
        -- if the operation is a delete
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO warehouses_history SELECT 'DELETE', OLD.*;
        -- if the operation is an update
        ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO warehouses_history SELECT 'UPDATE', NEW.*;
        -- if the operation is an insert
        ELSIF (TG_OP = 'INSERT') THEN
            INSERT INTO warehouses_history SELECT 'INSERT', NEW.*;
        END IF;
        RETURN NULL;
    END;

$warehouses_history$ 
LANGUAGE plpgsql;

-- create trigger
CREATE TRIGGER warehouses_history_trigger
    AFTER INSERT OR UPDATE OR DELETE ON warehouses
        FOR EACH ROW EXECUTE PROCEDURE warehouses_history_trigger_function();