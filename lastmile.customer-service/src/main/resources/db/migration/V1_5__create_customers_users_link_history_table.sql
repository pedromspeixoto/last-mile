CREATE TABLE IF NOT EXISTS customers_users_link_history (
    action varchar(20),
    id serial,
    customer_identification varchar(255),
    user_identification varchar(255),
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);

-- create trigger function
CREATE OR REPLACE FUNCTION customers_users_link_history_trigger_function() RETURNS trigger AS $customers_users_link_history$

    BEGIN
        -- if the operation is a delete
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO customers_users_link_history SELECT 'DELETE', OLD.*;
        -- if the operation is an update
        ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO customers_users_link_history SELECT 'UPDATE', NEW.*;
        -- if the operation is an insert
        ELSIF (TG_OP = 'INSERT') THEN
            INSERT INTO customers_users_link_history SELECT 'INSERT', NEW.*;
        END IF;
        RETURN NULL;
    END;

$customers_users_link_history$ 
LANGUAGE plpgsql;

-- create trigger
CREATE TRIGGER customers_users_link_history_trigger
    AFTER INSERT OR UPDATE OR DELETE ON customers_users_link
        FOR EACH ROW EXECUTE PROCEDURE customers_users_link_history_trigger_function();