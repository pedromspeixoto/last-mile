CREATE TABLE IF NOT EXISTS pricing_custom_configuration (
    action varchar(20),
    id serial,
    entity_identification varchar(255),
    entity_type varchar(255),
    execution_order integer,
    fee_name varchar(255),
    fee_type varchar(255),
    fee_value numeric,
    fee_cap numeric,
    reference_column varchar(255),
    reference_column_value varchar(255),
    approved boolean not null,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);
-- create trigger function
CREATE OR REPLACE FUNCTION pricing_custom_configuration_trigger_function() RETURNS trigger AS $pricing_custom_configuration$

    BEGIN
        -- if the operation is a delete
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO pricing_custom_configuration SELECT 'DELETE', OLD.*;
        -- if the operation is an update
        ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO pricing_custom_configuration SELECT 'UPDATE', NEW.*;
        -- if the operation is an insert
        ELSIF (TG_OP = 'INSERT') THEN
            INSERT INTO pricing_custom_configuration SELECT 'INSERT', NEW.*;
        END IF;
        RETURN NULL;
    END;

$pricing_custom_configuration$ 
LANGUAGE plpgsql;

-- create trigger
CREATE TRIGGER pricing_custom_configuration
    AFTER INSERT OR UPDATE OR DELETE ON pricing_custom_configuration
        FOR EACH ROW EXECUTE PROCEDURE pricing_custom_configuration_trigger_function();