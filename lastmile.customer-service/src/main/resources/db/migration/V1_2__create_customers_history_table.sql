CREATE TABLE IF NOT EXISTS customers_history (
    action varchar(20),
    id serial,
    customer_identification varchar(255),
    name varchar(255),
    public_name varchar(255),
    customer_email varchar(255),
    customer_phone_number varchar(255),
    customer_website varchar(255),
    nif varchar(255),
    active_address_id varchar(255),
    active_billing_address_id varchar(255),
    active_payment_details_id varchar(255),
    api_key varchar(255),
    private_key varchar(255),
    customer_callback_url varchar(255),
    status varchar(255),
    entity_validated boolean,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);

-- create trigger function
CREATE OR REPLACE FUNCTION customers_history_trigger_function() RETURNS trigger AS $customers_history$

    BEGIN
        -- if the operation is a delete
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO customers_history SELECT 'DELETE', OLD.*;
        -- if the operation is an update
        ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO customers_history SELECT 'UPDATE', NEW.*;
        -- if the operation is an insert
        ELSIF (TG_OP = 'INSERT') THEN
            INSERT INTO customers_history SELECT 'INSERT', NEW.*;
        END IF;
        RETURN NULL;
    END;

$customers_history$ 
LANGUAGE plpgsql;

-- create trigger
CREATE TRIGGER customers_history_trigger
    AFTER INSERT OR UPDATE OR DELETE ON customers
        FOR EACH ROW EXECUTE PROCEDURE customers_history_trigger_function();