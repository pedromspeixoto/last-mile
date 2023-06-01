-- create table
CREATE TABLE IF NOT EXISTS payment_details_history (
    action varchar(20),
    id serial,
    payment_detail_identification varchar(255),
    payment_detail_type varchar(255),
    entity_identification varchar(255),
    entity_type varchar(255),
    payment_email varchar(255),
    payment_fiscal_number varchar(255),
    payment_phone_number varchar(255),
    card_last_four_digits varchar(255),
    card_type varchar(255),
    card_expiry_date varchar(255),
    payment_token varchar(255),
    external_entity varchar(255),
    external_entity_identification varchar(255),
    status varchar(255),
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);

-- create trigger function
CREATE OR REPLACE FUNCTION payment_details_history_trigger_function() RETURNS trigger AS $payment_details_history$

    BEGIN
        -- if the operation is a delete
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO payment_details_history SELECT 'DELETE', OLD.*;
        -- if the operation is an update
        ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO payment_details_history SELECT 'UPDATE', NEW.*;
        -- if the operation is an insert
        ELSIF (TG_OP = 'INSERT') THEN
            INSERT INTO payment_details_history SELECT 'INSERT', NEW.*;
        END IF;
        RETURN NULL;
    END;

$payment_details_history$ 
LANGUAGE plpgsql;

-- create trigger
CREATE TRIGGER payment_details_history_trigger
    AFTER INSERT OR UPDATE OR DELETE ON payment_details
        FOR EACH ROW EXECUTE PROCEDURE payment_details_history_trigger_function();