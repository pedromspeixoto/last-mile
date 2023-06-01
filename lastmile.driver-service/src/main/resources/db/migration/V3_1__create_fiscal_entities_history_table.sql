CREATE TABLE IF NOT EXISTS fiscal_entities_history (
    action varchar(20),
    id serial,
    fiscal_entity_identification varchar(255),
    name varchar(255),
    email varchar(255),
    phone_number varchar(255),
    fiscal_number varchar(255),
    bank_account_holder_name varchar(255),
    bank_account_iban varchar(255),
    bank_account_country_code varchar(255),
    payment_frequency varchar(255),
    active_address_id varchar(255),
    active_billing_address_id varchar(255),
    status varchar(255),
    entity_validated boolean,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);

-- create trigger function
CREATE OR REPLACE FUNCTION fiscal_entities_history_trigger_function() RETURNS trigger AS $fiscal_entities_history$

    BEGIN
        -- if the operation is a delete
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO fiscal_entities_history SELECT 'DELETE', OLD.*;
        -- if the operation is an update
        ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO fiscal_entities_history SELECT 'UPDATE', NEW.*;
        -- if the operation is an insert
        ELSIF (TG_OP = 'INSERT') THEN
            INSERT INTO fiscal_entities_history SELECT 'INSERT', NEW.*;
        END IF;
        RETURN NULL;
    END;

$fiscal_entities_history$ 
LANGUAGE plpgsql;

-- create trigger
CREATE TRIGGER fiscal_entities_history_trigger
    AFTER INSERT OR UPDATE OR DELETE ON fiscal_entities
        FOR EACH ROW EXECUTE PROCEDURE fiscal_entities_history_trigger_function();