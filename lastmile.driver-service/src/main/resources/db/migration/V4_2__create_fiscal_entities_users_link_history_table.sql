CREATE TABLE IF NOT EXISTS fiscal_entities_users_link_history (
    action varchar(20),
    id serial,
    fiscal_entity_identification varchar(255),
    user_identification varchar(255),
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);

-- create trigger function
CREATE OR REPLACE FUNCTION fiscal_entities_users_link_history_trigger_function() RETURNS trigger AS $fiscal_entities_users_link_history$

    BEGIN
        -- if the operation is a delete
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO fiscal_entities_users_link_history SELECT 'DELETE', OLD.*;
        -- if the operation is an update
        ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO fiscal_entities_users_link_history SELECT 'UPDATE', NEW.*;
        -- if the operation is an insert
        ELSIF (TG_OP = 'INSERT') THEN
            INSERT INTO fiscal_entities_users_link_history SELECT 'INSERT', NEW.*;
        END IF;
        RETURN NULL;
    END;

$fiscal_entities_users_link_history$ 
LANGUAGE plpgsql;

-- create trigger
CREATE TRIGGER fiscal_entities_users_link_history_trigger
    AFTER INSERT OR UPDATE OR DELETE ON fiscal_entities_users_link
        FOR EACH ROW EXECUTE PROCEDURE fiscal_entities_users_link_history_trigger_function();