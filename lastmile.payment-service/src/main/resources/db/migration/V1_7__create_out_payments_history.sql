-- create table
CREATE TABLE IF NOT EXISTS out_payments_history (
    action varchar(20),
    id serial,
    out_payment_identification varchar(255),
    source_account_identification varchar(255),
    transaction_identification varchar(255),
    requester_identification varchar(255),
    requester_entity_type varchar(255),
    requester_account_holder_name varchar(255),
    requester_iban varchar(255),
    requester_email varchar(255),
    requester_phone_number varchar(255),
    requester_bank_account_country_code varchar(255),
    payment_value numeric,
    payment_type varchar(255),
    payment_scheduled_date timestamp,
    external_payment_identification varchar(255),
    status varchar(255),
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);

-- create trigger function
CREATE OR REPLACE FUNCTION out_payments_history_trigger_function() RETURNS trigger AS $out_payments_history$

    BEGIN
        -- if the operation is a delete
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO out_payments_history SELECT 'DELETE', OLD.*;
        -- if the operation is an update
        ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO out_payments_history SELECT 'UPDATE', NEW.*;
        -- if the operation is an insert
        ELSIF (TG_OP = 'INSERT') THEN
            INSERT INTO out_payments_history SELECT 'INSERT', NEW.*;
        END IF;
        RETURN NULL;
    END;

$out_payments_history$ 
LANGUAGE plpgsql;

-- create trigger
CREATE TRIGGER out_payments_history_trigger
    AFTER INSERT OR UPDATE OR DELETE ON out_payments
        FOR EACH ROW EXECUTE PROCEDURE out_payments_history_trigger_function();