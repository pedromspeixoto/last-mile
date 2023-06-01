-- create table
CREATE TABLE IF NOT EXISTS invoices_history (
    action varchar(20),
    id serial,
    invoice_identification varchar(255),
    payment_identification varchar(255),
    entity_identification varchar(255),
    entity_type varchar(255),
    invoice_client_name varchar(255),
    invoice_fiscal_number varchar(255),
    invoice_full_address varchar(255),
    invoice_zip_code varchar(255),
    invoice_url varchar(255),
    external_entity varchar(255),
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);

-- create trigger function
CREATE OR REPLACE FUNCTION invoices_history_trigger_function() RETURNS trigger AS $invoices_history$

    BEGIN
        -- if the operation is a delete
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO invoices_history SELECT 'DELETE', OLD.*;
        -- if the operation is an update
        ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO invoices_history SELECT 'UPDATE', NEW.*;
        -- if the operation is an insert
        ELSIF (TG_OP = 'INSERT') THEN
            INSERT INTO invoices_history SELECT 'INSERT', NEW.*;
        END IF;
        RETURN NULL;
    END;

$invoices_history$ 
LANGUAGE plpgsql;

-- create trigger
CREATE TRIGGER invoices_history_trigger
    AFTER INSERT OR UPDATE OR DELETE ON invoices
        FOR EACH ROW EXECUTE PROCEDURE invoices_history_trigger_function();