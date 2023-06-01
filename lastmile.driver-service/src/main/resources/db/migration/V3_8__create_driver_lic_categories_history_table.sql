CREATE TABLE IF NOT EXISTS driver_license_categories_history (
    id serial,
    driver_license_category_identification varchar(255),
    driver_license_identification varchar(255),
    category varchar(255),
    category_issue_date date,
    category_expiry_date date,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);

-- create trigger function
CREATE OR REPLACE FUNCTION driver_license_categories_history_trigger_function() RETURNS trigger AS $driver_license_categories_history$

    BEGIN
        -- if the operation is a delete
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO driver_license_categories_history SELECT 'DELETE', OLD.*;
        -- if the operation is an update
        ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO driver_license_categories_history SELECT 'UPDATE', NEW.*;
        -- if the operation is an insert
        ELSIF (TG_OP = 'INSERT') THEN
            INSERT INTO driver_license_categories_history SELECT 'INSERT', NEW.*;
        END IF;
        RETURN NULL;
    END;

$driver_license_categories_history$ 
LANGUAGE plpgsql;

-- create trigger
CREATE TRIGGER driver_license_categories_history_trigger
    AFTER INSERT OR UPDATE OR DELETE ON driver_license_categories
        FOR EACH ROW EXECUTE PROCEDURE driver_license_categories_history_trigger_function();