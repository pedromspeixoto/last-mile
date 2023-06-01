-- create table
CREATE TABLE IF NOT EXISTS account_devices_history (
    action varchar(20),
    id serial,
    user_identification varchar(255),
    external_entity varchar(255),
    external_entity_token varchar(255),
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);

-- create trigger function
CREATE OR REPLACE FUNCTION account_devices_history_trigger_function() RETURNS trigger AS $account_devices_history$

    BEGIN
        -- if the operation is a delete
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO account_devices_history SELECT 'DELETE', OLD.*;
        -- if the operation is an update
        ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO account_devices_history SELECT 'UPDATE', NEW.*;
        -- if the operation is an insert
        ELSIF (TG_OP = 'INSERT') THEN
            INSERT INTO account_devices_history SELECT 'INSERT', NEW.*;
        END IF;
        RETURN NULL;
    END;

$account_devices_history$ 
LANGUAGE plpgsql;

-- create trigger
CREATE TRIGGER account_devices_history_trigger
    AFTER INSERT OR UPDATE OR DELETE ON account_devices
        FOR EACH ROW EXECUTE PROCEDURE account_devices_history_trigger_function();