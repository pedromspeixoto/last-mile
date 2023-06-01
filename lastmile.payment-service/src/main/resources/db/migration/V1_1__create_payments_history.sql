-- create table
CREATE TABLE IF NOT EXISTS payments_history (
    action varchar(20),
    id serial,
    payment_identification varchar(255),
    requester_entity_identification varchar(255),
    requester_entity_type varchar(255),
    transaction_identification varchar(255),
    payment_details_id varchar(255),
    payment_value numeric,
    payment_type varchar(255),
    external_payment_identification varchar(255),
    status varchar(255),
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);

-- create trigger function
CREATE OR REPLACE FUNCTION payments_history_trigger_function() RETURNS trigger AS $payments_history$

    BEGIN
        -- if the operation is a delete
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO payments_history SELECT 'DELETE', OLD.*;
        -- if the operation is an update
        ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO payments_history SELECT 'UPDATE', NEW.*;
        -- if the operation is an insert
        ELSIF (TG_OP = 'INSERT') THEN
            INSERT INTO payments_history SELECT 'INSERT', NEW.*;
        END IF;
        RETURN NULL;
    END;

$payments_history$ 
LANGUAGE plpgsql;

-- create trigger
CREATE TRIGGER payments_history_trigger
    AFTER INSERT OR UPDATE OR DELETE ON payments
        FOR EACH ROW EXECUTE PROCEDURE payments_history_trigger_function();