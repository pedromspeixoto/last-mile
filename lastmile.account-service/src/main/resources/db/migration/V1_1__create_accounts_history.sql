-- create table
CREATE TABLE IF NOT EXISTS accounts_history (
    action varchar(20),
    id serial,
    user_identification varchar(255),
    account_type varchar(255),
    username varchar(255),
    email varchar(255),
    phone_number varchar(255),
    first_name varchar(255),
    last_name varchar(255),
    birth_date date,
    active_address_id varchar(255),
    active_billing_address_id varchar(255),
    active_payment_details_id varchar(255),
    profile_picture varchar(255),
    role varchar(255),
    is_active boolean,
    activation_code varchar(4),
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);

-- create trigger function
CREATE OR REPLACE FUNCTION accounts_history_trigger_function() RETURNS trigger AS $accounts_history$

    BEGIN
        -- if the operation is a delete
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO accounts_history SELECT 'DELETE', OLD.*;
        -- if the operation is an update
        ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO accounts_history SELECT 'UPDATE', NEW.*;
        -- if the operation is an insert
        ELSIF (TG_OP = 'INSERT') THEN
            INSERT INTO accounts_history SELECT 'INSERT', NEW.*;
        END IF;
        RETURN NULL;
    END;

$accounts_history$ 
LANGUAGE plpgsql;

-- create trigger
CREATE TRIGGER accounts_history_trigger
    AFTER INSERT OR UPDATE OR DELETE ON accounts
        FOR EACH ROW EXECUTE PROCEDURE accounts_history_trigger_function();