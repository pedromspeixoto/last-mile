CREATE TABLE IF NOT EXISTS document_identification_cards_history (
    action varchar(20),
    id serial,
    identification_card_identification varchar(255),
    driver_identification varchar(255),
    name varchar(255),
    surname varchar(255),
    birth_date date,
    document_number varchar(255),
    document_expiry_date date,
    document_front_file_id varchar(255),
    document_back_file_id varchar(255),
    validated boolean,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);

-- create trigger function
CREATE OR REPLACE FUNCTION document_identification_cards_history_trigger_function() RETURNS trigger AS $document_identification_cards_history$

    BEGIN
        -- if the operation is a delete
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO document_identification_cards_history SELECT 'DELETE', OLD.*;
        -- if the operation is an update
        ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO document_identification_cards_history SELECT 'UPDATE', NEW.*;
        -- if the operation is an insert
        ELSIF (TG_OP = 'INSERT') THEN
            INSERT INTO document_identification_cards_history SELECT 'INSERT', NEW.*;
        END IF;
        RETURN NULL;
    END;

$document_identification_cards_history$ 
LANGUAGE plpgsql;

-- create trigger
CREATE TRIGGER document_identification_cards_history_trigger
    AFTER INSERT OR UPDATE OR DELETE ON document_identification_cards
        FOR EACH ROW EXECUTE PROCEDURE document_identification_cards_history_trigger_function();